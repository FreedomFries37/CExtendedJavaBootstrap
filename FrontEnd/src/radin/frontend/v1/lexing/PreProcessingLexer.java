package radin.frontend.v1.lexing;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.errorhandling.CompilationError;
import radin.core.lexical.Token;
import radin.core.lexical.TokenType;

import radin.input.Tokenizer;
import radin.core.utility.ICompilationSettings;
import radin.core.utility.Reference;
import radin.core.utility.UniversalCompilerSettings;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PreProcessingLexer extends Tokenizer<Token> {
    
    private static class PreprocessorDirectiveError extends AbstractCompilationError {
        public PreprocessorDirectiveError(Token corr, String extraInfo) {
            super("Not a valid directive", corr, extraInfo);
        }
    }
    
    
    private class Define {
        private String identifier;
        public final boolean hasArgs;
        public final int numArgs;
        public final boolean isVararg;
        private List<String> args;
        private String replacementString;
        
        public Define(String identifier) {
            this(identifier, "");
        }
        
        public Define(String identifier, String replacementString) {
            this.identifier = identifier;
            this.replacementString = replacementString;
            numArgs = 0;
            isVararg = false;
            hasArgs = false;
        }
        
        
        public Define(String identifier, boolean isVararg, List<String> args, String replacementString) {
            this.identifier = identifier;
            numArgs =args.size();
            hasArgs = true;
            this.isVararg = isVararg;
            this.args = args;
            this.replacementString = replacementString;
        }
        
        public String invoke() {
            if(numArgs != 0) throw new IllegalArgumentException();
            return replacementString;
        }
        
        public String invoke(String[] args) {
            if(args.length < numArgs) throw new IllegalArgumentException();
            if(!isVararg && args.length > numArgs) throw new IllegalArgumentException();
            
            String output = replacementString;
            for (int i = 0; i < this.args.size(); i++) {
                String thisArg = this.args.get(i);
                String replace = args[i].trim().replaceAll("\\s+", " ");
                
                output = output.replaceAll("#" + thisArg + "(\\W|$)", "\"" + replace + "\"$2");
                output = output.replaceAll("([^\\w#])" + thisArg + "(\\W)", "$1" + replace + "$2");
                output = output.replaceAll("##" + thisArg + "\\W", replace + "$1");
                output = output.replaceAll("##" + thisArg + "$", replace);
                output = output.replaceAll("\\W" + thisArg + "##", "$1" + replace + "##");
                output = output.replaceAll("^" + thisArg + "##", replace + "##");
                //output = output.replaceAll("##" + thisArg + "##", replace);
            }
            if(isVararg) {
                List<String> extraArgs = new LinkedList<>();
                for(int i = 0; i < args.length - numArgs; i++) {
                    extraArgs.add(args[numArgs + i]);
                }
                String replace = String.join(", ", extraArgs);
                String thisArg = "__VA_ARGS__";
                output = output.replaceAll("(\\W)" + thisArg + "(\\W)", "$1" + replace + "$2");
                output = output.replaceAll("##" + thisArg + "\\W", replace + "$1");
                output = output.replaceAll("\\W" + thisArg + "##", "$1" + replace);
                output = output.replaceAll("##" + thisArg + "##", replace);
            }
            return output;
        }
        
    }
    
    private class FunctionDefine extends Define {
        
        private Function<String[], String> argsFunction;
        private Supplier<String> nonArgsFunction;
        
        public FunctionDefine(String identifier, Supplier<String> nonArgsFunction) {
            super(identifier);
            this.nonArgsFunction = nonArgsFunction;
        }
        
        public FunctionDefine(String identifier, boolean isVararg, List<String> args, Function<String[], String> argsFunction) {
            super(identifier, isVararg, args, null);
            this.argsFunction = argsFunction;
        }
        
        @Override
        public String invoke() {
            if(numArgs != 0) throw new IllegalArgumentException();
            return nonArgsFunction.get();
        }
        
        @Override
        public String invoke(String[] args) {
            if(args.length < numArgs) throw new IllegalArgumentException();
            if(!isVararg && args.length > numArgs) throw new IllegalArgumentException();
            
            return argsFunction.apply(args);
        }
    }
    
    private HashMap<String, Reference<Integer>> fileCurrentLineNumber;
    private String currentFile;
    private boolean inIfStatement;
    private boolean skipToIfFalse;
    private HashMap<String, Define> defines;
    private List<AbstractCompilationError> compilationErrors;
    private int finishedIndex = -1;
    
    public PreProcessingLexer(String filename, String inputString) {
        super(inputString, filename);
        defines = baseDefines();
        compilationErrors = new LinkedList<>();
        fileCurrentLineNumber = new HashMap<>();
        currentFile = filename;
    }
    
    public PreProcessingLexer() {
        super("", "");
        defines = baseDefines();
        compilationErrors = new LinkedList<>();
        fileCurrentLineNumber = new HashMap<>();
    }
    
    
    
    @Override
    public List<AbstractCompilationError> getErrors() {
        return compilationErrors;
    }
    
    /**
     * Consumes the current character, making the current character the next available character
     *
     * @return the current character before this method was ran.
     */
    @Override
    protected char consumeChar() {
        char c = super.consumeChar();
        if(c == '\n') {
            incrementLine();
        }
        return c;
    }
    
    private String findAndReplaceMacros(String str) {
        String output = str;
        output = output.replaceAll("defined\\s+(\\w+)\\W", "defined($1)");
        String prev;
        do {
            prev = output;
            List<String> strings = new ArrayList<>(defines.keySet());
            strings.remove("defined");
            strings.add(0, "defined");
            for (String macro : strings) {
                Define d = defines.get(macro);
                if (d.hasArgs) {
                    int startIndex = output.indexOf(d.identifier);
                    if(startIndex == -1) break;
                    int index = startIndex+ d.identifier.length();
                    String collected = d.identifier;
                    while (Character.isWhitespace(output.charAt(index))) {
                        ++index;
                    }
                    if(output.charAt(index) != '(') {
                        
                        break;
                    } else {
                        collected += output.charAt(index++);
                        int parens = 0;
                        List<String> collectedArguments = new LinkedList<>();
                        String currentArgument = "";
                        while (!(output.charAt(index) == ')' && parens == 0)) {
                            char c = output.charAt(index++);
                            collected += c;
                            
                            if (c == '(') {
                                currentArgument += c;
                                parens++;
                            } else if (c == ',' && parens == 0) {
                                collectedArguments.add(currentArgument);
                                currentArgument = "";
                            } else if (c != ')' || parens > 0) {
                                if (c == ')') {
                                    parens--;
                                }
                                currentArgument += c;
                            }
                            
                        }
                        collected += output.charAt(index);
                        collectedArguments.add(currentArgument);
                        collectedArguments.removeIf(String::isBlank);
                        
                        String invoke = d.invoke(collectedArguments.toArray(new String[0]));
                        output = output.replace(collected, invoke);
                    }
                } else {
                    output = output.replaceAll("(\\W|$)" + d.identifier + "(\\W|^)", "$1" + d.invoke() + "$2");
                }
            }
        }  while (!output.equals(prev));
        
        return output;
    }
    
    protected void incrementLine() {
        int line = fileCurrentLineNumber.containsKey(currentFile) ?
                fileCurrentLineNumber.get(currentFile).getValue() + 1 : 1;
        setLine(currentFile, line);
    }
    
    protected void decrementLine() {
        fileCurrentLineNumber.get(currentFile).setValue(fileCurrentLineNumber.get(currentFile).getValue() - 1);
    }
    
    protected int getLine() {
        return fileCurrentLineNumber.get(currentFile).getValue();
    }
    
    
    protected void setLine(int lineNumber) {
        setLine(currentFile, lineNumber);
    }
    
    protected void setLine(String filename) {
        setLine(filename, 1);
    }
    
    protected void setLine(String filename, int lineNumber) {
        currentFile = filename;
        if(!fileCurrentLineNumber.containsKey(filename)) {
            fileCurrentLineNumber.put(filename, new Reference<>(lineNumber));
        } else {
            fileCurrentLineNumber.get(currentFile).setValue(lineNumber);
        }
    }
    
    
    @Override
    public void setFilename(String filename) {
        super.setFilename(filename);
        currentFile = filename;
    }
    
    private void unconsume(String s) {
        char[] chars = s.toCharArray();
        for (int i = chars.length - 1; i >= 0; i--) {
            currentIndex--;
            //column--;
            if(!match(chars[i])) {
                currentIndex++;
                column++;
                return;
            } else if(chars[i] == '\n') {
                lineNumber--;
                decrementLine();
                //column = getColumn() + 1;
            }
            
        }
        column = getColumn();
    }
    
    
    
    private void insertString(String s) {
        inputString = getInputString().substring(0, currentIndex) + s + getInputString().substring(currentIndex);
    }
    
    private void removeString(int length) {
        inputString = getInputString().substring(0, currentIndex) + getInputString().substring(currentIndex + length);
    }
    
    private void removeChar() {
        removeString(1);
    }
    
    private void replaceString(String original, String replace) {
        unconsume(original);
        if(match(original)) {
            removeString(original.length());
            insertString(replace);
        }
    }
    
    private enum PreProcessorIfOutput {
        TRUE,
        FALSE,
        NONDETERMINED
    }
    
    private PreProcessorIfOutput checkIf(String statement) {
        String fixedStatement = findAndReplaceMacros(statement);
        ICompilationSettings.debugLog.finer("Fixed PP If Statement Condition = " + fixedStatement);
        return PreProcessorIfOutput.NONDETERMINED;
    }
    
    private void invokePreprocessorDirective(String directiveString, String originalString) {
        if(!directiveString.startsWith("#")) return;
        directiveString = directiveString.replaceAll("\\s+", " ");
        
        
        int endIndex = directiveString.indexOf(' ');
        String directive = directiveString;
        if(endIndex >= 0)
            directive = directiveString.substring(0, endIndex);
        
        if(skipToIfFalse) {
            if(!directive.equals("#else") && !directive.equals("#endif")) return;
        }
        
        String arguments = directiveString.substring(directiveString.indexOf(directive) + directive.length()).trim();
        ICompilationSettings.debugLog.finest("PP DIRECTIVE = " + directive);
        if(!arguments.isBlank())
            ICompilationSettings.debugLog.finest("PP ARGUMENTS = " + arguments);
        
        switch (directive) {
            case "#undef": {
                ICompilationSettings.debugLog.fine("PP Undefined: " + arguments);
                defines.remove(arguments);
                return;
            }
            case "#define": {
                
                Pattern function = Pattern.compile("(?<id>[_a-zA-Z]\\w*)(?<isfunc>\\(\\s*(?<args>(([_a-zA-Z]\\w*\\s*" +
                        "(," +
                        "\\s*[_a-zA-Z]\\w*\\s*)*)(,\\s*\\.\\.\\.\\s*)?)|(\\s*\\.\\.\\.\\s*)?)\\))?");
                
                Matcher matcher = function.matcher(arguments);
                if(matcher.find()) {
                    String rest = arguments.substring(matcher.end()).trim();
                    String identifier = matcher.group("id");
                    boolean isFunc = matcher.group("isfunc") != null;
                    Define define;
                    if(isFunc) {
                        String argsFull = matcher.group("args").trim();
                        String[] args = argsFull.split("\\s*,\\s*");
                        List<String> defArgs = new LinkedList<>();
                        boolean isVarArg = false;
                        for (String arg : args) {
                            if(arg.equals("...")) {
                                isVarArg = true;
                            } else {
                                defArgs.add(arg);
                            }
                        }
                        define = new Define(identifier, isVarArg, defArgs, rest);
                    } else {
                        if(rest.isEmpty() || rest.isBlank())
                            define = new Define(identifier);
                        else
                            define = new Define(identifier, rest);
                    }
                    ICompilationSettings.debugLog.fine("PP Defined: " + identifier);
                    defines.put(identifier, define);
                }
                return;
            }
            case "#ifndef": {
                ICompilationSettings.debugLog.finest("Checking if " + arguments + " is not defined...");
                inIfStatement = true;
                if(defines.containsKey(arguments)) {
                    ICompilationSettings.debugLog.finest("" + arguments + " is defined");
                    skipToIfFalse = true;
                    ICompilationSettings.debugLog.finer("Skipping until #else or #endif is found");
                } else {
                    ICompilationSettings.debugLog.finest("" + arguments + " is not defined");
                }
                return;
            }
            case "#ifdef": {
                ICompilationSettings.debugLog.finest("Checking if " + arguments + " is defined...");
                inIfStatement = true;
                if(!defines.containsKey(arguments)) {
                    ICompilationSettings.debugLog.finest("" + arguments + " is not defined");
                    skipToIfFalse = true;
                    ICompilationSettings.debugLog.finer("Skipping until #else or #endif is found");
                } else {
                    ICompilationSettings.debugLog.finest("" + arguments + " is defined");
                }
                return;
            }
            case "#elif": {
                if(!inIfStatement) return;
            }
            // FALLS THROUGH
            case "#if": {
                inIfStatement = true;
                switch (checkIf(arguments)) {
                    case TRUE:
                        break;
                    case FALSE:
                        skipToIfFalse = true;
                        break;
                    case NONDETERMINED:
                        break;
                }
                return;
            }
            case "#else": {
                
                if(inIfStatement) {
                    ICompilationSettings.debugLog.finer("#else found, compilation continuing = " + !skipToIfFalse);
                    skipToIfFalse = !skipToIfFalse;
                }
                return;
            }
            case "#endif": {
                // if(!inIfStatement) throw new IllegalArgumentException();
                if(inIfStatement) {
                    if(skipToIfFalse) ICompilationSettings.debugLog.finer("#endif found, compilation continuing at " + lineNumber);
                    skipToIfFalse = false;
                    inIfStatement = false;
                }
                return;
            }
            case "#include": {
                if(!((arguments.charAt(0) == arguments.charAt(arguments.length() - 1) && arguments.charAt(0) == '"') ||
                        (arguments.charAt(0) == '<' && arguments.charAt(arguments.length() - 1) == '>')))
                    throw new IllegalArgumentException();
                
                String filename = arguments.substring(1, arguments.length() - 1);
                
                boolean isLocal = arguments.charAt(0) == '"';
                File file;
                Token closestToken = new Token(TokenType.t_reserved, directiveString)
                        .addColumnAndLineNumber(1, lineNumber - 1);
                if(isLocal) {
                    ICompilationSettings.debugLog.finer("Include is local");
                    try {
                        File localDirectory = new File(this.filename).getCanonicalFile().getParentFile();
                        ICompilationSettings.debugLog.finer("Parent search directory is " + localDirectory);
                        if(localDirectory == null
                                || !localDirectory.isDirectory()) {
                            throw new CompilationError("File does not exist", closestToken);
                        }
                        Path path = Paths.get(localDirectory.getPath(), filename).toRealPath();
                        file = new File(path.toUri());
                    } catch (IOException e) {
                        ICompilationSettings.debugLog.severe("Local include searched failed");
                        throw new CompilationError("File does not exist", closestToken);
                    }
                } else {
                    ICompilationSettings.debugLog.finer("Include is non-local");
                    
                    File[] locations = UniversalCompilerSettings.getInstance().getSettings().includeDirectories();
                    
                    
                    file = null;
                    for (File dir : locations) {
                        
                        
                        Path path = Paths.get(dir.getPath(), filename);
                        file = new File(path.toUri());
                        if (file.exists()) {
                            break;
                        } else {
                            file = null;
                        }
                        
                        
                    }
                }
                
                
                
                
                if(file == null || !file.exists()) {
                    throw new CompilationError("File does not exist", closestToken);
                }
                StringBuilder text = new StringBuilder();
                try {
                    ICompilationSettings.debugLog.info("Including file " + file);
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                    
                    String line;
                    while((line = bufferedReader.readLine()) != null) {
                        
                        if(!line.endsWith("\\")) {
                            text.append(line);
                            text.append("\n");
                        } else {
                            text.append(line, 0, line.length() - 1);
                            text.append(' ');
                        }
                    }
                    
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
                
                
                int restoreLineNumber = getLine();
                String fullText = "#line " + 1 + " \""+ filename + "\"\n" + text.toString() +
                        "\n#line " + restoreLineNumber + " \""+ this.filename + "\"\n";
                
                fullText = fullText.replace("\t", " ".repeat(UniversalCompilerSettings.getInstance().getSettings().getTabSize()));
                //fullText = fullText.replaceAll("//.*\n", "\n");
                //fullText = Pattern.compile("/\\*.*\\*/", Pattern.DOTALL).matcher(fullText).replaceAll("");
                // fullText = fullText.replaceAll("/\\*.*\\*/", "");
                replaceString(originalString, fullText);
                return;
            }
            case "#line": {
                String[] split = arguments.split("\\s+");
                int lineNumber = Integer.parseInt(split[0]);
                if(split.length > 1) {
                    String filename = split[1].substring(1, split[1].length() - 1);
                    setLine(filename, lineNumber);
                } else {
                    setLine(lineNumber);
                }
                
                return;
            }
            
            default:
                return;
        }
    }
    
    public void define(String name) {
        defines.put(name, new Define(name));
    }
    
    public void define(String name, Object value) {
        Define define = new Define(name, value.toString());
        defines.put(name, define);
    }
    
    
    @Override
    protected Token singleLex() {
        
        
        
        while(true) {
            String image = "";
    
            /*
            while (skipToIfFalse || Character.isWhitespace(getChar()) || match("//") || match("/*") || match("#")) {
                
                
                
                while (Character.isWhitespace(getChar()) || match("//") || match("/*")) {
                    if(Character.isWhitespace(getChar())) {
                        consumeChar();
                    } else if(consume("//")) {
                        while (!consume("\n")) {
                            consumeChar();
                        }
                    } else if(consume("/*")) {
                        while (!consume("*//*")) {
                            consumeChar();
                        }
                    }
                }
                
                
                if (match('#')) {
                    String preprocessorDirective = "";
                    while (getChar() != '\n') {
                        preprocessorDirective += consumeChar();
                    }
                    
                    String original = preprocessorDirective + consumeChar();
                    preprocessorDirective = preprocessorDirective.replaceAll("\\s+", " ");
                    invokePreprocessorDirective(preprocessorDirective, original);
                    
                } else if(skipToIfFalse) {
                    removeChar();
                }
                
            }
            */
            
            do {
                if (match('#')) {
                    if(UniversalCompilerSettings.getInstance().getSettings().isDirectivesMustStartAtColumn1()) {
                        if(column != 1) {
                            Token token = new Token(TokenType.t_reserved, "#");
                            token.addColumnAndLineNumber(column, lineNumber);
                            throw new PreprocessorDirectiveError(token, "Preprocessor Directive must begin at start of " +
                                    "line, currently in column " + column);
                        }
                    } else {
                        if(!createdTokens.isEmpty()) {
                            // ICompilationSettings.debugLog.info("Previous line number is " + getPrevious()
                            // .getLineNumber());
                        }
                        if(!createdTokens.isEmpty() && getPrevious().getVirtualLineNumber() == lineNumber && getPrevious().getVirtualColumn() >= 1) {
                            
                            Token token = new Token(TokenType.t_reserved, "#");
                            token.addColumnAndLineNumber(column, lineNumber);
                            throw new PreprocessorDirectiveError(token, "Preprocessor Directive must begin line");
                        }
                    }
                    String preprocessorDirective = "";
                    while (getChar() != '\n') {
                        preprocessorDirective += consumeChar();
                    }
                    
                    String original = preprocessorDirective + consumeChar();
                    preprocessorDirective = preprocessorDirective.replaceAll("\\s+", " ");
                    invokePreprocessorDirective(preprocessorDirective, original);
                    
                } else if(skipToIfFalse) {
                    removeChar();
                } else if (Character.isWhitespace(getChar())) consumeChar();
                else if (consume("//")) {
                    while (!consume("\n") && !consume(System.lineSeparator())) consumeChar();
                } else if (consume("/*")) {
                    while (!consume("*/")) consumeChar();
                } else break;
            } while (true);
            
            if (match('\0')) {
                
                return new Token(TokenType.t_eof);
            }
            
            
            if (match('"')) {
                consumeChar();
                boolean inString = true;
                while (inString) {
                    if (match('\\')) {
                        String escape = consumeNextChars(2);
                        switch (escape.charAt(1)) {
                            case 't': {
                                image += '\t';
                                break;
                            }
                            case 'n': {
                                image += '\n';
                                break;
                            }
                            case 'r': {
                                image += '\r';
                                break;
                            }
                            case '\'': {
                                image += '\'';
                                break;
                            }
                            case '\\': {
                                image += '\\';
                                break;
                            }
                            case '"': {
                                image += '"';
                                break;
                            }
                            case '?': {
                                image += '?';
                                break;
                            }
                        }
                        //image += escape;
                    } else if (match('\n')) {
                        throw new TokenizationError("Incomplete String", getPrevious());
                    } else {
                        char nextChar = consumeChar();
                        if (nextChar == '"') {
                            inString = false;
                        } else {
                            image += nextChar;
                        }
                    }
                }
                return new Token(TokenType.t_string, "\"" + image + "\"");
            } else if (Character.isDigit(getChar()) || (match('.') && Character.isDigit(getNextChars(2).charAt(1)))) {
                if (match("0x") || match("0X")) {
                    image += consumeNextChars(2);
                    while ((getChar() >= 'a' && getChar() <= 'f') ||
                            (getChar() >= 'A' && getChar() <= 'F')) {
                        image += consumeChar();
                    }
                } else {
                    boolean decimalFound = false;
                    while (Character.isDigit(getChar()) || match('.')) {
                        char nextChar = consumeChar();
                        if (nextChar == '.') {
                            if (!decimalFound) {
                                decimalFound = true;
                            } else {
                                return null;
                            }
                        }
                        image += nextChar;
                    }
                }
                
                return new Token(TokenType.t_literal, image);
            } else if (Character.isLetter(getChar()) || match('_')) {
                image += consumeChar();
                while (Character.isLetter(getChar()) || match('_') || Character.isDigit(getChar())) {
                    image += consumeChar();
                }
                
                if (defines.containsKey(image)) {
                    Define define = defines.get(image);
                    
                    if (define.hasArgs) {
                        String collected = "";
                        while (Character.isWhitespace(getChar())) {
                            collected += consumeChar();
                        }
                        if(getChar() != '(') {
                            
                            unconsume(collected);
                        } else {
                            collected += consumeChar();
                            int parens = 0;
                            List<String> collectedArguments = new LinkedList<>();
                            String currentArgument = "";
                            while (!(match(')') && parens == 0)) {
                                char c = consumeChar();
                                collected += c;
                                
                                if(c == '(') {
                                    currentArgument += c;
                                    parens++;
                                } else if(c == ',' && parens == 0) {
                                    collectedArguments.add(currentArgument);
                                    currentArgument = "";
                                } else if (c != ')' || parens > 0){
                                    if(c == ')') {
                                        parens--;
                                    }
                                    currentArgument += c;
                                }
                                
                            }
                            collected += consumeChar();
                            collectedArguments.add(currentArgument);
                            collectedArguments.removeIf(String::isBlank);
                            
                            String invoke = define.invoke(collectedArguments.toArray(new String[collectedArguments.size()]));
                            String totalToReplace = image + collected;
                            replaceString(totalToReplace, invoke);
                            continue;
                        }
                        
                    } else {
                        replaceString(image, define.invoke());
                        continue;
                    }
                    
                }
                
                
                return getKeywordToken(image);
                
            }
            
            switch (consumeChar()) {
                case '<': {
                    switch (getChar()) {
                        case '<': {
                            consumeChar();
                            if (match('=')) {
                                consumeChar();
                                return new Token(TokenType.t_operator_assign, "<<=");
                            }
                            return new Token(TokenType.t_lshift);
                        }
                        case '=': {
                            consumeChar();
                            return new Token(TokenType.t_lte);
                        }
                        default:
                            return new Token(TokenType.t_lt);
                    }
                }
                case '>': {
                    switch (getChar()) {
                        case '>': {
                            consumeChar();
                            if (match('=')) {
                                consumeChar();
                                return new Token(TokenType.t_operator_assign, ">>=");
                            }
                            return new Token(TokenType.t_rshift);
                        }
                        case '=': {
                            consumeChar();
                            return new Token(TokenType.t_gte);
                        }
                        default:
                            return new Token(TokenType.t_gt);
                    }
                }
                case '+': {
                    if (match('=')) {
                        consumeChar();
                        return new Token(TokenType.t_operator_assign, "+=");
                    } else if (consume("+")) {
                        return new Token(TokenType.t_inc);
                    }
                    return new Token(TokenType.t_add);
                }
                case '-': {
                    if (match('=')) {
                        consumeChar();
                        return new Token(TokenType.t_operator_assign, "-=");
                    } else if (consume("-")) {
                        return new Token(TokenType.t_dec);
                    } else if (consume(">")) {
                        return new Token(TokenType.t_arrow);
                    }
                    return new Token(TokenType.t_minus);
                }
                case '&': {
                    if (getChar() != '&') {
                        return new Token(TokenType.t_and);
                    } else if (consume('&')) {
                        if (consume('=')) {
                            return new Token(TokenType.t_operator_assign, "&&=");
                        }
                        return new Token(TokenType.t_dand);
                    }
                }
                case '|': {
                    if (getChar() != '|') {
                        return new Token(TokenType.t_bar);
                    } else if (consume('|')) {
                        if (consume('=')) {
                            return new Token(TokenType.t_operator_assign, "||=");
                        }
                        return new Token(TokenType.t_dor);
                    }
                }
                case '=': {
                    if (consume('=')) {
                        return new Token(TokenType.t_eq);
                    }
                    return new Token(TokenType.t_assign);
                }
                case '!': {
                    if (consume('=')) {
                        return new Token(TokenType.t_neq);
                    }
                    return new Token(TokenType.t_bang);
                }
                case ';': {
                    return new Token(TokenType.t_semic);
                }
                case '{': {
                    return new Token(TokenType.t_lcurl);
                }
                case '}': {
                    return new Token(TokenType.t_rcurl);
                }
                case ',': {
                    return new Token(TokenType.t_comma);
                }
                case ':': {
                    if(consume(':')) {
                        return new Token(TokenType.t_namespace);
                    }
                    return new Token(TokenType.t_colon);
                }
                case '(': {
                    return new Token(TokenType.t_lpar);
                }
                case ')': {
                    return new Token(TokenType.t_rpar);
                }
                case '[':
                    return new Token(TokenType.t_lbrac);
                case ']':
                    return new Token(TokenType.t_rbrac);
                case '.': {
                    if (consume("..")) {
                        return new Token(TokenType.t_ellipsis);
                    }
                    return new Token(TokenType.t_dot);
                }
                case '~': {
                    if (consume('=')) {
                        return new Token(TokenType.t_operator_assign, "~=");
                    }
                    return new Token(TokenType.t_not);
                }
                case '*': {
                    if (consume('=')) {
                        return new Token(TokenType.t_operator_assign, "*=");
                    }
                    return new Token(TokenType.t_star);
                }
                case '/':
                    return new Token(TokenType.t_fwslash);
                case '%': {
                    if (consume('=')) {
                        return new Token(TokenType.t_operator_assign, "%=");
                    }
                    return new Token(TokenType.t_percent);
                }
                case '^': {
                    if (consume('=')) {
                        return new Token(TokenType.t_operator_assign, "^=");
                    }
                    return new Token(TokenType.t_crt);
                }
                case '?':
                    return new Token(TokenType.t_qmark);
                case '\'': {
                    String str = getNextChars(2);
                    if (str.length() != 2 || str.charAt(1) != '\'') {
                        if (str.charAt(0) == '\\') {
                            str = getNextChars(3);
                            if (str.length() != 3 || str.charAt(2) != '\'') return null;
                            consumeNextChars(3);
                            return new Token(TokenType.t_literal, "'" + str);
                        }
                        
                        return null;
                    }
                    consumeNextChars(2);
                    return new Token(TokenType.t_literal, "'" + str);
                } case '$': {
                    return new Token(TokenType.t_dollar);
                }
                case '@': {
                    return new Token(TokenType.t_at);
                }
                default:
                    return null;
            }
        }
    }
    
    public Token getNext() {
        if (++tokenIndex >= createdTokens.size()) {
            Token tok;
            try {
                tok = singleLex();
                
            } catch (AbstractCompilationError e) {
                getErrors().add(e);
                //finishedIndex = getTokenIndex();
                tok = null;
            }
            if (tok == null) return null;
            tok.setPrevious(getPrevious());
            String representation = tok.getRepresentation();
            tok.addColumnAndLineNumber(column - representation.length(), lineNumber);
            tok.setFilename(currentFile);
            tok.setActualLineNumber(fileCurrentLineNumber.get(currentFile).getValue());
            //prevLineNumber = lineNumber;
            //prevColumn = column;
            createdTokens.add(tok);
            return tok;
        }
        return createdTokens.get(getTokenIndex());
    }
    
    @Override
    public Iterator<Token> iterator() {
        return this;
    }
    
    @Override
    public boolean hasNext() {
        if(finishedIndex >= 0 && getTokenIndex() >= finishedIndex) {
            return false;
        }
        if(getTokenIndex() < createdTokens.size() - 1) return true;
        
        return currentIndex < getInputString().length();
    }
    
    @Override
    public Token next() {
        return getNext();
    }
    
    @Override
    public void reset() {
        super.reset();
        fileCurrentLineNumber = new HashMap<>();
        defines = baseDefines();
    }
    
    private HashMap<String, Define> baseDefines() {
        HashMap<String, Define> output = new HashMap<>();
        
        output.put("__LINE__", new FunctionDefine("__LINE__", () -> "" + lineNumber ));
        output.put("__FILE__", new FunctionDefine("__FILE__", () -> "\"" + currentFile + '"' ));
        output.put("defined", new FunctionDefine("defined", false, Collections.singletonList("X"),
                (String[] args) -> defines.containsKey(args[0]) ? "1" : "0" ));
        
        return output;
    }
}
