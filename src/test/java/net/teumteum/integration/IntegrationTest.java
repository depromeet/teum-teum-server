package net.teumteum.integration;

import net.teumteum.Application;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "10000")
@ContextConfiguration(classes = {Application.class, Api.class, Repository.class, SecurityContextSetting.class, TestLoginContext.class})
abstract public class IntegrationTest {

    @Autowired
    protected Api api;

    @Autowired
    protected Repository repository;

    @Autowired
    protected SecurityContextSetting securityContextSetting;

    @Autowired
    protected TestLoginContext loginContext;

    @AfterEach
    @BeforeEach
    void clearAll() {
        repository.clear();
    }

    @BeforeEach
    void setSecurityContextSetting() {
        securityContextSetting.set();
    }
}
