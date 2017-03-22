package com.centumengineering.imagescaler;

import com.centumengineering.imagescaler.utils.NetworkUtils;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;

/**
 * A simple utility class to scale images. This utility reads images, either
 * from the local file system or an online source, and creates a thumbnail
 * version as well as a "full-size" version.
 *
 * @author Phani Gaddipati
 */
public class ImageScaler {

    private static final String[] SUPPORTED_FORMATS = new String[]{
        "jpg", "jpeg", "png", "bmp", "gif", "wbmp"};
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
                    boolean overwrite = false;
                    if (opts.hasOption(CLIOptions.OPTION_OVERWRITE)) {
                        overwrite = true;
                    }
                    File thumbFile = new File(opts.getOptionValue(CLIOptions.OPTION_THUMB_OUT));
                    File fullFile = new File(opts.getOptionValue(CLIOptions.OPTION_FULL_OUT));
                    File[] paths;
                    if (opts.hasOption(CLIOptions.OPTION_IN_FILE)) {
                        File inFile = new File(opts.getOptionValue(CLIOptions.OPTION_IN_FILE));
                        boolean network = opts.hasOption(CLIOptions.OPTION_IN_FILE_NET);
                        try {
                            paths = loadFilePaths(inFile, network);
                        } catch (IOException ex) {
                            System.err.println("Failed to open the provided input file");
                            return;
                        }
                    } else {
                        try {
                            File imgDir = new File(opts.getOptionValue(CLIOptions.OPTION_IN_DIR));
                            boolean recursive = opts.hasOption(CLIOptions.OPTION_DIR_RECURSIVE);
                            Collection<File> files = FileUtils.listFiles(imgDir, SUPPORTED_FORMATS, recursive);
                            paths = files.toArray(new File[files.size()]);
                        } catch (IllegalArgumentException ex) {
                            System.err.println("Failed to enumerate the input directory");
                            return;
                        }
                    }
                    processPaths(paths, thumbW, fullW, thumbFile, fullFile, overwrite);
                }
            } catch (ParseException ex) {
                System.err.println("\nFailed to run. " + ex.getMessage());
                System.err.println();
                printHelp();
            }
        }
    }

    private void processPaths(File[] files, int thumbW, int fullW,
            File thumbDir, File fullDir, boolean overwrite) {

        System.out.println("Using thumbnail output directory " + thumbDir.getAbsolutePath());
        System.out.println("Using full output directory " + fullDir.getAbsolutePath());
        System.out.println("Thumbnail Width: " + thumbW + "px");
        System.out.println("Full Width: " + fullW + "px");
        System.out.println();
        System.out.println("Beginning work on " + files.length + " files.");
    }

    private File[] loadFilePaths(File inFile, boolean network) throws IOException {
        File[] paths;
        String absPaths[] = FileUtils.readLines(inFile).toArray(new String[0]);
        if (network) {
            // The loaded list of paths are network paths
            paths = NetworkUtils.downloadFilesByURLs(absPaths);
        } else {
            List<File> files = new LinkedList<>();
            for (String path : absPaths) {
                files.add(new File(path));
            }
            paths = files.toArray(new File[files.size()]);
        }
        return paths;
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
                && !opts.hasOption(CLIOptions.OPTION_IN_DIR)) {
            valid = false;
            System.err.println("Either " + CLIOptions.OPTION_IN_DIR + " or "
                    + CLIOptions.OPTION_IN_FILE + " must be given.");
        }

        // Check that the thumbnail and full img paths aren't the same, the images would overwrite
        File thumbFile = new File(opts.getOptionValue(CLIOptions.OPTION_THUMB_OUT));
        File fullFile = new File(opts.getOptionValue(CLIOptions.OPTION_FULL_OUT));

        thumbFile.mkdirs();
        fullFile.mkdirs();

        if (thumbFile.getAbsolutePath().equalsIgnoreCase(fullFile.getAbsolutePath())) {
            valid = false;
            System.err.println(CLIOptions.OPTION_THUMB_OUT + " and "
                    + CLIOptions.OPTION_FULL_OUT + "cannot be the same.");
        }

        // Check the outputs are actually dirs
        if (!thumbFile.isDirectory() || !fullFile.isDirectory()) {
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
        String usage = "ImageScaler [options] --"
                + CLIOptions.OPTION_THUMB_OUT + " --"
                + CLIOptions.OPTION_FULL_OUT + " --"
                + "[" + CLIOptions.OPTION_IN_FILE + " OR "
                + CLIOptions.OPTION_IN_DIR + "]";
        String header = "A simple utility class to scale images. This utility reads images, either"
                + " from the local file system or an online source, and creates a thumbnail"
                + " version as well as a \"full-size\" version. Supported formats include "
                + String.join(", ", SUPPORTED_FORMATS);
        formatter.printHelp(usage, header, CLI_OPTIONS, "");
    }

    public static void main(String[] args) {
        new ImageScaler().run(args);
    }
}
