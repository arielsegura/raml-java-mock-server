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

import feign.FeignException;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ramlmockserver.api.requestmapping.RequestMapping;
import ramlmockserver.api.requestmapping.RequestMappingBuilder;

import java.util.List;

import static org.junit.Assert.*;
import static ramlmockserver.api.requestmapping.RequestMappingBuilder.requestMappingBuilder;


/**
 * Created by arielsegura on 12/9/16.
 */
@RunWith(Parameterized.class)
public class OverrideUriParametersTestCase extends RamlMockServerTest{

    private RamlMockServer classUnderTest;

    @Test
    public void twoStatusCodePhones() {

        RequestMapping requestMapping =
                requestMappingBuilder().
                        addResource("/v1/employees/{employeeId}/phones", RequestMappingBuilder.resourceConfig()
                                .configureStatusCode(404, RequestMappingBuilder.uriParameter("employeeId", invalidEmployeeId))
                                .configureStatusCode(200, RequestMappingBuilder.uriParameter("employeeId", employeeId))
                                .build())
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

    }

    @Test
    public void overrideManyEndpoints() {
        RequestMapping requestMapping =
        requestMappingBuilder()
                .addResource("/v1/employees/{employeeId}/phones",
                        RequestMappingBuilder.resourceConfig()
                                .configureStatusCode(404, RequestMappingBuilder.uriParameter("employeeId", invalidEmployeeId))
                                .configureStatusCode(200, RequestMappingBuilder.uriParameter("employeeId", employeeId))
                                .build())
                .addResource("/v1/employees/{employeeId}/phones/{phoneId}",
                        RequestMappingBuilder.resourceConfig()
                                .configureStatusCode(404,
                                        RequestMappingBuilder.uriParameter("employeeId", employeeId),
                                        RequestMappingBuilder.uriParameter("phoneId", invalidPhoneId)
                                )
                                .configureStatusCode(200,
                                        RequestMappingBuilder.uriParameter("employeeId", employeeId),
                                        RequestMappingBuilder.uriParameter("phoneId", phoneId)
                                )
                                .build())
                .build();

        RamlMockServer classUnderTest = new RamlMockServer(requestMapping, "two-status-code/api.raml");

        SimpleScenarioClient client = SimpleScenarioClient.connect(classUnderTest);

        // test employee
        try{
            client.phones(invalidEmployeeId);
            fail();
        } catch (FeignException ex){
            assertEquals(404, ex.status());
        }

        try{
            List<Phone> phones = client.phones(employeeId);
            assertFalse(phones.isEmpty());
        } catch (FeignException ex){
            fail();
        }

        // test phone
        try{
            Phone phone = client.phoneById(employeeId, invalidPhoneId);
            fail();
        } catch (FeignException ex){
            assertEquals(404, ex.status());
        }

        try{
            Phone phone = client.phoneById(employeeId, phoneId);
            assertEquals("012001012", phone.getPhoneId());
            assertEquals("Buenos Aires", phone.getCity());
            assertEquals("116442323", phone.getNumber());
        } catch (FeignException ex){
            fail();
        }

    }

    private void assertNotFound(FeignException ex) {
        assertEquals(404, ex.status());
        assertTrue(ex.getMessage().contains("error"));
        assertTrue(ex.getMessage().contains("not found."));
    }

    @Test

    public void okResponseShouldBeUsedIfErrorResponseIsNotOverwritten() {
        RequestMapping requestMapping =
                requestMappingBuilder().
                        addResource("/v1/employees/{employeeId}/phones/{phoneId}", RequestMappingBuilder.resourceConfig()
                                .configureStatusCode(404, RequestMappingBuilder.uriParameter("phoneId", invalidPhoneId))
                                .configureStatusCode(200, RequestMappingBuilder.uriParameter("phoneId", phoneId))
                                .build())
                        .build();

        RamlMockServer classUnderTest = new RamlMockServer(requestMapping, "two-status-code/api.raml");

        SimpleScenarioClient client = SimpleScenarioClient.connect(classUnderTest);

        assertEquals(client.phones(employeeId), client.phones(invalidEmployeeId));

    }

    @Test
    public void twoStatusCodePhoneId() {

        RequestMapping requestMapping =
                requestMappingBuilder().
                        addResource("/v1/employees/{employeeId}/phones/{phoneId}", RequestMappingBuilder.resourceConfig()
                                .configureStatusCode(404, RequestMappingBuilder.uriParameter("phoneId", invalidPhoneId))
                                .configureStatusCode(200, RequestMappingBuilder.uriParameter("phoneId", phoneId))
                                .build())
                        .build();

        classUnderTest = new RamlMockServer(requestMapping, "two-status-code/api.raml");

        SimpleScenarioClient client = SimpleScenarioClient.connect(classUnderTest);

        Phone phone = client.phoneById(employeeId, phoneId);

        assertEquals("012001012", phone.getPhoneId());
        assertEquals("Buenos Aires", phone.getCity());
        assertEquals("116442323", phone.getNumber());

        try{
            client.phoneById(employeeId, invalidPhoneId);
            fail();
        } catch (FeignException ex){
            assertNotFound(ex);
        }


    }

    @After
    public void shutDown(){
        if(classUnderTest != null)
            classUnderTest.shutdown();
    }
}
