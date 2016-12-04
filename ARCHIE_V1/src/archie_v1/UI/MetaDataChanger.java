//License
package archie_v1.UI;

import archie_v1.ARCHIE;
import archie_v1.Dataset;
import archie_v1.outputAbstract;
import archie_v1.outputFormats.Zipper;
import archie_v1.outputFormats.outputIslandora;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;



public class MetaDataChanger extends JSplitPane implements TreeSelectionListener{

    
    public enum SaveType{Islandora, Dans}
    Path mainDirectory;
    JTree UITree;
    Dataset dataset;
    Zipper zipper;

    public MetaDataChanger(String name, Path path, Boolean fromArchie) {
        this.orientation = JSplitPane.HORIZONTAL_SPLIT;
        this.setResizeWeight(0.5);
        mainDirectory = path;
        dataset = new Dataset(name, path, fromArchie);
        zipper = new Zipper();

        UITree = new JTree(dataset.fileTree) {
            @Override
            public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Path path = (Path) node.getUserObject();
                return path.toFile().getName();
            }
        };
        UITree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        UITree.addTreeSelectionListener(this);

        JScrollPane fileTreeView = new JScrollPane(UITree);
        this.setLeftComponent(fileTreeView);

        WelcomeScreen ws = new WelcomeScreen();
        this.setRightComponent(ws);
    }
    
    public boolean Save(SaveType st, Path outputPath) throws IOException{
        outputAbstract output;
        if(st == SaveType.Islandora)
            output = new outputIslandora();
        else{
            Logger.getLogger(ARCHIE.class.getName()).log(Level.SEVERE, "The chosen save format has not yet been implemented", "his");
            output = null;
        }
        output.SaveToXML(outputPath.toString() + ".zip", dataset.aXML);
        
        XMLOutputter outputter = new XMLOutputter();

        try {
            PrintWriter writer = new PrintWriter(outputPath + ".xml");
            outputter.output(output.singleItem(dataset.aXML.getRootElement().getChildren().get(0)), writer);
        } catch (IOException ex) {
            Logger.getLogger(ARCHIE.class.getName()).log(Level.SEVERE, "Writer not found", ex);
        }
        return true;
    }
    
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)UITree.getLastSelectedPathComponent();
        if(node==null) return;
        Path nodeInfo = (Path)node.getUserObject();
        
        Iterator<Content> files = dataset.aXML.getDescendants();

        while (files.hasNext()) {
            Element temp = (Element) files.next();
            if (temp.getAttributeValue("path").equals(nodeInfo.toString())) {
                System.out.println(nodeInfo.toString());
            }
        }
    }
}
