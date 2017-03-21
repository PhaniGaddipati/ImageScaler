package com.centumengineering.imagescaler;

import com.centumengineering.imagescaler.utils.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
                CommandLine opts = parser.parse(CLI_OPTIONS, args);
                // Succesfully parsed args, do some extra arg validation
                if (validateArgs(opts)) {
                    // We can start the processing
                    int thumbW = CLIOptions.DEFAULT_THUMB_WIDTH;
                    if (opts.hasOption(CLIOptions.OPTION_THUMB_WIDTH)) {
                        thumbW = (Integer) opts.getParsedOptionValue(CLIOptions.OPTION_THUMB_WIDTH);
                    }
                    int fullW = CLIOptions.DEFAULT_FULL_WIDTH;
                    if (opts.hasOption(CLIOptions.OPTION_FULL_WIDTH)) {
                        fullW = (Integer) opts.getParsedOptionValue(CLIOptions.OPTION_FULL_WIDTH);
                    }
                    File thumbFile = new File(opts.getOptionValue(CLIOptions.OPTION_THUMB_OUT));
                    File fullFile = new File(opts.getOptionValue(CLIOptions.OPTION_FULL_OUT));
                    String[] paths;
                    if (opts.hasOption(CLIOptions.OPTION_IN_FILE)) {
                        File inFile = new File(opts.getOptionValue(CLIOptions.OPTION_IN_FILE));
                        try {
                            paths = FileUtils.loadPathsFromFile(inFile);
                        } catch (IOException ex) {
                            System.err.println("Failed to open the provided input file\"" + inFile.getAbsolutePath() + "\"");
                            ex.printStackTrace();
                        }
                    }
                }
            } catch (ParseException ex) {
                System.err.println("\nFailed to run. " + ex.getMessage());
                System.err.println();
                printHelp();
            }
        }
    }

    /**
     * Perform some extra validation on parsed command line arguments. Any
     * invalid arguments return false and an error message is printed. This only
     * checks the args itself, not the actual paths.
     *
     * @param opts
     * @return Whether the arguments are valid.
     */
    private boolean validateArgs(CommandLine opts) {
        boolean valid = true;
        // Check that 'files' is gives or that an input file is given
        if (!opts.hasOption(CLIOptions.OPTION_IN_FILE)
                && opts.getArgs().length == 0) {
            valid = false;
            System.err.println("A list of files must be given or " + CLIOptions.OPTION_IN_FILE + " must be given");
        }

        // Check that the thumbnail and full img paths aren't the same, the images would overwrite
        File thumbFile = new File(opts.getOptionValue(CLIOptions.OPTION_THUMB_OUT));
        File fullFile = new File(opts.getOptionValue(CLIOptions.OPTION_FULL_OUT));

        if (thumbFile.getAbsolutePath().equalsIgnoreCase(fullFile.getAbsolutePath())) {
            valid = false;
            System.err.println(CLIOptions.OPTION_THUMB_OUT + " and "
                    + CLIOptions.OPTION_FULL_OUT + "cannot be the same.");
        }

        // Check the outputs are actually dirs
        if (!thumbFile.isDirectory() || fullFile.isDirectory()) {
            valid = false;
            System.err.println(CLIOptions.OPTION_THUMB_OUT + " and "
                    + CLIOptions.OPTION_FULL_OUT + " must be directories.");
        }
        // Check integer params are actually integers of appropriate value
        if (opts.hasOption(CLIOptions.OPTION_THUMB_WIDTH)) {
            int parsedVal = -1;
            try {
                parsedVal = Integer.parseInt(opts.getOptionValue(CLIOptions.OPTION_THUMB_WIDTH));
            } catch (NumberFormatException ex) {
            }
            if (parsedVal < 1) {
                valid = false;
                System.err.println(CLIOptions.OPTION_THUMB_WIDTH + " requires an integer parameter greater than 0");
            }
        }

        if (opts.hasOption(CLIOptions.OPTION_FULL_WIDTH)) {
            int parsedVal = -1;
            try {
                parsedVal = Integer.parseInt(opts.getOptionValue(CLIOptions.OPTION_FULL_WIDTH));
            } catch (NumberFormatException ex) {
            }
            if (parsedVal < 1) {
                valid = false;
                System.err.println(CLIOptions.OPTION_FULL_WIDTH + " requires an integer parameter greater than 0");
            }
        }
        return valid;
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
