package ir.ceno;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Main class that boots the application.
 */
@SpringBootApplication
@EnableAspectJAutoProxy // to enable aspects
@ServletComponentScan // to enable scanning for @WebFilter
public class CenoApplication {

    // TODO: use elvis (?:) and safe navigation (?.) operators in thymeleaf

    // TODO: Use http push in the application

    /*
     * TODO: remove the “!important” keyword from the CSS rules that it has been
     *  applied to by refactoring the code so that it follows specificity rules.
     */

    // TODO: make border radiuses from 2px to 3px

    // FIXME: the register dialog and its javascript validation code

    // TODO: https://securityheaders.com/

    // TODO: www.speedment.com

    // TODO: do not log too much. for example do not log every request to your pages! as said in

    // TODO: https://images.guide

    // TODO: insert a warning icon next to the error prompts in input fields

    /*
     * TODO: display a GDPR compliant cookie message for first-time visits in
     *  that same prompt box of site
     */

    // FIXME: there is bug if like button is clicked multiple times very quickly

    /*
     * TODO: Usually text in the header or title has different typeface than the body
     *  Serifs and San-serifs work together really well so e.g. serif for title
     *  and san-serif for body so don't use serif with serif or san-serif with san-serif
     */

    // https://www.javaguides.net/2018/11/spring-boot-spring-mvc-spring-security-hibernate-mysql-tutorial.html1

    // from a book: "I want about one info-level log message for every significant application
    // event."

    // There must be only one <h1> on each page (not sure, google it)
    // For images, provide a text alternative. You can use alt="description" for informative images
    //   (those which have a meaning, like a picture or a standalone icon)
    //   and alt="" for decorative images (those which don’t have a meaning,
    //   like an icon inside a button and right next to its text).
    //   This is especially important on image links.

    // Concepts in AOP:
    // • Advice is the action we take. types include "around", "before" and "after"
    // • join point is kind of aspect (method or exception). In Spring it is always method execution
    // • pointcut is a predicate to match to see if an advice needs to be executed or not

    // bind data submitted by HTML form or query string parameters to a Java object (otherwise
    // known as a Spring command bean or backing bean)

    // owner (child) in hibernate means the entity that has the foreign key

    // Spring Boot Actuator dependency provides a number of production-ready monitoring features.
    // the list of endpoint are shown in the uri: /actuator
    // most of the endpoints are disabled on web; to enable them use this property:
    // management.endpoints.web.exposure.include=* // do this only in development environment

    // to auto-minify css and js files see: https://www.jetbrains.com/help/idea/compressing-css.html

    // spring-boot-devtools disables the caching options like thymeleaf and... to enable you to
    // see the real file you made changes to.

    // when spring-boot-devtools dependency is included, application automatically restarts
    // when any file in the classpath is updated. To cause the classpath to be updated In IntelliJ,
    // build the project. Alternatively to enable auto-build in IntelliJ, first in settings enable
    // the option "Compiler -> Build project automatically"; then press Ctrl + Shift + A and
    // type Registry... and in the new window enable the option "compiler.auto​make.allow..."
    //
    // These folders will not trigger a reload by default: (you can modify css and thymeleaf
    // files on the fly and then just build the project with Alt + Context. For the thymeleaf to
    // work, make sure to disable thymeleaf cache in application.properties file)
    //
    ///META-INF/maven
    ///META-INF/resources
    ///resources
    ///static
    ///public
    ///templates

    // The spring-boot-devtools module includes an embedded LiveReload server that can be used to
    // trigger a browser refresh when a resource is changed. LiveReload browser extensions are
    // freely available for Chrome, Firefox and Safari from livereload.com

    // to serve static content: <img th:src="@{/img/icon.png}" src="../img/icon.png"/>

    // layers can call APIs from below layers and also at the same level e.g. xxService can
    // call an API from yyService (as well as something such as mmRepository and so on)

    // TODO: what is sameSite cookie?
    // TODO: info on hover: https://codepen.io/tystrong/pen/rVJNrQ
    // TODO: make a panel (html page) for managing the site
    // TODO: add logging to the application
    // TODO: maximum of responses time should be less than 2 seconds
    // TODO: selenium testing
    // TODO: visit gatling.io
    // TODO: cobalt.io for penetration tests
    /*
     * TODO: log things whenever an illegal access is occurred, a failure happens
     *  or an exception is thrown
     */
    // TODO: what is cross-site scripting
    // TODO: handle DOS
    // TODO: after 3 times of incorrect login, prevent that user login for 5 minutes
    // TODO: validate user inputs (by @Valid)
    // TODO: check for long words and force break them (by inserting a special character)
    // TODO: add layout rtl support in css with the property direction:rtl;
    /*
     * TODO: fix cards box shadows (shadow is pale around card file). use pseudo element for
     *  shadow (performance will also be better with this approach)
     */
    // TODO: add character counter to new-post input fields
    // TODO: add page hit counter: https://kodejava.org/tag/hit-counter/
    // TODO: add button for "scroll to top" of the page
    // TODO: spring.io site mostly throws "*NotFoundException"s in its service layer
    // TODO: throw exceptions in service layer and handle them in an @ExceptionHandler
    // TODO: add remember me option for login
    // TODO: add permanent link for posts
    // TODO: prompt default categories when user attempts to add a category to new post
    // TODO: What is cache busting and how to append file hash to its name
    // TODO: What is server push

    /**
     * Everything starts from here!
     *
     * @param args the program arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(CenoApplication.class, args);
    }
}
