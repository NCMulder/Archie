//License
package archie_v1.UI;

import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class WelcomeScreen extends JPanel {

    public WelcomeScreen() {
        JLabel welcomeLabel = new JLabel("Welcome to Archie");
        Font ft = new Font("Helvetica", 36, 36);
        welcomeLabel.setFont(ft);

        this.add(welcomeLabel);
    }

}
