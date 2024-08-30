package ru.practicum.server.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "ru.practicum.*")
@EntityScan({"ru.practicum.dto", "ru.practicum.server.model"})
public class PersistenceConfig {
}