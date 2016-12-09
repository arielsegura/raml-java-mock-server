package org.raml.mockserver.api.requestmapping;

import org.raml.mockserver.internal.ResourceMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by arielsegura on 12/8/16.
 */
public class RequestMapping {

    private Map<String, ResourceMap> resources = new HashMap<>();

    public Optional<ResourceMap> getResource(String name){
        return Optional.ofNullable(resources.get(name));
    }

    public void addResource(String resource, ResourceMap resourceMap) {
        resources.put(resource, resourceMap);
    }
}
