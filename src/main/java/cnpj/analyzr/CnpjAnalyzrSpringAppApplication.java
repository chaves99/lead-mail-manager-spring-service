package cnpj.analyzr;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.unit.DataSize;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class CnpjAnalyzrSpringAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(CnpjAnalyzrSpringAppApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner() {
        return args -> {
            long totalMemory = DataSize.ofBytes(Runtime.getRuntime().totalMemory()).toGigabytes();
            long maxMemory = DataSize.ofBytes(Runtime.getRuntime().maxMemory()).toGigabytes();
            long freeMemory = DataSize.ofBytes(Runtime.getRuntime().freeMemory()).toGigabytes();
            log.info("runner - totalMemory:{}G maxMemory:{}G freeMemory:{}G", totalMemory, maxMemory, freeMemory);
        };
    }

}
