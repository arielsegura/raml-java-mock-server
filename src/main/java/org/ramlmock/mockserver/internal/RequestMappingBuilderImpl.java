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
package org.ramlmock.mockserver.internal;

import org.ramlmock.mockserver.api.requestmapping.RequestMapping;
import org.ramlmock.mockserver.api.requestmapping.RequestMappingBuilder;
import org.ramlmock.mockserver.api.requestmapping.UriParameters;

/**
 * Created by arielsegura on 12/8/16.
 */
public final class RequestMappingBuilderImpl implements RequestMappingBuilder {

    RequestMapping requestMapping = new RequestMapping();

    String resource;

    public RequestMappingBuilder addResource(String resource) {
        this.resource = resource;
        requestMapping.addResource(resource, new ResourceMap());
        return this;
    }

    @Override
    public RequestMapping build() {
        return requestMapping;
    }

    @Override
    public StatusCodeBuilder forStatusCode(Integer statusCode) {
        return new StatusCodeBuilderImpl(statusCode);
    }

    private class StatusCodeBuilderImpl implements StatusCodeBuilder {


        private final Integer statusCode;

        public StatusCodeBuilderImpl(Integer statusCode) {
            this.statusCode = statusCode;
        }

        @Override
        public ResourceMapBuilder useParameters(UriParameters uriParameters) {
            return new ResourceMapBuilderImpl(statusCode, uriParameters);
        }
    }

    private class ResourceMapBuilderImpl implements ResourceMapBuilder {
        public ResourceMapBuilderImpl(Integer statusCode, UriParameters uriParameters) {
            requestMapping.getResource(resource).ifPresent(resourceMap -> resourceMap.add(statusCode, uriParameters));
        }

        @Override
        public StatusCodeBuilder forStatusCode(Integer statusCode) {
            return new StatusCodeBuilderImpl(statusCode);
        }
    }
}
