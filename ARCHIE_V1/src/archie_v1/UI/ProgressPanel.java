/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package archie_v1.UI;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author N.C. Mulder <n.c.mulder at students.uu.nl>
 */
public class ProgressPanel extends JPanel{
    JProgressBar progBar;
    int taskLength;
        
    public ProgressPanel(int taskLength){
        this.taskLength = taskLength;
        this.setDoubleBuffered(true);
        this.setLayout(new BorderLayout());
        JLabel wOIL = new JLabel("Generating metadata...", SwingConstants.CENTER);
        Font ft = new Font("Helvetica", 36, 36);
        wOIL.setFont(ft);
        wOIL.setBorder(new EmptyBorder(100,0,0,0));

        this.add(wOIL, BorderLayout.NORTH);
        
        progBar = new JProgressBar(0, taskLength);
        this.add(progBar, BorderLayout.SOUTH);
    }
    
    public void pingProgBar(){
        progBar.setValue(progBar.getValue() + 1);
        //redo this concurrently.
        super.paint(super.getGraphics());
    }
    
    public void setProgBar(int value){
        progBar.setValue(value);
    }
}
