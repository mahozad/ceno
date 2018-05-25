package ir.ceno;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Runner that boots the application.
 */
@SpringBootApplication
public class CenoApplication {

    // TODO: spring.io site mostly throws "*NotFoundException"s in its controllers
    // TODO: throw exceptions in service layer and handle them in an @ExceptionHandler
    // TODO: add remember me option for login
    // TODO: add permanent link for posts
    // TODO: prompt default categories when user attempts to add a category to post
    // TODO: add http2 support

    /**
     * Everything starts from here!
     *
     * @param args the program arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(CenoApplication.class, args);
    }
}
