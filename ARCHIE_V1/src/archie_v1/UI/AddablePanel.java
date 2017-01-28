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
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

/**
 *
 * @author N.C. Mulder <n.c.mulder at students.uu.nl>
 */
public class AddablePanel extends JPanel implements ActionListener {

    MetadataKey[] Values;
    String singleTitle, pluralTitle;
    public int height = 0;
    JComponent mainPanel;

    JButton addItem;
    HashMap<JButton, Integer> removeButtons;

    HashMap<Integer, JPanel> valuePanels;

    private float xweight;
    private FileHelper fileHelper;

    public AddablePanel(String singleTitle, String pluralTitle, MetadataKey[] Values, FileHelper fileHelper) {
        this.singleTitle = singleTitle;
        this.pluralTitle = pluralTitle;
        this.Values = Values;
        this.fileHelper = fileHelper;
        removeButtons = new HashMap();
        valuePanels = new HashMap();

        createPanel();
    }

    private void resetMainPanel() {
        this.remove(mainPanel);
        createPanel();
        revalidate();
    }

    private void createPanel() {
        this.setLayout(new GridBagLayout());
        Border paneBorder = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), pluralTitle);
        this.setBorder(paneBorder);

        mainPanel = Values();

        GridBagConstraints gbc = new GridBagConstraints(
                0, 0, //GridX, GridY
                5, height, //GridWidth, GridHeight
                1, height / (height + 1), //WeightX, WeightY
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                new Insets(0, 0, 0, 0), 0, 0); //Insets, IpadX, IpadY
        this.add(mainPanel, gbc);
    }

    private JComponent Values() {
        JPanel gridPane = new JPanel(new GridBagLayout());
        GridBagConstraints gbc;

        if (fileHelper.metadataMap.get(Values[0]) != null) {
            System.out.println("Addable " + Values[0] + " with value " + fileHelper.metadataMap.get(Values[0]) + " is being prepared.");

            xweight = .8f / (Values.length);
            String[] mainKeys = fileHelper.metadataMap.get(Values[0]).split(";");
            height = mainKeys.length;

            for (int i = 0; i < height; i++) {
                JPanel singleValue = new JPanel(new GridBagLayout());

                for (int j = 0; j < Values.length; j++) {
                    String labelText = "";
                    try {
                        labelText = fileHelper.metadataMap.get(Values[j]).split(";")[i].trim();
                    } catch (NullPointerException | ArrayIndexOutOfBoundsException ex) {
                        String errorMessage = "No " + Values[j] + " found for " + singleTitle + " " + fileHelper.metadataMap.get(Values[0]).split(";")[i].trim();
                        errorMessage += ". \nPlease provide the missing component.";
                        JOptionPane.showConfirmDialog(this, errorMessage, "Missing " + Values[j], 0);

                        String[] availableStrings = new String[5];

                        for (int k = 0; k < Values.length; k++) {
                            try {
                                availableStrings[k] = fileHelper.metadataMap.get(Values[k]).split(";")[i].trim();
                            } catch (Exception e) {
                                availableStrings[k] = "";
                            }
                        }

                        SetPart sp = new SetPart(Values[0], availableStrings);
                        Object[] buttons = {"Add", "Cancel"};
                        int result = JOptionPane.showOptionDialog(this, sp, "Missing " + Values[j], JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, buttons[0]);
                        if (result == JOptionPane.OK_OPTION) {
                            for (Map.Entry<MetadataKey, String> metadata : sp.getInfo().entrySet()) {
                                String previousRecord = (fileHelper.metadataMap.get(metadata.getKey()) != null) ? fileHelper.metadataMap.get(metadata.getKey()) + ";" : "";
                                fileHelper.setRecord(metadata.getKey(), previousRecord + metadata.getValue(), true);
                            }
                        }
                    }
                    JLabel label = new JLabel(labelText);
                    gbc = new GridBagConstraints(
                            j, 0, //GridX, GridY
                            1, 1, //GridWidth, GridHeight
                            xweight, 1, //WeightX, WeightY
                            GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                            new Insets(2, 2, 2, 2), 0, 0); //Insets, IpadX, IpadY
                    singleValue.add(label, gbc);
                    //System.out.println("Labels size: " + (j * xweight) + "; Total size: " + (j * xweight + 0.2f));
                }

                JButton remove = new JButton("Remove");
                remove.addActionListener(this);
                gbc = new GridBagConstraints(
                        4, 0, //GridX, GridY
                        1, 1, //GridWidth, GridHeight
                        .2f, 1, //WeightX, WeightY
                        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(2, 2, 2, 2), 0, 0); //Insets, IpadX, IpadY
                singleValue.add(remove, gbc);

                gbc = new GridBagConstraints(
                        0, i, //GridX, GridY
                        5, 1, //GridWidth, GridHeight
                        1, 1, //WeightX, WeightY
                        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(2, 2, 2, 2), 0, 0); //Insets, IpadX, IpadY
                gridPane.add(singleValue, gbc);

                valuePanels.put(i, singleValue);
                removeButtons.put(remove, i);
            }
        }

        addItem = new JButton("Add " + singleTitle);
        addItem.addActionListener(this);
        gbc = new GridBagConstraints(
                4, height++, //GridX, GridY
                1, 1, //GridWidth, GridHeight
                .2f, 1, //WeightX, WeightY
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                new Insets(2, 2, 2, 2), 0, 0); //Insets, IpadX, IpadY
        gridPane.add(addItem, gbc);

        JScrollPane scrollPane = new JScrollPane(gridPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        return gridPane;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int f = removeButtons.getOrDefault(e.getSource(), -1);
        //Todo: only do this on save from above.
        if (f != -1) {
            for (int i = 0; i < Values.length; i++) {
                fileHelper.removeRecord(Values[i], f, true, true);
            }
            resetMainPanel();
        } else if (e.getSource() == addItem) {
            String[] strings = new String[Values.length];
            
            
            SetPart sp = new SetPart(Values[0]);
            String title = singleTitle + " addition";

            Object[] buttons = {"Add", "Cancel"};
            int result = JOptionPane.showOptionDialog(this, sp, title, JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, buttons[0]);
            if (result == JOptionPane.OK_OPTION) {
                for (Map.Entry<MetadataKey, String> metadata : sp.getInfo().entrySet()) {
                    fileHelper.setRecord(metadata.getKey(), metadata.getValue(), true);
                }
                resetMainPanel();
            }
        } else {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
