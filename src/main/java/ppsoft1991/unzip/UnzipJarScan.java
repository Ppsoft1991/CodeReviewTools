package ppsoft1991.unzip;

import cn.hutool.core.util.ZipUtil;
import ppsoft1991.IScan;
import ppsoft1991.Main;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UnzipJarScan implements IScan {

    public void scan(String dir, String groupName) throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(dir))) {
            paths.map(Path::toString).filter(f -> f.endsWith(".jar"))
                    .collect(Collectors.toList())
                    .forEach(c-> {
                        try {
                            UnzipJarScan.findGroupIdFromJar(new File(c), groupName);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void unzip(File jarfile, String path){
        ZipUtil.unzip(jarfile, new File(path));
    }

    public static void findGroupIdFromJar(File jarFile, String prex) throws IOException {
        Enumeration<JarEntry> en = new JarFile(jarFile).entries();
        while (en.hasMoreElements()){
            JarEntry entry = en.nextElement();
            if (entry.getName().endsWith(".class")){
                String name = entry.getName().split("\\.")[0].replace("/",".");
                if (name.matches(prex)){
                    System.out.println("[+] unzip "+jarFile.getName());
                    unzip(jarFile, Main.unzipPath);
                }
                break;
            }
        }
    }
}
