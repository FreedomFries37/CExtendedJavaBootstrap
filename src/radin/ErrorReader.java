package radin;

import radin.interphase.AbstractCompilationError;

import java.util.LinkedList;
import java.util.List;
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
    private List<AbstractCompilationError> errors;
    private List<LineHolder> lines;
    
    public ErrorReader(String filename, String inputString, List<AbstractCompilationError> errors) {
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
        for (AbstractCompilationError error : errors) {
            // System.out.println(error.getError());
            if(!Main.getSettings().isShowErrorStackTrace())
                System.out.println(error.toString());
            else
                error.printStackTrace(System.out);
            
            List<AbstractCompilationError.ErrorInformation> infoMessages = new LinkedList<>();
            LineHolder lineHolderCurrent = null;
            int maxSize = 0;
            boolean printedFile = false;
    
            for (AbstractCompilationError.ErrorInformation errorInformation : error.getInfo(true)) {
                LineHolder lineInfo = lines.get(errorInformation.getToken().getLineNumber() - 1);
                
                if(lineHolderCurrent != null && lineInfo != lineHolderCurrent) {
                    System.out.printf("In %s:\n", lineHolderCurrent.fileName);
                    printedFile =true;
                    printError(lineHolderCurrent, infoMessages);
                    infoMessages.clear();
                } else if(lineHolderCurrent == null) {
                    lineHolderCurrent = lineInfo;
                }
                
                infoMessages.add(errorInformation);
            }
    
            if(lineHolderCurrent != null) {
                if (!printedFile)
                    System.out.printf("In %s:\n", lineHolderCurrent.fileName);
                printError(lineHolderCurrent, infoMessages);
            }
            /*
            for (AbstractCompilationError.ErrorInformation errorInformation : error.getInfo(true)) {
                LineHolder lineInfo = lines.get(errorInformation.getToken().getLineNumber() - 1);
                int column = errorInformation.getToken().getColumn();
                int lineNumberDigits = (int) Math.log10(lineInfo.lineNumber) + 1;
                System.out.printf("In %s:\n", lineInfo.fileName);
                String repeat = " ".repeat(lineNumberDigits + 1);
                System.out.println(repeat + "|" );
                System.out.println(lineInfo.lineNumber + " | " + lineInfo.contents);
                maxSize = lineInfo.contents.length();
                System.out.println(repeat + "|" + errorAt(column, errorInformation.getToken().getImage().length(), maxSize,
                        errorInformation.getInfo()));
            }
            */
        }
    }
    
    private void printError(LineHolder lineHolder, List<AbstractCompilationError.ErrorInformation> informations) {
        int lineNumberDigits = (int) Math.log10(lineHolder.lineNumber) + 1;
        
        String repeat = " ".repeat(lineNumberDigits + 1);
        System.out.println(repeat + "|" );
        System.out.println(lineHolder.lineNumber + " | " + lineHolder.contents);
        List<Integer> columns = new LinkedList<>();
        for (AbstractCompilationError.ErrorInformation information : informations) {
            int imageLength = information.getToken().getImage().length();
            columns.add(information.getToken().getColumn() + imageLength/2);
        }
        columns.sort(Integer::compareTo);
        int maxSize = lineHolder.contents.length();
        boolean first = true;
        for (int i = informations.size() - 1; i >= 0; i--) {
            if(!first) System.out.println(repeat + "|" + errorAt(-1, maxSize, null, columns, first));
            System.out.println(repeat + "|" + errorAt(columns.get(i), maxSize, informations.get(i).getInfo(), columns, first));
            columns.remove(i);
            if(first) first = false;
            
        }
    }
    
    private String errorAt(int column, int maxSize, String info, List<Integer> otherColumns, boolean topLine) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < maxSize + 8; i++) {
            if(i < column || column == -1) {
                if(otherColumns.contains(i)) {
                    if(topLine) {
                        output.append('^');
                    } else {
                        output.append('|');
                    }
                }
                else
                    output.append(" ");
            } else if(i == column) {
                if(topLine) {
                    output.append('^');
                } else {
                    output.append('|');
                }
            } else {
                output.append("_");
            }
        }
        if(info != null) {
            output.append(' ');
            output.append(info);
        }
        return output.toString();
    }
    
    
}
