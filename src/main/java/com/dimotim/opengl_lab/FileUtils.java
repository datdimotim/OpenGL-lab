package com.dimotim.opengl_lab;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileUtils {
    public static String readTextFromRaw(String path) {
        StringBuilder stringBuilder = new StringBuilder();
        try(BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(FileUtils.class.getResourceAsStream(path)))){
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\r\n");
            }
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
        return stringBuilder.toString();
    }
}
