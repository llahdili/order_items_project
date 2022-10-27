package com.ortest.exceptions;

public class NotAccessibleActionException extends RuntimeException{
        private static final long serialVersionUID = 1L;
        private String erori;

        public NotAccessibleActionException(String erori) {
            this.erori = erori;
        }

        public String getSimpleMsg() {
            return this.erori;
        }
}

