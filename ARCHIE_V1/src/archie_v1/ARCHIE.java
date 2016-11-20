//License header
package archie_v1;

import archie_v1.outputFormats.outputIslandora;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jdom2.*;

public class ARCHIE {

    private Path mainDir;
    static private DefaultMutableTreeNode dirTree;
    private Document xml;
    private archieXMLcreator aXML;
    static UIHelper ui;

    public ARCHIE(Path path) {
        mainDir = path;

        //Filetree generation
        dirTree = new DefaultMutableTreeNode(path);
        for (File file : path.toFile().listFiles()) {
            CreateNodes(file.toPath(), dirTree);
        }

        //Tussenvorm XML creation
        aXML = new archieXMLcreator();
        xml = aXML.CreateDocument(dirTree);
        aXML.saveToXML(xml, "basic_xml");

        outputIslandora temp = new outputIslandora();
        Document iDoc = temp.createOutput(xml);
        aXML.saveToXML(iDoc, "islandora_XML");
    }

    public static void main(String[] args) {
        String str = "C:\\Users\\niels\\Documents\\Archie\\Archie\\Documentation\\testset";
        ARCHIE arch = new ARCHIE(Paths.get(str));
        setupUI();
    }

    public static void setupUI() {
        ui = new UIHelper(dirTree);
    }

    private void CreateNodes(Path path, DefaultMutableTreeNode tree) {
        DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(path);
        tree.add(fileNode);
        if (path.toFile().isDirectory()) {
            for (File file : path.toFile().listFiles()) {
                CreateNodes(file.toPath(), fileNode);
            }
        }
    }
}
