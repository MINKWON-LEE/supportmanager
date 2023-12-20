package com.mobigen.snet.supportagent.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by osujin12 on 2016. 4. 22..
 */
public class ZipUtil {

    public static void makeZip(List<File> files , String zipFileName) throws Exception {

        byte[] buf = new byte[4096];

        try {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));

            for (int i=0; i<files.size(); i++) {
                FileInputStream in = new FileInputStream(files.get(i).toString());
                Path p = Paths.get(files.get(i).toString());
                String fileName = p.getFileName().toString();

                ZipEntry ze = new ZipEntry(fileName);
                out.putNextEntry(ze);

                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                out.closeEntry();
                in.close();
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
