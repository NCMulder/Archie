//License
package archie_v1.UI;

import archie_v1.fileHelpers.FileHelper;
import archie_v1.fileHelpers.FolderHelper;
import archie_v1.fileHelpers.MetadataContainer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class MetadataChangerPane extends JSplitPane implements ActionListener {

    JButton saveButton, resetButton, addButton, saveChildrenButton, addCreator, addContributor;
    FileHelper fh;
    HashMap<MetadataContainer.MetadataKey, JComponent> labelText;

    MetadataContainer.MetadataKey[] skippableKeys
            = {MetadataContainer.MetadataKey.CreatorIdentifier,
                MetadataContainer.MetadataKey.CreatorTOA,
                MetadataContainer.MetadataKey.CreatorAffiliation,
                MetadataContainer.MetadataKey.ContributorIdentifier,
                MetadataContainer.MetadataKey.ContributorTOA,
                MetadataContainer.MetadataKey.ContributorAffiliation};

    public MetadataChangerPane(FileHelper fileHelper, MetadataChanger parent) {
        super(JSplitPane.VERTICAL_SPLIT);

        labelText = new HashMap();
        this.fh = fileHelper;

        this.setResizeWeight(1);
        JPanel bottomPane = new JPanel();

        resetButton = new JButton("Reset");
        resetButton.addActionListener(this);
        bottomPane.add(resetButton);
        saveButton = new JButton("Save");
        saveButton.addActionListener(this);
        bottomPane.add(saveButton);
        if (fh instanceof FolderHelper) {
            saveChildrenButton = new JButton("Save (overwrite)");
            saveChildrenButton.addActionListener(this);
            bottomPane.add(saveChildrenButton);
        }

        //do context switch for filehelper?
        setTopPane();
        this.setBottomComponent(bottomPane);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveButton) {
            saveToFileHelper(!(fh instanceof FolderHelper));
        } else if (e.getSource() == resetButton) {
            setTopPane();
        } else if (e.getSource() == saveChildrenButton) {
            saveToFileHelper(true);
        } else if (e.getSource() == addCreator){
            SetPart sp = new SetPart(SetPart.Role.Creator);
            Object[] buttons  = {"Add","Cancel"};
            int result = JOptionPane.showOptionDialog(this, sp, "Role addition", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, buttons[0]);
            //do something with result
        } else if (e.getSource() == addContributor){
            SetPart sp = new SetPart(SetPart.Role.Contributor);
            Object[] buttons  = {"Add","Cancel"};
            int result = JOptionPane.showOptionDialog(this, sp, "Role addition", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, buttons[0]);
            //do something with result
        } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    public void setTopPane() {
        JPanel topPane = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        topPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 10));
        
        int height = 0;

        for (final Map.Entry<MetadataContainer.MetadataKey, String> metadata : fh.metadataContainer.metadataMap.entrySet()) {
            if (metadata.getKey() == MetadataContainer.MetadataKey.CreatorName || metadata.getKey() == MetadataContainer.MetadataKey.ContributorName) {
                JPanel namesPanel = setNamePanel(metadata.getKey());
                int thisHeight = (namesPanel.getComponentCount())/4;
                gbc = new GridBagConstraints(
                        0, height, //GridX, GridY
                        5, thisHeight, //GridWidth, GridHeight
                        1, 1, //WeightX, WeightY
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                topPane.add(namesPanel, gbc);
                height+=thisHeight;
            } else if (Arrays.asList(skippableKeys).contains(metadata.getKey())) {
                continue;
            } else {
                JLabel label = new JLabel(metadata.getKey().toString());
                gbc = new GridBagConstraints(
                        0, height, //GridX, GridY
                        2, 1, //GridWidth, GridHeight
                        .4, 1, //WeightX, WeightY
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                topPane.add(label, gbc);
                
                JComponent value = (metadata.getKey().settable) ? new JTextField(metadata.getValue(), 20) : new JComboBox(metadata.getKey().getSetOptions());
                if (!metadata.getKey().settable)
                    ((JComboBox) value).setSelectedItem(metadata.getValue());
                gbc = new GridBagConstraints(
                        2, height++, //GridX, GridY
                        3, 1, //GridWidth, GridHeight
                        .6, 1, //WeightX, WeightY
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                topPane.add(value, gbc);
                
                labelText.put(metadata.getKey(), value);
            }
        }

        JScrollPane panel = new JScrollPane(topPane);

        this.setTopComponent(panel);
    }

    private void saveToFileHelper(boolean hardSet) {
        for (Map.Entry<MetadataContainer.MetadataKey, JComponent> metadataKeyTextEntry : labelText.entrySet()) {
            String value = (metadataKeyTextEntry.getKey().settable) ? ((JTextField) metadataKeyTextEntry.getValue()).getText() : ((JComboBox) metadataKeyTextEntry.getValue()).getSelectedItem().toString();
            fh.setRecord(metadataKeyTextEntry.getKey(), value, hardSet);
        }
    }

    private JPanel setNamePanel(MetadataContainer.MetadataKey key) {
        JPanel namePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        String borderText = (key == MetadataContainer.MetadataKey.CreatorName)? "Creators" : "Contributors";
        Border paneBorder = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), borderText);
        namePanel.setBorder(paneBorder);
        
        String[] titles, names, identifiers, affiliations;
        
        if(key==MetadataContainer.MetadataKey.CreatorName){
            titles = fh.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.CreatorTOA).split(";");
            names = fh.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.CreatorName).split(";");
            identifiers = fh.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.CreatorIdentifier).split(";");
            affiliations = fh.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.CreatorAffiliation).split(";");
        } else {
            titles = fh.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.ContributorTOA).split(";");
            names = fh.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.ContributorName).split(";");
            identifiers = fh.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.ContributorIdentifier).split(";");
            affiliations = fh.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.ContributorAffiliation).split(";");
        }
        
        for(int i = 0; i < names.length; i++){
            JLabel name = new JLabel(titles[i].replace("None", "").trim() + " " + names[i].trim());
            gbc = new GridBagConstraints(
                    0, 2 * i, //GridX, GridY
                    2, 1, //GridWidth, GridHeight
                    .333, 1, //WeightX, WeightY
                    GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                    new Insets(4, 4, 4, 4), 0, 0); //Insets, IpadX, IpadY
            namePanel.add(name, gbc);

            JLabel identifier = new JLabel(identifiers[i].trim());
            gbc = new GridBagConstraints(
                    2, 2 * i, //GridX, GridY
                    1, 1, //GridWidth, GridHeight
                    .167, 1, //WeightX, WeightY
                    GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                    new Insets(4, 4, 4, 4), 0, 0); //Insets, IpadX, IpadY
            namePanel.add(identifier, gbc);
            
            JLabel affiliation = new JLabel(affiliations[i].trim());
            gbc = new GridBagConstraints(
                    3, 2 * i, //GridX, GridY
                    2, 1, //GridWidth, GridHeight
                    .333, 1, //WeightX, WeightY
                    GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                    new Insets(4, 4, 4, 4), 0, 0); //Insets, IpadX, IpadY
            namePanel.add(affiliation, gbc);
            
            JButton removeName = new JButton("Remove");
            gbc = new GridBagConstraints(
                    5, 2 * i, //GridX, GridY
                    1, 1, //GridWidth, GridHeight
                    .167, 1, //WeightX, WeightY
                    GridBagConstraints.WEST, GridBagConstraints.NONE, //Anchor, Fill
                    new Insets(4, 4, 4, 4), 0, 0); //Insets, IpadX, IpadY
            namePanel.add(removeName, gbc);
        }

        gbc = new GridBagConstraints(
                5, namePanel.getComponentCount()/4 + 1, //GridX, GridY
                1, 1, //GridWidth, GridHeight
                .167, 1, //WeightX, WeightY
                GridBagConstraints.WEST, GridBagConstraints.NONE, //Anchor, Fill
                new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY

//        if(key==MetadataContainer.MetadataKey.CreatorName) {
//            addCreator = new JButton("Add creator");
//            addCreator.addActionListener(this);
//            namePanel.add(addCreator, gbc);
//        } else {
//            addContributor = new JButton("Add contributor");
//            addContributor.addActionListener(this);
//            namePanel.add(addContributor, gbc);
//        }
        
        return namePanel;
    }
}
