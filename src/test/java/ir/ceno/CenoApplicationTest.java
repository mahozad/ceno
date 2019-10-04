package ir.ceno;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Tag("integration")
@ExtendWith(SpringExtension.class)
@SpringBootTest
class CenoApplicationTest {

    /**
     * An integration test that determines whether the whole application successfully starts or not.
     */
    @Test
    void contextLoads() {}
}
