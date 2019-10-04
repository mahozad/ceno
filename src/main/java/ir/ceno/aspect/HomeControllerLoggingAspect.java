package ir.ceno.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j(topic = "ir.ceno.controller.HomeController")
public class HomeControllerLoggingAspect {

    @Before("execution(* ir.ceno.controller.HomeController.home(..))")
    public void logBefore(JoinPoint joinPoint) {
        log.info("************************************");
        log.info(joinPoint.getSignature().getName());
        log.info("************************************");
    }
}
