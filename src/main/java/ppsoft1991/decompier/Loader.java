package ppsoft1991.decompier;

import org.jd.core.v1.api.loader.LoaderException;

import java.io.*;

public class Loader implements org.jd.core.v1.api.loader.Loader {
    protected File base;


    @Override
    public byte[] load(String internalName) throws LoaderException {
        File file = new File(internalName);

        if (file.exists()) {
            try (FileInputStream in= new FileInputStream(file); ByteArrayOutputStream out=new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int read = in.read(buffer);

                while (read > 0) {
                    out.write(buffer, 0, read);
                    read = in.read(buffer);
                }

                return out.toByteArray();
            } catch (Exception e) {
                throw new LoaderException(e);
            }
        } else {
            return null;
        }
    }

    @Override
    public boolean canLoad(String internalName) {
        return newFile(internalName).exists();
    }

    protected File newFile(String internalName) {
        return new File(internalName);
    }
}