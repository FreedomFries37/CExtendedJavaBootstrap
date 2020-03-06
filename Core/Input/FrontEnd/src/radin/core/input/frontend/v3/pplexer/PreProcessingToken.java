package radin.core.input.frontend.v3.pplexer;

public class PreProcessingToken {
    public enum Type {
        keyword,
        identifier,
        cosntant,
        operator,
        punctuator
    }
    
    private Type type;
    private String backingImage;
    private String file;
    private int linenumber;
    private int column;
    
    public PreProcessingToken(Type type, String backingImage, String file, int linenumber, int column) {
        this.type = type;
        this.backingImage = backingImage;
        this.file = file;
        this.linenumber = linenumber;
        this.column = column;
    }
    
    public Type getType() {
        return type;
    }
    
    public String getBackingImage() {
        return backingImage;
    }
    
    public String getFile() {
        return file;
    }
    
    public int getLinenumber() {
        return linenumber;
    }
    
    public int getColumn() {
        return column;
    }
}
