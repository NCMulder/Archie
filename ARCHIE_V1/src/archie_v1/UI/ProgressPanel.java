/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package archie_v1.UI;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author N.C. Mulder <n.c.mulder at students.uu.nl>
 */
public class ProgressPanel extends JPanel {

    JProgressBar progBar;
    JLabel progressLabel;
    int taskLength, progress = 0;
    String fileText = "File 0 / 0", baseText = "Generating metadata...";

    public ProgressPanel(int taskLength) {
        this.taskLength = taskLength;
        
        SetupUI();
    }
    
    private void SetupUI(){
        this.setLayout(new BorderLayout());

        Font ft = new Font("Helvetica", 36, 36);
        fileText = "File " + progress + " / " + taskLength;

        JLabel baseLabel = new JLabel(baseText, SwingConstants.CENTER);
        baseLabel.setFont(ft);
        baseLabel.setBorder(new EmptyBorder(100, 0, 0, 0));

        progressLabel = new JLabel(fileText, SwingConstants.CENTER);
        progressLabel.setFont(ft);
        progressLabel.setBorder(new EmptyBorder(100, 0, 0, 0));

        JPanel textPane = new JPanel(new GridLayout(0, 1));
        textPane.add(baseLabel);
        textPane.add(progressLabel);

        this.add(textPane, BorderLayout.NORTH);

        progBar = new JProgressBar(0, taskLength);
        this.add(progBar, BorderLayout.SOUTH);
    }

    public void pingProgBar() {
        progress++;
        fileText = "File " + progress + " / " + taskLength;
        progressLabel.setText(fileText);
        progBar.setValue(progress);
        
        //REDO TODO WIP: Very bad code smell. Make concurrently so paint is called automatically (or at least repaint suffices).
        revalidate();
        super.paint(super.getGraphics());
    }

    public void setProgBar(int value) {
        progBar.setValue(value);
    }
}
