package radin.lexing;

import radin.utility.ICompilationSettings;
import radin.interphase.lexical.Token;
import radin.interphase.lexical.TokenType;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer implements Iterable<Token>, Iterator<Token> {
    
    
    
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
                
                output = output.replaceAll("(\\W)" + thisArg + "(\\W)", "$1" + replace + "$2");
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
    
    private String inputString;
    private int maxIndex;
    private int currentIndex;
    private int prevColumn;
    private int prevLineNumber;
    private int column;
    private int lineNumber;
    private List<Token> createdTokens;
    private int tokenIndex;
    private boolean inIfStatement;
    private boolean skipToIfFalse;
    private String filename;
    private HashMap<String, Define> defines;
    private static ICompilationSettings compilationSettings;
    
    public static ICompilationSettings getCompilationSettings() {
        return compilationSettings;
    }
    
    public static void setCompilationSettings(ICompilationSettings compilationSettings) {
        Lexer.compilationSettings = compilationSettings;
    }
    
    public int getTokenIndex() {
        return tokenIndex;
    }
    
    public void setTokenIndex(int tokenIndex) {
        if(tokenIndex < -1 || tokenIndex >= createdTokens.size()) return;
        this.tokenIndex = tokenIndex;
    }
    
    public Lexer(String filename, String inputString) {
        this.inputString = inputString;
        this.filename = filename;
        createdTokens = new LinkedList<>();
        tokenIndex = -1;
        column = 1;
        lineNumber = 1;
        prevColumn = 1;
        prevLineNumber = 1;
        maxIndex = this.inputString.length();
        defines = new HashMap<>();
    }
    
    private char getChar() {
        if(currentIndex == inputString.length()) return '\0';
        return inputString.charAt(currentIndex);
    }
    
    private char consumeChar() {
        if(getChar() == '\n') {
            ++lineNumber;
            column = 1;
        } else if(getChar() == '\t' ) {
            column += getCompilationSettings().getTabSize();
        } else {
            column++;
        }
        return inputString.charAt(currentIndex++);
    }
    
    private String getNextChars(int count) {
        int min = Math.min(inputString.length(), currentIndex + count);
        return inputString.substring(currentIndex, min);
    }
    
    private String consumeNextChars(int count) {
        int min = Math.min(inputString.length(), currentIndex + count);
        String substring = inputString.substring(currentIndex, min);
        for (int i = currentIndex; i < min; i++) {
            consumeChar();
        }
        return substring;
    }
    
    private boolean consume(String str) {
        if(match(str)) {
            consumeNextChars(str.length());
            return true;
        }
        return false;
    }
    
    private boolean consume(char c) {
        if(match(c)) {
            consumeChar();
            return true;
        }
        return false;
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
                //column = getColumn() + 1;
            }
            
        }
        column = getColumn();
    }
    
    private int getColumn() {
        int output = 1;
        int fakeIndex = currentIndex- 1;
        while(fakeIndex >= 0 && inputString.charAt(fakeIndex) != '\n') {
            fakeIndex--;
            output++;
        }
        return output;
    }
    
    private void insertString(String s) {
        inputString = inputString.substring(0, currentIndex) + s + inputString.substring(currentIndex);
        maxIndex = inputString.length();
    }
    
    private void removeString(int length) {
        inputString = inputString.substring(0, currentIndex) + inputString.substring(currentIndex + length);
        maxIndex = inputString.length();
    }
    
    private void replaceString(String original, String replace) {
        unconsume(original);
        if(match(original)) {
            removeString(original.length());
            insertString(replace);
        }
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
        
        switch (directive) {
            case "#define": {
                
                Pattern function = Pattern.compile("(?<id>[a-zA-Z]\\w*)(?<isfunc>\\(\\s*(?<args>(([a-zA-Z]\\w*\\s*(," +
                        "\\s*[a-zA-Z]\\w*\\s*)*)(,\\s*\\.\\.\\.\\s*)?)|(\\s*\\.\\.\\.\\s*)?)\\))?");
                
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
                    
                    defines.put(identifier, define);
                }
                return;
            }
            case "#ifndef": {
                inIfStatement = true;
                if(defines.containsKey(arguments)) {
                    skipToIfFalse = true;
                }
                return;
            }
            case "#ifdef": {
                inIfStatement = true;
                if(!defines.containsKey(arguments)) {
                    skipToIfFalse = true;
                }
                return;
            }
            case "#else": {
                if(!inIfStatement) throw new IllegalArgumentException();
                skipToIfFalse = !skipToIfFalse;
                return;
            }
            case "#endif": {
                if(!inIfStatement) throw new IllegalArgumentException();
                skipToIfFalse = false;
                inIfStatement = false;
                return;
            }
            case "#include": {
                if(!(arguments.charAt(0) == arguments.charAt(arguments.length() - 1) &&
                        (arguments.charAt(0) == '<' || arguments.charAt(0) == '"')))
                    throw new IllegalArgumentException();
                
                String filename = arguments.substring(1, arguments.length() - 1);
                boolean isLocal = arguments.charAt(0) == '"';
                File file;
                if(isLocal) {
                    file = new File(filename);
                } else {
                    throw new IllegalArgumentException();
                }
    
                if(!file.exists()) {
                    throw new IllegalArgumentException();
                }
                StringBuilder text = new StringBuilder();
                try {
        
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
        
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
                int restoreLineNumber = lineNumber;
                String fullText = "#line " + 1 + " \""+ filename + "\"\n" + text.toString() +
                        "\n#line " + restoreLineNumber + " \""+ this.filename + "\"\n";
                replaceString(originalString, fullText);
                return;
            }
            case "#line": {
                //int lineNumber = Integer.parseInt(arguments);
                //this.lineNumber = lineNumber;
                return;
            }
            default:
                return;
        }
    }
    
    private boolean match(char c) {
        return getChar() == c;
    }
    
    
    private boolean match(String str) {
        return getNextChars(str.length()).equals(str);
    }
    
    public String getInputString() {
        return inputString;
    }
    
    private Token singleLex() {
       
        
        
        while(true) {
            String image = "";
            
            do {
                if (consume("//")) {
                    while (!consume("\n")) {
                        consumeChar();
                    }
                } else if (consume("/*")) {
                    while (!consume("*/")) consumeChar();
                }
                //(skipToIfFalse && getChar() != '#') &&
                while ( getChar() == ' ' || getChar() == '\n' || getChar() == '\t' || getChar() == '\r') {
                    consumeChar();
                    if (consume("//")) {
                        while (!consume('\n')) {
                            consumeChar();
                        }
                    } else if (consume("/*")) {
                        while (!consume("*/")) consumeChar();
                    }
                }
                
                if (getChar() == '#') {
                    if (column != 1) return null;
                    String preprocessorDirective = "";
                    while (getChar() != '\n') {
                        preprocessorDirective += consumeChar();
                    }
                    
                    String original = preprocessorDirective + consumeChar();
                    preprocessorDirective = preprocessorDirective.replaceAll("\\s+", " ");
                    invokePreprocessorDirective(preprocessorDirective, original);
                    
                } else if(!skipToIfFalse) break;
                else consumeChar();
                
            } while (true);
            
            if (getChar() == '\0') {
                
                return new Token(TokenType.t_eof);
            }
            
            
            if (getChar() == '"') {
                consumeChar();
                boolean inString = true;
                while (inString) {
                    if (getChar() == '\\') {
                        image += consumeNextChars(2);
                    } else if (getChar() == '\n') {
                        return null;
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
            } else if (Character.isDigit(getChar()) || (getChar() == '.' && Character.isDigit(getNextChars(2).charAt(1)))) {
                if (match("0x") || match("0X")) {
                    image += consumeNextChars(2);
                    while ((getChar() >= 'a' && getChar() <= 'f') ||
                            (getChar() >= 'A' && getChar() <= 'F')) {
                        image += consumeChar();
                    }
                } else {
                    boolean decimalFound = false;
                    while (Character.isDigit(getChar()) || getChar() == '.') {
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
            } else if (Character.isLetter(getChar()) || getChar() == '_') {
                image += consumeChar();
                while (Character.isLetter(getChar()) || getChar() == '_' || Character.isDigit(getChar())) {
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
                            while (!(getChar() == ')' && parens == 0)) {
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
                        replaceString(image, define.replacementString);
                        continue;
                    }
                    
                }
                
                
                if (image.equals("char")) {
                    return new Token(TokenType.t_char);
                } else if (image.equals("const")) {
                    return new Token(TokenType.t_const);
                } else if (image.equals("do")) {
                    return new Token(TokenType.t_do);
                } else if (image.equals("double")) {
                    return new Token(TokenType.t_double);
                } else if (image.equals("else")) {
                    return new Token(TokenType.t_else);
                } else if (image.equals("float")) {
                    return new Token(TokenType.t_float);
                } else if (image.equals("for")) {
                    return new Token(TokenType.t_for);
                } else if (image.equals("if")) {
                    return new Token(TokenType.t_if);
                } else if (image.equals("int")) {
                    return new Token(TokenType.t_int);
                } else if (image.equals("long")) {
                    return new Token(TokenType.t_long);
                } else if (image.equals("return")) {
                    return new Token(TokenType.t_return);
                } else if (image.equals("short")) {
                    return new Token(TokenType.t_short);
                } else if (image.equals("static")) {
                    return new Token(TokenType.t_static);
                } else if (image.equals("typedef")) {
                    return new Token(TokenType.t_typedef);
                } else if (image.equals("union")) {
                    return new Token(TokenType.t_union);
                } else if (image.equals("unsigned")) {
                    return new Token(TokenType.t_unsigned);
                } else if (image.equals("struct")) {
                    return new Token(TokenType.t_struct);
                } else if (image.equals("void")) {
                    return new Token(TokenType.t_void);
                } else if (image.equals("while")) {
                    return new Token(TokenType.t_while);
                } else if (image.equals("class")) {
                    return new Token(TokenType.t_class);
                } else if (image.equals("public")) {
                    return new Token(TokenType.t_public);
                } else if (image.equals("private")) {
                    return new Token(TokenType.t_private);
                } else if (image.equals("new")) {
                    return new Token(TokenType.t_new);
                } else if (image.equals("super")) {
                    return new Token(TokenType.t_super);
                } else if (image.equals("virtual")) {
                    return new Token(TokenType.t_virtual);
                } else if (image.equals("sizeof")) {
                    return new Token(TokenType.t_sizeof);
                } else if (image.equals("boolean")) {
                    return new Token(TokenType.t_typename, image);
                }
                
                return new Token(TokenType.t_id, image);
            }
            
            switch (consumeChar()) {
                case '<': {
                    switch (getChar()) {
                        case '<': {
                            consumeChar();
                            if (getChar() == '=') {
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
                            if (getChar() == '=') {
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
                    if (getChar() == '=') {
                        consumeChar();
                        return new Token(TokenType.t_operator_assign, "+=");
                    } else if (consume("+")) {
                        return new Token(TokenType.t_inc);
                    }
                    return new Token(TokenType.t_add);
                }
                case '-': {
                    if (getChar() == '=') {
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
                    if (consume("..")) return new Token(TokenType.t_ellipsis);
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
                }
            }
        }
    }
    
    public Token getFirst() {
        return createdTokens.get(0);
    }
    
    public Token getLast() {
        return createdTokens.get(createdTokens.size() - 1);
    }
    
    public Token getPrevious() {
        if(createdTokens.size() <= 1) return null;
        return createdTokens.get(tokenIndex - 1);
    }
    
    public Token getCurrent() {
        if(createdTokens.size() == 0) return getNext();
        return createdTokens.get(tokenIndex);
    }
    
    public Token getNext() {
        if(++tokenIndex == createdTokens.size()) {
            
            Token tok = singleLex();
            if(tok == null) return null;
            String representation = tok.getRepresentation();
            tok.addColumnAndLineNumber(column - representation.length(), lineNumber);
            prevLineNumber = lineNumber;
            prevColumn = column;
            createdTokens.add(tok);
            return tok;
        }
        return createdTokens.get(tokenIndex);
    }
    
    public void reset() {
        tokenIndex = 0;
    }
    
    
    @Override
    public Iterator<Token> iterator() {
        return this;
    }
    
    @Override
    public boolean hasNext() {
        if(tokenIndex < createdTokens.size() - 1) return true;
        
        return currentIndex < inputString.length();
    }
    
    @Override
    public Token next() {
        return getNext();
    }
}
