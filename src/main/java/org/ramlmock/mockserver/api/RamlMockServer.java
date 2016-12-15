/**
 * Copyright © 2016 Ariel Segura (ariel.segura@yahoo.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ramlmock.mockserver.api;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.google.common.base.Preconditions;
import org.ramlmock.mockserver.api.requestmapping.RequestMapping;
import org.ramlmock.mockserver.api.requestmapping.UriParameters;
import org.ramlmock.mockserver.internal.ResourceMap;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v08.api.Api;
import org.raml.v2.api.model.v08.bodies.BodyLike;
import org.raml.v2.api.model.v08.bodies.Response;
import org.raml.v2.api.model.v08.methods.Method;
import org.raml.v2.api.model.v08.resources.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static java.lang.String.format;

/**
 * Created by arielsegura on 12/8/16.
 */
public class RamlMockServer extends WireMockServer{

    private final Logger logger = LoggerFactory.getLogger(RamlMockServer.class);
    private RequestMapping requestMapping = new RequestMapping();
    private String regex = "\\{\\w*\\}"; // regex to match uri params i.e. /v1/employees/{employeeId}/phones/{phoneId}


    public RamlMockServer(RequestMapping requestMapping, String ramlLocation) {
        super(wireMockConfig().dynamicPort());
        this.requestMapping = requestMapping;
        start(ramlLocation);
        init(ramlLocation);
    }

    public RamlMockServer(RequestMapping requestMapping, String ramlLocation, int port) {
        super(wireMockConfig().port(port));
        this.requestMapping = requestMapping;
        start(ramlLocation);

        init(ramlLocation);
    }

    public RamlMockServer(String ramlLocation) {
        super(wireMockConfig().dynamicPort());
        start(ramlLocation);
        init(ramlLocation);
    }

    public RamlMockServer(String ramlLocation, int port) {
        super(wireMockConfig().port(port));
        start(ramlLocation);

        init(ramlLocation);
    }

    private void start(String ramlLocation) {
        this.start();
        logger.debug("Loading mocking server with raml {} in port {}. ", ramlLocation, this.port());
    }

    private void init(String ramlLocation) {
        try {
            RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(ramlLocation);

            Preconditions.checkArgument(!ramlModelResult.hasErrors(),
                    ramlModelResult.getValidationResults()
                            .stream()
                            .map(ValidationResult::getMessage)
                            .collect(Collectors.joining(", "))
            );

            Api api = ramlModelResult.getApiV08();

            Preconditions.checkArgument(api != null, "API cannot be null. Check if there were errors or the RAML version is not 0.8");

            logger.debug("RAML title: {}, version: {}", api.title(), api.version());

            api.resources().forEach(this::addStubIfHaveExamples);

        } catch(Exception ex) {
            logger.error("Error {} while loading raml. Shutting down server. ", ex.getMessage());
            this.shutdownServer();
            throw new IllegalStateException(ex);
        }
    }

    void addStubIfHaveExamples(Resource resource) {
        logger.debug("Processing resource {} ", resource.displayName());

        List<Method> methods = resource.methods();

        methods.forEach(this::addStubIfHaveExamples);

        resource.resources().forEach(this::addStubIfHaveExamples);

    }

    void addStubIfHaveExamples(final Method method) {
        Optional<ResourceMap> resourceMapOptional = requestMapping.getResource(method.resource().resourcePath());
        method.responses()
                .stream()
                .filter(response -> {
                    // if the response represents an error (i.e. 404)
                    // and the uri parameters are not overwritten
                    // ignore the response so it does not overrides 'ok' responses like 200
                    Integer statusCode = Integer.valueOf(response.code().value());

                    boolean anError = isStatusCodeAnError(statusCode);
                    if(resourceMapOptional.isPresent()){
                        ResourceMap resourceMap = resourceMapOptional.get();
                        if(anError){
                            return resourceMap.getStatusCode(statusCode).isPresent();
                        } else {
                            return true;
                        }
                    } else {
                        return !anError;
                    }
                })
                .forEach(response -> addStubIfHaveExamples(method, response));
    }

    private boolean isStatusCodeAnError(Integer code) {
        return code > 400;
    }

    void addStubIfHaveExamples(Method method, Response response) {
        List<BodyLike> body = response.body();
        if(body.isEmpty()){
            logger.debug("Ignoring resource because has no examples. ");
        } else {
            BodyLike bodyLike = body.get(0);// only get the first example. Multiple examples not supported.
            String example = bodyLike.example().value();
            String code = response.code().value();
            String contentType = bodyLike.name();
            super.stubFor(mappingBuilder(method, example, code, contentType));

        }
    }

    MappingBuilder mappingBuilder(Method method, String example, String code, String contentType){
        Integer status = Integer.valueOf(code);
        String urlRegex = buildPathForMethod(status, method);

        MappingBuilder request = WireMock.request(method.method().toUpperCase(), urlPathMatching(urlRegex));
        request.willReturn(aResponse()
                .withBody(example)
                .withHeader("Content-Type", contentType)
                .withStatus(status));
        return request;
    }

    String buildPathForMethod(Integer statusCode, Method method) {
        Resource resource = method.resource();
        Preconditions.checkArgument(resource != null, "Resource for method {} cannot be null. ", method.method());
        String resourcePath = resource.resourcePath();
        String urlRegex = getPathAsRegex(resourcePath);

        Optional<ResourceMap> anyStubWithSamePath = requestMapping.getResource(resourcePath);

        if(anyStubWithSamePath.isPresent()){
            // this path already exists. There should be UriParameters to use for this status code.
            Optional<ResourceMap> resourceMapOptional = requestMapping.getResource(resourcePath);
            if(resourceMapOptional.isPresent()) {
                ResourceMap resourceMap = resourceMapOptional.get();
                Optional<UriParameters> uriParametersOptional = resourceMap.getStatusCode(statusCode);
                if (uriParametersOptional.isPresent()) {
                    UriParameters uriParameters = uriParametersOptional.get();

                    for(String paramName : uriParameters.keys()){
                        String value = uriParameters.get(paramName);
                        resourcePath = resourcePath.replace(format("{%s}", paramName), value);
                    }
                    return getPathAsRegex(resourcePath);
                }
            }
        }
        return urlRegex;
    }

    private String getPathAsRegex(String resourcePath) {
        return resourcePath.replaceAll(regex, ".*");
    }
}
