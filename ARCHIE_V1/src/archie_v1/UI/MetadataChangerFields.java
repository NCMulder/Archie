/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package archie_v1.UI;

import archie_v1.fileHelpers.FileHelper;
import archie_v1.fileHelpers.MetadataKey;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author N.C. Mulder <n.c.mulder at students.uu.nl>
 */
public class MetadataChangerFields extends JScrollPane implements ActionListener{
    
    public int panelY;
    private FileHelper fileHelper;
    private boolean newDataset = false;
    
    public String datasetLocation = null;
    
    public HashMap<MetadataKey, JComponent> labelText;
    public ArchieTextField datasetLocationField;
    private JButton chooseButton;
    
    public MetadataChangerFields(FileHelper fileHelper){
        this.fileHelper = fileHelper;
        labelText = new HashMap();
        
        resetPane();
    }
    
    public MetadataChangerFields(FileHelper fileHelper, boolean newDataset){
        this.fileHelper = fileHelper;
        labelText = new HashMap();
        
        this.newDataset = true;
        this.datasetLocation = "C:\\Users\\niels\\Documents\\Archie\\Testset\\testset";
        
        resetPane();
    }
    
    public void resetPane(){
        this.setViewportView(createMetadataFieldsPanel());
    }
    
    private JPanel createMetadataFieldsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 10));

        panelY = 0;

        for (MetadataKey key : fileHelper.metadataMap.keySet()) {
            createValueFields(key, panel);
        }
        
        if(newDataset){
            JLabel label = new JLabel("Dataset location");
            GridBagConstraints gbc = new GridBagConstraints(
                    0, panelY, //GridX, GridY
                    2, 1, //GridWidth, GridHeight
                    .4, 1, //WeightX, WeightY
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                    new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
            panel.add(label, gbc);
            
            datasetLocationField = new ArchieTextField(datasetLocation);
            gbc = new GridBagConstraints(
                    2, panelY, //GridX, GridY
                    2, 1, //GridWidth, GridHeight
                    .3, 1, //WeightX, WeightY
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                    new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
            panel.add(datasetLocationField, gbc);
            
            chooseButton = new JButton("Choose");
            chooseButton.addActionListener(this);
            gbc = new GridBagConstraints(
                    4, panelY, //GridX, GridY
                    1, 1, //GridWidth, GridHeight
                    .3, 1, //WeightX, WeightY
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                    new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
            panel.add(chooseButton, gbc);
        }

        return panel;
    }

    private AddablePanel createAddable(JComponent comp, String single, String plural, MetadataKey[] keyStrings) {
        AddablePanel addablePanel = new AddablePanel(single, plural, keyStrings, fileHelper);
        GridBagConstraints gbc = new GridBagConstraints(
                0, panelY, //GridX, GridY
                5, addablePanel.height, //GridWidth, GridHeight
                1, 1, //WeightX, WeightY
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
        comp.add(addablePanel, gbc);
        panelY += addablePanel.height;

        return addablePanel;
    }

    //possibly change to void return type
    private JPanel createValueFields(MetadataKey key, JComponent parent) {
        switch (key) {
            case CreatorName:
                AddablePanel creatorPanel = createAddable(parent, "Creator", "Creators", MetadataKey.creatorKeys);
                return creatorPanel;
            case ContributorName:
                AddablePanel contributorPanel = createAddable(parent, "Contributor", "Contributors", MetadataKey.contributorKeys);
                return contributorPanel;
            case CreatorIdentifier:
            case CreatorTOA:
            case CreatorAffiliation:
            case ContributorIdentifier:
            case ContributorTOA:
            case ContributorAffiliation:
                return null;
            case Subject:
                AddablePanel subjectPanel = createAddable(parent, "Subject", "Subjects", MetadataKey.subjectKeys);
                return subjectPanel;
            case RelatedDatasetName:
                AddablePanel relatedDatasetPanel = createAddable(parent, "Related dataset", "Related datasets", MetadataKey.relatedDatasetKeys);
                return relatedDatasetPanel;
            case RelatedDatasetLocation:
                return null;
            default:
                JLabel label = new JLabel(key.toString());
                GridBagConstraints gbc = new GridBagConstraints(
                        0, panelY, //GridX, GridY
                        2, 1, //GridWidth, GridHeight
                        .4, 1, //WeightX, WeightY
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                parent.add(label, gbc);

                JComponent value = (key.settable) ? new ArchieTextField(20, "E.G. " + key.getDefaultValue()) : new JComboBox(key.getSetOptions());
                if (!key.settable) {
                    if(Arrays.asList(key.getSetOptions()).contains(fileHelper.metadataMap.get(key)))
                        ((JComboBox) value).setSelectedItem(fileHelper.metadataMap.get(key));
                    else
                        ((JComboBox) value).setSelectedIndex(0);
                }
                gbc = new GridBagConstraints(
                        2, panelY++, //GridX, GridY
                        3, 1, //GridWidth, GridHeight
                        .6, 1, //WeightX, WeightY
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                parent.add(value, gbc);

                labelText.put(key, value);
                return null;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == chooseButton){
            JFileChooser fileChooser = new JFileChooser(datasetLocation);
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int status = fileChooser.showOpenDialog(this);
            if (status == JFileChooser.APPROVE_OPTION) {
                datasetLocationField.setText(fileChooser.getSelectedFile().toString());
            }
        } else {
            System.out.println(e.getSource());
            JOptionPane.showInputDialog(this, "hello");
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
