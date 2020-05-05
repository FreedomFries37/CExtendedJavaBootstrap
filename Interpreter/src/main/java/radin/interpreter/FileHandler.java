package radin.interpreter;


import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * Handles file descriptors for the interpreter
 */
public class FileHandler {

    private final static int MAX_FILES = 4096;
    private HashMap<Integer, OutputStream> fdToFile;
    
    /**
     * Creates the file handler
     * Starts with 3 predefined "files", which are actually output streams
     */
    public FileHandler() {
    
    }

}
