package ppsoft1991.Scanner.find;

import ppsoft1991.Scanner.IScan;
import ppsoft1991.Main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FindClassScan implements IScan {

    @Override
    public void scan(String dir, String fileName) throws Exception {
        File d = new File(dir);
        if (!d.isDirectory()) {
            if (d.getName().endsWith(".jar")){
                FindClassScan.findClassFromJar(d, fileName);
            }
        }else {
            if (Main.log){
                System.out.println("[+] Scan Dir"+dir+"\n");
            }
            File[] files = d.listFiles();
            assert files != null;
            for (File f:files){
                this.scan(f.getAbsolutePath(), fileName);
            }
        }
    }

    public static void findClassFromJar(File jarFile,String prex) throws IOException {
        List<String> clssName = new ArrayList<>();
        try {
            Enumeration<JarEntry> en = new JarFile(jarFile).entries();
            while (en.hasMoreElements()){
                JarEntry je = en.nextElement();
                String name = je.getName().split("\\.")[0].replace("/",".");
                if (name.matches(prex)&&je.getName().endsWith(".class")){
                    String clss = name.replace(".class", "").replaceAll("/", ".");
                    clssName.add(clss);
                }
            }
            if (!clssName.isEmpty()){
                System.out.println("=== "+ jarFile.getName() + ": "+jarFile.getPath() +" ===");
                for (String clsName: clssName){
                    System.out.println("[!]"+clsName);
                }
            }
        }catch (Exception e){
            System.out.println(jarFile.getAbsolutePath()+" 搜索失败");
            System.out.println(e);
        }

    }
}

