package ppsoft1991.unzip;

import ppsoft1991.IScan;
import ppsoft1991.Main;
import ppsoft1991.find.FindClassScan;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClearScan implements IScan {
    @Override
    public void scan(String dir, String fileName) throws Exception {
        try (Stream<Path> paths = Files.walk(Paths.get(dir))) {
            paths.map(Path::toString).filter(f -> f.endsWith(".class"))
                    .collect(Collectors.toList())
                    .forEach(c-> (new File(c)).delete());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
