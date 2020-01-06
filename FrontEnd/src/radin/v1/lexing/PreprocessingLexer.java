package radin.v1.lexing;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.frontend.Tokenizer;
import radin.core.lexical.Token;
import radin.core.lexical.TokenType;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PreprocessingLexer extends Tokenizer<Token> {
    
    
    
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
    
    private int maxIndex;
    private int prevColumn;
    private int prevLineNumber;
    private boolean inIfStatement;
    private boolean skipToIfFalse;
    private HashMap<String, Define> defines;
    private List<AbstractCompilationError> compilationErrors;
    private int finishedIndex = -1;
    
    public PreprocessingLexer(String filename, String inputString) {
        super(inputString, filename);
        prevColumn = 1;
        prevLineNumber = 1;
        maxIndex = this.getInputString().length();
        defines = new HashMap<>();
        compilationErrors = new LinkedList<>();
    }
    
    @Override
    public List<AbstractCompilationError> getErrors() {
        return compilationErrors;
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
    
    private void insertString(String s) {
        inputString = getInputString().substring(0, currentIndex) + s + getInputString().substring(currentIndex);
        maxIndex = getInputString().length();
    }
    
    private void removeString(int length) {
        inputString = getInputString().substring(0, currentIndex) + getInputString().substring(currentIndex + length);
        maxIndex = getInputString().length();
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
                while ( match(' ') || match('\n') || match('\t') || match('\r')) {
                    consumeChar();
                    if (consume("//")) {
                        while (!consume('\n')) {
                            consumeChar();
                        }
                    } else if (consume("/*")) {
                        while (!consume("*/")) consumeChar();
                    }
                }
                
                if (match('#')) {
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
            
            if (match('\0')) {
                
                return new Token(TokenType.t_eof);
            }
            
            
            if (match('"')) {
                consumeChar();
                boolean inString = true;
                while (inString) {
                    if (match('\\')) {
                        image += consumeNextChars(2);
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
                        replaceString(image, define.replacementString);
                        continue;
                    }
                    
                }
    
    
                return getReservedToken(image);
    
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
                    if (consume("")) return new Token(TokenType.t_ellipsis);
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
    
    @Override
    public Token getNext() {
        if(++tokenIndex == createdTokens.size()) {
            Token tok;
            try {
                 tok = singleLex();
                 
            }catch (AbstractCompilationError e) {
                compilationErrors.add(e);
                finishedIndex = getTokenIndex();
                tok = null;
            }
            if(tok == null) return null;
            tok.setPrevious(getPrevious());
            String representation = tok.getRepresentation();
            tok.addColumnAndLineNumber(column - representation.length(), lineNumber);
            prevLineNumber = lineNumber;
            prevColumn = column;
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
}
