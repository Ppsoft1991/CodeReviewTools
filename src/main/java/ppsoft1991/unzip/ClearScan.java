package ppsoft1991.unzip;

import ppsoft1991.IScan;
import ppsoft1991.Main;
import ppsoft1991.find.FindClassScan;

import java.io.File;

public class ClearScan implements IScan {
    @Override
    public void scan(String dir, String fileName) throws Exception {
        File d = new File(dir);
        if (!d.isDirectory()) {
            if (d.getName().endsWith(".class")){
                d.delete();
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
}
