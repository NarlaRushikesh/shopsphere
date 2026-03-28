package com.example.demo;

// FILE LOCATION:
// eureka-server/src/test/java/com/example/demo/EurekaServerApplicationTest.java
//
// NOTE: eureka-server and config-server contain no business logic or custom
// classes to unit-test. The only meaningful test is a context-load smoke test
// that verifies the application context starts without errors.
//
// Add this dependency to pom.xml if not already present:
//   <dependency>
//     <groupId>org.springframework.boot</groupId>
//     <artifactId>spring-boot-starter-test</artifactId>
//     <scope>test</scope>
//   </dependency>
//
// Also add the following to src/test/resources/application.properties
// so the server does not try to register with itself during the test:
//   eureka.client.register-with-eureka=false
//   eureka.client.fetch-registry=false
//   spring.cloud.config.enabled=false

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "eureka.client.register-with-eureka=false",
        "eureka.client.fetch-registry=false"
})
class EurekaServerApplicationTest {

    @Test
    void contextLoads() {
        // Passes if the Spring application context starts successfully
    }
}