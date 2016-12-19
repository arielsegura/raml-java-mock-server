# RAML Mock Server for Java [![Build Status](https://travis-ci.org/arielsegura/raml-java-mock-server.svg?branch=master)](https://travis-ci.org/arielsegura/raml-java-mock-server) [![Coverage Status](https://coveralls.io/repos/github/arielsegura/raml-java-mock-server/badge.svg?branch=master)](https://coveralls.io/github/arielsegura/raml-java-mock-server?branch=master)

Mock server that uses [WireMock] (https://github.com/tomakehurst/wiremock) and [RAML Parser] (https://github.com/raml-org/raml-java-parser) to configure a mocked API based on the examples provided in RAML. 

## Usage

To mock an API just pass the path to the root RAML file and every resource will be stubbed in wiremock. 

```java
RamlMockServer ramlMockServer = new RamlMockServer(pathToRaml);
// this will launch wiremock stubbing every endpoint with an example in the RAML file. 
ramlMockServer.shutDown();
```

For those resources with more than one response (i.e. 200, 400, 404, 500), only non-error responses will be processed. 
So for instance, for these responses: 
```raml
  responses:
    200:
      description: Phones generated
      body:
        application/json:
          example:
            |
              [
                {
                  "phoneId": "012001012",
                  "city": "Buenos Aires",
                  "number": "116442323"
                }
              ]
    404:
      description: Employee not found
      body:
        application/json:
          example:
            |
              {
                "error": "employee not found."
              }
```
Only the one with status code 200 will be mocked. If you want to test or mock the API including the status code you need to provide both valid and invalid uri parameters for the resource: 
```java
RequestMapping requestMapping =
    requestMappingBuilder().
        addResource("/v1/employees/{employeeId}/phones", resourceConfig()
              .configureStatusCode(404, uriParameter("employeeId", invalidEmployeeId))
              .configureStatusCode(200, uriParameter("employeeId", employeeId))
              .build())
    .build();
RamlMockServer ramlMockServer = new RamlMockServer(requestMapping, pathToRaml);
```
If you still have nested resources with uri parameters you don't need to override every uri parameter: 
```java
RequestMapping requestMapping =
    requestMappingBuilder().
        addResource("/v1/employees/{employeeId}/phones/{phoneId}", resourceConfig()
              .configureStatusCode(404, uriParameter("phoneId", invalidPhoneId))
              .configureStatusCode(200, uriParameter("phoneId", phoneId))
              .build())
        .build();
RamlMockServer ramlMockServer = new RamlMockServer(requestMapping, pathToRaml);
```
In this case, `{employeeId}` will match with any input you send, but `{phoneId}` will only send 200 with `phoneId`. 
