package com.example.planifest.service.dao;
import java.util.List;
public interface Idao <T, ID> {

    List<T> getAll();

    void create(T entity);

    void update(T entity);

    void deleteById(ID id);

}