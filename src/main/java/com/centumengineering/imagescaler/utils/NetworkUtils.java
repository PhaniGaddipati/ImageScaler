/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.centumengineering.imagescaler.utils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author phani
 */
public class NetworkUtils {

    /**
     * Attempts to download all of the urls passed into the working directory.
     * The successfully downloaded urls are added to the return set of
     * downloaded files
     *
     * @param urls
     * @return An array of local files that the images were downloaded to
     */
    public static File[] downloadFilesByURLs(String[] urls) {
        System.out.println("Attempting to download " + urls.length + " files");
        List<File> newPaths = new LinkedList<>();
        for (String path : urls) {
            try {
                URL url = new URL(path);
                File downloadedFile = new File(FilenameUtils.getName(url.getPath()));
                if (downloadedFile.exists()) {
                    System.out.println(downloadedFile.getAbsolutePath() + " already exists. Skipping.");
                } else {
                    FileUtils.copyURLToFile(url, downloadedFile);                    
                    System.out.println("Downloaded " + downloadedFile.getAbsolutePath());
                }
                newPaths.add(downloadedFile);
            } catch (MalformedURLException ex) {
                System.out.println("Invalid URL, ignoring \"" + path + "\"");
            } catch (IOException ex) {
                System.out.println("Failed to download a file, ignoring \"" + path + "\"");
            } catch (Exception ex) {
                System.out.println("Unexpected error. Failed to download a file, ignoring \"" + path + "\"");
            }
        }
        return newPaths.toArray(new File[newPaths.size()]);
    }

}
