package com.example.planifest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.planifest.entity.restoreDeleted.SoftDeletable;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class TrashService {

    @Autowired
    private EntityManager em;

    public <T> void restore(Class<T> clazz, Long id) {
        T entity = em.createQuery(
                "SELECT e FROM " + clazz.getSimpleName() + " e WHERE e.id = :id", clazz)
                .getSingleResult();

        if (entity instanceof SoftDeletable soft) {
            soft.setDeleted(false);
            em.merge(soft);
        }
    }

    public <T> List<T> listDeleted(Class<T> clazz) {
        return em.createQuery(
                "SELECT e FROM " + clazz.getSimpleName() + " e WHERE e.deleted = true", clazz)
                .getResultList();
    }

}
