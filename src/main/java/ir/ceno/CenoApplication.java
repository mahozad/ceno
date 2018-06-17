package ir.ceno;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class that boots the application.
 */
@SpringBootApplication
public class CenoApplication {

    // TODO: validate user inputs (by @Valid)
    // TODO: check for long words and force break them (by inserting a special character)
    // TODO: add layout rtl support in css with the property direction:rtl;
    // TODO: fix cards box shadows (shadow is pale around card file). use pseudo element for
    // shadow (performance will also be better with this approach)
    // TODO: add character counter to new-post input fields
    // TODO: add page hit counter: https://kodejava.org/tag/hit-counter/
    // TODO: add button for "scroll to top" of the page
    // TODO: spring.io site mostly throws "*NotFoundException"s in its controllers
    // TODO: throw exceptions in service layer and handle them in an @ExceptionHandler
    // TODO: add remember me option for login
    // TODO: add permanent link for posts
    // TODO: prompt default categories when user attempts to add a category to new post

    /**
     * Everything starts from here!
     *
     * @param args the program arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(CenoApplication.class, args);
    }
}
