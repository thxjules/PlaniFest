package com.example.planifest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.planifest.entity.auditoria.AudiLog;

public interface AuditLogRepository extends JpaRepository<AudiLog, Long> {
}
