package com.edgecloud.device.service;

import com.edgecloud.device.config.DeviceThresholdProperties;
import com.edgecloud.device.dto.DeviceInventoryItemResponse;
import com.edgecloud.device.dto.DeviceInventoryResponse;
import com.edgecloud.device.dto.DeviceOrganisationLabelResponse;
import com.edgecloud.device.entity.EdgeDevice;
import com.edgecloud.device.entity.DeviceGroupMembership;
import com.edgecloud.device.entity.DeviceTagAssignment;
import com.edgecloud.device.exception.ProjectScopeAccessException;
import com.edgecloud.device.repository.*;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Subquery;

@Service
public class DeviceInventoryServiceImpl implements DeviceInventoryService {

    private static final Map<String, String> SORT_FIELDS = Map.of(
            "name", "deviceName",
            "status", "status",
            "lastSeen", "lastHeartbeat",
            "registrationDate", "registeredAt");

    private final EdgeDeviceRepository repository;
    private final DeviceThresholdProperties thresholdProperties;
    private final DeviceGroupRepository groups;
    private final DeviceTagRepository tags;
    private final DeviceGroupMembershipRepository memberships;
    private final DeviceTagAssignmentRepository assignments;
    private final ProjectScopeClient projects;
    private final DeviceConfigurationRepository configurations;
    private final HeartbeatPolicyService heartbeatPolicy;

    public DeviceInventoryServiceImpl(EdgeDeviceRepository repository,
                                      DeviceThresholdProperties thresholdProperties, DeviceGroupRepository groups,
                                      DeviceTagRepository tags, DeviceGroupMembershipRepository memberships,
                                      DeviceTagAssignmentRepository assignments, ProjectScopeClient projects,
                                      DeviceConfigurationRepository configurations, HeartbeatPolicyService heartbeatPolicy) {
        this.repository = repository;
        this.thresholdProperties = thresholdProperties;
        this.groups=groups;this.tags=tags;this.memberships=memberships;this.assignments=assignments;this.projects=projects;this.configurations=configurations;this.heartbeatPolicy=heartbeatPolicy;
    }

    @Override
    @Transactional(readOnly = true)
    public DeviceInventoryResponse getInventory(
            String search, int page, int size, String sort, String direction) {
        return getInventory(search,page,size,sort,direction,null,null,List.of(),null,null);
    }

    @Override
    @Transactional(readOnly = true)
    public DeviceInventoryResponse getInventory(String search,int page,int size,String sort,String direction,
            UUID projectId,UUID groupId,List<UUID> tagIds,String bearerToken) {
        return getInventory(search,page,size,sort,direction,projectId,groupId,tagIds,null,bearerToken);
    }

    @Override @Transactional(readOnly=true)
    public DeviceInventoryResponse getInventory(String search,int page,int size,String sort,String direction,
            UUID projectId,UUID groupId,List<UUID> tagIds,com.edgecloud.device.entity.HeartbeatStatus heartbeatStatus,String bearerToken) {
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException("Page must be at least 0 and size must be between 1 and 100");
        }

        String sortProperty = SORT_FIELDS.get(sort);
        if (sortProperty == null) {
            throw new IllegalArgumentException("Unsupported sort field: " + sort);
        }

        Sort.Direction sortDirection;
        try {
            sortDirection = Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Direction must be asc or desc");
        }

        var pageable = PageRequest.of(page, size,
                Sort.by(sortDirection, sortProperty).and(Sort.by(Sort.Direction.ASC, "id")));
        String query = search == null ? "" : search.trim();
        boolean filtered=projectId!=null||groupId!=null||!tagIds.isEmpty()||heartbeatStatus!=null;
        org.springframework.data.domain.Page<EdgeDevice> result;
        if(!filtered) result=query.isEmpty()?repository.findAll(pageable):findByNameOrId(query,pageable);
        else {
            if(projectId==null&&(groupId!=null||!tagIds.isEmpty()))throw new IllegalArgumentException("projectId is required for group or tag filters");
            Set<UUID> projectDevices=projectId==null?null:projects.projectDeviceIds(projectId,bearerToken);
            if(groupId!=null&&groups.findByIdAndProjectId(groupId,projectId).isEmpty())throw new ProjectScopeAccessException("Project-scoped resource is not accessible");
            if(tags.findAllByIdInAndProject(tagIds,projectId).size()!=new HashSet<>(tagIds).size())throw new ProjectScopeAccessException("Project-scoped resource is not accessible");
            result=repository.findAll(specification(query,projectDevices,groupId,new LinkedHashSet<>(tagIds),heartbeatStatus),pageable);
        }
        LocalDateTime now = LocalDateTime.now();
        List<UUID> ids=result.getContent().stream().map(EdgeDevice::getId).toList();
        Map<UUID,List<DeviceOrganisationLabelResponse>> groupMap=groupLabels(ids);
        Map<UUID,List<String>> tagMap=tagNames(ids);
        Map<UUID,com.edgecloud.device.entity.DeviceConfiguration> configMap=new HashMap<>();
        configurations.findAllById(ids).forEach(c->configMap.put(c.getDeviceId(),c));

