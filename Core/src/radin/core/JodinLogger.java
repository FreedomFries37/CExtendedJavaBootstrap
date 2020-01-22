package radin.core;


import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.logging.*;

public class JodinLogger {
    
    private Logger base;
    private final long startTime = System.currentTimeMillis();
    
    private class JodinFormatter extends Formatter {
        
        private String prefix(Level level) {
            double elapsedTime = (double) (System.currentTimeMillis() - startTime) / 10E3;
            
            return String.format("t+%.3f [%-7S] ", elapsedTime, level);
        }
        
        @Override
        public String format(LogRecord logRecord) {
            StringBuilder message = new StringBuilder();
            if(logRecord.getSourceClassName() != null && !logRecord.getSourceClassName().equals(JodinLogger.class.getName())) {
                if (logRecord.getSourceMethodName() != null) {
                    message.append("from ").append(logRecord.getSourceMethodName()).append(" ");
                }
                
                message.append("in ").append(logRecord.getSourceClassName()).append(" ");
                
            }
            
            
            message.append(logRecord.getMessage());
            if(logRecord.getThrown() != null) {
                logRecord.setLevel(Level.SEVERE);
            }
            message = new StringBuilder(prefix(logRecord.getLevel()) + message.toString() + "\n");
            
            return message.toString();
        }
    }
    
    public class LoggerPrintStream extends PrintStream {
        
        private Level severity;
        
        public LoggerPrintStream(PrintStream out, Level severity) {
            super(out);
            this.severity = severity;
        }
        
        @Override
        public void print(String s) {
            JodinLogger.this.log(severity, s);
        }
        
        @Override
        public void println() {
            println("");
        }
        
        @Override
        public void println(String x) {
            JodinLogger.this.log(severity, x + System.lineSeparator());
        }
    }
    
    public JodinLogger(String filename) {
        base = Logger.getLogger("JodinLogger");
        FileHandler fh;
        
        try {
            fh = new FileHandler(filename);
            base.addHandler(fh);
            Formatter formatter = new JodinFormatter();
            fh.setFormatter(formatter);
            
            base.info("Log Start");
            base.setLevel(Level.ALL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    public PrintStream divertOutput(Level severity, PrintStream other) {
        return new LoggerPrintStream(other, severity);
    }
    
    
    public void severe(String msg) {
        base.severe(msg);
    }
    
    public void warning(String msg) {
        base.warning(msg);
    }
    
    public void info(String msg) {
        base.info(msg);
    }
    
    public void config(String msg) {
        base.config(msg);
    }
    
    public void fine(String msg) {
        base.fine(msg);
    }
    
    public void finer(String msg) {
        base.finer(msg);
    }
    
    public void finest(String msg) {
        base.finest(msg);
    }
    
    public static Logger getGlobal() {
        return Logger.getGlobal();
    }
    
    
    public static Logger getAnonymousLogger() {
        return Logger.getAnonymousLogger();
    }
    
    
    
    public ResourceBundle getResourceBundle() {
        return base.getResourceBundle();
    }
    
    public String getResourceBundleName() {
        return base.getResourceBundleName();
    }
    
    public void setFilter(Filter newFilter) throws SecurityException {
        base.setFilter(newFilter);
    }
    
    public Filter getFilter() {
        return base.getFilter();
    }
    
    public void log(LogRecord record) {
        base.log(record);
    }
    
    public void log(Level level, String msg) {
        base.log(level, msg);
    }
    
    public void log(Level level, Supplier<String> msgSupplier) {
        base.log(level, msgSupplier);
    }
    
    public void log(Level level, String msg, Object param1) {
        base.log(level, msg, param1);
    }
    
    public void log(Level level, String msg, Object[] params) {
        base.log(level, msg, params);
    }
    
    public void log(Level level, String msg, Throwable thrown) {
        base.log(level, msg, thrown);
    }
    
    public void log(Level level, Throwable thrown, Supplier<String> msgSupplier) {
        base.log(level, thrown, msgSupplier);
    }
    
    public void logp(Level level, String sourceClass, String sourceMethod, String msg) {
        base.logp(level, sourceClass, sourceMethod, msg);
    }
    
    public void logp(Level level, String sourceClass, String sourceMethod, Supplier<String> msgSupplier) {
        base.logp(level, sourceClass, sourceMethod, msgSupplier);
    }
    
    public void logp(Level level, String sourceClass, String sourceMethod, String msg, Object param1) {
        base.logp(level, sourceClass, sourceMethod, msg, param1);
    }
    
    public void logp(Level level, String sourceClass, String sourceMethod, String msg, Object[] params) {
        base.logp(level, sourceClass, sourceMethod, msg, params);
    }
    
    public void logp(Level level, String sourceClass, String sourceMethod, String msg, Throwable thrown) {
        base.logp(level, sourceClass, sourceMethod, msg, thrown);
    }
    
    public void logp(Level level, String sourceClass, String sourceMethod, Throwable thrown, Supplier<String> msgSupplier) {
        base.logp(level, sourceClass, sourceMethod, thrown, msgSupplier);
    }
    
    /** @deprecated
     * @param level
     * @param sourceClass
     * @param sourceMethod
     * @param bundleName
     * @param msg */
    @Deprecated
    public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg) {
        base.logrb(level, sourceClass, sourceMethod, bundleName, msg);
    }
    
    /** @deprecated
     * @param level
     * @param sourceClass
     * @param sourceMethod
     * @param bundleName
     * @param msg
     * @param param1 */
    @Deprecated
    public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Object param1) {
        base.logrb(level, sourceClass, sourceMethod, bundleName, msg, param1);
    }
    
    /** @deprecated
     * @param level
     * @param sourceClass
     * @param sourceMethod
     * @param bundleName
     * @param msg
     * @param params */
    @Deprecated
    public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Object[] params) {
        base.logrb(level, sourceClass, sourceMethod, bundleName, msg, params);
    }
    
