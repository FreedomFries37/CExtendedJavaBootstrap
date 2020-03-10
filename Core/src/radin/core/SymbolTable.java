package radin.core;

import radin.core.lexical.Token;
import radin.core.semantics.types.CXIdentifier;
import radin.core.utility.Pair;

import java.util.*;

public class SymbolTable<K, T> implements Iterable<Map.Entry<SymbolTable<K, T>.Key, T>> {
    
    public class Key {
        private K key;
        private String fileOrigin;
        private Token token;
    
        public Key(K key, String fileOrigin, Token token) {
            this.key = key;
            this.fileOrigin = fileOrigin;
            this.token = token;
        }
    
        public K getKey() {
            return key;
        }
    
        public String getFileOrigin() {
            return fileOrigin;
        }
    
        public Token getToken() {
            return token;
        }
    
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key1 = (Key) o;
            return key.equals(key1.key);
        }
    
        @Override
        public int hashCode() {
            return Objects.hash(key);
        }
    
        @Override
        public String toString() {
            return "Key{" +
                    "key=" + key +
                    ", fileOrigin='" + fileOrigin + '\'' +
                    '}';
        }
    }
    
    
    private HashMap<Key, T> table;
    
    public SymbolTable() {
        table = new HashMap<>();
    }
    
    public SymbolTable(Collection<? extends SymbolTable<K, T>> combine) {
        this();
        for (SymbolTable<K, T> ktSymbolTable : combine) {
            if(ktSymbolTable == null) continue;
            for (Map.Entry<Key, T> keyTEntry : ktSymbolTable) {
                put(keyTEntry.getKey(), keyTEntry.getValue());
            }
        }
    }
    
    @Override
    public String toString() {
        return "SymbolTable{size = "+ size() +"}";
    }
    
    public int size() {
        return table.size();
    }
    
    public boolean isEmpty() {
        return table.isEmpty();
    }
    
    public T get(K key) {
        return table.get(new Key(key, null, null));
    }
    
    public boolean containsKey(K key) {
        return table.containsKey(new Key(key, null, null));
    }
    
    public T put(Key key, T value) {
        return table.put(key, value);
    }
    
    public void putAll(Map<? extends Key, ? extends T> m) {
        table.putAll(m);
    }
    
    public T remove(Object key) {
        return table.remove(key);
    }
    
    public void clear() {
        table.clear();
    }
    
    public boolean containsValue(Object value) {
        return table.containsValue(value);
    }
    
    public Set<Key> keySet() {
        return table.keySet();
    }
    
    public Collection<T> values() {
        return table.values();
    }
    
    public Set<Map.Entry<Key, T>> entrySet() {
        return table.entrySet();
    }
    
    public T getOrDefault(Object key, T defaultValue) {
        return table.getOrDefault(key, defaultValue);
    }
    
    public T putIfAbsent(Key key, T value) {
        return table.putIfAbsent(key, value);
    }
    
    public boolean remove(Object key, Object value) {
        return table.remove(key, value);
    }
    
    public boolean replace(Key key, T oldValue, T newValue) {
        return table.replace(key, oldValue, newValue);
    }
    
    public T replace(Key key, T value) {
        return table.replace(key, value);
    }
    
    @Override
    public Iterator<Map.Entry<Key, T>> iterator() {
        return entrySet().iterator();
    }
}
