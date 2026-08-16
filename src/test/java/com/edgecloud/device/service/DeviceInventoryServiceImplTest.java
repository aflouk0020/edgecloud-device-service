package com.edgecloud.device.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.edgecloud.device.config.DeviceThresholdProperties;
import com.edgecloud.device.entity.DeviceStatus;
import com.edgecloud.device.entity.EdgeDevice;
import com.edgecloud.device.repository.EdgeDeviceRepository;
import com.edgecloud.device.repository.DeviceGroupRepository;
import com.edgecloud.device.repository.DeviceTagRepository;
import com.edgecloud.device.repository.DeviceGroupMembershipRepository;
import com.edgecloud.device.repository.DeviceTagAssignmentRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Set;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class DeviceInventoryServiceImplTest {

    @Mock EdgeDeviceRepository repository;
    @Mock DeviceGroupRepository groups;
    @Mock DeviceTagRepository tags;
    @Mock DeviceGroupMembershipRepository memberships;
    @Mock DeviceTagAssignmentRepository assignments;
    @Mock ProjectScopeClient projects;
    private DeviceInventoryServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new DeviceInventoryServiceImpl(repository, new DeviceThresholdProperties(60), groups, tags, memberships, assignments, projects);
        org.mockito.Mockito.lenient().when(memberships.findByDeviceIdIn(any())).thenReturn(List.of());
        org.mockito.Mockito.lenient().when(assignments.findByDeviceIdIn(any())).thenReturn(List.of());
    }

    @Test
    void returnsPagedInventoryIncludingOfflineDevicesAndMissingOptionalMetadata() {
        EdgeDevice online = device("Alpha", DeviceStatus.ONLINE, LocalDateTime.now().minusSeconds(10));
        EdgeDevice offline = device("Bravo", DeviceStatus.OFFLINE, LocalDateTime.now().minusMinutes(5));
        when(repository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(online, offline)));

        var response = service.getInventory("", 0, 20, "name", "asc");

        assertThat(response.devices()).hasSize(2);
        assertThat(response.devices()).extracting(item -> item.operationalStatus())
                .containsExactly(DeviceStatus.ONLINE, DeviceStatus.OFFLINE);
        assertThat(response.devices().get(0).heartbeatStatus()).isEqualTo("CURRENT");
        assertThat(response.devices().get(1).heartbeatStatus()).isEqualTo("STALE");
        assertThat(response.devices()).allSatisfy(item -> {
            assertThat(item.firmwareVersion()).isNull();
            assertThat(item.assignedProject()).isNull();
            assertThat(item.tags()).isEmpty();
            assertThat(item.location()).isNull();
        });
    }

    @Test
    void searchesAtRepositoryLevelAndUsesRequestedPageAndSort() {
        when(repository.findByDeviceNameContainingIgnoreCase(eq("Alpha"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        service.getInventory(" Alpha ", 2, 10, "lastSeen", "desc");

        var pageable = org.mockito.ArgumentCaptor.forClass(Pageable.class);
        verify(repository).findByDeviceNameContainingIgnoreCase(eq("Alpha"), pageable.capture());
        assertThat(pageable.getValue().getPageNumber()).isEqualTo(2);
        assertThat(pageable.getValue().getPageSize()).isEqualTo(10);
        assertThat(pageable.getValue().getSort().getOrderFor("lastHeartbeat").isDescending()).isTrue();
    }

    @Test
    void searchesFullDeviceIdEfficiently() {
        var id = java.util.UUID.fromString("11111111-1111-1111-1111-111111111111");
        when(repository.findByIdOrDeviceNameContainingIgnoreCase(eq(id), eq(id.toString()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));
        service.getInventory(id.toString(), 0, 20, "name", "asc");
        verify(repository).findByIdOrDeviceNameContainingIgnoreCase(eq(id), eq(id.toString()), any(Pageable.class));
    }

    @Test
    void supportsEmptyInventory() {
        when(repository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));
        assertThat(service.getInventory("", 0, 20, "status", "asc").devices()).isEmpty();
    }

    @Test
    void composesProjectGroupMultipleTagSearchPaginationAndSortingFilters() {
        UUID project=UUID.randomUUID(),group=UUID.randomUUID(),tag1=UUID.randomUUID(),tag2=UUID.randomUUID();
        EdgeDevice match=device("Alpha",DeviceStatus.ONLINE,LocalDateTime.now());
        ReflectionTestUtils.setField(match,"id",UUID.randomUUID());
        when(projects.projectDeviceIds(project,"token")).thenReturn(Set.of(match.getId()));
        when(groups.findByIdAndProjectId(group,project)).thenReturn(java.util.Optional.of(new com.edgecloud.device.entity.DeviceGroup(group,project)));
        when(tags.findAllByIdInAndProject(any(),eq(project))).thenReturn(List.of(new com.edgecloud.device.entity.DeviceTag(tag1,project),new com.edgecloud.device.entity.DeviceTag(tag2,project)));
        when(repository.findAll(any(Specification.class),any(Pageable.class))).thenReturn(new PageImpl<>(List.of(match)));
        var result=service.getInventory("Alpha",0,10,"registrationDate","desc",project,group,List.of(tag1,tag2),"token");
        assertThat(result.devices()).hasSize(1);
        assertThat(result.devices().get(0).assignedProject()).isEqualTo(project.toString());
        var pageable=org.mockito.ArgumentCaptor.forClass(Pageable.class);
        verify(repository).findAll(any(Specification.class),pageable.capture());
        assertThat(pageable.getValue().getPageSize()).isEqualTo(10);
        assertThat(pageable.getValue().getSort().getOrderFor("registeredAt").isDescending()).isTrue();
    }

    @Test
    void rejectsFiltersWithoutProjectAndCrossProjectIdentifiers() {
        UUID project=UUID.randomUUID(),group=UUID.randomUUID();
        assertThatThrownBy(()->service.getInventory("",0,20,"name","asc",null,group,List.of(),"token")).isInstanceOf(IllegalArgumentException.class);
        when(projects.projectDeviceIds(project,"token")).thenReturn(Set.of());
        when(groups.findByIdAndProjectId(group,project)).thenReturn(java.util.Optional.empty());
        assertThatThrownBy(()->service.getInventory("",0,20,"name","asc",project,group,List.of(),"token")).isInstanceOf(com.edgecloud.device.exception.ProjectScopeAccessException.class);
    }

    @Test
    void rejectsInvalidPaginationSortAndDirection() {
        assertThatThrownBy(() -> service.getInventory("", -1, 20, "name", "asc"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.getInventory("", 0, 101, "name", "asc"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.getInventory("", 0, 20, "firmware", "asc"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.getInventory("", 0, 20, "name", "sideways"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private EdgeDevice device(String name, DeviceStatus status, LocalDateTime heartbeat) {
        EdgeDevice device = new EdgeDevice();
        device.setDeviceName(name);
        device.setDeviceType("SENSOR");
        device.setIpAddress("10.0.0.1");
        device.setStatus(status);
        device.setLastHeartbeat(heartbeat);
        return device;
    }
}
