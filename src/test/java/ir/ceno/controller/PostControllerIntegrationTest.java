package ir.ceno.controller;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@Tag("integration")
@ExtendWith(SpringExtension.class)
/*
 * Bootstraps the entire and full application context. Application context is cached in between
 * @tests (it starts only once). You can control the cache using the @DirtiesContext annotation.
 */
@SpringBootTest
/*
 * MockMvc causes the full Spring application context to be started (so an integration test),
 * BUT without the cost of starting the tomcat server.
 * Remove this annotation and the MockMvc object and the tests will be with the real server behind!
 * (You can use TestRestTemplate object instead of MockMvc to send requests)
 */
@AutoConfigureMockMvc
@ActiveProfiles("test") /* load properties from application-test.properties */
class PostControllerIntegrationTest {

    @Autowired
    private PostController postController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void home() {}
}
