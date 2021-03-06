//License
package archie_v1.UI;

import archie_v1.fileHelpers.FileHelper;
import archie_v1.fileHelpers.FolderHelper;
import archie_v1.fileHelpers.MetadataKey;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class MetadataChangerPane extends JSplitPane implements ActionListener {

    JButton saveButton, resetButton, saveChildrenButton;
    FileHelper fileHelper;
    private MetadataChangerFields topPane;
    private MetadataChanger parent;

    public MetadataChangerPane(FileHelper fileHelper, MetadataChanger parent) {
        super(JSplitPane.VERTICAL_SPLIT);
        this.fileHelper = fileHelper;
        this.parent = parent;
        createUI();
    }

    private void createUI() {
        this.setResizeWeight(1);
        JPanel bottomPanel = new JPanel();

        resetButton = new JButton("Reset");
        resetButton.addActionListener(this);
        bottomPanel.add(resetButton);
        saveButton = new JButton("Save");
        saveButton.addActionListener(this);
        bottomPanel.add(saveButton);
        if (fileHelper instanceof FolderHelper) {
            saveChildrenButton = new JButton("Save (no overwrite)");
            saveChildrenButton.addActionListener(this);
            bottomPanel.add(saveChildrenButton);
        }

        setTopPane();
        this.setBottomComponent(bottomPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveButton) {
            saveToFileHelper(false);
        } else if (e.getSource() == resetButton) {
            setTopPane();
        } else if (e.getSource() == saveChildrenButton) {
            saveToFileHelper(true);
        } else {
            System.out.println(e.getSource());
            JOptionPane.showInputDialog(this, "hello");
        }
    }

    public void setTopPane() {
        topPane = new MetadataChangerFields(fileHelper);
        this.setTopComponent(topPane);
    }

    public void saveToFileHelper(boolean softSet) {
        parent.dataset.saved = false;
        for (Map.Entry<MetadataKey, JComponent> metadataKeyTextEntry : topPane.labelText.entrySet()) {
            String value = (metadataKeyTextEntry.getKey().unrestricted) ? ((ArchieTextField) metadataKeyTextEntry.getValue()).getText() : ((JComboBox) metadataKeyTextEntry.getValue()).getSelectedItem().toString();
            if(value=="")
                if(softSet)
                    continue;
                else
                    value = null;
            fileHelper.metadataMap.put(metadataKeyTextEntry.getKey(), value);
            fileHelper.setRecord(metadataKeyTextEntry.getKey(), value, softSet);
            //System.out.println("Set key " + metadataKeyTextEntry.getKey() + " to " + value + " for file " + fileHelper.filePath.getFileName());
        }

        for (AddablePanel addablePanel : topPane.addablePanels) {
            fileHelper.SetAddableRecord(addablePanel.Values, addablePanel.valueArray, softSet);
//            for (String[] values : addablePanel.valueArray) {
//                for (int i = 0; i < addablePanel.Values.length; i++) {
//                    System.out.println("Saving addable key with key " + addablePanel.Values[i] + " and value " + values[i]);
//                    fileHelper.setRecord(addablePanel.Values[i], values[i], hardSet);
//                }
//            }
        }
    }
}
