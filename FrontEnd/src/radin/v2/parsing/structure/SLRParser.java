package radin.v2.parsing.structure;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.frontend.IParser;
import radin.core.frontend.ITokenizer;
import radin.core.lexical.Token;
import radin.utility.Pair;
import radin.v1.parsing.CategoryNode;
import radin.v1.parsing.LeafNode;
import radin.v1.parsing.ParseNode;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class SLRParser<Input extends Token, Check> implements IParser<Input, ParseNode> {
    
    @FunctionalInterface
    public interface Map<T, R> {
        R map(T in);
    }
    
    private Map<? super Input, ? extends Check> mappingFunction;
    private ITokenizer<? extends Input> tokenizer;
    private HashMap<Pair<ParsableObject<?>, Integer>, LRActionRecord<Integer>> parseTable;
    private SLRData<Check> slrData;
    private Stack<NonTerminal<Check>> buffer = new Stack<>();
    
    public SLRParser(Map<? super Input, ? extends Check> mappingFunction, ITokenizer<? extends Input> tokenizer, SLRData<Check> slrData) {
        this.mappingFunction = mappingFunction;
        this.tokenizer = tokenizer;
        this.slrData = slrData;
        parseTable = this.slrData.transformParseTable();
    }
    
    private NonTerminal<Check> scan() {
        if(!buffer.isEmpty()) return buffer.pop();
        Input next = getNext();
        return new NonTerminal<>(mappingFunction.map(next));
    }
    
    private ParsableObject<?> symbolOverwrite(ParsableObject<?> o) {
        buffer.push(new NonTerminal<>(mappingFunction.map(getCurrent())));
        return o;
    }
    
    @Override
    public ITokenizer<? extends Input> getTokenizer() {
        return tokenizer;
    }
    
    @Override
    public void setTokenizer(ITokenizer<? extends Input> tokenizer) {
        this.tokenizer = tokenizer;
    }
    
    @Override
    public ParseNode parse() {
        Stack<Pair<ParsableObject<?>, Integer>> parseStack = new Stack<>();
        parseStack.push(new Pair<>(null, 0));
        ParsableObject<?> symbol = scan();
        List<ParseNode> createdParseNodes = new LinkedList<>();
        
        while(true) {
            printStack(parseStack);
            int currentState = parseStack.peek().getVal2();
            if(currentState == 0 && symbol.equals(slrData.getStartingSymbol())) {
                printStack(parseStack);
                return createdParseNodes.get(0);
            }
            Pair<ParsableObject<?>, Integer> key = new Pair<>(symbol, currentState);
            LRActionRecord<Integer> actionRecord = parseTable.get(key);
            if(actionRecord == null) {
                throw new IllegalStateException();
            }
            switch (actionRecord.getAction()) {
                case SHIFT:
                    parseStack.push(new Pair<>(symbol, actionRecord.getNextState()));
                    if(symbol instanceof NonTerminal) {
                        createdParseNodes.add(new LeafNode(getCurrent()));
                    }
                    symbol = scan();
                    break;
                case SHIFT_REDUCE: {
                    symbol = symbolOverwrite(actionRecord.getProduction().getLhs());
                    List<ParseNode> children = new LinkedList<>();
                    for (int i = 0; i < actionRecord.getProduction().getRhs().size() - 1; i++) {
                        children.add(0, createdParseNodes.remove(createdParseNodes.size() - 1));
                        parseStack.pop();
                    }
                    assert symbol != null;
                    CategoryNode e = new CategoryNode(((Symbol) symbol).getBackingObject());
                    for (ParseNode child : children) {
                        e.addChild(child);
                    }
                    createdParseNodes.add(e);
                    System.out.println(createdParseNodes);
                    break;
                }
                case REDUCE: {
                    symbol = symbolOverwrite(actionRecord.getProduction().getLhs());
                    List<ParseNode> children = new LinkedList<>();
                    for (int i = 0; i < actionRecord.getProduction().getRhs().size(); i++) {
                        children.add(0, createdParseNodes.remove(createdParseNodes.size() - 1));
                        parseStack.pop();
                    }
                    assert symbol != null;
                    CategoryNode e = new CategoryNode(((Symbol) symbol).getBackingObject());
                    for (ParseNode child : children) {
                        e.addChild(child);
                    }
                    createdParseNodes.add(e);
                    System.out.println(createdParseNodes);
                    break;
                }
                case ERROR:
                    throw new IllegalStateException();
            }
        }
    }
    
    private void printStack(Stack<Pair<ParsableObject<?>, Integer>> s) {
        for (Pair<ParsableObject<?>, Integer> parsableObjectIntegerPair : s) {
            if(parsableObjectIntegerPair.getVal1() != null) {
                System.out.print(" " + parsableObjectIntegerPair.getVal1() + " ");
            }
            System.out.print(parsableObjectIntegerPair.getVal2());
        }
        System.out.println();
    }
    
    @Override
    public List<AbstractCompilationError> getErrors() {
        return null;
    }
}
