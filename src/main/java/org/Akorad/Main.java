package org.Akorad;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        try {
            // Определяем какой .env файл загружать
            String profile = System.getenv("SPRING_PROFILES_ACTIVE");
            String envFile = "docker".equals(profile) ? ".env.docker" : ".env";

            Dotenv dotenv = Dotenv.configure()
                    .filename(envFile)
                    .ignoreIfMissing() // Не падать если файл отсутствует
                    .load();

            dotenv.entries().forEach(entry ->
                    System.setProperty(entry.getKey(), entry.getValue())
            );

            System.out.println("Loaded environment from: " + envFile);
        } catch (Exception e) {
            System.out.println("Warning: Could not load .env file: " + e.getMessage());
        }

        SpringApplication.run(Main.class, args);
    }
}