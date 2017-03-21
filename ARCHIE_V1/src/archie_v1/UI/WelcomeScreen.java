//License
package archie_v1.UI;

import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class WelcomeScreen extends JPanel {

    public WelcomeScreen() {
        this.setLayout(new GridLayout(0,1));
        JLabel welcomeLabel = new JLabel("Welcome to Archie", SwingConstants.CENTER);
        Font ft = new Font(welcomeLabel.getFont().getFontName(), 36, 36);
        welcomeLabel.setFont(ft);
        JLabel instructionLabel = new JLabel("<html><center>To generate metadata for a new dataset, go to Dataset>New.<br>To open an existing project, go to Dataset>Open.<br><br><br><br><font size=\"3\">Developed bij Nexus1492, an ERC Synergy Project.", SwingConstants.CENTER);
        Font ft2 = new Font(instructionLabel.getFont().getFontName(), 16, 16);
        instructionLabel.setFont(ft2);

        this.add(welcomeLabel);
        this.add(instructionLabel);
    }

}
