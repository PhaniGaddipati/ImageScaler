package com.centumengineering.imagescaler;

import com.centumengineering.imagescaler.scaling.ScaleResult;
import com.centumengineering.imagescaler.scaling.ScaleTask;
import com.centumengineering.imagescaler.utils.NetworkUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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
                    // We can start the processing. Get all of the params from the
                    // CLI args and start
                    int thumbW = CLIOptions.DEFAULT_THUMB_WIDTH;
                    if (opts.hasOption(CLIOptions.OPTION_THUMB_WIDTH)) {
                        thumbW = Integer.parseInt(opts.getOptionValue(CLIOptions.OPTION_THUMB_WIDTH));
                    }
                    int fullW = CLIOptions.DEFAULT_FULL_WIDTH;
                    if (opts.hasOption(CLIOptions.OPTION_FULL_WIDTH)) {
                        fullW = Integer.parseInt(opts.getOptionValue(CLIOptions.OPTION_FULL_WIDTH));
                    }

                    File thumbFile = new File(opts.getOptionValue(CLIOptions.OPTION_THUMB_OUT));
                    File fullFile = new File(opts.getOptionValue(CLIOptions.OPTION_FULL_OUT));
                    File[] files = getFilesToProcess(opts);

                    if (files == null || files.length == 0) {
                        System.out.println("No files to process.");
                    } else {
                        try {                            
                            processFiles(files, thumbW, fullW, thumbFile, fullFile);
                        } catch (ExecutionException | InterruptedException ex) {
                            System.out.println("Processing was interrupted! " + ex.getMessage());
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
     * Loads the list of files to process from the CLI args. If it is a
     * directory, the files are enumerated. If it's a file, then the file is
     * loaded and files downloaded if necessary.
     *
     * @param opts The CLI arguments
     * @return An array of files to process
     */
    private File[] getFilesToProcess(CommandLine opts) {
        if (opts.hasOption(CLIOptions.OPTION_IN_FILE)) {
            // Load the list of files from the file
            File inFile = new File(opts.getOptionValue(CLIOptions.OPTION_IN_FILE));
            boolean network = opts.hasOption(CLIOptions.OPTION_IN_FILE_NET);
            return loadFilesFromFile(inFile, network);
        } else {
            File imgDir = new File(opts.getOptionValue(CLIOptions.OPTION_IN_DIR));
            boolean recursive = opts.hasOption(CLIOptions.OPTION_DIR_RECURSIVE);
            return loadFilesFromDir(imgDir, recursive);
        }
    }

    /**
     * Loads the files to process from a directory. This involves enumerating
     * all compatible files from the dir
     *
     * @param opts CLI arguments
     * @return Array of files to process or null on error.
     */
    private File[] loadFilesFromDir(File imgDir, boolean recursive) {
        try {
            Collection<File> files = FileUtils.listFiles(imgDir, SUPPORTED_FORMATS, recursive);
            return files.toArray(new File[files.size()]);
        } catch (IllegalArgumentException ex) {
            System.err.println("Failed to enumerate the input directory");
            return null;
        }
    }

    /**
     * Loads the array of files to process from the given file. If the file
     * paths are network paths, they are downloaded into the working directory.
     *
     * @param inFile The file containing the list of paths
     * @param network Whether the paths are network locations
     * @return Array of files to process, or null on error.
     */
    private File[] loadFilesFromFile(File inFile, boolean network) {
        try {
            String absPaths[] = FileUtils.readLines(inFile).toArray(new String[0]);
            if (network) {
                // The loaded list of paths are network paths
                return NetworkUtils.downloadFilesByURLs(absPaths);
            } else {
                // Use the paths as local paths
                List<File> files = new LinkedList<>();
                for (String path : absPaths) {
                    files.add(new File(path));
                }
                return files.toArray(new File[files.size()]);
            }            
        } catch (IOException ex) {
            System.err.println("Failed to open the provided input file");
            return null;
        }
    }

    /**
     * Processes the given files with the given parameters. This method blocks
     * until all processing is done.
     *
     * @param files The array of files to process
     * @param thumbW Thumbnail image width
     * @param fullW Full-size image width
     * @param thumbDir The directory to write the thumbnails
     * @param fullDir The directory to write the full-size images
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private void processFiles(File[] files, int thumbW, int fullW,
            File thumbDir, File fullDir) throws ExecutionException, InterruptedException {

        System.out.println();
        System.out.println("Using thumbnail output directory " + thumbDir.getAbsolutePath());
        System.out.println("Using full output directory " + fullDir.getAbsolutePath());
        System.out.println("Thumbnail Width: " + thumbW + "px");
        System.out.println("Full Width: " + fullW + "px");
        System.out.println();
        System.out.println("Beginning work on " + files.length + " files.");

        // Start all of the async tasks
        List<CompletableFuture> futures = new ArrayList<>(files.length);
        for (final File f : files) {
            futures.add(ScaleTask.newCompletableFuture(f, thumbW, fullW, thumbDir, fullDir)
                    .thenApply(ScaleResult::printTaskSummary)
                    .exceptionally(t -> {
                        System.out.println("Failed to process " + f.getAbsolutePath() + ".\n" + t.getMessage() + "\n");
                        t.printStackTrace();
                        return null;
                    }));
        }

        // Block until all futures complete so the program doesn't terminate early
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[files.length])).get();
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
