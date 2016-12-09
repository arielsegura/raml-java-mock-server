package org.ramlmock.mockserver.internal;

import org.ramlmock.mockserver.api.requestmapping.UriParameters;

import java.util.HashMap;
import java.util.Optional;

/**
 * Created by arielsegura on 12/8/16.
 */
public class ResourceMap {
    private HashMap<Integer, UriParameters> content = new HashMap<>();

    public Optional<UriParameters> getStatusCode(int statusCode){
        return Optional.ofNullable(content.get(statusCode));
    }

    public void add(Integer statusCode, UriParameters uriParameters){
        content.put(statusCode, uriParameters);
    }
}
