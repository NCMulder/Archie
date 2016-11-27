/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package archie_v1.UI;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author niels
 */
public class NewDataset extends JPanel implements ActionListener {
    
    public JTextField name;
    public JFileChooser fileChooser;
    private MainFrame parent;
    
    public NewDataset(MainFrame parent){
        this.parent = parent;
        
        this.setLayout(new GridBagLayout());
        fileChooser = new JFileChooser("C:\\Users\\niels\\Documents\\Archie\\Archie\\Documentation");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.addActionListener(this);
        fileChooser.setApproveButtonText("Generate");
        
        name = new JTextField();
        GridBagConstraints tfg      = new GridBagConstraints(2, 0, 1, 1, .4, .25, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 100), 0, 0);
        
        JLabel nameLabel = new JLabel("Name:");
        GridBagConstraints nameg    = new GridBagConstraints(1, 0, 1, 1, .2, .25, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0, 100, 0, 0), 0, 0);
        
        GridBagConstraints fcg      = new GridBagConstraints(1, 1, 3, 4, .6, .75, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 60, 60, 60), 0, 0);
        
        this.add(nameLabel, nameg);
        this.add(name, tfg);
        this.add(fileChooser, fcg);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand()=="CancelSelection"){
            parent.goToHome();
        }
        else if (e.getActionCommand()=="ApproveSelection"){
            if("".equals(name.getText())){
                JOptionPane.showMessageDialog(this, "The name of a dataset can not be empty.");
            } else {
                parent.remove(parent.mainPanel);
                parent.mainPanel = parent.WorkingOnItPanel();
                parent.add(parent.mainPanel, BorderLayout.CENTER);
                parent.pack();
                parent.paint(parent.getGraphics());
                
                //todo: get name
                parent.metadatachanger = new MetaDataChanger(name.getText(), fileChooser.getSelectedFile().toPath(), false);
                parent.remove(parent.mainPanel);
                parent.mainPanel = parent.metadatachanger;
                parent.add(parent.mainPanel,BorderLayout.CENTER);
                parent.validate();
                parent.pack();
                System.out.println(parent.mainPanel);
            }
        }
    }
}
