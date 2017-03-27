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
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author N.C. Mulder <n.c.mulder at students.uu.nl>
 */
public class MetadataChangerFields extends JScrollPane implements ActionListener {

    public int panelY;
    private FileHelper fileHelper;
    private boolean newDataset = false;

    public String datasetLocation = null;

    public HashMap<MetadataKey, JComponent> labelText;
    public HashMap<MetadataKey, String> addableValues;
    public ArrayList<AddablePanel> addablePanels;

    public ArchieTextField datasetLocationField;
    private JButton chooseButton;
    private int totalHeight;
    private int categoryY;
    private int panelHeight;
    private JButton chooseCodeBook;
    private ArchieTextField codeField;

    public MetadataChangerFields(FileHelper fileHelper) {
        this.fileHelper = fileHelper;
        labelText = new HashMap();
        addableValues = new HashMap();
        addablePanels = new ArrayList();

        resetPane();
    }

    public MetadataChangerFields(FileHelper fileHelper, boolean newDataset) {
        this.fileHelper = fileHelper;
        labelText = new HashMap();
        addableValues = new HashMap();
        addablePanels = new ArrayList();

        this.newDataset = true;
        this.datasetLocation = "C:\\Users\\niels\\Documents\\Archie\\Testset\\testset";

        resetPane();
    }

    public void resetPane() {
        this.setViewportView(createMetadataFieldsPanel());
    }

