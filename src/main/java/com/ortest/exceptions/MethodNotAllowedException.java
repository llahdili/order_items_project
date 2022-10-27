package com.ortest.exceptions;

public class MethodNotAllowedException extends Exception {
        public MethodNotAllowedException(String errorMessage) {
            super(errorMessage);
        }
    }