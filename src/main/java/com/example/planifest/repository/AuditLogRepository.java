package com.example.planifest.repository;

import com.example.planifest.entity.AudiLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AudiLog, Long> {
}
