package radin.core;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.utility.ICompilationSettings;
import radin.core.utility.UniversalCompilerSettings;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ErrorReader {
    
   
    
    private static class LineHolder {
        public final String fileName;
        public final int lineNumber;
        public final String contents;
        public final List<String> parentFiles;
        public LineHolder(String fileName, int lineNumber, String contents, List<String> parentFiles) {
            this.fileName = fileName;
            this.lineNumber = lineNumber;
            this.contents = contents;
            this.parentFiles = parentFiles;
        }

        public LineHolder(String fileName, int lineNumber, String contents) {
            this.fileName = fileName;
            this.lineNumber = lineNumber;
            this.contents = contents;
            this.parentFiles = new LinkedList<>();
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
        List<String> parentFiles = new LinkedList<>();
        parentFiles.add(filename);
        for (String line : split) {
            Matcher m = lineFormat.matcher(line);
            if(m.find()) {
                lineNumber = Integer.parseInt(m.group("ln"));
                String file = m.group("file");
                if(file != null) {

                    if (parentFiles.size() >= 2
                            && !file.equals(parentFiles.get(parentFiles.size() - 1))
                            && !file.equals(filename)) {
                        String up2 = parentFiles.get(parentFiles.size() - 2);
                        if(up2.equals(file)) {
                            parentFiles.remove(parentFiles.size() - 1);
                        } else {
                            parentFiles.add(file);
                        }
                    } else if (!file.equals(parentFiles.get(parentFiles.size() - 1)) && !file.equals(filename)){
                        parentFiles.add(file);
                    }
                    /*
                    if (parentFiles.size() >= 2 && !file.equals(parentFiles.peek())) {
                        parentFiles.push(reportingFileName);
                    } else if (!parentFiles.empty() && file.equals(parentFiles.peek())) {
                        parentFiles.pop();
                    }

                     */
                    reportingFileName = file;
                }
                lines.add(null);
            } else {

                ArrayList<String> adjParentFiles = new ArrayList<>(parentFiles);
                adjParentFiles.remove(adjParentFiles.size() - 1);
                LineHolder holder = new LineHolder(reportingFileName, lineNumber, line, adjParentFiles);
                parentFiles = new LinkedList<>();
                parentFiles.add(filename);
                lines.add(holder);
                lineNumber++;
            }
        }
    }
    
    public void readErrors() {
        String outputedFileName = null;
        for (AbstractCompilationError error : errors) {
            // System.out.println(error.getError());
            if(error == null) continue;
            ICompilationSettings.debugLog.severe(error.getClass().getSimpleName() + ": " + error.getMessage());
    
            List<AbstractCompilationError.ErrorInformation> infoMessages = new LinkedList<>();
            LineHolder lineHolderCurrent = null;
            int maxSize = 0;
            boolean firstLine = true;
            String repeat = "";
            boolean errorPrinted = false;
    
            for (AbstractCompilationError.ErrorInformation errorInformation : error.getInfo(true)) {
                
                if(errorInformation.getToken() == null) {
                    System.out.println(errorInformation.getInfo());
                    continue;
                }
                int i = errorInformation.getToken().getVirtualLineNumber() - 1;
                if( i < 0) i = 0;
                if (i >= lines.size()) break;
                LineHolder lineInfo = lines.get(i);
                
                if(lineHolderCurrent != null && lineInfo != lineHolderCurrent) {

                    if (!lineInfo.fileName.equals(outputedFileName)) {

                        System.out.printf("In %s:\n", lineHolderCurrent.fileName);
                        ICompilationSettings.debugLog.warning(String.format("In %s:", lineHolderCurrent.fileName));

                        outputedFileName = lineInfo.fileName;
                    }
                    
                    if(!errorPrinted) {
                        if (!UniversalCompilerSettings.getInstance().getSettings().isShowErrorStackTrace())
                            System.out.println(error.toString());
                        else
                            error.printStackTrace(System.out);
                        errorPrinted = true;
                    }
                    
                    
                    if(firstLine) {
                        
                        firstLine = false;
                    } else {
                        System.out.println(repeat + "|" );
                        ICompilationSettings.debugLog.warning(repeat + "|" );
                    }
                    repeat = printError(lineHolderCurrent, infoMessages);
                    infoMessages.clear();
                    lineHolderCurrent = lineInfo;
                } else if(lineHolderCurrent == null) {
                    lineHolderCurrent = lineInfo;
                }
                
                infoMessages.add(errorInformation);
            }
    
            if(lineHolderCurrent != null) {
                if (!lineHolderCurrent.fileName.equals(outputedFileName)) {
                    for (int k = lineHolderCurrent.parentFiles.size() - 1; k >= 0; k--) {
                        System.out.printf("While compiling %s:\n", lineHolderCurrent.parentFiles.get(k));
                    }
                    System.out.printf("In %s:\n", lineHolderCurrent.fileName);
                    ICompilationSettings.debugLog.severe(String.format("In %s:", lineHolderCurrent.fileName));
                    outputedFileName = lineHolderCurrent.fileName;
                }
                if(!errorPrinted) {
                    if (!UniversalCompilerSettings.getInstance().getSettings().isShowErrorStackTrace()) {
                        System.out.println(error.toString());
                        // ICompilationSettings.debugLog.finer(error.toString());
                    }
                    else {
                        error.printStackTrace(System.out);
                        error.printStackTrace(ICompilationSettings.debugLog.divertOutput(Level.FINER, System.out));
                    }
                }
                if(!firstLine) {
                    System.out.println(repeat + "|" );
                    ICompilationSettings.debugLog.warning(repeat + "|" );
                }
                printError(lineHolderCurrent, infoMessages);
            }
            if(UniversalCompilerSettings.getInstance().getSettings().isErrorMessageIncludeJavaBacktrace()) {
                System.out.println("Java Backtrace:");
                //error.printStackTrace();

                StackTraceElement[] stackTrace = error.getStackTrace();
                System.out.println(stackTrace[0]);
                for (int i = 1; i < stackTrace.length; i++) {
                    System.out.printf("\tat %s\n", stackTrace[i]);
                }



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
    
    private String printError(LineHolder lineHolder, List<AbstractCompilationError.ErrorInformation> informations) {
        int lineNumberDigits = (int) Math.log10(lineHolder.lineNumber) + 1;
        
        String repeat = " ".repeat(lineNumberDigits + 1);
        System.out.println(repeat + "|" );
        ICompilationSettings.debugLog.warning(repeat + "|" );
        
        System.out.println(lineHolder.lineNumber + " | " + lineHolder.contents);
        ICompilationSettings.debugLog.warning(lineHolder.lineNumber + " | " + lineHolder.contents);
        
        List<Integer> columns = new LinkedList<>();
        for (AbstractCompilationError.ErrorInformation information : informations) {
            int imageLength = information.getToken().getRepresentation().length();
            columns.add(information.getToken().getVirtualColumn() + imageLength/2);
        }
        columns.sort(Integer::compareTo);
        int maxSize = lineHolder.contents.length();
        boolean first = true;
        for (int i = informations.size() - 1; i >= 0; i--) {
            if(!first) {
                var log = repeat + "|" + errorAt(-1, maxSize, null, columns, first);
                System.out.println(log);
                ICompilationSettings.debugLog.warning(log);
            }
            var log = repeat + "|" + errorAt(columns.get(i), maxSize, informations.get(i).getInfo(), columns
                    , first);
            ICompilationSettings.debugLog.warning(log);
            System.out.println(log);
            columns.remove(i);
            if(first) first = false;
            
        }
        
        return repeat;
    }
    
    private String errorAt(int column, int maxSize, String info, List<Integer> otherColumns, boolean topLine) {
        StringBuilder output = new StringBuilder();
        int addition = 8;
        if(info == null) addition = 0;
        for (int i = 0; i < maxSize + addition; i++) {
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
                if(info != null)
                    output.append("_");
                else
                    output.append('~');
            }
        }
        if(info != null) {
            output.append(' ');
            output.append(info);
        }
        return output.toString();
    }
    
    
}
