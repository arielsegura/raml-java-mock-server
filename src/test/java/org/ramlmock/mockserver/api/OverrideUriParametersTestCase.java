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

import feign.FeignException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.ramlmock.mockserver.api.requestmapping.RequestMapping;
import org.ramlmock.mockserver.api.requestmapping.RequestMappingBuilder;
import org.ramlmock.mockserver.api.requestmapping.UriParameters;
import org.ramlmock.mockserver.internal.RequestMappingBuilderImpl;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by arielsegura on 12/9/16.
 */
@RunWith(Parameterized.class)
public class OverrideUriParametersTestCase extends RamlMockServerTest{

    @Test
    public void twoStatusCodePhones() {
        RequestMappingBuilder requestMappingBuilder = new RequestMappingBuilderImpl();
        requestMappingBuilder
                .addResource("/v1/employees/{employeeId}/phones")
                .forStatusCode(404).useParameters(
                new UriParameters()
                        .add("employeeId", invalidEmployeeId)
        ).forStatusCode(200).useParameters(
                new UriParameters()
                        .add("employeeId", employeeId)
        );
        RequestMapping requestMapping = requestMappingBuilder
                .build();
        RamlMockServer classUnderTest = new RamlMockServer(requestMapping, "two-status-code/api.raml");
        SimpleScenarioClient client = SimpleScenarioClient.connect(classUnderTest);

        List<Phone> result = client.phones(employeeId);
        Phone phone = result.get(0);

        assertEquals("012001012", phone.getPhoneId());
        assertEquals("Buenos Aires", phone.getCity());
        assertEquals("116442323", phone.getNumber());

        try{
            client.phones(invalidEmployeeId);
            fail();
        } catch (FeignException ex){
            assertEquals(404, ex.status());
            assertTrue(ex.getMessage().contains("error"));
            assertTrue(ex.getMessage().contains("employee not found."));
        }

        classUnderTest.shutdown();
    }

    @Test
    public void okResponseShouldBeUsedIfErrorResponseIsNotOverwritten() {
        RequestMappingBuilder requestMappingBuilder = new RequestMappingBuilderImpl();
        requestMappingBuilder
                .addResource("/v1/employees/{employeeId}/phones/{phoneId}")
                .forStatusCode(404).useParameters(
                new UriParameters()
                        .add("phoneId", invalidPhoneId)
        ).forStatusCode(200).useParameters(
                new UriParameters()
                        .add("phoneId", phoneId)
        );
        RequestMapping requestMapping = requestMappingBuilder
                .build();
        RamlMockServer classUnderTest = new RamlMockServer(requestMapping, "two-status-code/api.raml");

        SimpleScenarioClient client = SimpleScenarioClient.connect(classUnderTest);

        assertEquals(client.phones(employeeId), client.phones(invalidEmployeeId));

        classUnderTest.shutdown();
    }

    @Test
    public void twoStatusCodePhoneId() {
        RequestMappingBuilder requestMappingBuilder = new RequestMappingBuilderImpl();
        requestMappingBuilder
                .addResource("/v1/employees/{employeeId}/phones/{phoneId}")
                .forStatusCode(404).useParameters(
                new UriParameters()
                        .add("phoneId", invalidPhoneId)
        ).forStatusCode(200).useParameters(
                new UriParameters()
                        .add("phoneId", phoneId)
        );
        RequestMapping requestMapping = requestMappingBuilder
                .build();
        RamlMockServer classUnderTest = new RamlMockServer(requestMapping, "two-status-code/api.raml");

        SimpleScenarioClient client = SimpleScenarioClient.connect(classUnderTest);

        Phone phone = client.phoneById(employeeId, phoneId);

        assertEquals("012001012", phone.getPhoneId());
        assertEquals("Buenos Aires", phone.getCity());
        assertEquals("116442323", phone.getNumber());

        try{
            client.phoneById(employeeId, invalidPhoneId);
            fail();
        } catch (FeignException ex){
            assertEquals(404, ex.status());
            assertTrue(ex.getMessage().contains("error"));
            assertTrue(ex.getMessage().contains("phone not found."));
        }

        classUnderTest.shutdown();
    }
}
