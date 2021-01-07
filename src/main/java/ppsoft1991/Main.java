package ppsoft1991;

import ppsoft1991.find.FindClassScan;
import ppsoft1991.unzip.ClearScan;
import ppsoft1991.unzip.DecompilerClassScan;
import ppsoft1991.unzip.UnzipJarScan;

import java.util.Locale;

public class Main {

    public static Boolean log = false;
    public static String unzipPath;
    public static IScan scanner = null;

    public static void main( String[] args ) throws Exception {
        final String type = args[0].toLowerCase(Locale.ROOT);
        final String className = args[1];
        final String[] argList = args[2].split(",");

        if (args.length>3){
            unzipPath = args[3];
        }

        switch (type){
            case "search":
                scanner = new FindClassScan();
                break;
            case "unzip":
                scanner = new UnzipJarScan();
                break;
            case "decompiler":
                scanner = new DecompilerClassScan();
                break;
            case "clear":
                scanner = new ClearScan();
                break;
            case "all":
                String[] typeArg = {"unzip", "decompiler", "clear"};
                for (String aType: typeArg){
                    Main.main(new String[]{aType, className, args[2], unzipPath});
                    Main.main(new String[]{aType, "1", unzipPath});
                    Main.main(new String[]{aType, "1", unzipPath});
                }
                break;
        }
        for (String jarPath:argList){
            scanner.scan(jarPath, className);
        }
    }
}
