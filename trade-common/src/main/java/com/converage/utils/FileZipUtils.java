package com.converage.utils;


import com.converage.exception.BusinessException;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileZipUtils {
    private static final int BUFFER_SIZE = 2 * 1024;

    /**
     * 将存放在sourceFilePath目录下的源文件，打包成fileName名称的zip文件，并存放到zipFilePath路径下
     *
     * @param sourceFilePath :待压缩的文件路径
     * @param zipFilePath    :压缩后存放路径
     * @param fileName       :压缩后文件的名称
     * @return
     */
    public static void fileToZip(String sourceFilePath, String zipFilePath, String fileName) {
        File sourceFile = new File(sourceFilePath);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        ZipOutputStream zos = null;

        if (!sourceFile.exists()) {
            throw new BusinessException("未找到原文件夹");
        }
        String base = sourceFile.getName();

        try {
            File zipFile = new File(zipFilePath + "/" + fileName + ".zip");
            fos = new FileOutputStream(zipFile);
            zos = new ZipOutputStream(new BufferedOutputStream(fos));
            compressZip(zos, sourceFile, fis, bis, base);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            //关闭流
            try {
                if (null != bis) bis.close();
                if (null != fis) fis.close();
                if (null != zos) zos.close();
                if (null != fos) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    private static void compressZip(ZipOutputStream zos, File file, FileInputStream fis, BufferedInputStream bis, String base) throws IOException {

        if (file.isDirectory()) {
            File sourceFiles[] = file.listFiles();
            for (int i = 0; i < sourceFiles.length; i++) {
                File sourceFile = sourceFiles[i];
                if (sourceFile.isDirectory()) {
                    compressZip(zos, sourceFile, fis, bis, base + File.separator + sourceFile.getName());
                } else {
                    zip(zos, sourceFile, fis, bis, base);
                }
            }
        } else {
            zip(zos, file, fis, bis, base);
        }

    }

    private static void zip(ZipOutputStream zos, File file, FileInputStream fis, BufferedInputStream bis, String base) throws IOException {
        byte[] bufs = new byte[1024 * 10];
        //创建ZIP实体，并添加进压缩包
        ZipEntry zipEntry = new ZipEntry(base + File.separator + file.getName());
        zos.putNextEntry(zipEntry);
        //读取待压缩的文件并写进压缩包里
        fis = new FileInputStream(file);
        bis = new BufferedInputStream(fis, 1024 * 10);
        int read;
        while ((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
            zos.write(bufs, 0, read);
        }
    }
}
