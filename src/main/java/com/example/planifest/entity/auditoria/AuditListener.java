package com.example.planifest.entity.auditoria;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.planifest.repository.AuditLogRepository;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;

@Component
public class AuditListener {

    private static AuditLogRepository staticRepo;

    @Autowired
    public void init(AuditLogRepository repo) {
        staticRepo = repo;
    }

    @PrePersist
    public void prePersist(Object entity) {
        saveAudit("CREATE", entity);
    }

    @PreUpdate
    public void preUpdate(Object entity) {
        saveAudit("UPDATE", entity);
    }

    @PreRemove
    public void preRemove(Object entity) {
        saveAudit("DELETE", entity);
    }

    private void saveAudit(String action, Object entity) {
        try {
            AudiLog log = new AudiLog();
            log.setAction(action);
            log.setEntityName(entity.getClass().getSimpleName());

            try {
                var idField = entity.getClass().getDeclaredField("id");
                idField.setAccessible(true);
                Object idValue = idField.get(entity);
                if (idValue != null) {
                    log.setEntityId(Long.valueOf(idValue.toString()));
                }
            } catch (Exception ignored) {}

            log.setPerformedBy("SYSTEM"); 
            staticRepo.save(log);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

