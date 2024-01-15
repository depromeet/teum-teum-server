package net.teumteum.integration;

import net.teumteum.Application;
import net.teumteum.core.config.RedisTestContainerConfig;
import net.teumteum.user.infra.GptTestServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "10000")
@ExtendWith(RedisTestContainerConfig.class)
@ContextConfiguration(classes = {
    Api.class,
    Repository.class,
    Application.class,
    GptTestServer.class,
    TestLoginContext.class,
    SecurityContextSetting.class})
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
