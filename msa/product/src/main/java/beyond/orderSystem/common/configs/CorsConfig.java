package beyond.orderSystem.common.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry){
        corsRegistry.addMapping("/**") // ex. localhost:8081/---/--- 모두 허용
                .allowedOrigins("http://localhost:8081") // 허용 url 명시
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true); // 보안처리 허용 여부
    }
}
