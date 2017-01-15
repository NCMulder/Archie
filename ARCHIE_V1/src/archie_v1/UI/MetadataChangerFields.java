/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package archie_v1.UI;

import archie_v1.fileHelpers.FileHelper;
import archie_v1.fileHelpers.MetadataContainer;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 *
 * @author N.C. Mulder <n.c.mulder at students.uu.nl>
 */
public class MetadataChangerFields extends JScrollPane implements ActionListener{
    
    private int panelY;
    private JButton addCreator, addContributor, addSubject, addRelatedDataset;
    private FileHelper fileHelper;
    
    public HashMap<MetadataContainer.MetadataKey, JComponent> labelText;
    
    public MetadataChangerFields(FileHelper fileHelper){
        this.fileHelper = fileHelper;
        labelText = new HashMap();
        
        resetPane();
    }
    
    public void resetPane(){
        this.setViewportView(createMetadataFieldsPanel());
    }
    
    private JPanel createMetadataFieldsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 10));

        panelY = 0;

        for (MetadataContainer.MetadataKey key : fileHelper.metadataContainer.metadataMap.keySet()) {
            createValueFields(key, panel);
        }

        return panel;
    }

    private AddablePanel createAddable(JComponent comp, String single, String plural, String[] keyStrings) {
        AddablePanel addablePanel = new AddablePanel(single, plural, keyStrings);
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
    private JPanel createValueFields(MetadataContainer.MetadataKey key, JComponent parent) {
        switch (key) {
            case CreatorName:
                String[] creatorStrings
                        = {fileHelper.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.CreatorTOA),
                            fileHelper.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.CreatorName),
                            fileHelper.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.CreatorIdentifier),
                            fileHelper.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.CreatorAffiliation)};
                AddablePanel creatorPanel = createAddable(parent, "Creator", "Creators", creatorStrings);
                addCreator = creatorPanel.addItem;
                addCreator.addActionListener(this);
                return creatorPanel;
            case ContributorName:
                String[] contributorStrings
                        = {fileHelper.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.ContributorTOA),
                            fileHelper.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.ContributorName),
                            fileHelper.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.ContributorIdentifier),
                            fileHelper.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.ContributorAffiliation)};
                AddablePanel contributorPanel = createAddable(parent, "Contributor", "Contributors", contributorStrings);
                addContributor = contributorPanel.addItem;
                addContributor.addActionListener(this);
                return contributorPanel;
            case CreatorIdentifier:
            case CreatorTOA:
            case CreatorAffiliation:
            case ContributorIdentifier:
            case ContributorTOA:
            case ContributorAffiliation:
                return null;
            case Subject:
                String[] subjectStrings = {fileHelper.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.Subject)};
                AddablePanel subjectPanel = createAddable(parent, "Subject", "Subjects", subjectStrings);
                addSubject = subjectPanel.addItem;
                addSubject.addActionListener(this);
                return subjectPanel;
            case RelatedDatasetName:
                String[] relatedDatasetStrings
                        = {fileHelper.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.RelatedDatasetName),
                            fileHelper.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.RelatedDatasetLocation)};
                AddablePanel relatedDatasetPanel = createAddable(parent, "Related dataset", "Related datasets", relatedDatasetStrings);
                addRelatedDataset = relatedDatasetPanel.addItem;
                addRelatedDataset.addActionListener(this);
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

                JComponent value = (key.settable) ? new JTextField(fileHelper.metadataContainer.metadataMap.get(key), 20) : new JComboBox(key.getSetOptions());
                if (!key.settable) {
                    ((JComboBox) value).setSelectedItem(fileHelper.metadataContainer.metadataMap.get(key));
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
        if (e.getSource() == addCreator || e.getSource() == addContributor || e.getSource() == addSubject || e.getSource() == addRelatedDataset) {
            SetPart sp = null;
            String title = "";
            if (e.getSource() == addCreator) {
                sp = new SetPart(SetPart.Addable.Creator);
                title = "Creator addition";
            } else if (e.getSource() == addContributor){
                sp = new SetPart(SetPart.Addable.Contributor);
                title = "Contributor addition";
            } else if (e.getSource() == addSubject){
                sp = new SetPart(SetPart.Addable.Subject);
                title = "Subject addition";
            } else if (e.getSource() == addRelatedDataset){
                sp = new SetPart(SetPart.Addable.RelatedDataset);
                title = "Related dataset addition";
            } 
            Object[] buttons = {"Add", "Cancel"};
            int result = JOptionPane.showOptionDialog(this, sp, title, JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, buttons[0]);
                if (result == JOptionPane.OK_OPTION) {
                for (Map.Entry<MetadataContainer.MetadataKey, String> metadata : sp.getInfo().entrySet()) {
                    fileHelper.setRecord(metadata.getKey(), fileHelper.metadataContainer.metadataMap.get(metadata.getKey()) + ";" + metadata.getValue(), true);
                }
                resetPane();
            }
        } else {
            System.out.println(e.getSource());
            JOptionPane.showInputDialog(this, "hello");
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
}