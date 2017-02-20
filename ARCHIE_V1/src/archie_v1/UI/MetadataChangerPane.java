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

    public MetadataChangerPane(FileHelper fileHelper, MetadataChanger parent) {
        super(JSplitPane.VERTICAL_SPLIT);
        this.fileHelper = fileHelper;

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
        for (Map.Entry<MetadataKey, JComponent> metadataKeyTextEntry : topPane.labelText.entrySet()) {
            if(!metadataKeyTextEntry.getKey().file)
                continue;
            String value = (metadataKeyTextEntry.getKey().unrestricted) ? ((ArchieTextField) metadataKeyTextEntry.getValue()).getText() : ((JComboBox) metadataKeyTextEntry.getValue()).getSelectedItem().toString();
            fileHelper.setRecord(metadataKeyTextEntry.getKey(), value, softSet);
            //System.out.println("Set key " + metadataKeyTextEntry.getKey() + " to " + value + " for file " + fileHelper.filePath.getFileName());
        }

        for (AddablePanel addablePanel : topPane.addablePanels) {
            if(!addablePanel.Values[0].file)
                continue;
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
