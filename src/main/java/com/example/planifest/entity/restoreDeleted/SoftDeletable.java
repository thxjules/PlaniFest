package com.example.planifest.entity.restoreDeleted;

public interface SoftDeletable {

    boolean isDeleted();
    void setDeleted(boolean deleted);
}