    private JPanel createMetadataFieldsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 10));

        panelY = 0;
        totalHeight = 0;

        for (MetadataKey.KeyCategory keyCat : MetadataKey.KeyCategory.values()) {
            JPanel categoryPanel = new JPanel(new GridBagLayout());
            categoryY = 0;
            panelHeight = 0;
            Border paneBorder = BorderFactory.createTitledBorder(
                    BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), keyCat.name());
            categoryPanel.setBorder(paneBorder);
            for (MetadataKey key : fileHelper.metadataMap.keySet()) {
                if (key.keyCategory.equals(keyCat)) {
                    createValueFields(key, categoryPanel);
                }
            }

            if (categoryPanel.getComponentCount() == 0) {
                continue;
            }

            GridBagConstraints gbc = new GridBagConstraints(
                    0, panelY++, //GridX, GridY
                    5, 1, //GridWidth, GridHeight
                    1, 1, //WeightX, WeightY
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                    new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
            panel.add(categoryPanel, gbc);
            totalHeight += panelHeight;
        }

        if (newDataset) {
            JPanel finalPanel = new JPanel(new GridBagLayout());
            Border paneBorder = BorderFactory.createTitledBorder(
                    BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Dataset location");
            finalPanel.setBorder(paneBorder);

            JLabel label = new JLabel("Dataset location");
            GridBagConstraints gbc = new GridBagConstraints(
                    0, 0, //GridX, GridY
                    2, 1, //GridWidth, GridHeight
                    .4, 1, //WeightX, WeightY
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                    new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
            finalPanel.add(label, gbc);

            datasetLocationField = new ArchieTextField(datasetLocation);
            gbc = new GridBagConstraints(
                    2, 0, //GridX, GridY
                    2, 1, //GridWidth, GridHeight
                    .3, 1, //WeightX, WeightY
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                    new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
            finalPanel.add(datasetLocationField, gbc);

            chooseButton = new JButton("Choose");
            chooseButton.addActionListener(this);
            gbc = new GridBagConstraints(
                    4, 0, //GridX, GridY
                    1, 1, //GridWidth, GridHeight
                    .3, 1, //WeightX, WeightY
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                    new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
            finalPanel.add(chooseButton, gbc);

            gbc = new GridBagConstraints(
                    0, panelY, //GridX, GridY
                    1, 1, //GridWidth, GridHeight
                    1, 1, //WeightX, WeightY
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                    new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
            panel.add(finalPanel, gbc);
        }

        return panel;
    }

    private AddablePanel createAddable(JComponent comp, String single, String plural, MetadataKey[] keyStrings) {
        AddablePanel addablePanel = new AddablePanel(single, plural, keyStrings, fileHelper);
        addablePanels.add(addablePanel);
        GridBagConstraints gbc = new GridBagConstraints(
                0, categoryY++, //GridX, GridY
                5, 1, //GridWidth, GridHeight
                1, 1, //WeightX, WeightY
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
        comp.add(addablePanel, gbc);
        panelHeight += addablePanel.height;

        return addablePanel;
    }

    //possibly change to void return type
    private JPanel createValueFields(MetadataKey key, JComponent parent) {
        switch (key) {
            case CreatorGivenName:
                AddablePanel creatorPanel = createAddable(parent, "Creator", "Creators *", MetadataKey.creatorKeys);
                return creatorPanel;
            case ContributorGivenName:
                AddablePanel contributorPanel = createAddable(parent, "Contributor", "Contributors *", MetadataKey.contributorKeys);
                return contributorPanel;
            case CreatorIdentifier:
            case CreatorTOA:
            case CreatorFamilyName:
            case CreatorAffiliation:
            case ContributorIdentifier:
            case ContributorTOA:
            case ContributorFamilyName:
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
            case RelatedCodeBookLocation:
                JLabel codeLabel = new JLabel(key.toString());
                GridBagConstraints cGBC = new GridBagConstraints(
                        0, categoryY, //GridX, GridY
                        2, 1, //GridWidth, GridHeight
                        .4, 1, //WeightX, WeightY
                        GridBagConstraints.WEST, GridBagConstraints.NONE, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                parent.add(codeLabel, cGBC);

                String codeLocation = fileHelper.metadataMap.get(key);
                if (codeLocation == null) {
                    codeField = new ArchieTextField(20, "E.G. " + key.getDefaultValue());
                } else {
                    codeField = new ArchieTextField(codeLocation, 20, "E.G. " + key.getDefaultValue());
                }

                cGBC = new GridBagConstraints(
                        2, categoryY, //GridX, GridY
                        2, 1, //GridWidth, GridHeight
                        .4, 1, //WeightX, WeightY
                        GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                parent.add(codeField, cGBC);

                chooseCodeBook = new JButton("Choose");
                chooseCodeBook.addActionListener(this);

                cGBC = new GridBagConstraints(
                        4, categoryY++, //GridX, GridY
                        1, 1, //GridWidth, GridHeight
                        .2, 1, //WeightX, WeightY
                        GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                parent.add(chooseCodeBook, cGBC);

                panelHeight++;

                labelText.put(key, codeField);
                return null;
            default:
                JLabel label = new JLabel(key.toString());
                GridBagConstraints gbc = new GridBagConstraints(
                        0, categoryY, //GridX, GridY
                        2, 1, //GridWidth, GridHeight
                        .4, 1, //WeightX, WeightY
                        GridBagConstraints.WEST, GridBagConstraints.NONE, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                parent.add(label, gbc);

                String valueString = fileHelper.metadataMap.get(key);
                JComponent value;

                if (valueString == null) {
                    value = (key.unrestricted) ? new ArchieTextField(20, "E.G. " + key.getDefaultValue()) : new JComboBox(key.getSetOptions());
                    if (!key.unrestricted) {
                        if (Arrays.asList(key.getSetOptions()).contains(fileHelper.metadataMap.get(key))) {
                            ((JComboBox) value).setSelectedItem(fileHelper.metadataMap.get(key));
                        } else {
                            ((JComboBox) value).setSelectedIndex(0);
                        }
                    }
                } else {
                    value = (key.unrestricted) ? new ArchieTextField(valueString, 20, "E.G. " + key.getDefaultValue()) : new JComboBox(key.getSetOptions());
                    if (!key.unrestricted) {
                        ((JComboBox) value).setSelectedItem(valueString);
                    }
                }
                gbc = new GridBagConstraints(
                        2, categoryY++, //GridX, GridY
                        3, 1, //GridWidth, GridHeight
                        .6, 1, //WeightX, WeightY
                        GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                parent.add(value, gbc);

                panelHeight++;

                labelText.put(key, value);
                return null;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == chooseButton) {
            JFileChooser fileChooser = new JFileChooser(datasetLocation);
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int status = fileChooser.showOpenDialog(this);
            if (status == JFileChooser.APPROVE_OPTION) {
                datasetLocationField.setText(fileChooser.getSelectedFile().toString());
            }
        } else if (e.getSource() == chooseCodeBook) {
            JFileChooser fileChooser = new JFileChooser(datasetLocation);
            FileNameExtensionFilter zipFilter = new FileNameExtensionFilter("Access files (*.accdb), Excel files (*.xls/*.xlsx), ", "accdb", "xls", "xlsx");
            fileChooser.addChoosableFileFilter(zipFilter);
            int status = fileChooser.showOpenDialog(this);
            if (status == JFileChooser.APPROVE_OPTION) {
                codeField.setText(fileChooser.getSelectedFile().toString());
                codeField.setFont(codeField.normalFont);
                codeField.setForeground(codeField.normalColor);
            }
        } else {
            System.out.println(e.getSource());
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
