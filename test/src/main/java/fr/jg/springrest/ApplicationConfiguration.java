package fr.jg.springrest;

import fr.jg.springrest.errors.RestErrorAttributes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Value("${spring.profiles.active}")
    String profile;

    @Bean
    public ErrorAttributes errorAttributes() {
        return new RestErrorAttributes("dev".equals(this.profile));
    }
}
