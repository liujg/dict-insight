/**
 * @(#)FileUtils.java, 2014年9月15日. Copyright 2014 Yodao, Inc. All rights
 * reserved. YODAO PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package dictinsight.utils.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author jasonliu
 */
public class FileUtils {

    public static String constructPath(String... args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            sb.append(args[i]);
            if (i != args.length - 1)
                sb.append(File.separator);
        }
        return sb.toString();
    }

    public static Integer getIntegerConfig(String filename, String confKey,
            Integer defaultResult) {
        String value = loadConfFile(filename, confKey);
        try {
            return Integer.valueOf(value);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultResult;
        }
    }

    public static Boolean getBooleanConfig(String filename, String confKey,
            Boolean defaultResult) {
        String value = loadConfFile(filename, confKey);
        try {
            return Boolean.valueOf(value);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultResult;
        }
    }

    public static String getStringConfig(String filename, String confKey,
            String defaultResult) {
        String value = loadConfFile(filename, confKey);
        return value == null ? defaultResult : value;
    }

    public static String loadConfFile(String filename, String key) {
        BufferedReader fread = null;
        try {
            fread = new BufferedReader(new InputStreamReader(
                    new FileInputStream(filename), "UTF-8"));
            String line;
            while ((line = fread.readLine()) != null) {
                if (line.startsWith("#"))
                    continue;
                if (line.startsWith(key + "="))
                    return line.substring(line.indexOf('=') + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(fread!=null)
                    fread.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }
}
