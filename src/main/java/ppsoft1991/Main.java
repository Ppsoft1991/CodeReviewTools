package ppsoft1991;

import java.io.IOException;

public class Main {

    public static Boolean log = false;
    public static String unzipPath;
    public static IScan scanner = null;

    public static void main( String[] args ) throws IOException {
        final String type = args[0];
        final String className = args[1];
        final String[] argList = args[2].split(",");

        if (args.length>3){
            unzipPath = args[3];
        }
        if (type.contains("findcls")){
            scanner = new FindClass();
        }else if (type.contains("unzip")){
            scanner = new UnzipJar();
        }
        for (String jarPath:argList){
            scanner.scan(jarPath, className);
        }
    }
}
