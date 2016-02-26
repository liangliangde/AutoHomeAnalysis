package com.IO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by llei on 16-2-26.
 */
public class IOProcess {
    public static void writeFile(String filePath, String content) throws IOException {
        FileOutputStream out= new FileOutputStream(filePath,false);
        out.write(content.getBytes());
        out.close();
    }
}
