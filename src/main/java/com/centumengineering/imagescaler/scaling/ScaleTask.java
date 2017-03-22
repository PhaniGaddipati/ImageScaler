/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.centumengineering.imagescaler.scaling;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;

/**
 *
 * @author Phani
 */
public class ScaleTask {

    private final File file;
    private final int thumbW;
    private final int fullW;
    private final File thumbDir;
    private final File fullDir;

    public ScaleTask(File file, int thumbW, int fullWW, File thumbDir, File fullDir) {
        this.file = file;
        this.thumbW = thumbW;
        this.fullW = fullWW;
        this.thumbDir = thumbDir;
        this.fullDir = fullDir;
    }

    public ScaleResult run() {
        long startTime = System.currentTimeMillis();
        File thumbDest = new File(thumbDir.getAbsoluteFile(), file.getName());
        File fullDest = new File(fullDir.getAbsoluteFile(), file.getName());
        String format = FilenameUtils.getExtension(file.getAbsolutePath());

        try {
            BufferedImage img = ImageIO.read(file);
            BufferedImage thumbnail = Scalr.resize(img, thumbW,
                    (int) ((((float) thumbW) / img.getWidth()) * img.getHeight()));
            BufferedImage full = Scalr.resize(img, fullW,
                    (int) ((((float) fullW) / img.getWidth()) * img.getHeight()));
            ImageIO.write(thumbnail, format, thumbDest);
            ImageIO.write(full, format, fullDest);
        } catch (IOException ex) {
            // Complete exceptionally
            throw new RuntimeException(ex);
            //Thread.currentThread().interrupt();
        }

        long endTime = System.currentTimeMillis();
        return new ScaleResult(startTime, endTime, FileUtils.sizeOf(file),
                FileUtils.sizeOf(thumbDest), FileUtils.sizeOf(fullDest), file);
    }

    public static CompletableFuture<ScaleResult> newCompletableFuture(File f,
            int thumbW, int fullW, File thumbDir, File fullDir) {
        return CompletableFuture.supplyAsync(new ScaleTask(f, thumbW, fullW, thumbDir, fullDir)::run);
    }

    public static ScaleResult printTaskSummary(ScaleResult result) {
        System.out.println(result.getFile().getName()
                + "\t Original: " + result.getStartBytes() + " bytes\t Thumbnail: "
                + result.getThumbBytes() + " bytes\t Full: " + result.getFullBytes()
                + " bytes. Time: " + result.getProcessingTimeInMillis() + "ms");
        return result;
    }
}
