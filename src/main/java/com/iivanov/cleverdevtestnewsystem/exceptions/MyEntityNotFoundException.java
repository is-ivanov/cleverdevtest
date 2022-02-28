package com.iivanov.cleverdevtestnewsystem.exceptions;

public class MyEntityNotFoundException extends RuntimeException {
    public MyEntityNotFoundException(String entityName, String fieldName,
                                     Object fieldValue) {
        super(String.format("%s with %s(%s) not found",
            entityName, fieldName, fieldValue));
    }

    public MyEntityNotFoundException(String entityName, String fieldName,
                                     Object fieldValue, Exception ex) {
        super(String.format("%s with %s(%s) not found",
            entityName, fieldName, fieldValue), ex);
    }
}
