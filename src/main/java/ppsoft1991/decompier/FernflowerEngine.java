package ppsoft1991.decompier;

import org.jetbrains.java.decompiler.main.Fernflower;
import org.jetbrains.java.decompiler.main.extern.IBytecodeProvider;
import org.jetbrains.java.decompiler.main.extern.IResultSaver;
import org.jetbrains.java.decompiler.util.InterpreterUtil;

import java.io.File;
import java.io.IOException;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FernflowerEngine implements IBytecodeProvider, IResultSaver {

    public FernflowerEngine(){
        //new Fernflower(this, this, options, logger);
    }
    @Override
    public byte[] getBytecode(String externalPath, String internalPath) throws IOException {
        File file = new File(externalPath);
        if (internalPath == null) {
            return InterpreterUtil.getBytes(file);
        }
        else {
            try (ZipFile archive = new ZipFile(file)) {
                ZipEntry entry = archive.getEntry(internalPath);
                if (entry == null) throw new IOException("Entry not found: " + internalPath);
                return InterpreterUtil.getBytes(archive, entry);
            }
        }
    }



    @Override
    public void saveFolder(String path) {

    }

    @Override
    public void copyFile(String source, String path, String entryName) {

    }

    @Override
    public void saveClassFile(String path, String qualifiedName, String entryName, String content, int[] mapping) {

    }

    @Override
    public void createArchive(String path, String archiveName, Manifest manifest) {

    }

    @Override
    public void saveDirEntry(String path, String archiveName, String entryName) {

    }

    @Override
    public void copyEntry(String source, String path, String archiveName, String entry) {

    }

    @Override
    public void saveClassEntry(String path, String archiveName, String qualifiedName, String entryName, String content) {

    }

    @Override
    public void closeArchive(String path, String archiveName) {

    }
}
