/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package archie_v1.UI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author N.C. Mulder <n.c.mulder at students.uu.nl>
 */
public class PreferenceChanger extends JPanel implements ActionListener{
    
    MainFrame parent;
    JButton defaultValuesButton, cancel, save, reset;
    
    public PreferenceChanger(MainFrame parent){
        this.parent = parent;
        this.setLayout(new BorderLayout());
        
        JPanel options = new JPanel(new GridLayout(0,1));
        
        JPanel test = new JPanel();
        defaultValuesButton = new JButton("Change default values");
        defaultValuesButton.setHorizontalAlignment(JButton.LEFT);
        defaultValuesButton.addActionListener(this);
        test.add(defaultValuesButton);
        
        
        options.add(test);
        
        
        this.add(options, BorderLayout.CENTER);
        
        JPanel bottom = new JPanel();
        
        cancel = new JButton("Cancel");
        cancel.addActionListener(this);
        bottom.add(cancel);
        save = new JButton("Save");
        save.addActionListener(this);
        bottom.add(save);
        reset = new JButton("Reset");
        reset.addActionListener(this);
        bottom.add(reset);
        
        this.add(bottom, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == cancel){
            parent.ChangeMainPanel(parent.prevPanel);
        } else if (e.getSource() == defaultValuesButton){
            System.out.println("huh");
        } else {
            System.out.println(e);
        }
    }
    
}
