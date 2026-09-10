package cnpj.analyzr.config;

import org.springframework.boot.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import jakarta.servlet.MultipartConfigElement;

@Configuration
public class FileConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofTerabytes(1));
        factory.setMaxRequestSize(DataSize.ofTerabytes(1));
        factory.setFileSizeThreshold(DataSize.ofTerabytes(1));
        factory.setLocation("/tmp");
        return factory.createMultipartConfig();
    }

}
