import java.io.File;
import java.io.FileWriter;
import java.util.regex.Pattern;

public class Search {
    public static void main(String[] args) {
        String path, fileName, root;
        try {
            path = args[0];
            fileName = args[1];
            root = search(path, fileName);
        } catch (Exception ArrayIndexOutOfBounds) {
            System.out.println("Wrong parameter given");
            root = null;
        }
        if (root != null) {
            System.out.println(root);
            String xmlString = toXML(root);
            saveXML(xmlString);

        } else {
            System.out.println("Can't find file/folder");
        }
    }

    public static String search(String rootPath, String fileName) {
        /**
         * @param rootPath search location
         * @param fileName search file name
         */
        File f = new File(rootPath);
        File[] files = f.listFiles();
        if (files != null && files.length > 0) { // folder isn't empty
            for (File file : files) {
                if (Pattern.matches(fileName, file.getName())) { // found file
                    System.out.println("\n\n\n\n\nFound. \n");
                    String path = rootPath + "/" + file.getName();
                    return path;
                }
                if (file.isDirectory()) {
                    System.out.println("Processing in " + file.getAbsolutePath());
                    String path = search(file.getAbsolutePath(), fileName); // traverse
                    if (path != null) {
                        return path;
                    }
                }
            }
        }
        return null;
    }

    public static String toXML(String path) {
        File f = new File(path);
        String root = "<folder name=" + '"' + f.getName() + '"' + '>';
        File[] files = f.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (!file.isDirectory()) {
                    String fileTag = "<file>" + file.getName() + "</file>";
                    root += fileTag;
                } else {
                    root += toXML(file.getAbsolutePath());
                }
            }
        }
        root += "</folder>";
        return root;
    }

    public static void saveXML(String xmlString) {
        try {
            FileWriter myWriter = new FileWriter("save.xml");
            // <?xml version="1.0" encoding="UTF-8"?>
            myWriter.write(
                    "<?xml version=" + '"' + "1.0" + '"' + " encoding=" + '"' + "UTF-8" + '"' + "?>" + xmlString);
            myWriter.close();
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}