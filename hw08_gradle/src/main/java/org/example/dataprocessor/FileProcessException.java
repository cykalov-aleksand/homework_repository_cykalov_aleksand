package org.example.dataprocessor;

public class FileProcessException extends RuntimeException {
    public FileProcessException(String msg,Exception ex) {
        super(msg,ex);
    }

    public FileProcessException(String msg) {
        super(msg);
    }
   }
