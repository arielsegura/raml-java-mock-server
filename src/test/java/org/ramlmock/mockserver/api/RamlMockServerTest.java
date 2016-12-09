package org.ramlmock.mockserver.api;

import feign.Feign;
import feign.Param;
import feign.RequestLine;
import feign.gson.GsonDecoder;
import feign.slf4j.Slf4jLogger;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

/**
 * Created by arielsegura on 12/8/16.
 */
@RunWith(Parameterized.class)
public abstract class RamlMockServerTest {

    @Parameterized.Parameters
    public static Iterable<String> data() {
        //TODO read folder instead of adding them manually
        return Arrays.asList("simple-raml/api.raml", "two-status-code/api.raml");
    }

    @Parameterized.Parameter
    public String pathToRaml;

    String employeeId = "1234";
    String invalidPhoneId = "invalidPhoneId";
    String invalidEmployeeId = "invalidEmployeeId";
    String phoneId = "1234556";

    private static String getUrl(RamlMockServer ramlMockServer) {
        return String.format("http://localhost:%s", ramlMockServer.port());
    }

    class Phone{
        String phoneId;
        String city;
        String number;

        public String getPhoneId() {
            return phoneId;
        }

        public void setPhoneId(String phoneId) {
            this.phoneId = phoneId;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Phone)) return false;

            Phone phone = (Phone) o;

            if (phoneId != null ? !phoneId.equals(phone.phoneId) : phone.phoneId != null) return false;
            if (city != null ? !city.equals(phone.city) : phone.city != null) return false;
            return number != null ? number.equals(phone.number) : phone.number == null;

        }

        @Override
        public int hashCode() {
            int result = phoneId != null ? phoneId.hashCode() : 0;
            result = 31 * result + (city != null ? city.hashCode() : 0);
            result = 31 * result + (number != null ? number.hashCode() : 0);
            return result;
        }
    }

    class Address{
        String street;
        String city;
        String number;

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Address)) return false;

            Address address = (Address) o;

            if (street != null ? !street.equals(address.street) : address.street != null) return false;
            if (city != null ? !city.equals(address.city) : address.city != null) return false;
            return number != null ? number.equals(address.number) : address.number == null;

        }

        @Override
        public int hashCode() {
            int result = street != null ? street.hashCode() : 0;
            result = 31 * result + (city != null ? city.hashCode() : 0);
            result = 31 * result + (number != null ? number.hashCode() : 0);
            return result;
        }
    }

    interface SimpleScenarioClient {

        @RequestLine("GET /v1/employees/{employeeId}/address")
        Address address(@Param("employeeId") String employeeId);

        @RequestLine("GET /v1/employees/{employeeId}/phones")
        List<Phone> phones(@Param("employeeId") String employeeId);

        @RequestLine("GET /v1/employees/{employeeId}/phones/{phoneId}")
        Phone phoneById(@Param("employeeId") String employeeId,
                                          @Param("phoneId") String phoneId);

        static SimpleScenarioClient connect(RamlMockServer ramlMockServer) {
            return Feign.builder()
                    .decoder(new GsonDecoder())
                    .logger(new Slf4jLogger())
                    .target(SimpleScenarioClient.class, getUrl(ramlMockServer));
        }
    }
}
