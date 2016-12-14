//License
package archie_v1.UI;

import archie_v1.fileHelpers.FileHelper;
import archie_v1.fileHelpers.MetadataContainer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class MetadataChangerPane extends JSplitPane implements ActionListener{
    JButton saveButton, resetButton, addButton, saveChildrenButton;
    FileHelper fh;
    HashMap<MetadataContainer.MetadataKey, JComponent> labelText;
    HashMap<JButton, MetadataContainer.MetadataKey> buttonLabel;
    
    public MetadataChangerPane(FileHelper fileHelper, MetadataChanger parent){
        super(JSplitPane.VERTICAL_SPLIT);
        this.fh = fileHelper;
        this.setResizeWeight(1);
        
        labelText = new HashMap();
        buttonLabel = new HashMap();
        
        JPanel bottomPane = new JPanel();
        resetButton = new JButton("Reset");
        resetButton.addActionListener(this);
        bottomPane.add(resetButton);
        saveButton = new JButton("Save");
        saveButton.addActionListener(this);
        bottomPane.add(saveButton);
        saveChildrenButton = new JButton("Set for all files in folder");
        saveChildrenButton.addActionListener(this);
        bottomPane.add(saveChildrenButton);
        
        //do context switch for filehelper?
        setTopPane();
        this.setBottomComponent(bottomPane);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==saveButton){
            saveToFileHelper(false);
        } else if (e.getSource()==resetButton){
            setTopPane();
        } else if (e.getSource()==addButton){
            addMetadata();
        } else if(e.getSource()==saveChildrenButton){
            saveToFileHelper(true);
        } else if(e.getActionCommand().equals("remove")){
            System.out.println(e.getSource());
            fh.metadataContainer.metadataMap.remove(buttonLabel.get(e.getSource()));
            setTopPane();
        } else {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
    public void setTopPane(){
        JPanel topPane = new JPanel();
        topPane.setLayout(new GridLayout(0, 2, 10, 10));
        topPane.setBorder(BorderFactory.createEmptyBorder(10,20,20,10));
        
        //this foreach randomizes things. How to order clearly? TODO
        for(Map.Entry<MetadataContainer.MetadataKey, String> metadata : fh.metadataContainer.metadataMap.entrySet()){
            JLabel label = new JLabel(metadata.getKey().toString());
            JComponent value; 
            if(metadata.getKey().settable){
                JTextField tobeValue = new JTextField(metadata.getValue());
                if(metadata.getValue().equals(metadata.getKey().getDefaultValue()))
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
                        if(!metadata.getKey().getDefaultValue().equals(tobeValue.getText()))
                            tobeValue.setForeground(Color.BLACK);
                        else
                            tobeValue.setForeground(Color.LIGHT_GRAY);
                    }
                });
                tobeValue.addFocusListener(new FocusListener() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        if(metadata.getKey().getDefaultValue().equals(tobeValue.getText()))
                            tobeValue.setText("");
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        if("".equals(tobeValue.getText()))
                            tobeValue.setText(metadata.getKey().getDefaultValue());
                    }
                });
                value = tobeValue;
            } else {
                value = new JComboBox(metadata.getKey().getSetOptions());
                ((JComboBox)value).setSelectedItem(metadata.getValue());
            }
            labelText.put(metadata.getKey(), value);
            topPane.add(label);
            topPane.add(value);
        }
        
        JScrollPane panel = new JScrollPane(topPane);
        //panel.add(topPane);
        
        this.setTopComponent(panel);
    }
    
    private void saveToFileHelper(boolean hardSet){
        for(Map.Entry<MetadataContainer.MetadataKey, JComponent> metadataKeyTextEntry : labelText.entrySet()){
            String value = (metadataKeyTextEntry.getKey().settable)? ((JTextField)metadataKeyTextEntry.getValue()).getText() : ((JComboBox)metadataKeyTextEntry.getValue()).getSelectedItem().toString();
            fh.setRecord(metadataKeyTextEntry.getKey(), value, hardSet);
        }
    }

    private void addMetadata() {
        AddMetadataField adder = new AddMetadataField();
    }
}
