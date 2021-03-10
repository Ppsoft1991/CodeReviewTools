package ppsoft1991.Scanner.unzip;

import ppsoft1991.Scanner.IScan;
import ppsoft1991.decompier.Decompile;

import java.io.File;

public class WarScan implements IScan {
    @Override
    public void scan(String dir, String fileName) throws Exception {
        if(dir.endsWith(".war") || dir.endsWith(".jar")){
            File file = new File(dir);
            File saveFile = new File(dir.substring(0, dir.length()-4)+".src.zip");
            System.out.println("[+]装载war包: "+dir);
            Decompile.doSaveJarDecompiled(file, saveFile);
            System.out.println("反编译成功！");
        }else {
            System.out.println("解压war包模式请指定war包地址");
        }
    }
}
