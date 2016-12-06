//License

//TODO: Check Pattern package for easy XML parsing
//TODO: redo this
package archie_v1;

import archie_v1.fileHelpers.basicFile;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;

public class archieXMLcreator {
    
    long convertTime = 0;

    public Document CreateDocument(DefaultMutableTreeNode dirTree) {
        long metStart = System.nanoTime();
        Path filePath = (Path) dirTree.getUserObject();
        Element file = new Element("folder");
        file.setAttribute("name", filePath.getFileName().toString());
        file.setAttribute("path", filePath.toString());

        setFolderElements(dirTree, file);
        for (Object files : Collections.list(dirTree.children())) {
            CreateElements((DefaultMutableTreeNode) files, file);
        }
        
        System.out.println("Other time in the axml: " + (System.nanoTime() - metStart - convertTime)/1000000 + " ms");
        System.out.println("Time spent converting: " + convertTime/1000000 + " ms");
        return new Document(file);
    }

    private void CreateElements(DefaultMutableTreeNode dir, Element element) {
        Path filePath = (Path) dir.getUserObject();
        Element file = new Element("file");
        file.setAttribute("name", filePath.getFileName().toString());
        file.setAttribute("path", filePath.toString());

        if (filePath.toFile().isFile()) {
            setFileElements(filePath, file);
        } else {
            file.setName("folder");
            setFolderElements(dir, file);
            for (Object files : Collections.list(dir.children())) {
                CreateElements((DefaultMutableTreeNode) files, file);
            }
        }
        element.addContent(file);
    }

    private FileHelper getFile(Path filePath) {
        //Todo: context specific file type selection
        basicFile bf = new basicFile(filePath);
        return bf;
    }

    //setFileElements sets several content types for files, such as filetype and modification date.
    private void setFileElements(Path filePath, Element file) {
            
            long start = System.nanoTime();
        FileHelper fh = getFile(filePath);
            convertTime+=(System.nanoTime() - start);
        Map<String, String> fileMap = fh.getMetaData();
        for (String metadata : fileMap.keySet()) {
            //needs a better way of metadataname replacement
            Attribute attribute = new Attribute(metadata.replaceAll(":", "_").replaceAll(" ", "_").replaceAll("/", "_"), fileMap.get(metadata));
            file.setAttribute(attribute);
        }
    }

    //setFolderElements sets several content types for folders, such as filecount.
    private void setFolderElements(DefaultMutableTreeNode folderNode, Element folder) {
        folder.setAttribute("filecount", Integer.toString(folderNode.getChildCount()));
    }

    public void saveToXML(Document xml, String filePath) {
        XMLOutputter outputter = new XMLOutputter();

        try {
            PrintWriter writer = new PrintWriter(filePath);
            outputter.output(xml, writer);
        } catch (IOException ex) {
            Logger.getLogger(ARCHIE.class.getName()).log(Level.SEVERE, "Writer not found", ex);
        }
    }
}
