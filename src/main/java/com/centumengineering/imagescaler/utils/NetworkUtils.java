package com.centumengineering.imagescaler.utils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * Basic utilities for dealing with online files.
 * 
 * @author Phani Gaddipati
 */
public class NetworkUtils {
    
    private static final String DOWNLOAD_DIR = "net";

    /**
     * Attempts to download all of the urls passed into the net directory.
     * The successfully downloaded urls are added to the return set of
     * downloaded files
     *
     * @param urls
     * @return An array of local files that the images were downloaded to
     */
    public static File[] downloadFilesByURLs(String[] urls) {
        System.out.println("Attempting to download " + urls.length + " files");
        List<File> newPaths = new LinkedList<>();
        File downloadDir = new File(DOWNLOAD_DIR);
        downloadDir.mkdirs();
        
        for (String path : urls) {
            try {
                URL url = new URL(path);
                File downloadedFile = new File(downloadDir, FilenameUtils.getName(url.getPath()));
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
