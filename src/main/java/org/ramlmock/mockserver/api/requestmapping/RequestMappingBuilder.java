package org.ramlmock.mockserver.api.requestmapping;

/**
 * Created by arielsegura on 12/8/16.
 */
public interface RequestMappingBuilder {

    RequestMapping build();

    RequestMappingBuilder addResource(String resource);

    StatusCodeBuilder forStatusCode(Integer statusCode);

    interface StatusCodeBuilder {
        ResourceMapBuilder useParameters(UriParameters uriParameters);
    }

    interface ResourceMapBuilder {
        StatusCodeBuilder forStatusCode(Integer statusCode);
    }

}
