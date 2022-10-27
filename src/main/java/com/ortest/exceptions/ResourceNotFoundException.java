package com.ortest.exceptions;

public class    ResourceNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    private Class clazz;
    private String fieldName;
    private Object fieldValue;

    public ResourceNotFoundException(Class clazz, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", clazz.getSimpleName(), fieldName, fieldValue));
        this.clazz = clazz;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getClassName() {
        return clazz.getSimpleName();
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}