/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package archie_v1.UI;

import java.awt.Component;
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class AboutScreen extends JPanel {

    public AboutScreen() {
        this.setLayout(new GridBagLayout());
        
        JLabel aboutLabel = new JLabel("About Archie", JLabel.CENTER);
        Font ft1 = new Font(aboutLabel.getFont().getFontName(), 36, 36);
        aboutLabel.setFont(ft1);
        GridBagConstraints gbc = new GridBagConstraints(
                0, 0, //GridX, GridY
                3, 1, //GridWidth, GridHeight
                1, 1, //WeightX, WeightY
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY);
        this.add(aboutLabel, gbc);
        
        JLabel introductionLabel = new JLabel(
                "<html><center>Archie was developed at the University of Leiden, Faculty of Archaeology<br>"
                + "as part of the Nexus1492 project (www.nexus1492.eu), directed by Corinne L. Hofman.<br><br>",
                JLabel.CENTER);
        Font ft2 = new Font(introductionLabel.getFont().getFontName(), 16, 16);
        introductionLabel.setFont(ft2);
        gbc = new GridBagConstraints(
                0, 1, //GridX, GridY
                3, 1, //GridWidth, GridHeight
                1, 1, //WeightX, WeightY
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY);
        this.add(introductionLabel, gbc);
        
        JLabel developmentLabel = new JLabel("Development:", JLabel.RIGHT);
        developmentLabel.setFont(ft2);
        gbc = new GridBagConstraints(
                0, 2, //GridX, GridY
                1, 1, //GridWidth, GridHeight
                .3, 1, //WeightX, WeightY
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY);
        this.add(developmentLabel, gbc);
        
        JLabel mnLabel = new JLabel("Niels Mulder", JLabel.CENTER);
        mnLabel.setFont(ft2);
        gbc = new GridBagConstraints(
                1, 2, //GridX, GridY
                1, 1, //GridWidth, GridHeight
                0.1, 1, //WeightX, WeightY
                GridBagConstraints.CENTER, GridBagConstraints.NONE, //Anchor, Fill
                new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY);
        this.add(mnLabel, gbc);
        
        JLabel meLabel = new JLabel("n.c.mulder [at] students.uu.nl", JLabel.LEFT);
        meLabel.setFont(ft2);
        gbc = new GridBagConstraints(
                2, 2, //GridX, GridY
                1, 1, //GridWidth, GridHeight
                .3, 1, //WeightX, WeightY
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY);
        this.add(meLabel, gbc);
        
        JLabel designLabel = new JLabel("Concept/Functional design:", JLabel.RIGHT);
        designLabel.setFont(ft2);
        gbc = new GridBagConstraints(
                0, 3, //GridX, GridY
                1, 1, //GridWidth, GridHeight
                .3, 1, //WeightX, WeightY
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY);
        this.add(designLabel, gbc);
        
        JLabel knLabel = new JLabel("Drs. W. Kool", JLabel.CENTER);
        knLabel.setFont(ft2);
        gbc = new GridBagConstraints(
                1, 3, //GridX, GridY
                1, 1, //GridWidth, GridHeight
                0.1, 1, //WeightX, WeightY
                GridBagConstraints.CENTER, GridBagConstraints.NONE, //Anchor, Fill
                new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY);
        this.add(knLabel, gbc);
        
        JLabel keLabel = new JLabel("w.kool [at] arch.leidenuniv.nl", JLabel.LEFT);
        keLabel.setFont(ft2);
        gbc = new GridBagConstraints(
                2, 3, //GridX, GridY
                1, 1, //GridWidth, GridHeight
                .3, 1, //WeightX, WeightY
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY);
        this.add(keLabel, gbc);
        
        JLabel gitLabel = new JLabel("https://github.com/NCMulder/Archie", JLabel.CENTER);
        gitLabel.setFont(ft2);
        gbc = new GridBagConstraints(
                0, 4, //GridX, GridY
                3, 1, //GridWidth, GridHeight
                1, 1, //WeightX, WeightY
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, //Anchor, Fill
                new Insets(4, 4, 4, 4), 3, 3); //Insets, IpadX, IpadY);
        this.add(gitLabel, gbc);
    }

}
