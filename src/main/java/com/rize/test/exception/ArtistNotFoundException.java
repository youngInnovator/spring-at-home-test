package com.rize.test.exception;

public class ArtistNotFoundException extends RuntimeException {

    public ArtistNotFoundException(Integer id) {
        super("Could not find artist with id: " + id);
    }
}
