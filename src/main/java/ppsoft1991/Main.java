package ppsoft1991;

import java.io.IOException;

public class Main
{
    public static void main( String[] args ) throws IOException {
        final String className = args[0];
        final String[] argList = args[1].split(",");
        System.out.println("==Start find class mode==\n");
        for (String jarPath:argList){
            FindClass.scan(jarPath, className);
        }
    }
}
