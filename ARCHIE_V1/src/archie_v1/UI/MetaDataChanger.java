//License
package archie_v1.UI;

import archie_v1.Dataset;
import archie_v1.archieXMLcreator;
import archie_v1.outputAbstract;
import archie_v1.outputFormats.Zipper;
import archie_v1.outputFormats.outputIslandora;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import org.jdom2.Content;
import org.jdom2.Element;



public class MetaDataChanger extends JSplitPane implements TreeSelectionListener{

    public enum SaveType{ArchieXML, Islandora, Dans}
    
    Path mainDirectory;
    JTree UITree;
    Dataset dataset;

    public MetaDataChanger(String name, Path path, Boolean fromArchie) {
        //Directory, dataset
        mainDirectory = path;
        long startTime = System.nanoTime();
        dataset = new Dataset(name, path, fromArchie);
        long dataSetTime = System.nanoTime();
        System.out.println("Dataset created in " + (dataSetTime - startTime)/1000000 + " ms");
        
        //UI
        this.orientation = JSplitPane.HORIZONTAL_SPLIT;
        this.setResizeWeight(0.5);
        
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

        //Todo: change right component to thingy
        WelcomeScreen ws = new WelcomeScreen();
        this.setRightComponent(ws);
        long UITime = System.nanoTime();
        System.out.println("UI created in " + (UITime - dataSetTime)/1000000 + " ms");
    }
    
    public boolean Save(SaveType st, Path outputPath) throws IOException{
        outputAbstract output;
        if(st == SaveType.Islandora)
            output = new outputIslandora();
        else if(st == SaveType.ArchieXML){
            archieXMLcreator axml = new archieXMLcreator();
            axml.saveToXML(dataset.aXML, outputPath + ".xml");
            return true;
        } else {
            JOptionPane.showMessageDialog(this, "The chosen save mode has not been implemented yet.");
            return false;
        }
        output.Save(outputPath.toString() + ".zip", dataset.aXML);
        
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
