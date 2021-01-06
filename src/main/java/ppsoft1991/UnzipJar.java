package ppsoft1991;

import cn.hutool.core.util.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class UnzipJar implements IScan{

    public void scan(String dir, String groupName) throws IOException {
        File d = new File(dir);
        if (!d.isDirectory()) {
            if (d.getName().endsWith(".jar")){
                UnzipJar.findGroupIdFromJar(d, groupName);
            }
        }else {
            if (Main.log){
                System.out.println("[+] Scan Dir"+dir+"\n");
            }
            File[] files = d.listFiles();
            for (File f:files){
                this.scan(f.getAbsolutePath(), groupName);
            }
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
                    unzip(jarFile, Main.unzipPath);
                }
                break;
            }
        }
    }
}
