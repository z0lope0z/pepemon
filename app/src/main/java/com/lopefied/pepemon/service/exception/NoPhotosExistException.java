package com.lopefied.pepemon.service.exception;

/**
 * 
 * @author lemano
 * 
 */
public class NoPhotosExistException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String albumName;

    public NoPhotosExistException(String albumName) {
        this.albumName = albumName;
    }

    @Override
    public String toString() {
        return "No photo exists in the given album : " + albumName;
    }
}
