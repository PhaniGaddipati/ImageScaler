/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.centumengineering.imagescaler.scaling;

import java.io.File;

/**
 * A class to hold the result params of a ScaleTask.
 *
 * @author Phani Gaddipati
 */
public class ScaleResult {

    private final long startTime;
    private final long endTime;
    private final long startBytes;
    private final long thumbBytes;
    private final long fullBytes;
    private final File file;

    public ScaleResult(long startTime, long endTime, long startBytes, long thumbBytes, long fullBytes, File file) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.startBytes = startBytes;
        this.thumbBytes = thumbBytes;
        this.fullBytes = fullBytes;
        this.file = file;
    }

    public long getStartBytes() {
        return startBytes;
    }

    public long getThumbBytes() {
        return thumbBytes;
    }

    public long getFullBytes() {
        return fullBytes;
    }

    public File getFile() {
        return file;
    }

    public long getProcessingTimeInMillis() {
        return endTime - startTime;
    }

    /**
     * Prints a basic summary based on the ScaleResult
     *
     * @param result
     * @return
     */
    public static ScaleResult printTaskSummary(ScaleResult result) {
        System.out.println(result.getFile().getName()
                + "\t Original: " + result.getStartBytes() + " bytes\t Thumbnail: "
                + result.getThumbBytes() + " bytes\t Full: " + result.getFullBytes()
                + " bytes. Time: " + result.getProcessingTimeInMillis() + "ms");
        return result;
    }
}
