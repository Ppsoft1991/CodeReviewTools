package ppsoft1991.unzip;

import org.jd.core.v1.ClassFileToJavaSourceDecompiler;
import org.jd.core.v1.api.loader.Loader;
import org.jd.core.v1.api.printer.Printer;
import ppsoft1991.IScan;
import ppsoft1991.Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DecompilerClassScan implements IScan {
    @Override
    public void scan(String dir, String fileName) throws Exception {
        File d = new File(dir);
        if (!d.isDirectory()) {
            if (d.getName().endsWith(".class")){
                decompiler(d);
            }
        }else {
            if (Main.log){
                System.out.println("[+] Scan Dir"+dir+"\n");
            }
            File[] files = d.listFiles();
            assert files != null;
            for (File f:files){
                this.scan(f.getAbsolutePath(), "1");
            }
        }
    }

    public static void decompiler(File clazzFile) throws Exception {
        String path = clazzFile.getAbsolutePath();
        System.out.println(clazzFile.getAbsolutePath());
        if (path.endsWith(".class")){
            Loader loader = new ppsoft1991.decompier.Loader();
            Printer printer = new ppsoft1991.decompier.Printer();
            ClassFileToJavaSourceDecompiler decompiler = new ClassFileToJavaSourceDecompiler();
            decompiler.decompile(loader, printer, path);
            String source = printer.toString();
            FileWriter fileWriter = new FileWriter(path.replace(".class",".java"));
            fileWriter.write(source);
            fileWriter.close();
        }
    }
}
