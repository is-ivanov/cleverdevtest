package com.iivanov.cleverdevtestnewsystem.entities;

public enum Status {

    ACTIVE (200),
    PENDING (210),
    INACTIVE (230);

    private final int id;

    Status(int id) {
        this.id = id;
    }


}
