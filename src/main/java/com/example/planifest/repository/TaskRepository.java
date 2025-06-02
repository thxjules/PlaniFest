package com.example.planifest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.planifest.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
   
}
