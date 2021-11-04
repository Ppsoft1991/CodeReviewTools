package ppsoft1991.Scanner.unzip;

import org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler;
import ppsoft1991.Scanner.IScan;

public class DecompilerFernFlowerScan implements IScan {
    @Override
    public void scan(String dir, String fileName) throws Exception {
        if (!dir.endsWith("/")){
            dir = dir +"/";
        }
        System.out.println("反编译开始");
        System.out.println("目标目录: "+dir);
        String[] args = new String[]{"-hes=0","-hdc=0","-log=ERROR", dir, dir};
        ConsoleDecompiler.main(args);
        System.out.println("反编译结束");
    }

    public static void main(String[] args) throws Exception {
        new DecompilerFernFlowerScan().scan("/tmp/TongWeb", "");
    }
}
