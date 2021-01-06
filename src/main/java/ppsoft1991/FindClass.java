package ppsoft1991;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

public class FindClass {

    public static void scan(String dir, String fileName) throws IOException {
        File d = new File(dir);
        if (!d.isDirectory()) {
            if (d.getName().endsWith(".jar")){
                System.out.println("====Scan "+d.getName()+" ====");
                FindClass.findClassFromJar(new JarFile(d), fileName);
            }
        }else {
            System.out.println("[+] Scan Dir"+dir+"\n");
            File[] files = d.listFiles();
            for (File f:files){
                FindClass.scan(f.getAbsolutePath(), fileName);
            }
        }
    }

    public static void findClassFromJar(JarFile jarFile,String prex){
        Enumeration<JarEntry> en = jarFile.entries();
        while (en.hasMoreElements()){
            JarEntry je = en.nextElement();
            String name = je.getName().split("\\.")[0].replace("/",".");
            if (name.matches(prex)){
                String clss = name.replace(".class", "").replaceAll("/", ".");
                System.out.println("[!]"+clss);
            }
        }
    }
}

