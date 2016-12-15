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
