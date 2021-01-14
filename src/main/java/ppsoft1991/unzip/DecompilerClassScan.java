package ppsoft1991.unzip;

import org.jd.core.v1.ClassFileToJavaSourceDecompiler;
import org.jd.core.v1.api.loader.Loader;
import org.jd.core.v1.api.printer.Printer;
import ppsoft1991.IScan;
import ppsoft1991.Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DecompilerClassScan implements IScan {
    @Override
    public void scan(String dir, String s) throws Exception {
        if (!dir.endsWith("/")){
            dir = dir +"/";
        }
        try (Stream<Path> paths = Files.walk(Paths.get(dir))) {
            List<String> collect = paths.map(Path::toString).filter(f -> f.endsWith(".class"))
                    .collect(Collectors.toList());
            int collectLength = collect.size();
            int part = collectLength/100;
            int aPart = part;
            int partNum = 0;
            for (int i = 0; i < collectLength; i++) {
                if (aPart == i){
                    System.out.println("完成了: "+partNum+"%");
                    partNum++;
                    aPart = aPart + part;
                }
                decompiler(new File(collect.get(i)), dir);
            }
            //.forEach(c -> decompiler(new File(c), finalDir));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void decompiler(File clazzFile, String base){
        int baseLength = base.length();
        String path = clazzFile.getAbsolutePath();
        if (Main.log){
            System.out.println(clazzFile.getAbsolutePath());
        }
        path = path.substring(baseLength);
        if (path.endsWith(".class")){
            Loader loader = new ppsoft1991.decompier.Loader(new File(base));
            Printer printer = new ppsoft1991.decompier.Printer();
            ClassFileToJavaSourceDecompiler decompiler = new ClassFileToJavaSourceDecompiler();
            try {
                decompiler.decompile(loader, printer, path);
                String source = printer.toString();
                FileWriter fileWriter = new FileWriter(clazzFile.getAbsolutePath().replace(".class",".java"));
                fileWriter.write(source);
                fileWriter.close();
            }catch (Exception e){
                System.out.println("[-] 文件："+clazzFile.getName()+"反编译失败！");
            }

        }
    }
}
