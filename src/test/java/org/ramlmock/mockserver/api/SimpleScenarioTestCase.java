package org.ramlmock.mockserver.api;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by arielsegura on 12/9/16.
 */
public class SimpleScenarioTestCase extends RamlMockServerTest{

    @Test
    public void simpleScenarioRetrievesAddress() {
        RamlMockServer classUnderTest = new RamlMockServer(pathToRaml);
        RamlMockServerTest.SimpleScenarioClient client = RamlMockServerTest.SimpleScenarioClient.connect(classUnderTest);

        RamlMockServerTest.Address address = client.address(employeeId);

        assertEquals("9 de Julio", address.getStreet());
        assertEquals("Buenos Aires", address.getCity());
        assertEquals("4352", address.getNumber());

        classUnderTest.shutdown();
    }

    @Test public void simpleScenarioRetrievesPhones() {
        RamlMockServer classUnderTest = new RamlMockServer(pathToRaml);
        RamlMockServerTest.SimpleScenarioClient client = RamlMockServerTest.SimpleScenarioClient.connect(classUnderTest);

        List<RamlMockServerTest.Phone> phones = client.phones(employeeId);

        assertEquals(1, phones.size());

        RamlMockServerTest.Phone phone = phones.get(0);

        assertEquals("012001012", phone.getPhoneId());
        assertEquals("Buenos Aires", phone.getCity());
        assertEquals("116442323", phone.getNumber());

        classUnderTest.shutdown();
    }

    @Test public void simpleScenarioRetrievesPhoneById() {
        RamlMockServer classUnderTest = new RamlMockServer("simple-raml/api.raml");
        RamlMockServerTest.SimpleScenarioClient client = RamlMockServerTest.SimpleScenarioClient.connect(classUnderTest);

        RamlMockServerTest.Phone phone = client.phoneById(employeeId, phoneId);

        assertEquals("012001012", phone.getPhoneId());
        assertEquals("Buenos Aires", phone.getCity());
        assertEquals("116442323", phone.getNumber());

        classUnderTest.shutdown();
    }

}
