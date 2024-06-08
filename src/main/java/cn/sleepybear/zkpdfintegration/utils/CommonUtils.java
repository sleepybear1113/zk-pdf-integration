package cn.sleepybear.zkpdfintegration.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2024/05/02 00:10
 */
@Slf4j
public class CommonUtils {
    public static void ensureParentDir(String filename) {
        File file = new File(filename);
        File parentFile = file.getParentFile();
        if (parentFile == null) {
            return;
        }
        if (!parentFile.exists()) {
            if (!parentFile.mkdirs()) {
                System.err.printf("创建文件夹 %s 失败%n", parentFile);
            }
        }
    }

    public static String generateRandomString(int n) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            int index = (int) (Math.random() * str.length());
            sb.append(str.charAt(index));
        }
        return sb.toString();
    }

    public static void deleteFileAndDir(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File f : files) {
                        deleteFileAndDir(f.getAbsolutePath());
                    }
                }
            }
            if (!file.delete()) {
                log.warn("删除文件 {} 失败", file.getName());
            }
        }
    }

    public static void deleteInnerFilesAndDir(String path) {
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    deleteFileAndDir(f.getAbsolutePath());
                }
            }
        }
    }
}
