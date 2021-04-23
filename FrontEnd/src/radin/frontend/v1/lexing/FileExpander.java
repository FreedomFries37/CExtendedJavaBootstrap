package radin.frontend.v1.lexing;

import radin.core.errorhandling.CompilationError;
import radin.core.utility.UniversalCompilerSettings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileExpander {

    public String expandFile(File file) throws CompilationError {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new Error("File " + file + " could not be found");
        }
        Stream<String> lines = reader.lines();
        StringBuilder builder = new StringBuilder();
        File parent = file.getParentFile();
        Pattern local = Pattern.compile("#include\\s+\"(.*?)\"");
        Pattern global = Pattern.compile("#include\\s+<(.*?)>") ;
        
        int lineNumber = 1;
    
        for (String line : lines.collect(Collectors.toList())) {
            int restoreLineNumber = lineNumber;
            if (line.startsWith("#include")) {
                Matcher match = local.matcher(line);
                File foundFile;
                if (match.find()) {
                    String filename = match.group(1);
                    foundFile = new File(parent, filename);
                } else {
                    match = global.matcher(line);
                    if (match.find()) {
                        String filename = match.group(1);
                        File[] locations = UniversalCompilerSettings.getInstance().getSettings().includeDirectories();
        
        
                        foundFile = null;
                        for (File dir : locations) {
            
            
                            Path path = Paths.get(dir.getPath(), filename);
                            foundFile = new File(path.toUri());
                            if (foundFile.exists()) {
                                break;
                            } else {
                                foundFile = null;
                            }
            
                        }
                        if (foundFile == null) {
                            throw new Error(filename + " does not exist");
                        }
                    } else {
                        throw new Error("Include directive format incorrect");
                    }
                }
    
                String expanded = expandFile(foundFile);
                String formatted = "#line " + 1 + " \"" + foundFile.getPath() + "\"\n" + expanded +
                        "\n#line " + restoreLineNumber + " \"" + file + "\"\n";
                builder.append(formatted);
            } else {
                builder.append(line).append("\n");
            }
            lineNumber++;
            
        }
        return builder.toString();
    }
    
}
