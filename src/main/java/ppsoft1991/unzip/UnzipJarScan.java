package ppsoft1991.unzip;

import cn.hutool.core.util.ZipUtil;
import ppsoft1991.IScan;
import ppsoft1991.Main;

import java.io.*;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class UnzipJarScan implements IScan {

    public void scan(String dir, String groupName) throws IOException {
        File d = new File(dir);
        if (!d.isDirectory()) {
            if (d.getName().endsWith(".jar")){
                UnzipJarScan.findGroupIdFromJar(d, groupName);
            }
        }else {
            if (Main.log){
                System.out.println("[+] Scan Dir"+dir+"\n");
            }
            File[] files = d.listFiles();
            assert files != null;
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
                    System.out.println("[+] unzip "+jarFile.getName());
                    unzip(jarFile, Main.unzipPath);
                }
                break;
            }
        }
    }
}
