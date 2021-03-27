package ppsoft1991.decompier;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import com.strobel.assembler.metadata.*;
import com.strobel.core.StringUtilities;
import com.strobel.decompiler.DecompilationOptions;
import com.strobel.decompiler.DecompilerSettings;
import com.strobel.decompiler.PlainTextOutput;
import us.deathmarine.luyten.*;

public class Decompile {
    public static DecompilerSettings settings = ConfigSaver.getLoadedInstance().getDecompilerSettings();
    public static DecompilationOptions decompilationOptions = new DecompilationOptions();
    public static LuytenTypeLoader typeLoader = new LuytenTypeLoader();
    public static MetadataSystem metadataSystem = new MetadataSystem(typeLoader);
    public static boolean isUnicodeEnabled = decompilationOptions.getSettings().isUnicodeOutputEnabled();

    static {
        decompilationOptions.setSettings(settings);
        decompilationOptions.setFullDecompilation(true);
    }

    public static void doSaveClassDecompiled(File inFile, File outFile) throws Exception {
        DecompilerSettings settings = Decompile.settings;
        LuytenTypeLoader typeLoader = new LuytenTypeLoader();
        MetadataSystem metadataSystem = new MetadataSystem(typeLoader);
        TypeReference type = metadataSystem.lookupType(inFile.getCanonicalPath());

        DecompilationOptions decompilationOptions = new DecompilationOptions();
        decompilationOptions.setSettings(settings);
        decompilationOptions.setFullDecompilation(true);

        boolean isUnicodeEnabled = decompilationOptions.getSettings().isUnicodeOutputEnabled();
        TypeDefinition resolvedType = null;
        if (type == null || ((resolvedType = type.resolve()) == null)) {
            throw new Exception("Unable to resolve type.");
        }
        StringWriter stringwriter = new StringWriter();
        PlainTextOutput plainTextOutput = new PlainTextOutput(stringwriter);
        plainTextOutput.setUnicodeOutputEnabled(isUnicodeEnabled);
        settings.getLanguage().decompileType(resolvedType, plainTextOutput, decompilationOptions);
        String decompiledSource = stringwriter.toString();

        //System.out.println("[SaveAll]: " + inFile.getName() + " -> " + outFile.getName());
        try (FileOutputStream fos = new FileOutputStream(outFile);
             OutputStreamWriter writer = isUnicodeEnabled ? new OutputStreamWriter(fos, StandardCharsets.UTF_8)
                     : new OutputStreamWriter(fos);
             BufferedWriter bw = new BufferedWriter(writer);) {
            bw.write(decompiledSource);
            bw.flush();
        }
    }

    public static void doSaveJarDecompiled(File inFile, File outFile) throws Exception {
        try (JarFile jfile = new JarFile(inFile);
             FileOutputStream dest = new FileOutputStream(outFile);
             BufferedOutputStream buffDest = new BufferedOutputStream(dest);
             ZipOutputStream out = new ZipOutputStream(buffDest);) {
            byte[] data = new byte[1024];
            //DecompilerSettings settings = ConfigSaver.getLoadedInstance().getDecompilerSettings();
            //LuytenTypeLoader typeLoader = new LuytenTypeLoader();
            //MetadataSystem metadataSystem = new MetadataSystem(typeLoader);
            ITypeLoader jarLoader = new JarTypeLoader(jfile);
            typeLoader.getTypeLoaders().add(jarLoader);

            //DecompilationOptions decompilationOptions = new DecompilationOptions();


            List<String> mass = null;
            JarEntryFilter jarEntryFilter = new JarEntryFilter(jfile);
            LuytenPreferences luytenPrefs = ConfigSaver.getLoadedInstance().getLuytenPreferences();
            if (luytenPrefs.isFilterOutInnerClassEntries()) {
                mass = jarEntryFilter.getEntriesWithoutInnerClasses();
            } else {
                mass = jarEntryFilter.getAllEntriesFromJar();
            }

            Enumeration<JarEntry> ent = jfile.entries();
            Set<String> history = new HashSet<String>();
            /*
            *进度条
             */
            int min = 0;
            final int max = jfile.size();
            NumberFormat format = NumberFormat.getInstance();
            format.setMaximumFractionDigits(0);
            while (ent.hasMoreElements() ) {
                min++;
                if ((min%300==0)){
                    System.out.println(format.format((float) min/ (float)max*100)+"%");
                }
                JarEntry entry = ent.nextElement();
                if (!mass.contains(entry.getName()))
                    continue;
                if (entry.getName().endsWith(".class")) {
                    JarEntry etn = new JarEntry(entry.getName().replace(".class", ".java"));
                    //System.out.println("[SaveAll]: " + etn.getName() + " -> " + outFile.getName());
                    if (history.add(etn.getName())) {
                        out.putNextEntry(etn);
                        try {
                            //boolean isUnicodeEnabled = decompilationOptions.getSettings().isUnicodeOutputEnabled();
                            String internalName = StringUtilities.removeRight(entry.getName(), ".class");
                            TypeReference type = metadataSystem.lookupType(internalName);
                            TypeDefinition resolvedType = null;
                            if ((type == null) || ((resolvedType = type.resolve()) == null)) {
                                throw new Exception("Unable to resolve type.");
                            }
                            Writer writer = isUnicodeEnabled ? new OutputStreamWriter(out, StandardCharsets.UTF_8)
                                    : new OutputStreamWriter(out);
                            PlainTextOutput plainTextOutput = new PlainTextOutput(writer);
                            plainTextOutput.setUnicodeOutputEnabled(isUnicodeEnabled);
                            settings.getLanguage().decompileType(resolvedType, plainTextOutput, decompilationOptions);
                            writer.flush();
                        } catch (Exception e) {
                            Luyten.showExceptionDialog("Unable to Decompile file!\nSkipping file...", e);
                        } finally {
                            out.closeEntry();
                        }
                    }
                } else {
                    try {
                        JarEntry etn = new JarEntry(entry.getName());
                        if (history.add(etn.getName())) {
                            out.putNextEntry(etn);
                            try {
                                InputStream in = jfile.getInputStream(etn);
                                    try {
                                        int count;
                                        while ((count = in.read(data, 0, 1024)) != -1) {
                                            out.write(data, 0, count);
                                        }
                                        in.close();
                                    }catch (Exception e){
                                        System.out.println("文件"+etn+"跳过");
                                    }
                            } finally {
                                out.closeEntry();
                            }
                        }
                    } catch (ZipException ze) {
                        if (!ze.getMessage().contains("duplicate")) {
                            throw ze;
                        }
                    }
                }
            }
        }
    }
}