    public void logrb(Level level, String sourceClass, String sourceMethod, ResourceBundle bundle, String msg, Object... params) {
        base.logrb(level, sourceClass, sourceMethod, bundle, msg, params);
    }
    
    public void logrb(Level level, ResourceBundle bundle, String msg, Object... params) {
        base.logrb(level, bundle, msg, params);
    }
    
    /** @deprecated
     * @param level
     * @param sourceClass
     * @param sourceMethod
     * @param bundleName
     * @param msg
     * @param thrown */
    @Deprecated
    public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Throwable thrown) {
        base.logrb(level, sourceClass, sourceMethod, bundleName, msg, thrown);
    }
    
    public void logrb(Level level, String sourceClass, String sourceMethod, ResourceBundle bundle, String msg, Throwable thrown) {
        base.logrb(level, sourceClass, sourceMethod, bundle, msg, thrown);
    }
    
    public void logrb(Level level, ResourceBundle bundle, String msg, Throwable thrown) {
        base.logrb(level, bundle, msg, thrown);
    }
    
    public void entering(String sourceClass, String sourceMethod) {
        base.entering(sourceClass, sourceMethod);
    }
    
    public void entering(String sourceClass, String sourceMethod, Object param1) {
        base.entering(sourceClass, sourceMethod, param1);
    }
    
    public void entering(String sourceClass, String sourceMethod, Object[] params) {
        base.entering(sourceClass, sourceMethod, params);
    }
    
    public void exiting(String sourceClass, String sourceMethod) {
        base.exiting(sourceClass, sourceMethod);
    }
    
    public void exiting(String sourceClass, String sourceMethod, Object result) {
        base.exiting(sourceClass, sourceMethod, result);
    }
    
    public void throwing(String sourceClass, String sourceMethod, Throwable thrown) {
        base.throwing(sourceClass, sourceMethod, thrown);
        severe(thrown.toString());
        boolean first = true;
        for (StackTraceElement stackTraceElement : thrown.getStackTrace()) {
            if(first) {
                finer("\tat " + stackTraceElement.toString());
                first = false;
            }
            else finest("\tat " + stackTraceElement.toString());
        }
        finer("");
    }
    
    public void severe(Supplier<String> msgSupplier) {
        base.severe(msgSupplier);
    }
    
    public void warning(Supplier<String> msgSupplier) {
        base.warning(msgSupplier);
    }
    
    public void info(Supplier<String> msgSupplier) {
        base.info(msgSupplier);
    }
    
    public void config(Supplier<String> msgSupplier) {
        base.config(msgSupplier);
    }
    
    public void fine(Supplier<String> msgSupplier) {
        base.fine(msgSupplier);
    }
    
    public void finer(Supplier<String> msgSupplier) {
        base.finer(msgSupplier);
    }
    
    public void finest(Supplier<String> msgSupplier) {
        base.finest(msgSupplier);
    }
    
    public void setLevel(Level newLevel) throws SecurityException {
        base.setLevel(newLevel);
    }
    
    public Level getLevel() {
        return base.getLevel();
    }
    
    public boolean isLoggable(Level level) {
        return base.isLoggable(level);
    }
    
    public String getName() {
        return base.getName();
    }
    
    public void addHandler(Handler handler) throws SecurityException {
        base.addHandler(handler);
    }
    
    public void removeHandler(Handler handler) throws SecurityException {
        base.removeHandler(handler);
    }
    
    public Handler[] getHandlers() {
        return base.getHandlers();
    }
    
    public void setUseParentHandlers(boolean useParentHandlers) {
        base.setUseParentHandlers(useParentHandlers);
    }
    
    public boolean getUseParentHandlers() {
        return base.getUseParentHandlers();
    }
    
    public void setResourceBundle(ResourceBundle bundle) {
        base.setResourceBundle(bundle);
    }
    
    public Logger getParent() {
        return base.getParent();
    }
    
    public void setParent(Logger parent) {
        base.setParent(parent);
    }
}
