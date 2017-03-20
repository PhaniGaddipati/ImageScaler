/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.centumengineering.imagescaler;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

/**
 *
 * @author phani
 */
public class CLIOptions {

    public static final String OPTION_INPUT_FILE = "input-file";
    public static final String OPTION_THUMB_OUT = "thumb-out";
    public static final String OPTION_FULL_OUT = "full-out";

    public static final String OPTION_OVERWRITE = "overwrite";
    public static final boolean DEFAULT_OVERWRITE = false;

    public static final String THUMB_WIDTH = "thumb-width";
    public static final int DEFAULT_THUMB_WIDTH = 150;

    public static final String FULL_WIDTH = "full-width";
    public static final int DEFAULT_FULL_WIDTH = 400;

    public static Options createImageScalerOptions() {
        Options options = new Options();

        options.addOption(Option.builder("t")
                .longOpt(THUMB_WIDTH)
                .desc("The width in pixels of the generated thumbnail. Default: "
                        + DEFAULT_THUMB_WIDTH + "px")
                .required(false)
                .hasArg()
                .numberOfArgs(1)
                .type(Integer.TYPE)
                .build());

        options.addOption(Option.builder("f")
                .longOpt(FULL_WIDTH)
                .desc("The width in pixels of the generated full image. Default: "
                        + DEFAULT_FULL_WIDTH + "px")
                .required(false)
                .hasArg()
                .numberOfArgs(1)
                .type(Integer.TYPE)
                .build());

        options.addOption(Option.builder()
                .longOpt(OPTION_OVERWRITE)
                .desc("If the thumbnail or full image exists already, overwrite it.")
                .required(false)
                .build());

        options.addOption(Option.builder("i")
                .longOpt(OPTION_INPUT_FILE)
                .desc("File containing paths of images to process, including both local and online sources."
                        + " If this is provided, the files argument will be ignored.")
                .hasArg()
                .numberOfArgs(1)
                .required(false)
                .build());

        options.addOption(Option.builder("to")
                .longOpt(OPTION_THUMB_OUT)
                .desc("Directory to write the thumbnails")
                .hasArg()
                .numberOfArgs(1)
                .required(true)
                .build());

        options.addOption(Option.builder("fo")
                .longOpt(OPTION_FULL_OUT)
                .desc("Directory to write the full images")
                .hasArg()
                .numberOfArgs(1)
                .required(true)
                .build());

        return options;
    }

}
