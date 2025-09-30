package com.example.planifest.service;

import com.example.planifest.entity.AudiLog;
import com.example.planifest.repository.AuditLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AuditServiceTest {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Test
    void testLogAction() {
        AuditService auditService = new AuditService(auditLogRepository);

        auditService.logAction("CREATE", "Event", 100L);

        List<AudiLog> logs = auditLogRepository.findAll();

        assertThat(logs).hasSize(1);
        AudiLog log = logs.get(0);
        assertThat(log.getAction()).isEqualTo("CREATE");
        assertThat(log.getEntityName()).isEqualTo("Event");
        assertThat(log.getEntityId()).isEqualTo(100L);
        assertThat(log.getPerformedBy()).isEqualTo("SYSTEM"); 
    }
}
