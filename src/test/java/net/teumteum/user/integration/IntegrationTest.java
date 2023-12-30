package net.teumteum.user.integration;

import net.teumteum.Application;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {Application.class, Api.class, Repository.class})
abstract class IntegrationTest {

    @Autowired
    protected Api api;

    @Autowired
    protected Repository repository;

    @AfterEach
    @BeforeEach
    void clearAll() {
        repository.clear();
    }

}
