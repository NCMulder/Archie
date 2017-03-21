/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package archie_v1.UI;

import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author N.C. Mulder <n.c.mulder at students.uu.nl>
 */

import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class AboutScreen extends JPanel {

    public AboutScreen() {
        this.setLayout(new GridLayout(0,1));
        JLabel aboutLabel = new JLabel("About Archie", SwingConstants.CENTER);
        Font ft = new Font(aboutLabel.getFont().getFontName(), 36, 36);
        aboutLabel.setFont(ft);
        JLabel instructionLabel = new JLabel(
                "<html><center>Archie was developed at the University of Leiden, Faculty of Archaeology as part of the Nexus1492 project (www.nexus1492.eu)<br>"
                        + "Development: Niels Mulder\tn.c.mulder [at] students.uu.nl<br>"
                        + "Concept / Functional design: Wouter Kool\tw.kool [at] arch.leidenuniv.nl<br>"
                        + "https://github.com/NCMulder/Archie"
                , SwingConstants.CENTER);
        Font ft2 = new Font(instructionLabel.getFont().getFontName(), 16, 16);
        instructionLabel.setFont(ft2);

        this.add(aboutLabel);
        this.add(instructionLabel);
    }

}