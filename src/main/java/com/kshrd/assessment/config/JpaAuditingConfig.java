package com.kshrd.assessment.config;

import com.kshrd.assessment.utils.SecurityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<UUID> auditorProvider() {
        return new AuditorAware<UUID>() {
            @Override
            public Optional<UUID> getCurrentAuditor() {
                UUID userId = SecurityUtils.getCurrentUserId();
                return Optional.ofNullable(userId);
            }
        };
    }
}

