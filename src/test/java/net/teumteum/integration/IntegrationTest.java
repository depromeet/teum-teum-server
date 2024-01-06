package net.teumteum.integration;

import net.teumteum.Application;
import net.teumteum.core.context.LoginContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {Application.class, Api.class, Repository.class, TestLoginContext.class})
abstract public class IntegrationTest {

    @Autowired
    protected Api api;

    @Autowired
    protected Repository repository;

    @Autowired
    protected LoginContext loginContext;

    @AfterEach
    @BeforeEach
    void clearAll() {
        repository.clear();
    }
}
