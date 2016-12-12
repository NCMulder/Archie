//License
package archie_v1.UI;

import archie_v1.fileHelpers.DatasetInitialInformation;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 *
 * @author niels
 */
public class NewDataset extends JPanel implements ActionListener {
    
    public JTextField datasetName, creatorName, creatorAffiliation, creatorIdentifier,
                      contributorName, rightsholder, subject, description, publisher, 
                      temporalCoverage, spatialCoverage;
    public JComboBox creatorTOA, language, accessLevel;
    public JFileChooser fileChooser;
    private MainFrame parent;
    
    public NewDataset(MainFrame parent){
        this.parent = parent;
//        
//        this.setLayout(new GridBagLayout());
        fileChooser = new JFileChooser("C:\\Users\\niels\\Documents\\Archie\\Testset\\testset");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.addActionListener(this);
        fileChooser.setApproveButtonText("Generate");
//        datasetName = new JTextField("testset");
//        GridBagConstraints tfg      = new GridBagConstraints(2, 0, 1, 1, .4, .25, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 100), 0, 0);
//        
//        JLabel nameLabel = new JLabel("Name:");
//        GridBagConstraints nameg    = new GridBagConstraints(1, 0, 1, 1, .2, .25, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0, 100, 0, 0), 0, 0);
//        
//        GridBagConstraints fcg      = new GridBagConstraints(1, 1, 3, 4, .6, .75, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 60, 60, 60), 0, 0);
//        
//        this.add(nameLabel, nameg);
//        this.add(datasetName, tfg);
//        this.add(fileChooser, fcg);
        
        this.setLayout(new GridLayout(0,1));
        
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(0,2));
        
        JLabel datasetNameLabel = new JLabel("Dataset name: ");
        datasetName = new JTextField("test");
        topPanel.add(datasetNameLabel); topPanel.add(datasetName);
        
        JLabel creatorNameLabel = new JLabel("Creator name: ");
        creatorName = new JTextField();
        topPanel.add(creatorNameLabel); topPanel.add(creatorName);
        
        JLabel creatorAffiliationLabel = new JLabel("Creator affiliation: ");
        creatorAffiliation = new JTextField();
        topPanel.add(creatorAffiliationLabel); topPanel.add(creatorAffiliation);
        
        JLabel creatorIdentifierLabel = new JLabel("Creator identifier: ");
        creatorIdentifier = new JTextField();
        topPanel.add(creatorIdentifierLabel); topPanel.add(creatorIdentifier);
        
        JLabel contributorNameLabel = new JLabel("Contributor name: ");
        contributorName = new JTextField();
        topPanel.add(contributorNameLabel); topPanel.add(contributorName);
        
        JLabel rightsholderLabel = new JLabel("Rightsholder: ");
        rightsholder = new JTextField();
        topPanel.add(rightsholderLabel); topPanel.add(rightsholder);
        
        JLabel subjectLabel = new JLabel("Subject: ");
        subject = new JTextField();
        topPanel.add(subjectLabel); topPanel.add(subject);
        
        JLabel descriptionLabel = new JLabel("Description: ");
        description = new JTextField();
        topPanel.add(descriptionLabel); topPanel.add(description);
        
        JLabel publisherLabel = new JLabel("Publisher: ");
        publisher = new JTextField();
        topPanel.add(publisherLabel); topPanel.add(publisher);
        
        JLabel temporalCoverageLabel = new JLabel("Temporal coverage: ");
        temporalCoverage = new JTextField();
        topPanel.add(temporalCoverageLabel); topPanel.add(temporalCoverage);
        
        JLabel spatialCoverageLabel = new JLabel("Spatial coverage: ");
        spatialCoverage = new JTextField();
        topPanel.add(spatialCoverageLabel); topPanel.add(spatialCoverage);
        
        //wont work?
//        JScrollPane scrollPane = new JScrollPane();
//        scrollPane.add(topPanel);
        
        this.add(topPanel);
        this.add(fileChooser);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand()=="CancelSelection"){
            parent.goToHome();
        }
        else if (e.getActionCommand()=="ApproveSelection"){
            if("".equals(datasetName.getText())){
                JOptionPane.showMessageDialog(this, "The name of a dataset can not be empty.");
            } else {
                parent.remove(parent.mainPanel);
                parent.mainPanel = parent.WorkingOnItPanel();
                parent.add(parent.mainPanel, BorderLayout.CENTER);
                parent.pack();
                parent.paint(parent.getGraphics());
                
                //todo: get includeislandora
                DatasetInitialInformation diI = new DatasetInitialInformation(creatorName.getText(), creatorAffiliation.getText(), "None", creatorIdentifier.getText(),
                      contributorName.getText(), rightsholder.getText(), subject.getText(), description.getText(), publisher.getText(), 
                      "English", temporalCoverage.getText(), spatialCoverage.getText(), "Open access");
                parent.metadatachanger = new MetadataChanger(datasetName.getText(), fileChooser.getSelectedFile().toPath(), false, true, diI);
                parent.remove(parent.mainPanel);
                parent.mainPanel = parent.metadatachanger;
                parent.add(parent.mainPanel,BorderLayout.CENTER);
                parent.validate();
                parent.pack();
            }
        }
    }
}
