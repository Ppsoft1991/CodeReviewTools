package ppsoft1991;

import org.apache.commons.cli.*;
import ppsoft1991.Scanner.IScan;
import ppsoft1991.Scanner.find.FindClassScan;
import ppsoft1991.Scanner.unzip.ClearScan;
import ppsoft1991.Scanner.unzip.DecompilerClassScan;
import ppsoft1991.Scanner.unzip.UnzipJarScan;
import ppsoft1991.Scanner.unzip.WarScan;

public class ParseCli {
    public static IScan scanner = null;
    public static CommandLine cmd = null;

    public static void parser(String[] args) throws Exception {
        String dir = null;
        String name = null;

        Options options = new Options();
        options.addOption("m", "method", true, "[search, unzip, decompiler, clear, all, war]");
        options.addOption("d", "dir", true, "target path");
        options.addOption("n","name",true,"search class file or group name");
        options.addOption("o", "output", true, "output path");
        options.addOption("f", "file", true ,"target file");
        options.addOption("h", "help", false, "print help information");

        CommandLineParser parser = new DefaultParser();
        cmd = parser.parse(options, args);
        HelpFormatter help = new HelpFormatter();

        /*
        *  输出help
        * */

        if (cmd.hasOption("h")){
            System.out.println(Banner.getBanner()+" v1.0 by Ppsoft1991\n");
            help.printHelp("java -jar CodeReviewTools.jar -m <method>", options);
            System.exit(0);
        }

        /*
        *  解析method
        * */
        final String type = cmd.getOptionValue("m");
        switch (type){
            case "search":      scanner = new FindClassScan();                  break;
            case "unzip":       scanner = new UnzipJarScan();                   break;
            case "decompiler":  scanner = new DecompilerClassScan();            break;
            case "clear":       scanner = new ClearScan();                      break;
            case "war":         scanner = new WarScan();                        break;
            case "all":         allCase(args);                                  break;
            default:            parser(new String[]{"-h"});System.exit(0);break;
        }

        if (cmd.hasOption("d")){
            dir = cmd.getOptionValue("d");
            if (cmd.hasOption("o") && cmd.getOptionValue("m").equals("decompiler")){
                dir = cmd.getOptionValue("o");
            }
        }else if(cmd.hasOption("f")){
            dir = cmd.getOptionValue("f");
        }else {
            System.out.println("please set -d or -f");
            System.exit(0);
        }

        if (cmd.hasOption("n")) {
            name = cmd.getOptionValue("name");
        }else if(type.equals("decompiler") ||type.equals("clear")||type.equals("war")){
            name = "";
        }else {
            System.out.println("please set --name");
            System.exit(0);
        }
        scanner.scan(dir, name);
    }

    public static void allCase(String[] args) throws Exception {
        String[] typeArg = {"unzip", "decompiler", "clear"};
        for (String aType: typeArg){
            String method = cmd.getOptionValue("m");
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals(method)){
                    args[i] = aType;
                }
            }
            ParseCli.parser(args);
        }
    }
}
