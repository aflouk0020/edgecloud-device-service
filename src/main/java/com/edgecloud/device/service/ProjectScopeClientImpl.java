package com.edgecloud.device.service;
import com.edgecloud.device.exception.ProjectScopeAccessException; import java.util.*; import org.springframework.beans.factory.annotation.Value; import org.springframework.core.ParameterizedTypeReference; import org.springframework.http.HttpHeaders; import org.springframework.stereotype.Component; import org.springframework.web.client.RestClient;
@Component public class ProjectScopeClientImpl implements ProjectScopeClient {
 private final RestClient client; public ProjectScopeClientImpl(@Value("${edgecloud.project-service.url:${EDGE_CLOUD_PROJECT_URL:http://localhost:8086}}") String url){client=RestClient.builder().baseUrl(url).build();}
 @Override public void requireProjectAccess(UUID projectId,String token){client.get().uri("/api/v2/projects/{id}",projectId).header(HttpHeaders.AUTHORIZATION,"Bearer "+token).exchange((request,response)->{if(!response.getStatusCode().is2xxSuccessful())throw denied();return null;});}
 @Override public void requireDeviceAssociation(UUID projectId,UUID deviceId,String token){if(!projectDeviceIds(projectId,token).contains(deviceId))throw denied();}
 @Override public Set<UUID> projectDeviceIds(UUID projectId,String token){List<Association> associations=client.get().uri("/api/v2/projects/{id}/devices",projectId).header(HttpHeaders.AUTHORIZATION,"Bearer "+token).exchange((request,response)->{if(!response.getStatusCode().is2xxSuccessful())throw denied();return response.bodyTo(new ParameterizedTypeReference<List<Association>>(){});});if(associations==null)return Set.of();Set<UUID> ids=new HashSet<>();for(Association a:associations)if("ACTIVE".equals(a.status()))try{ids.add(UUID.fromString(a.deviceId()));}catch(IllegalArgumentException ignored){}return ids;}
 private ProjectScopeAccessException denied(){return new ProjectScopeAccessException("Project-scoped resource is not accessible");}
 private record Association(UUID projectId,String deviceId,String status){}
}
