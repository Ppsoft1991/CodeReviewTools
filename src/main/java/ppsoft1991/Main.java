package ppsoft1991;

import ppsoft1991.Scanner.IScan;
import ppsoft1991.Scanner.find.FindClassScan;
import ppsoft1991.Scanner.unzip.ClearScan;
import ppsoft1991.Scanner.unzip.DecompilerClassScan;
import ppsoft1991.Scanner.unzip.UnzipJarScan;
import ppsoft1991.Scanner.unzip.WarScan;

import java.util.Locale;

public class Main {

    public static Boolean log = false;
    public static IScan scanner = null;

    public static void main( String[] args ) throws Exception {
        ParseCli.parser(args);
    }
}
