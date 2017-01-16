/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package archie_v1.UI;

import archie_v1.fileHelpers.MetadataKey;
import java.awt.GridLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author niels
 */
public class AddMetadataField extends JFrame {
    JComboBox metadataKey;
    JTextField metadataValue;
    
    public AddMetadataField(){
        super();
        metadataKey = new JComboBox(MetadataKey.names());
        metadataValue = new JTextField("your value here");
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0,2));
        panel.add(metadataKey);
        panel.add(metadataValue);
        this.add(panel);
        this.pack();
        this.setVisible(true);
    }
}
