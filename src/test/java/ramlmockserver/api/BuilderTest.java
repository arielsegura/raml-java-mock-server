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
package ramlmockserver.api;

import org.junit.Test;
import ramlmockserver.api.requestmapping.RequestMapping;
import ramlmockserver.api.requestmapping.RequestMappingBuilder;
import ramlmockserver.internal.ResourceMap;
import ramlmockserver.internal.UriParameter;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by Ariel on 18/12/2016.
 */
public class BuilderTest {
    String employeeId = "1234";
    String invalidPhoneId = "invalidPhoneId";
    String invalidEmployeeId = "invalidEmployeeId";
    String phoneId = "1234556";

    @Test
    public void simpleScenario(){
        String resourceName = "/v1/employees/{employeeId}/phones";
        RequestMapping requestMapping =
                RequestMappingBuilder.requestMappingBuilder().
                        addResource(resourceName, RequestMappingBuilder.resourceConfig()
                                .configureStatusCode(404, RequestMappingBuilder.uriParameter("employeeId", invalidEmployeeId))
                                .configureStatusCode(200, RequestMappingBuilder.uriParameter("employeeId", employeeId))
                                .build())
                        .build();

        Optional<ResourceMap> resource = requestMapping.getResource(resourceName);
        assertTrue(resource.isPresent());
        ResourceMap resourceMap = resource.get();

        Optional<UriParameter[]> notFound = resourceMap.getStatusCode(404);
        assertTrue(notFound.isPresent());

        UriParameter[] uriParameters = notFound.get();
        assertTrue(Arrays.stream(uriParameters).anyMatch(uriParameter -> uriParameter.getKey().equals("employeeId")));

        Optional<UriParameter[]> ok = resourceMap.getStatusCode(404);
        assertTrue(ok.isPresent());

        uriParameters = ok.get();
        assertTrue(Arrays.stream(uriParameters).anyMatch(uriParameter -> uriParameter.getKey().equals("employeeId")));
    }
}
