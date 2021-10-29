import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Search {

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        String path, fileName, root, regex;
        try {
            path = args[0];
            fileName = args[1];
            regex = args[2];
            root = search(path, fileName);
        } catch (Exception ArrayIndexOutOfBounds) {
            System.out.println("Wrong parameter given");
            System.out.println("path, saveFolderToXML, regex");
            root = null;
            regex = "";
        }
        if (root != null) {
            System.out.println(root);
            String xmlString = toXML(root);
            Document doc = convertStringToDocument(xmlString);
            saveXML(doc);
            Document XML = loadXML("./save.xml");
            ArrayList<String> found = searchXML(XML, regex);
            System.out.println("Found\n");
            for (String f : found) {
                System.out.println(f);
            }
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

    public static String toXML(String path) throws IOException,NoSuchAlgorithmException {
        File f = new File(path);
        String root = "<folder name=\"" + f.getName() + "\">";
        File[] files = f.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (!file.isDirectory()) {
                    MessageDigest md5Digest = MessageDigest.getInstance("MD5");
                    String hash = getFileChecksum(md5Digest, file);
                    String fileTag = "<file md5=\""+hash+"\">" + file.getName() + "</file>";
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

    public static Document loadXML(String saveName) {
        try {
            File file = new File(saveName);
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(file);
            document.getDocumentElement().normalize();
            return document;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<String> searchXML(Document doc, String regex) {
        ArrayList<String> found = new ArrayList<String>();
        NodeList folder = doc.getElementsByTagName("folder");
        NodeList file = doc.getElementsByTagName("file");
        for (int i = 0; i < folder.getLength(); i++) {
            Node node = folder.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node;
                String name = eElement.getAttribute("name");
                if (Pattern.matches(regex, name)) {
                    found.add(name);
                }
            }
        }
        for (int i = 0; i < file.getLength(); i++) {
            Node node = file.item(i);
            String name = node.getTextContent();
            if (Pattern.matches(regex, name)) {
                found.add(name);
            }
        }
        return found;
    }

    private static String getFileChecksum(MessageDigest digest, File file) throws IOException {
        // Get file input stream for reading the file content
        
        FileInputStream fis = new FileInputStream(file);

        // Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        // Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }
        ;

        // close the stream; We don't need it now.
        fis.close();

        // Get the hash's bytes
        byte[] bytes = digest.digest();

        // This bytes[] has bytes in decimal format;
        // Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        // return complete hash
        return sb.toString();
    }
}