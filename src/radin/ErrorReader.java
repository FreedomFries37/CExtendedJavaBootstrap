package radin;

import radin.interphase.CompilationError;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ErrorReader {
    
   
    
    private static class LineHolder {
        public final String fileName;
        public final int lineNumber;
        public final String contents;
    
        public LineHolder(String fileName, int lineNumber, String contents) {
            this.fileName = fileName;
            this.lineNumber = lineNumber;
            this.contents = contents;
        }
    
        @Override
        public String toString() {
            return "LineHolder{" +
                    "fileName='" + fileName + '\'' +
                    ", lineNumber=" + lineNumber +
                    ", contents='" + contents + '\'' +
                    '}';
        }
    }
    private List<CompilationError<?>> errors;
    private List<LineHolder> lines;
    
    public ErrorReader(String filename, String inputString, List<CompilationError<?>> errors) {
        this.errors = errors;
        this.lines = new LinkedList<>();
        
        int lineNumber = 1;
        String reportingFileName = filename;
        String[] split = inputString.split("\n");
        Pattern lineFormat = Pattern.compile("^#line\\s+(?<ln>\\d+)\\s*(\\s\"(?<file>[^\"]*)\"\\s*)?$");
        for (String line : split) {
            Matcher m = lineFormat.matcher(line);
            if(m.find()) {
                lineNumber = Integer.parseInt(m.group("ln"));
                String file = m.group("file");
                if(file != null) {
                    reportingFileName = file;
                }
                lines.add(null);
            } else {
                LineHolder holder = new LineHolder(reportingFileName, lineNumber, line);
                lines.add(holder);
                lineNumber++;
            }
        }
    }
    
    public void readErrors() {
        for (CompilationError<?> error : errors) {
            // System.out.println(error.getError());
            if(!Main.getSettings().isShowErrorStackTrace())
                System.out.println(error.getError());
            else
                error.getError().printStackTrace(System.out);
            
            if(error.getClosestToken() != null) {
                LineHolder lineInfo = lines.get(error.getClosestToken().getLineNumber() - 1);
                int column = error.getClosestToken().getColumn();
                int lineNumberDigits = (int) Math.log10(lineInfo.lineNumber) + 1;
                System.out.printf("In %s:\n", lineInfo.fileName);
                String repeat = " ".repeat(lineNumberDigits + 1);
                System.out.println(repeat + "|" );
                System.out.println(lineInfo.lineNumber + " | " + lineInfo.contents);
                int maxSize = lineInfo.contents.length();
                System.out.println(repeat + "|" + errorAt(column, maxSize));
                
            }
        }
    }
    
    private String errorAt(int column, int maxSize) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < maxSize + 1; i++) {
            if(i < column) {
                output.append(" ");
            } else if(i == column) {
                output.append("^");
            } else {
                output.append("~");
            }
        }
        return output.toString();
    }
    
}
