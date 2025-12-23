package com.kshrd.assessment.config;

import com.kshrd.assessment.utils.SecurityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "utcDateTimeProvider")
public class JpaAuditingConfig {

    private static final ZoneId UTC_ZONE = ZoneId.of("UTC");

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

    @Bean(name = "utcDateTimeProvider")
    public DateTimeProvider utcDateTimeProvider() {
        return () -> Optional.of(LocalDateTime.now(UTC_ZONE));
    }
}

