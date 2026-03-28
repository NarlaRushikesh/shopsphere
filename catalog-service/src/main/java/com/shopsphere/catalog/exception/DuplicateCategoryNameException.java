package com.shopsphere.catalog.exception;

public class DuplicateCategoryNameException extends RuntimeException {
    public DuplicateCategoryNameException(String name) {
        super("A category with name '" + name + "' already exists");
    }
}