package ppsoft1991.Scanner.unzip;

import ppsoft1991.Scanner.IScan;
import ppsoft1991.Main;
import ppsoft1991.decompier.Decompile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DecompilerClassScan implements IScan {

    int num = 0;
    @Override
    public void scan(String dir, String s) throws Exception {
        if (!dir.endsWith("/")){
            dir = dir +"/";
        }
        //Loader loader = new ppsoft1991.decompier.Loader(new File(dir));
        ExecutorService executorService = Executors.newFixedThreadPool(200);
        System.out.println("反编译模式开始");
            while (true){
                try (Stream<Path> paths = Files.walk(Paths.get(dir))) {
                    List<String> collect = paths.map(Path::toString).filter(f -> f.endsWith(".class"))
                            .collect(Collectors.toList());
                    int collectLength = collect.size();
                    NumberFormat format = NumberFormat.getInstance();
                    format.setMaximumFractionDigits(0);
                    for (String value : collect) {
                        File decompileFile = new File(value);
                        File saveFile = new File(decompileFile.getAbsolutePath().replace(".class", ".java"));
                        executorService.execute(() -> {
                            try {
                                Decompile.doSaveClassDecompiled(decompileFile, saveFile);
                                System.out.println(num );
                                if (num % 100 == 0) {
                                    System.out.println("完成了: " + format.format((float) num / (float) collectLength * 100) + "%");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        //decompiler(new File(collect.get(i)), dir, loader);
                    }
                    //.forEach(c -> decompiler(new File(c), finalDir));
                } catch (IOException e) {e.printStackTrace();
                    e.printStackTrace();
                }
            if (executorService.isTerminated()){
                System.out.println("子线程全部结束");
                break;
            }
            }
    }
}
