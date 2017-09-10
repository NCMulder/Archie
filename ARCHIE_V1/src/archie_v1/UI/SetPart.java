/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package archie_v1.UI;

import archie_v1.fileHelpers.MetadataKey;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author N.C. Mulder <n.c.mulder at students.uu.nl>
 */
public class SetPart extends JPanel {

    private String[] identifierTypes = {"DAI", "ORCID", "ISNI"};
    private String[] relationTypes = {"DAI", "ORCID", "ISNI"};
    public JComboBox idComboBox;

    public ArchieTextField idField, toaField, nameGivenField, nameFamilyField, affiliationField;
    public ArchieTextField singleField, relatedDatasetNameField, relatedDatasetLocationField;
    public JComboBox relatedDatasetRelationBox;
    private MetadataKey role;
    
    
    private MetadataKey[] keys;
    private JComponent[] fields;
    private String[] availableStrings = {"", "", "", "", "", ""};

    public SetPart(MetadataKey[] keys) {
        this.keys = keys;
        fields = new JComponent[keys.length];
        createUI();
    }

    public SetPart(MetadataKey[] keys, String... availableStrings) {
        this.keys = keys;
        this.availableStrings = availableStrings;
        fields = new JComponent[keys.length];
        createUI();
    }
    
    private void createUI() {
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        for (int i = 0; i < keys.length; i++) {
            MetadataKey key = keys[i];
            JLabel label = new JLabel(key.toString());
            gbc = new GridBagConstraints(
                    0, i, //GridX, GridY
                    2, 1, //GridWidth, GridHeight
                    1, 1, //WeightX, WeightY
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                    new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
            this.add(label, gbc);
            
            if(key.unrestricted){
                //Textfield
                ArchieTextField input = new ArchieTextField(getHintString(key), 20, "E.G. " + key.getDefaultValue());
                gbc = new GridBagConstraints(
                        3, i, //GridX, GridY
                        2, 1, //GridWidth, GridHeight
                        1, 1, //WeightX, WeightY
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                this.add(input, gbc);
                fields[i] = input;
                //Add to array
            } else {
                //Combobox
                JComboBox comboBox = new JComboBox(key.getSetOptions());
                gbc = new GridBagConstraints(
                        2, i, //GridX, GridY
                        2, 1, //GridWidth, GridHeight
                        1, 1, //WeightX, WeightY
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                this.add(comboBox, gbc);
                fields[i] = comboBox;
                //Add to array
            }
        }
    }

    public SetPart(MetadataKey role) {
        this.role = role;
        setUI();
    }

    public SetPart(MetadataKey role, String... availableStrings) {
        this.role = role;
        this.availableStrings = availableStrings;
        setUI();
    }

    public void setUI() {
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        switch (role) {
            case CreatorGivenName:
            case CreatorFamilyName:
            case CreatorIdentifier:
            case CreatorTOA:
            case CreatorAffiliation:
            case ContributorGivenName:
            case ContributorFamilyName:
            case ContributorIdentifier:
            case ContributorTOA:
            case ContributorAffiliation:
                JLabel identifier = new JLabel("Identifier");
                gbc = new GridBagConstraints(
                        0, 0, //GridX, GridY
                        2, 1, //GridWidth, GridHeight
                        1, 1, //WeightX, WeightY
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                this.add(identifier, gbc);

                idComboBox = new JComboBox(identifierTypes);
                gbc = new GridBagConstraints(
                        2, 0, //GridX, GridY
                        1, 1, //GridWidth, GridHeight
                        1, 1, //WeightX, WeightY
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                this.add(idComboBox, gbc);

                idField = new ArchieTextField(getHintString(MetadataKey.CreatorIdentifier), 20, "E.G. " + MetadataKey.CreatorIdentifier.getDefaultValue());
                gbc = new GridBagConstraints(
                        3, 0, //GridX, GridY
                        2, 1, //GridWidth, GridHeight
                        1, 1, //WeightX, WeightY
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                this.add(idField, gbc);

                JLabel toaLabel = new JLabel("Terms of Adress");
                gbc = new GridBagConstraints(
                        0, 1, //GridX, GridY
                        2, 1, //GridWidth, GridHeight
                        1, 1, //WeightX, WeightY
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                this.add(toaLabel, gbc);

                toaField = new ArchieTextField(getHintString(MetadataKey.CreatorTOA), 20, "E.G. " + MetadataKey.CreatorTOA.getDefaultValue());
                gbc = new GridBagConstraints(
                        2, 1, //GridX, GridY
                        3, 1, //GridWidth, GridHeight
                        1, 1, //WeightX, WeightY
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                this.add(toaField, gbc);

                JLabel nameLabel = (role == MetadataKey.CreatorGivenName) ? new JLabel("Creator Name") : new JLabel("Contributor Name");
                gbc = new GridBagConstraints(
                        0, 2, //GridX, GridY
                        2, 1, //GridWidth, GridHeight
                        1, 1, //WeightX, WeightY
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                this.add(nameLabel, gbc);

                nameGivenField = new ArchieTextField(getHintString(MetadataKey.CreatorGivenName), 0, "E.G. " + MetadataKey.CreatorGivenName.getDefaultValue());
                gbc = new GridBagConstraints(
                        2, 2, //GridX, GridY
                        1, 1, //GridWidth, GridHeight
                        1, 1, //WeightX, WeightY
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                this.add(nameGivenField, gbc);

                nameFamilyField = new ArchieTextField(getHintString(MetadataKey.CreatorFamilyName), 20, "E.G. " + MetadataKey.CreatorFamilyName.getDefaultValue());
                gbc = new GridBagConstraints(
                        3, 2, //GridX, GridY
                        2, 1, //GridWidth, GridHeight
                        1, 1, //WeightX, WeightY
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                this.add(nameFamilyField, gbc);

                JLabel affiliationLabel = new JLabel("Affiliation");
                gbc = new GridBagConstraints(
                        0, 3, //GridX, GridY
                        2, 1, //GridWidth, GridHeight
                        1, 1, //WeightX, WeightY
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                this.add(affiliationLabel, gbc);

                affiliationField = new ArchieTextField(getHintString(MetadataKey.CreatorAffiliation), 20, "E.G. " + MetadataKey.CreatorAffiliation.getDefaultValue());
                gbc = new GridBagConstraints(
                        2, 3, //GridX, GridY
                        3, 1, //GridWidth, GridHeight
                        1, 1, //WeightX, WeightY
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                this.add(affiliationField, gbc);

                break;

            case RelatedDatasetName:
            case RelatedDatasetLocation:
                JLabel relatedDatasetNameLabel = new JLabel("Dataset name");
                gbc = new GridBagConstraints(
                        0, 0, //GridX, GridY
                        2, 1, //GridWidth, GridHeight
                        1, 1, //WeightX, WeightY
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                this.add(relatedDatasetNameLabel, gbc);

                relatedDatasetNameField = new ArchieTextField(getHintString(MetadataKey.RelatedDatasetName), 20, "E.G. " + MetadataKey.RelatedDatasetName.getDefaultValue());
                gbc = new GridBagConstraints(
                        2, 0, //GridX, GridY
                        3, 1, //GridWidth, GridHeight
                        1, 1, //WeightX, WeightY
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                this.add(relatedDatasetNameField, gbc);

                JLabel relatedDatasetLocationLabel = new JLabel("Dataset location");
                gbc = new GridBagConstraints(
                        0, 1, //GridX, GridY
                        2, 1, //GridWidth, GridHeight
                        1, 1, //WeightX, WeightY
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                this.add(relatedDatasetLocationLabel, gbc);

                relatedDatasetLocationField = new ArchieTextField(getHintString(MetadataKey.RelatedDatasetLocation), 20, "E.G. " + MetadataKey.RelatedDatasetLocation.getDefaultValue());
                gbc = new GridBagConstraints(
                        2, 1, //GridX, GridY
                        3, 1, //GridWidth, GridHeight
                        1, 1, //WeightX, WeightY
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                this.add(relatedDatasetLocationField, gbc);

                JLabel relatedDatasetRelationLabel = new JLabel("Dataset relation");
                gbc = new GridBagConstraints(
                        0, 2, //GridX, GridY
                        2, 1, //GridWidth, GridHeight
                        1, 1, //WeightX, WeightY
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                this.add(relatedDatasetRelationLabel, gbc);

                relatedDatasetRelationBox = new JComboBox(relationTypes);
                ((JComboBox) relatedDatasetRelationBox).setSelectedIndex(0);
                gbc = new GridBagConstraints(
                        2, 2, //GridX, GridY
                        3, 1, //GridWidth, GridHeight
                        1, 1, //WeightX, WeightY
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                this.add(relatedDatasetRelationBox, gbc);

                break;
            default:
                JLabel singleLabel = new JLabel(role.toString());
                gbc = new GridBagConstraints(
                        0, 0, //GridX, GridY
                        2, 1, //GridWidth, GridHeight
                        1, 1, //WeightX, WeightY
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                this.add(singleLabel, gbc);

                singleField = new ArchieTextField(getHintString(role), 20, "E.G. " + role.getDefaultValue());
                gbc = new GridBagConstraints(
                        2, 0, //GridX, GridY
                        3, 1, //GridWidth, GridHeight
                        1, 1, //WeightX, WeightY
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                this.add(singleField, gbc);

                break;
        }
    }

    public HashMap<MetadataKey, String> getInfo() {
        HashMap<MetadataKey, String> info = new HashMap();
        
        for (int i = 0; i < fields.length; i++) {
            JComponent field = fields[i];
            if(field instanceof ArchieTextField){
                info.put(keys[i], ((ArchieTextField) field).getText());
            } else {
                info.put(keys[i], ((JComboBox)field).getSelectedItem().toString());
            }
        }
        
        return info;
//        
//        switch (role) {
//            case CreatorGivenName:
//            case CreatorFamilyName:
//            case CreatorIdentifier:
//            case CreatorTOA:
//            case CreatorAffiliation:
//                //Todo: differentiate between ID types
//                info.put(MetadataKey.CreatorIdentifier, idComboBox.getSelectedItem().toString() + " " + idField.getText());
//                info.put(MetadataKey.CreatorTOA, toaField.getText());
//                //Todo: differentiate between name parts
//                info.put(MetadataKey.CreatorGivenName, nameGivenField.getText());
//                info.put(MetadataKey.CreatorFamilyName, nameFamilyField.getText());
//                info.put(MetadataKey.CreatorAffiliation, affiliationField.getText());
//                break;
//            case ContributorGivenName:
//            case ContributorFamilyName:
//            case ContributorIdentifier:
//            case ContributorTOA:
//            case ContributorAffiliation:
//                //Todo: differentiate between ID types
//                info.put(MetadataKey.ContributorIdentifier, idComboBox.getSelectedItem().toString() + " " + idField.getText());
//                info.put(MetadataKey.ContributorTOA, toaField.getText());
//                //Todo: differentiate between name parts
//                info.put(MetadataKey.ContributorGivenName, nameGivenField.getText());
//                info.put(MetadataKey.ContributorFamilyName, nameFamilyField.getText());
//                info.put(MetadataKey.ContributorAffiliation, affiliationField.getText());
//                break;
//            case RelatedDatasetName:
//            case RelatedDatasetLocation:
//                info.put(MetadataKey.RelatedDatasetName, relatedDatasetNameField.getText());
//                info.put(MetadataKey.RelatedDatasetLocation, relatedDatasetLocationField.getText());
//                info.put(MetadataKey.RelatedDatasetRelation, relatedDatasetRelationBox.getSelectedItem().toString());
//                break;
//            default:
//                info.put(role, singleField.getText());
//                break;
//        }
//
//        return info;
    }

    private String getHintString(MetadataKey key) {
        String hintString = "";
        switch (key) {
            case CreatorGivenName:
            case CreatorInsertions:
            case CreatorFamilyName:
            case CreatorIdentifier:
            case CreatorTOA:
            case CreatorAffiliation:
                hintString = availableStrings[Arrays.asList(MetadataKey.creatorKeys).indexOf(key)];
                break;
            case ContributorGivenName:
            case ContributorInsertions:
            case ContributorFamilyName:
            case ContributorIdentifier:
            case ContributorTOA:
            case ContributorAffiliation:
                hintString = availableStrings[Arrays.asList(MetadataKey.contributorKeys).indexOf(key)];
                break;
            case RelatedDatasetName:
            case RelatedDatasetLocation:
                hintString = availableStrings[Arrays.asList(MetadataKey.relatedDatasetKeys).indexOf(key)];
                break;
            default:
                hintString = availableStrings[0];
                break;
        }

        return hintString;
    }
}
