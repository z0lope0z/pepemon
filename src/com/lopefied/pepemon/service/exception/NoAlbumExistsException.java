package com.lopefied.pepemon.service.exception;

/**
 * 
 * @author lemano
 *
 */
public class NoAlbumExistsException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String facebookID;

    public NoAlbumExistsException(String facebookID) {
        this.facebookID = facebookID;
    }

    @Override
    public String toString() {
        return "No Album exists in the given facebookID : " + facebookID;
    }
}
