//License

//TODO: Check Pattern package for easy XML parsing
package archie_v1.outputFormats;

import archie_v1.ARCHIE;
import archie_v1.fileHelpers.FileHelper;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;

public class outputArchieXML extends outputAbstract {
    
    public Document CreateDocument(DefaultMutableTreeNode dirTree) {
        Path filePath = (Path) dirTree.getUserObject();
        Element file = new Element("file");
        file.setAttribute("type", "folder");
        file.setAttribute("name", filePath.getFileName().toString());
        file.setAttribute("path", filePath.toString());

        setFolderElements(dirTree, file);
        for (Object files : Collections.list(dirTree.children())) {
            CreateElements((DefaultMutableTreeNode) files, file);
        }
        
        return new Document(file);
    }

    private void CreateElements(DefaultMutableTreeNode dir, Element element) {
        Path filePath = (Path) dir.getUserObject();
        Element file = new Element("file");
        file.setAttribute("type", "file");
        file.setAttribute("name", filePath.getFileName().toString());
        file.setAttribute("path", filePath.toString());

        if (filePath.toFile().isFile()) {
            setFileElements(filePath, file);
        } else {
            file.setName("folder");
            file.setAttribute("type", "folder");
            setFolderElements(dir, file);
            for (Object files : Collections.list(dir.children())) {
                CreateElements((DefaultMutableTreeNode) files, file);
            }
        }
        element.addContent(file);
    }

    //setFileElements sets several content types for files, such as filetype and modification date.
    private void setFileElements(Path filePath, Element file) {
        FileHelper fh = ARCHIE.fileSelector(filePath, true);
        Map<String, String> fileMap = fh.getMetaData();
        for (String metadata : fileMap.keySet()) {
            //needs a better way of metadataname replacement
            Element element = new Element(metadata.replaceAll(":", "_").replaceAll(" ", "_").replaceAll("/", "_"));
            element.setText(fileMap.get(metadata));
            file.addContent(element);
        }
    }

    //setFolderElements sets several content types for folders, such as filecount.
    private void setFolderElements(DefaultMutableTreeNode folderNode, Element folder) {
        folder.setAttribute("filecount", Integer.toString(folderNode.getChildCount()));
    }

    //Redo this for the new fileMap
    @Override
    public void Save(String destination, ArrayList<FileHelper> files) throws IOException {
        XMLOutputter outputter = new XMLOutputter();

            PrintWriter writer = new PrintWriter(destination + ".xml");
            //outputter.output(xml, writer);
    }
}
