/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package archie_v1.UI;

import archie_v1.fileHelpers.MetadataContainer;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author N.C. Mulder <n.c.mulder at students.uu.nl>
 */
public class SetPart extends JPanel {

    private String[] identifierTypes = {"DAI", "ORCID", "ISNI"};
    public JComboBox idComboBox;

    public JTextField idField, toaField, nameGivenField, nameFamilyField, affiliationField;
    public JTextField subjectField, relatedDatasetNameField, relatedDatasetLocationField;
    private Addable role;

    public enum Addable {
        Creator, Contributor, Subject, RelatedDataset
    };

    public SetPart(Addable role) {
        this.role = role;
        setUI();
    }

    public void setUI() {
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        switch (role) {
            case Creator:
            case Contributor:
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

                idField = new JTextField(20);
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

                toaField = new JTextField(20);
                gbc = new GridBagConstraints(
                        2, 1, //GridX, GridY
                        3, 1, //GridWidth, GridHeight
                        1, 1, //WeightX, WeightY
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                this.add(toaField, gbc);

                JLabel nameLabel = (role == Addable.Creator) ? new JLabel("Creator Name") : new JLabel("Contributor Name");
                gbc = new GridBagConstraints(
                        0, 2, //GridX, GridY
                        2, 1, //GridWidth, GridHeight
                        1, 1, //WeightX, WeightY
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                this.add(nameLabel, gbc);

                nameGivenField = new JTextField("H.F.");
                gbc = new GridBagConstraints(
                        2, 2, //GridX, GridY
                        1, 1, //GridWidth, GridHeight
                        1, 1, //WeightX, WeightY
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                this.add(nameGivenField, gbc);

                nameFamilyField = new JTextField("Coninck", 20);
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

                affiliationField = new JTextField(20);
                gbc = new GridBagConstraints(
                        2, 3, //GridX, GridY
                        3, 1, //GridWidth, GridHeight
                        1, 1, //WeightX, WeightY
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                this.add(affiliationField, gbc);

                break;
            case Subject:
                JLabel subjectLabel = new JLabel("Subject");
                gbc = new GridBagConstraints(
                        0, 0, //GridX, GridY
                        2, 1, //GridWidth, GridHeight
                        1, 1, //WeightX, WeightY
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                this.add(subjectLabel, gbc);

                subjectField = new JTextField(20);
                gbc = new GridBagConstraints(
                        2, 0, //GridX, GridY
                        3, 1, //GridWidth, GridHeight
                        1, 1, //WeightX, WeightY
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                this.add(subjectField, gbc);

                break;
            case RelatedDataset:
                JLabel relatedDatasetNameLabel = new JLabel("Dataset name");
                gbc = new GridBagConstraints(
                        0, 0, //GridX, GridY
                        2, 1, //GridWidth, GridHeight
                        1, 1, //WeightX, WeightY
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                this.add(relatedDatasetNameLabel, gbc);

                relatedDatasetNameField = new JTextField(20);
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

                relatedDatasetLocationField = new JTextField(20);
                gbc = new GridBagConstraints(
                        2, 1, //GridX, GridY
                        3, 1, //GridWidth, GridHeight
                        1, 1, //WeightX, WeightY
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY
                this.add(relatedDatasetLocationField, gbc);

                break;
        }
    }

    public HashMap<MetadataContainer.MetadataKey, String> getInfo() {
        HashMap<MetadataContainer.MetadataKey, String> info = new HashMap();

        switch (role) {
            case Creator:
                //Todo: differentiate between ID types
                info.put(MetadataContainer.MetadataKey.CreatorIdentifier, idComboBox.getSelectedItem().toString() + " " + idField.getText());
                info.put(MetadataContainer.MetadataKey.CreatorTOA, toaField.getText());
                //Todo: differentiate between name parts
                info.put(MetadataContainer.MetadataKey.CreatorName, nameGivenField.getText() + " " + nameFamilyField.getText());
                info.put(MetadataContainer.MetadataKey.CreatorAffiliation, affiliationField.getText());
                break;
            case Contributor:
                //Todo: differentiate between ID types
                info.put(MetadataContainer.MetadataKey.ContributorIdentifier, idComboBox.getSelectedItem().toString() + " " + idField.getText());
                info.put(MetadataContainer.MetadataKey.ContributorTOA, toaField.getText());
                //Todo: differentiate between name parts
                info.put(MetadataContainer.MetadataKey.ContributorName, nameGivenField.getText() + " " + nameFamilyField.getText());
                info.put(MetadataContainer.MetadataKey.ContributorAffiliation, affiliationField.getText());
                break;
            case Subject:
                info.put(MetadataContainer.MetadataKey.Subject, subjectField.getText());
                break;
            case RelatedDataset:
                info.put(MetadataContainer.MetadataKey.RelatedDatasetName, relatedDatasetNameField.getText());
                info.put(MetadataContainer.MetadataKey.RelatedDatasetLocation, relatedDatasetLocationField.getText());
                break;
        }

        return info;
    }
}