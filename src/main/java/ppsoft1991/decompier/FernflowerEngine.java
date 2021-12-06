package ppsoft1991.decompier;

import org.jetbrains.java.decompiler.main.DecompilerContext;
import ppsoft1991.decompier.fernflower.Fernflower;
import org.jetbrains.java.decompiler.main.decompiler.PrintStreamLogger;
import org.jetbrains.java.decompiler.main.extern.IBytecodeProvider;
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;
import org.jetbrains.java.decompiler.main.extern.IResultSaver;
import org.jetbrains.java.decompiler.util.InterpreterUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class FernflowerEngine implements IBytecodeProvider, IResultSaver {
    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println(
                    "Usage: java -jar fernflower.jar [-<option>=<value>]* [<source>]+ <destination>\n" +
                            "Example: java -jar fernflower.jar -dgs=true c:\\my\\source\\ c:\\my.jar d:\\decompiled\\");
            return;
        }

        Map<String, Object> mapOptions = new HashMap<>();
        List<File> sources = new ArrayList<>();
        List<File> libraries = new ArrayList<>();

        boolean isOption = true;
        for (int i = 0; i < args.length - 1; ++i) { // last parameter - destination
            String arg = args[i];

            if (isOption && arg.length() > 5 && arg.charAt(0) == '-' && arg.charAt(4) == '=') {
                String value = arg.substring(5);
                if ("true".equalsIgnoreCase(value)) {
                    value = "1";
                } else if ("false".equalsIgnoreCase(value)) {
                    value = "0";
                }

                mapOptions.put(arg.substring(1, 4), value);
            } else {
                isOption = false;

                if (arg.startsWith("-e=")) {
                    addPath(libraries, arg.substring(3));
                } else {
                    addPath(sources, arg);
                }
            }
        }

        if (sources.isEmpty()) {
            System.out.println("error: no sources given");
            return;
        }

        File destination = new File(args[args.length - 1]);
        if (!destination.isDirectory()) {
            System.out.println("error: destination '" + destination + "' is not a directory");
            return;
        }

        PrintStreamLogger logger = new PrintStreamLogger(System.out);
        FernflowerEngine decompiler = new FernflowerEngine(destination, mapOptions, logger);

        for (File library : libraries) {
            decompiler.addLibrary(library);
        }
        for (File source : sources) {
            decompiler.addSource(source);
        }

        decompiler.decompileContext();
    }

    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    private static void addPath(List<? super File> list, String path) {
        File file = new File(path);
        if (file.exists()) {
            list.add(file);
        } else {
            System.out.println("warn: missing '" + path + "', ignored");
        }
    }

    // *******************************************************************
    // Implementation
    // *******************************************************************

    private final File root;
    private final Fernflower engine;
    private final Map<String, ZipOutputStream> mapArchiveStreams = new HashMap<>();
    private final Map<String, Set<String>> mapArchiveEntries = new HashMap<>();

    protected FernflowerEngine(File destination, Map<String, Object> options, IFernflowerLogger logger) {
        root = destination;
        engine = new Fernflower(this, this, options, logger);
    }

    public void addSource(File source) {
        engine.addSource(source);
    }

    public void addLibrary(File library) {
        engine.addLibrary(library);
    }

    public void decompileContext() {
        try {
            engine.decompileContext();
        } finally {
            engine.clearContext();
        }
    }

    // *******************************************************************
    // Interface IBytecodeProvider
    // *******************************************************************

    @Override
    public byte[] getBytecode(String externalPath, String internalPath) throws IOException {
        File file = new File(externalPath);
        if (internalPath == null) {
            return InterpreterUtil.getBytes(file);
        } else {
            try (ZipFile archive = new ZipFile(file)) {
                ZipEntry entry = archive.getEntry(internalPath);
                if (entry == null) throw new IOException("Entry not found: " + internalPath);
                return InterpreterUtil.getBytes(archive, entry);
            }
        }
    }

    // *******************************************************************
    // Interface IResultSaver
    // *******************************************************************

    private String getAbsolutePath(String path) {
        return new File(root, path).getAbsolutePath();
    }

    @Override
    public void saveFolder(String path) {
    }

    @Override
    public void copyFile(String source, String path, String entryName) {
    }

    @Override
    public void saveClassFile(String path, String qualifiedName, String entryName, String content, int[] mapping) {
        File file = new File(getAbsolutePath(path), entryName);
        try (Writer out = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            out.write(content);
        } catch (IOException ex) {
            DecompilerContext.getLogger().writeMessage("Cannot write class file " + file, ex);
        }
    }

    @Override
    public void createArchive(String path, String archiveName, Manifest manifest) {
    }

    @Override
    public void saveDirEntry(String path, String archiveName, String entryName) {
    }

    @Override
    public void copyEntry(String source, String path, String archiveName, String entryName) {
    }

    @Override
    public void saveClassEntry(String path, String archiveName, String qualifiedName, String entryName, String content) {
    }

    private boolean checkEntry(String entryName, String file) {
        Set<String> set = mapArchiveEntries.computeIfAbsent(file, k -> new HashSet<>());

        boolean added = set.add(entryName);
        if (!added) {
            String message = "Zip entry " + entryName + " already exists in " + file;
            DecompilerContext.getLogger().writeMessage(message, IFernflowerLogger.Severity.WARN);
        }
        return added;
    }

    @Override
    public void closeArchive(String path, String archiveName) {
    }
}