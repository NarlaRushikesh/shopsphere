package com.example.demo;

// FILE LOCATION:
// config-server/src/test/java/com/example/demo/ConfigServerApplicationTest.java
//
// NOTE: config-server contains no custom business logic to unit-test.
// This smoke test verifies the application context starts correctly.
//
// Add the following to src/test/resources/application.properties to avoid
// connecting to a remote git repo during the test:
//   spring.cloud.config.server.git.uri=classpath:/config-repo
//   spring.profiles.active=native
//   spring.cloud.config.server.native.search-locations=classpath:/config-repo

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.profiles.active=native",
        "spring.cloud.config.server.native.search-locations=classpath:/config-repo"
})
class ConfigServerApplicationTest {

    @Test
    void contextLoads() {
        // Passes if the Spring application context starts successfully
    }
}