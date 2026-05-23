package org.lib;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class BuildInformation {
    private static final Properties props = new Properties();

    static {
        try (InputStream input =
                     BuildInformation.class.getClassLoader()
                             .getResourceAsStream("build.properties")) {

            props.load(input);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }

    public static String getLicense() {
        try {
            InputStream inputStream = BuildInformation.class.getResourceAsStream("/LICENSE");
            InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder sb = new StringBuilder();
            for (String line; (line = reader.readLine()) != null; ) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}