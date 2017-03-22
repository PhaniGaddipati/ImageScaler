# ImageScaler
A simple CLI utility class to scale images written in Java. This utility reads images, either from the local file system or an online source, and creates a thumbnail version as well as a \"full-size\" version. Supported formats include JPG, PNG, GIF, BMP, WBMP.

To use the utility, it can be compiled to a JAR, and ImageScaler can be run using `java -jar ImageScaler.jar`

# Usage
Every usage requires `--thumb-out` and `--full-out` to be defined. A source of files to process must also be defined, which can either be a newline-delimited file with `--in-file` or a directory containing files with `-in-dir`. Run ImageScaler without any arguments to view this usage.

```
usage: ImageScaler [options] --thumb-out --full-out --[in-file OR in-dir]
 -d,--in-dir <arg>        Directory containing images to process.  Only
                          this or in-file should be provided, not both.
 -f,--full-width <arg>    The width in pixels of the generated full image.
                          Default: 400px
 -fo,--full-out <arg>     Directory to write the full images
 -i,--in-file <arg>       File containing paths of images to process,
                          separated by newline. By default, the paths will
                          be treated as local paths. Use the --online
                          switch if the paths are to be downloaded to the
                          working directory first. Only this or in-dir
                          should be provided, not both.
 -o,--online              To be used with the --in-file switch to indicate
                          that the paths are online sources
 -r,--recursive           If using in-dir, whether to scan directories
                          recursively. Default is false.
 -t,--thumb-width <arg>   The width in pixels of the generated thumbnail.
                          Default: 150px
 -to,--thumb-out <arg>    Directory to write the thumbnails
 ```
 
 ## Example: Scale All Images In a Directory
 If the directory "C:\imgs" contains image files and you'd like to have thumbnails of size 128 and full-size images of size 256 in the directories "C:\imgs\thumbs" and "C:\imgs\full", execute the following.
 
 `ImageScaler -d "C:\imgs" -to "C:\imgs\thumbs" -fo "C:\imgs\full" -t 128 -f 256`
 
 ## Example: Scale Images By Paths
 Assuming the file `imgs.txt` contains a list of local paths of images to scale, the images can be scaled into the defined directories.
 
 `ImageScaler -i "C:\imgs\imgs.txt" -to "C:\imgs\thumbs" -fo "C:\imgs\full"`
 
 ## Example: Download and Scale Images
 Assuming the file `imgs.txt` contains a list of newline-delimited URLs of images, the images can be downloaded and scaled in 1 step.
 
 `ImageScaler -i -o "C:\imgs\imgs.txt" -to "C:\imgs\thumbs" -fo "C:\imgs\full"`

# Credits
ImageScaler makes use of the following libraries.
* [ImageScalr](https://github.com/rkalla/imgscalr): Efficient image scaling
* [Apache Commons IO](https://commons.apache.org/proper/commons-io/): Various file utilities
* [Apache Commons CLI](https://commons.apache.org/proper/commons-cli/): Command line argument handling
