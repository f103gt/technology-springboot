package com.technology.user.exceptions;

public class FileAlreadyUploadedException extends RuntimeException {

    public FileAlreadyUploadedException(String message) {
        super(message);
    }
}
