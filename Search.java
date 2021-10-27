import java.io.File;
import java.io.StringReader;

import java.util.regex.Pattern;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

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
            System.out.println(xmlString);
            Document doc = convertStringToDocument(xmlString);
            saveXML(doc);

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
        String root = "<folder name=\"" + f.getName() + "\">";
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

    public static Document convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlStr)));
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveXML(Document doc) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource domSource = new DOMSource(doc);
            StreamResult streamResult = new StreamResult(new File("./save.xml"));
            transformer.transform(domSource, streamResult);
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }
}