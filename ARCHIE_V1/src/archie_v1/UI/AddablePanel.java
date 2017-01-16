/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package archie_v1.UI;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

/**
 *
 * @author N.C. Mulder <n.c.mulder at students.uu.nl>
 */
public class AddablePanel extends JPanel{
    
    String[] Values;
    String singleTitle, pluralTitle;
    public int height;
    
    JButton addItem;
    private float xweight;
    
    public AddablePanel(String singleTitle, String pluralTitle, String[] Values){
        this.singleTitle = singleTitle;
        this.pluralTitle = pluralTitle;
        this.Values = Values;
        
        createPanel();
    }
    
    private void createPanel(){
        this.setLayout(new GridBagLayout());
        Border paneBorder = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), pluralTitle);
        this.setBorder(paneBorder);
        
        JComponent values = Values();
        GridBagConstraints gbc = new GridBagConstraints(
                        0, 0, //GridX, GridY
                        5, height, //GridWidth, GridHeight
                        1, height/(height+1), //WeightX, WeightY
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(0, 0, 0, 0), 0, 0); //Insets, IpadX, IpadY
        this.add(values, gbc);
    }

    private JComponent Values() {
        JPanel gridPane = new JPanel(new GridBagLayout());
        GridBagConstraints gbc;
        
        xweight = .8f / (Values.length);
        String[] mainKeys = Values[0].split(";");
        height = mainKeys.length;
        
        for(int i = 0; i < height; i++){
            for(int j = 0; j < Values.length; j++){
                JLabel label = new JLabel(Values[j].split(";")[i].trim());
                gbc = new GridBagConstraints(
                        j, i, //GridX, GridY
                        1, 1, //GridWidth, GridHeight
                        xweight, 1, //WeightX, WeightY
                        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(2, 2, 2, 2), 0, 0); //Insets, IpadX, IpadY
                gridPane.add(label, gbc);
                System.out.println("Labels size: " + (j * xweight) + "; Total size: " + (j * xweight + 0.2f));
            }
            
            JButton remove = new JButton("Remove");
            gbc = new GridBagConstraints(
                        4, i, //GridX, GridY
                        1, 1, //GridWidth, GridHeight
                        .2f, 1, //WeightX, WeightY
                        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(2, 2, 2, 2), 0, 0); //Insets, IpadX, IpadY
            gridPane.add(remove, gbc);
        }
        
        addItem = new JButton("Add " + singleTitle);
        gbc = new GridBagConstraints(
                        4, height, //GridX, GridY
                        1, 1, //GridWidth, GridHeight
                        .2f, 1, //WeightX, WeightY
                        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(2, 2, 2, 2), 0, 0); //Insets, IpadX, IpadY
        gridPane.add(addItem, gbc);
        
        JScrollPane scrollPane = new JScrollPane(gridPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
        
        return gridPane;
    }
}