        return new DeviceInventoryResponse(
                result.getContent().stream().map(device -> toItem(device, now,configMap.get(device.getId()),
                        tagMap.getOrDefault(device.getId(),List.of()),groupMap.getOrDefault(device.getId(),List.of()),projectId)).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages(),
                sort, sortDirection.name().toLowerCase(Locale.ROOT));
    }

    private Specification<EdgeDevice> specification(String query,Set<UUID> projectDevices,UUID groupId,Set<UUID> tagIds,com.edgecloud.device.entity.HeartbeatStatus heartbeatStatus){return(root,cq,cb)->{List<jakarta.persistence.criteria.Predicate> p=new ArrayList<>();if(projectDevices!=null)p.add(root.get("id").in(projectDevices));if(heartbeatStatus!=null)p.add(cb.equal(root.get("heartbeatState"),heartbeatStatus));if(!query.isEmpty()){try{UUID id=UUID.fromString(query);p.add(cb.or(cb.equal(root.get("id"),id),cb.like(cb.lower(root.get("deviceName")),"%"+query.toLowerCase(Locale.ROOT)+"%")));}catch(IllegalArgumentException e){p.add(cb.like(cb.lower(root.get("deviceName")),"%"+query.toLowerCase(Locale.ROOT)+"%"));}}if(groupId!=null){Subquery<UUID>s=cq.subquery(UUID.class);var m=s.from(DeviceGroupMembership.class);s.select(m.get("deviceId")).where(cb.equal(m.get("groupId"),groupId),cb.equal(m.get("deviceId"),root.get("id")));p.add(cb.exists(s));}for(UUID tagId:tagIds){Subquery<UUID>s=cq.subquery(UUID.class);var a=s.from(DeviceTagAssignment.class);s.select(a.get("deviceId")).where(cb.equal(a.get("tagId"),tagId),cb.equal(a.get("deviceId"),root.get("id")));p.add(cb.exists(s));}return cb.and(p.toArray(jakarta.persistence.criteria.Predicate[]::new));};}

    private Map<UUID,List<DeviceOrganisationLabelResponse>> groupLabels(List<UUID> deviceIds){if(deviceIds.isEmpty())return Map.of();List<DeviceGroupMembership> links=memberships.findByDeviceIdIn(deviceIds);Map<UUID,com.edgecloud.device.entity.DeviceGroup> byId=new HashMap<>();groups.findAllById(links.stream().map(DeviceGroupMembership::getGroupId).collect(java.util.stream.Collectors.toSet())).forEach(g->byId.put(g.getId(),g));Map<UUID,List<DeviceOrganisationLabelResponse>> out=new HashMap<>();for(DeviceGroupMembership link:links){var g=byId.get(link.getGroupId());if(g!=null)out.computeIfAbsent(link.getDeviceId(),k->new ArrayList<>()).add(new DeviceOrganisationLabelResponse(g.getId(),g.getName()));}out.values().forEach(v->v.sort(Comparator.comparing(DeviceOrganisationLabelResponse::name,String.CASE_INSENSITIVE_ORDER)));return out;}
    private Map<UUID,List<String>> tagNames(List<UUID> deviceIds){if(deviceIds.isEmpty())return Map.of();List<DeviceTagAssignment> links=assignments.findByDeviceIdIn(deviceIds);Map<UUID,com.edgecloud.device.entity.DeviceTag> byId=new HashMap<>();tags.findAllById(links.stream().map(DeviceTagAssignment::getTagId).collect(java.util.stream.Collectors.toSet())).forEach(t->byId.put(t.getId(),t));Map<UUID,List<String>> out=new HashMap<>();for(DeviceTagAssignment link:links){var t=byId.get(link.getTagId());if(t!=null)out.computeIfAbsent(link.getDeviceId(),k->new ArrayList<>()).add(t.getName());}out.values().forEach(v->v.sort(String.CASE_INSENSITIVE_ORDER));return out;}

    private org.springframework.data.domain.Page<EdgeDevice> findByNameOrId(
            String query, org.springframework.data.domain.Pageable pageable) {
        try {
            return repository.findByIdOrDeviceNameContainingIgnoreCase(UUID.fromString(query), query, pageable);
        } catch (IllegalArgumentException ignored) {
            return repository.findByDeviceNameContainingIgnoreCase(query, pageable);
        }
    }

    private DeviceInventoryItemResponse toItem(EdgeDevice device, LocalDateTime now,com.edgecloud.device.entity.DeviceConfiguration configuration,List<String> organisationTags,List<DeviceOrganisationLabelResponse> groupLabels,UUID projectId) {
        int interval=configuration==null?DeviceConfigurationServiceImpl.DEFAULT_HEARTBEAT:configuration.getHeartbeatIntervalSeconds();
        int timeout=configuration==null?DeviceConfigurationServiceImpl.DEFAULT_HEARTBEAT_TIMEOUT:configuration.getHeartbeatTimeoutSeconds();
        var heartbeat=heartbeatPolicy.calculate(device.getLastHeartbeat(),device.getOnlineSince(),interval,timeout,now);

        return new DeviceInventoryItemResponse(
                device.getId(), device.getDeviceName(), device.getDeviceType(), device.getIpAddress(), device.getStatus(),
                heartbeat.status().name(), device.getLastHeartbeat(),heartbeat.nextExpectedHeartbeat(),interval,timeout,
                heartbeat.consecutiveMissedHeartbeats(),device.getLastRecoveryAt(),heartbeat.connectionDuration()==null?null:heartbeat.connectionDuration().getSeconds(),
                device.getFirmwareVersion(), projectId==null?null:projectId.toString(),
                device.getRegisteredAt(), device.getLastHeartbeat(), organisationTags, groupLabels, device.getPhysicalLocation(),
                device.getDescription(), device.getOperatingSystem(), device.isActive(), device.isMaintenanceMode(),
                device.getMaintenanceReason(), device.getMaintenanceEnabledAt(), device.getMaintenanceEnabledBy(),
                device.getMaintenanceScheduledEndAt(), device.getUpdatedAt());
    }
}
