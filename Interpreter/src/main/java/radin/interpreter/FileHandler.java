package radin.interpreter;


import radin.core.utility.Option;
import radin.core.utility.Reference;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static radin.core.utility.Option.*;

/**
 * Handles file descriptors for the interpreter
 */
public class FileHandler {

    private final static int MAX_FILES = 4096;
    
    private HashMap<Integer, Option<InputStream>> fdToInput;
    private HashMap<Integer, Option<OutputStream>> fdToOutput;
    private boolean[] usedFileDescriptors;
    private int openFiles;
    
    /**
     * Creates the file handler
     * Starts with 3 predefined file descriptors
     * FD 0 ->
     */
    public FileHandler() {
        fdToInput = new HashMap<>(MAX_FILES);
        fdToOutput = new HashMap<>(MAX_FILES);
        usedFileDescriptors = new boolean[MAX_FILES];
        
        // stdin
        addInputStream(0, Some(System.in));
        addOutputStream(0, None());
        usedFileDescriptors[0] = true;
    
        // stdout
        addInputStream(1, None());
        addOutputStream(1, Some(System.out));
        usedFileDescriptors[1] = true;
    
        //sterr
        addInputStream(2, None());
        addOutputStream(2, Some(System.err));
        usedFileDescriptors[2] = true;
        
        openFiles = 3;
    }
    
    private int findOpenFileDescriptor() throws IOException {
        if(openFiles == MAX_FILES) {
            throw new IOException("Too many files open");
        }
    
        for (int i = 0; i < usedFileDescriptors.length; i++) {
            if(!usedFileDescriptors[i]) return i;
        }
        
        return -1;
    }
    
    private Option<OutputStream> getOutputStream(int fd) {
        return fdToOutput.getOrDefault(fd, None());
    }
    
    private Option<InputStream> getInputStream(int fd) {
        return fdToInput.getOrDefault(fd, None());
    }
    
    private boolean addInputStream(int fd, Option<InputStream> inputStream) {
        if(fdToInput.containsKey(fd)) return false;
        fdToInput.put(fd, inputStream);
        return true;
    }
    
    private boolean addOutputStream(int fd, Option<OutputStream> outputStream) {
        if(fdToOutput.containsKey(fd)) return false;
        fdToOutput.put(fd, outputStream);
        return true;
    }
    
    public boolean closeFileDescriptor(int fd){
        Option<InputStream> input = fdToInput.getOrDefault(fd, None());
        boolean inputSuccess = input.onSome( i -> {
            try {
                i.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        },
                false);
    
        Option<OutputStream> output = fdToOutput.getOrDefault(fd, None());
        boolean outputSuccess = output.onSome( i -> {
            try {
                i.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        },
                false);
    
        return inputSuccess && outputSuccess;
    }
    
    public enum AccessOption {
        READ,
        WRITE
    }
    
    /**
     * Attempts to create a file, returning a file descriptor
     * @param path the path of the file
     * @param options input options
     * @return the file descriptor
     * @throws IOException
     */
    public int openFile(String path, AccessOption... options) throws IOException {
        File file = new File(path);
        if(!file.exists()) {
            if(!file.createNewFile()) throw new IOException(path + " can not exist");
        }
    
        List<AccessOption> optionsFixed = Arrays.asList(options);
        int fd = findOpenFileDescriptor(); // if no fd exists, IOEXCEPTION will throw
        
        if(optionsFixed.contains(AccessOption.READ)) {
            addInputStream(fd, Some(new FileInputStream(file)));
        } else {
            addInputStream(fd, None());
        }
        if(optionsFixed.contains(AccessOption.WRITE)) {
            addOutputStream(fd, Some(new FileOutputStream(file)));
        } else {
            addOutputStream(fd, None());
        }
        
        
        return fd;
    }
    
    public void flushFile(int fd) {
        Reference<OutputStream> stream = new Reference<>();
        if(getOutputStream(fd).match(stream)) {
            try {
                stream.getValue().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void closeFile(int fd)  {
        Reference<OutputStream> stream = new Reference<>();
        if(getOutputStream(fd).match(stream)) {
            try {
                stream.getValue().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public char readFile(int fd) throws IOException {
        Reference<InputStream> stream = new Reference<>();
        if(getInputStream(fd).match(stream)) {
            InputStream value = stream.getValue();
            return (char) value.read();
        } else {
            throw new IOException("File Descriptor not open");
        }
    }
    
    public boolean writeFile(int fd, char c) throws IOException {
        Reference<OutputStream> stream = new Reference<>();
        if(getOutputStream(fd).match(stream)) {
            stream.getValue().write(c);
            return true;
        } else {
            throw new IOException("File Descriptor not open");
        }
    }

    
}
