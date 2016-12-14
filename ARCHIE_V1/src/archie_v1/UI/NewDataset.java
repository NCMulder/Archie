//License
package archie_v1.UI;

import archie_v1.fileHelpers.DatasetInitialInformation;
import archie_v1.fileHelpers.MetadataContainer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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
    private DatasetInitialInformation dataII = new DatasetInitialInformation();
    private LinkedHashMap<MetadataContainer.MetadataKey, JComponent> keyToText;
    
    public NewDataset(MainFrame parent){
        this.parent = parent;
        keyToText = new LinkedHashMap();
        
        this.setLayout(new GridLayout(0,1));
        this.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 10));
        
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(0,2));
        
        JLabel datasetNameLabel = new JLabel("Dataset name: ");
        datasetName = new JTextField("test");
        topPanel.add(datasetNameLabel); topPanel.add(datasetName);
        
        for(MetadataContainer.MetadataKey metadataKey : dataII.initKeys){
            JLabel keyLabel = new JLabel(metadataKey.toString() + " :");
            JComponent keyValue;
            if(metadataKey.settable){
                JTextField tobeValue = new JTextField(metadataKey.getDefaultValue(), 20);
                tobeValue.setForeground(Color.LIGHT_GRAY);
                (tobeValue.getDocument()).addDocumentListener(new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        changeColor();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        changeColor();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        changeColor();
                    }
                    
                    public void changeColor(){
                        if(!metadataKey.getDefaultValue().equals(tobeValue.getText()))
                            tobeValue.setForeground(Color.BLACK);
                        else
                            tobeValue.setForeground(Color.LIGHT_GRAY);
                    }
                });
                tobeValue.addFocusListener(new FocusListener() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        if(metadataKey.getDefaultValue().equals(tobeValue.getText()))
                            tobeValue.setText("");
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        if("".equals(tobeValue.getText()))
                            tobeValue.setText(metadataKey.getDefaultValue());
                    }
                });
                keyValue = tobeValue;
            }
            else
                keyValue = new JComboBox(metadataKey.getSetOptions());
            keyToText.put(metadataKey, keyValue);
            topPanel.add(keyLabel);
            topPanel.add(keyValue);
        }
        
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
                
                for(Map.Entry<MetadataContainer.MetadataKey, JComponent> keyComponent : keyToText.entrySet()){
                    if(keyComponent.getKey().settable){
                        dataII.initInfo.put(keyComponent.getKey(), ((JTextField)keyComponent.getValue()).getText());
                    } else {
                        dataII.initInfo.put(keyComponent.getKey(), ((JComboBox)keyComponent.getValue()).getSelectedItem().toString());
                    }
                }
                
                parent.metadatachanger = new MetadataChanger(datasetName.getText(), fileChooser.getSelectedFile().toPath(), false, true, dataII);
                parent.remove(parent.mainPanel);
                parent.mainPanel = parent.metadatachanger;
                parent.add(parent.mainPanel,BorderLayout.CENTER);
                parent.validate();
                parent.pack();
            }
        }
    }
}
