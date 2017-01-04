/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package archie_v1.UI;

import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 *
 * @author N.C. Mulder <n.c.mulder at students.uu.nl>
 */
public class ProgressPanel extends JPanel{
    JProgressBar progBar;
        
    public ProgressPanel(int taskLength){
        progBar = new JProgressBar(0, taskLength);
        this.add(progBar);
    }
    
    public void pingProgBar(){
        progBar.setValue(progBar.getValue() + 1);
    }
    
    public void setProgBar(int value){
        progBar.setValue(value);
    }
}
