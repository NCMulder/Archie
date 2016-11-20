//License
package archie_v1;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

public class UIHelper extends JPanel {

    JTree fileTree;
    JFrame frame;

    public UIHelper(DefaultMutableTreeNode tree) {
        fileTree = new JTree(tree);
        JScrollPane fileTreeView = new JScrollPane(fileTree);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(fileTreeView);
        add(splitPane);

        frame = new JFrame("DirTree");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.pack();
        frame.setVisible(true);
    }
}
