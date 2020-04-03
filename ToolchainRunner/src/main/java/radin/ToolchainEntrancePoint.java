package radin;

import java.io.File;
import java.io.IOException;

public class ToolchainEntrancePoint {
    
    /**
     * Runs the toolchain execution mode
     * @param args
     */
    public static void main(String[] args) throws IOException {
        File toolchainDirectory = new File(".toolchain");
        if (!toolchainDirectory.exists() || !toolchainDirectory.isDirectory()) {
            if (System.getenv("JODIN_HOME") == null) throw new IOException("Jodin Home not set");
            toolchainDirectory = new File(System.getenv("JODIN_HOME") + "/toolchain");
            if (!toolchainDirectory.exists() || !toolchainDirectory.isDirectory()) {
                throw new IOException("No toolchain directory!");
            }
        }
        
        File configFile = new File(toolchainDirectory, "config");
        
    }
}
