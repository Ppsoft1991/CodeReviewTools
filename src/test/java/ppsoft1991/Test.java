package ppsoft1991;

import java.io.File;

public class Test {
    public static void main(String[] args) throws Exception {
        File f = new File("/tmp/nc/nc/bs/framework/adaptor/IHttpServletAdaptor.class");
        System.out.println(f.exists());
    }
}
