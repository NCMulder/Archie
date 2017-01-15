/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package archie_v1.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
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
        
        addItem = new JButton("Add " + singleTitle);
        
        createPanel();
    }
    
    private void createPanel(){
        this.setLayout(new GridBagLayout());
        Border paneBorder = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), pluralTitle);
        this.setBorder(paneBorder);
        
        JScrollPane values = Values();
        GridBagConstraints gbc = new GridBagConstraints(
                        0, 0, //GridX, GridY
                        5, height, //GridWidth, GridHeight
                        1, height/(height+1), //WeightX, WeightY
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(0, 0, 0, 0), 0, 0); //Insets, IpadX, IpadY
        this.add(values, gbc);
        
        JPanel bottomPane = BottomPane();
        gbc = new GridBagConstraints(
                        0, height++, //GridX, GridY
                        5, 1, //GridWidth, GridHeight
                        1, 1/height, //WeightX, WeightY
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(0, 0, 0, 0), 0, 0); //Insets, IpadX, IpadY
        this.add(bottomPane, gbc);
    }

    private JScrollPane Values() {
        JPanel gridPane = new JPanel(new GridBagLayout());
        GridBagConstraints gbc;
        
        xweight = 1f / (Values.length + 1);
        String[] mainKeys = Values[0].split(";");
        height = mainKeys.length;
        
        for(int i = 0; i < height; i++){
            for(int j = 0; j < Values.length; j++){
                JLabel label = new JLabel(Values[j].split(";")[i].trim());
                gbc = new GridBagConstraints(
                        j, i, //GridX, GridY
                        1, 1, //GridWidth, GridHeight
                        xweight, 1, //WeightX, WeightY
                        GridBagConstraints.WEST, GridBagConstraints.BOTH, //Anchor, Fill
                        new Insets(2, 2, 2, 2), 0, 0); //Insets, IpadX, IpadY
                gridPane.add(label, gbc);
            }
            
            JButton remove = new JButton("Remove");
            gbc = new GridBagConstraints(
                        Values.length, i, //GridX, GridY
                        1, 1, //GridWidth, GridHeight
                        xweight, 1, //WeightX, WeightY
                        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(2, 2, 2, 2), 0, 0); //Insets, IpadX, IpadY
            gridPane.add(remove, gbc);
        }
        
        JScrollPane scrollPane = new JScrollPane(gridPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
        
        return scrollPane;
    }

    private JPanel BottomPane() {
        JPanel BottomPane = new JPanel(new GridBagLayout());
        BottomPane.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
        
        int test = Values.length * 2;
        
        GridBagConstraints gbc;
        
        JPanel filler = new JPanel();
        gbc = new GridBagConstraints(
                        0, 0, //GridX, GridY
                        Values.length, 1, //GridWidth, GridHeight
                        1-xweight, 1, //WeightX, WeightY
                        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(test, test, test, test), 0, 0); //Insets, IpadX, IpadY
        BottomPane.add(filler, gbc);
        
        gbc = new GridBagConstraints(
                        Values.length, 0, //GridX, GridY
                        1, 1, //GridWidth, GridHeight
                        xweight, 1, //WeightX, WeightY
                        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                        new Insets(2, 2, 2, 2), 0, 0); //Insets, IpadX, IpadY
        BottomPane.add(addItem, gbc);
        
        return BottomPane;
    }
}