package com.kshrd.assessment.exception;

public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String resource, String identifier) {
        super(resource + " not found with identifier: " + identifier);
    }
    
    public ResourceNotFoundException(String resource, java.util.UUID identifier) {
        super(resource + " not found with identifier: " + identifier);
    }
}

