//License
package archie_v1;

import java.io.File;
import java.nio.file.Path;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jdom2.Document;
import org.jdom2.Element;

public class Dataset {

    public Path mainDirectory;
    public DefaultMutableTreeNode fileTree;
    public Document aXML;

    public Dataset(String name, Path path, Boolean fromArchie) {
        this.mainDirectory = path;
        if (!fromArchie) {
            fileTree = getFileTree();
        } else {
            //fileTreeFromDoc
            fileTree = null;
        }

        archieXMLcreator axc = new archieXMLcreator();
        aXML = axc.CreateDocument(fileTree);

    }

    public DefaultMutableTreeNode getFileTree() {
        DefaultMutableTreeNode dirTree = new DefaultMutableTreeNode(mainDirectory);
        for (File file : mainDirectory.toFile().listFiles()) {
            createNodes(file.toPath(), dirTree);
        }
        return dirTree;
    }

    private void createNodes(Path file, DefaultMutableTreeNode tree) {
        DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(file);
        tree.add(fileNode);
        if (file.toFile().isDirectory()) {
            for (File dirFile : file.toFile().listFiles()) {
                createNodes(dirFile.toPath(), fileNode);
            }
        }
    }

    public DefaultMutableTreeNode docToTree(Document doc) {
        DefaultMutableTreeNode resultTree = new DefaultMutableTreeNode(doc.getRootElement());
        for (Element elem : doc.getRootElement().getChildren()) {
            createNodes(elem, resultTree);
        }
        return resultTree;
    }

    private void createNodes(Element elem, DefaultMutableTreeNode tree) {
        DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(elem);
        tree.add(fileNode);
        File f = new File(elem.getAttributeValue("path"));
        if (f.isDirectory()) {
            for (Element element : elem.getChildren()) {
                createNodes(element, fileNode);
            }
        }
    }

    //Output/saving- todo
//        outputIslandora temp = new outputIslandora();
//        Document iDoc = temp.createOutput(xml);
//        aXML.saveToXML(iDoc, "islandora_XML");
}
