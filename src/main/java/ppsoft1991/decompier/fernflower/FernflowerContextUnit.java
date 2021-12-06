package ppsoft1991.decompier.fernflower;

import org.jetbrains.java.decompiler.main.DecompilerContext;
import org.jetbrains.java.decompiler.main.extern.IFernflowerPreferences;
import org.jetbrains.java.decompiler.main.extern.IResultSaver;
import org.jetbrains.java.decompiler.struct.ContextUnit;
import org.jetbrains.java.decompiler.struct.IDecompiledData;
import org.jetbrains.java.decompiler.struct.StructClass;
import org.jetbrains.java.decompiler.struct.lazy.LazyLoader;
import org.jetbrains.java.decompiler.util.DataInputFullStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class FernflowerContextUnit extends ContextUnit {
    public static final int TYPE_FOLDER = 0;
    public static final int TYPE_JAR = 1;
    public static final int TYPE_ZIP = 2;

    private final int type;
    private final boolean own;

    private final String archivePath;  // relative path to jar/zip
    private final String filename;     // folder: relative path, archive: file name
    private final IResultSaver resultSaver;
    private final IDecompiledData decompiledData;

    private final List<String> classEntries = new ArrayList<>();  // class file or jar/zip entry
    private final List<String> dirEntries = new ArrayList<>();
    private final List<String[]> otherEntries = new ArrayList<>();

    private List<StructClass> classes = new ArrayList<>();
    private Manifest manifest;

    public FernflowerContextUnit(int type, String archivePath, String filename, boolean own, IResultSaver resultSaver, IDecompiledData decompiledData) {
        super(type, archivePath, filename, own, resultSaver, decompiledData);
        this.type = type;
        this.own = own;
        this.archivePath = archivePath;
        this.filename = filename;
        this.resultSaver = resultSaver;
        this.decompiledData = decompiledData;
    }

    public void save() {
        // 不操作除文件夹内class以外的任何文件
        if (type == TYPE_FOLDER) {// create folder
            // classes
            for (int i = 0; i < classes.size(); i++) {
                StructClass cl = classes.get(i);
                if (!cl.isOwn()) {
                    continue;
                }
                String entryName = decompiledData.getClassEntryName(cl, classEntries.get(i));
                if (entryName != null) {
                    String content = decompiledData.getClassContent(cl);
                    if (content != null) {
                        int[] mapping = null;
                        if (DecompilerContext.getOption(IFernflowerPreferences.BYTECODE_SOURCE_MAPPING)) {
                            mapping = DecompilerContext.getBytecodeSourceMapper().getOriginalLinesMapping();
                        }
                        resultSaver.saveClassFile(filename, cl.qualifiedName, entryName, content, mapping);
                    }
                }
            }
        }
    }

    public void addClass(StructClass cl, String entryName) {
        classes.add(cl);
        classEntries.add(entryName);
    }

    public void addDirEntry(String entry) {
        dirEntries.add(entry);
    }

    public void addOtherEntry(String fullPath, String entry) {
        otherEntries.add(new String[]{fullPath, entry});
    }

    public void reload(LazyLoader loader) throws IOException {
        List<StructClass> lstClasses = new ArrayList<>();

        for (StructClass cl : classes) {
            String oldName = cl.qualifiedName;

            StructClass newCl;
            try (DataInputFullStream in = loader.getClassStream(oldName)) {
                newCl = StructClass.create(in, cl.isOwn(), loader);
            }

            lstClasses.add(newCl);

            LazyLoader.Link lnk = loader.getClassLink(oldName);
            loader.removeClassLink(oldName);
            loader.addClassLink(newCl.qualifiedName, lnk);
        }

        classes = lstClasses;
    }

    public void setManifest(Manifest manifest) {
        this.manifest = manifest;
    }

    public boolean isOwn() {
        return own;
    }

    public List<StructClass> getClasses() {
        return classes;
    }

}
