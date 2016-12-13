//License
package archie_v1.UI;

import archie_v1.fileHelpers.DatasetInitialInformation;
import archie_v1.fileHelpers.MetadataContainer;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
        
        JLabel creatorTOALabel = new JLabel("Creator TOA: ");
        creatorTOA = new JComboBox(MetadataContainer.toas);
        topPanel.add(creatorTOALabel); topPanel.add(creatorTOA);
        
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
        
        JLabel languageLabel = new JLabel("Language: ");
        language = new JComboBox(MetadataContainer.langs);
        topPanel.add(languageLabel); topPanel.add(language);
        
        JLabel temporalCoverageLabel = new JLabel("Temporal coverage: ");
        temporalCoverage = new JTextField();
        topPanel.add(temporalCoverageLabel); topPanel.add(temporalCoverage);
        
        JLabel spatialCoverageLabel = new JLabel("Spatial coverage: ");
        spatialCoverage = new JTextField();
        topPanel.add(spatialCoverageLabel); topPanel.add(spatialCoverage);
        
        JLabel accessLevelLabel = new JLabel("Access level: ");
        accessLevel = new JComboBox(MetadataContainer.access);
        topPanel.add(accessLevelLabel); topPanel.add(accessLevel);
        
        //wont work?
        //JScrollPane scrollPane = new JScrollPane();
        //scrollPane.add(topPanel);
        
        //change default folder to more logical folder.
        fileChooser = new JFileChooser("C:\\Users\\niels\\Documents\\Archie\\Testset\\testset");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.addActionListener(this);
        fileChooser.setApproveButtonText("Generate");
        

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
                
                DatasetInitialInformation diI = 
                        new DatasetInitialInformation(creatorName.getText(), 
                                                      creatorAffiliation.getText(), 
                                                      creatorTOA.getSelectedItem().toString(), 
                                                      creatorIdentifier.getText(),
                                                      contributorName.getText(), 
                                                      rightsholder.getText(), 
                                                      subject.getText(), 
                                                      description.getText(), 
                                                      publisher.getText(), 
                                                      language.getSelectedItem().toString(), 
                                                      temporalCoverage.getText(), 
                                                      spatialCoverage.getText(), 
                                                      accessLevel.getSelectedItem().toString());
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
