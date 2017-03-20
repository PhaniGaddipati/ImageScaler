package com.centumengineering.imagescaler;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * A simple utility class to scale images. This utility reads images, either
 * from the local file system or an online source, and creates a thumbnail
 * version as well as a "full-size" version.
 *
 * @author Phani Gaddipati
 */
public class ImageScaler {

    private final Options CLI_OPTIONS;

    public ImageScaler() {
        CLI_OPTIONS = CLIOptions.createImageScalerOptions();
    }

    public void run(String[] args) {
        if (args.length == 0) {
            printHelp();
        } else {
            CommandLineParser parser = new DefaultParser();
            try {
                CommandLine line = parser.parse(CLI_OPTIONS, args);
            } catch (ParseException ex) {
                System.err.println("\nFailed to run. " + ex.getMessage());
                System.err.println();
                printHelp();
            }
        }
    }

    private void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("ImageScaler [options] --"
                + CLIOptions.OPTION_THUMB_OUT + " --" 
                + CLIOptions.OPTION_FULL_OUT + " files...", CLI_OPTIONS);
    }

    public static void main(String[] args) {
        new ImageScaler().run(args);
    }
}
