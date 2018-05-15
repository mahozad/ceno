package ir.ceno;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CenoApplication {

    // TODO: add remember me option for login
    // TODO: Add a link icon along with a box containing post url and a copy icon that copies that on click in post page
    // TODO: Prompt default categories when user attempts to add a category to post

    public static void main(String[] args) {
        SpringApplication.run(CenoApplication.class, args);
    }
}
