package com.centumengineering.imagescaler;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * This class contains the CLI option definitions for ImageScaler,
 * for use with the Apache commons-cli package.
 * 
 * @author Phani Gaddipati
 */
public class CLIOptions {

    public static final String OPTION_IN_FILE = "in-file";
    public static final String OPTION_IN_FILE_NET = "online";    

    public static final String OPTION_IN_DIR = "in-dir";
    public static final String OPTION_DIR_RECURSIVE = "recursive";

    public static final String OPTION_THUMB_OUT = "thumb-out";
    public static final String OPTION_FULL_OUT = "full-out";    

    public static final String OPTION_THUMB_WIDTH = "thumb-width";
    public static final int DEFAULT_THUMB_WIDTH = 150;

    public static final String OPTION_FULL_WIDTH = "full-width";
    public static final int DEFAULT_FULL_WIDTH = 400;

    public static Options createImageScalerOptions() {
        Options options = new Options();

        options.addOption(Option.builder("t")
                .longOpt(OPTION_THUMB_WIDTH)
                .desc("The width in pixels of the generated thumbnail. Default: "
                        + DEFAULT_THUMB_WIDTH + "px")
                .required(false)
                .hasArg()
                .numberOfArgs(1)
                .type(Integer.TYPE)
                .build());

        options.addOption(Option.builder("f")
                .longOpt(OPTION_FULL_WIDTH)
                .desc("The width in pixels of the generated full image. Default: "
                        + DEFAULT_FULL_WIDTH + "px")
                .required(false)
                .hasArg()
                .numberOfArgs(1)
                .type(Integer.TYPE)
                .build());

        options.addOption(Option.builder("i")
                .longOpt(OPTION_IN_FILE)
                .desc("File containing paths of images to process, separated by newline."
                        + " By default, the paths will be treated as local paths."
                        + " Use the --" + OPTION_IN_FILE_NET
                        + " switch if the paths are to be downloaded to the working directory first."
                        + " Only this or " + OPTION_IN_DIR + " should be provided, not both.")
                .hasArg()
                .numberOfArgs(1)
                .required(false)
                .build());

        options.addOption(Option.builder("d")
                .longOpt(OPTION_IN_DIR)
                .desc("Directory containing images to process. "
                        + " Only this or " + OPTION_IN_FILE + " should be provided, not both.")
                .hasArg()
                .numberOfArgs(1)
                .required(false)
                .build());

        options.addOption(Option.builder("r")
                .longOpt(OPTION_DIR_RECURSIVE)
                .desc("If using " + OPTION_IN_DIR + ", whether to scan directories recursively. Default is false.")
                .required(false)
                .build());

        options.addOption(Option.builder("o")
                .longOpt(OPTION_IN_FILE_NET)
                .desc("To be used with the --" + OPTION_IN_FILE + " switch to indicate that the paths are online sources")
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
