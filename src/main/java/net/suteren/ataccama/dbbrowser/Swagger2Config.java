package net.suteren.ataccama.dbbrowser;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;

@Configuration
@EnableSwagger2
@Import({ /*springfox.documentation.spring.data.rest.configuration.SpringDataRestConfiguration.class,*/
        springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration.class })
public class Swagger2Config {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SPRING_WEB).select()
                .apis(basePackage("net.suteren"))
                .paths(PathSelectors.regex("/.*"))
                .build().apiInfo(apiEndPointsInfo());
    }

    private ApiInfo apiEndPointsInfo() {
        return new ApiInfoBuilder().title("Spring Boot REST API")
                .description("Database Browser REST API")
                .version("1.0.0")
                .build();
    }
}