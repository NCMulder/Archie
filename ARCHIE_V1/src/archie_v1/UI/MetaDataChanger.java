//License
package archie_v1.UI;

import archie_v1.Dataset;
import java.awt.BorderLayout;
import java.nio.file.Path;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

public class MetaDataChanger extends JSplitPane {

    Path mainDirectory;
    JTree UITree;
    Dataset dataset;

    public MetaDataChanger(String name, Path path, Boolean fromArchie) {
        this.orientation = JSplitPane.HORIZONTAL_SPLIT;
        this.setResizeWeight(0.5);
        mainDirectory = path;
        dataset = new Dataset(name, path, fromArchie);

        UITree = new JTree(dataset.fileTree) {
            @Override
            public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Path path = (Path) node.getUserObject();
                return path.toFile().getName();
//                Element elm = (Element)node.getUserObject();
//                return elm.getAttributeValue("name");
            }
        };

        JScrollPane fileTreeView = new JScrollPane(UITree);
        this.setLeftComponent(fileTreeView);

        WelcomeScreen ws = new WelcomeScreen();
        this.setRightComponent(ws);
    }
}
