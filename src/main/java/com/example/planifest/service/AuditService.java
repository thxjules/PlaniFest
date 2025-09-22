package com.example.planifest.service;

import com.example.planifest.entity.AudiLog;
import com.example.planifest.repository.AuditLogRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void logAction(String action, String entityName, Long entityId) {
        AudiLog log = new AudiLog();
        log.setAction(action);
        log.setEntityName(entityName);
        log.setEntityId(entityId);

        String user = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : "SYSTEM";
        log.setPerformedBy(user);

        auditLogRepository.save(log);
    }
}
