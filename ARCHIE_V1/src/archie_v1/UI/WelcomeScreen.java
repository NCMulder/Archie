//License
package archie_v1.UI;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class WelcomeScreen extends JPanel {

    public WelcomeScreen() {
        JLabel welcomeLabel = new JLabel("Welcome to Archie");
        Font ft = new Font("Helvetica", 36, 36);
        welcomeLabel.setFont(ft);
        //welcomeLabel.setBorder(new EmptyBorder(200, 200, 200, 200));

        this.add(welcomeLabel);
    }

}
