/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.centumengineering.imagescaler.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author phani
 */
public class FileUtils {
    
    /**
     * Loads paths from a file, separated by a newline
     * @param file The file to read
     * @return An array of all of the paths in the file
     */
    public static String[] loadPathsFromFile(File file) throws FileNotFoundException, IOException{
        List<String> paths = new LinkedList<String>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        while((line = reader.readLine()) != null){
            if(!"".equals(line.trim())){
                paths.add(line.trim());
            }
        }
        return paths.toArray(new String[paths.size()]);
    }
    
}
