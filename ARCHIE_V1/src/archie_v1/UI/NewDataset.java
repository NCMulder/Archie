//License
package archie_v1.UI;

import archie_v1.fileHelpers.DatasetInitialInformation;
import archie_v1.fileHelpers.MetadataContainer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

//Currently using a borderlayout for the panel as a whole, with a gridbaglayout within a scrollpane for the data fields
//at the center and a gridbaglayout with the main buttons at the page end. Would like to change the main panel to 
/**
 *
 * @author niels
 */
public class NewDataset extends JPanel implements ActionListener {

    public JTextField datasetName, creatorName, creatorAffiliation, creatorIdentifier,
            contributorName, rightsholder, subject, description, publisher,
            temporalCoverage, spatialCoverage, locText;
    public JComboBox creatorTOA, language, accessLevel;
    public JFileChooser fileChooser;
    private JButton choose, generate, cancel;
    private MainFrame parent;
    private DatasetInitialInformation dataII = new DatasetInitialInformation();
    private LinkedHashMap<MetadataContainer.MetadataKey, JComponent> keyToText;

    public NewDataset(MainFrame parent) {
        this.parent = parent;
//        this.setLayout(new BorderLayout());
        this.setLayout(new GridBagLayout());
        keyToText = new LinkedHashMap();

        fileChooser = new JFileChooser("C:\\Users\\niels\\Documents\\Archie\\Testset\\testset");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        //Panel setup
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // <editor-fold defaultstate="collapsed" desc="Dataset name fields">
        //Dataset name fields
        JLabel datasetNameLabel = new JLabel("Dataset name: ");
        gbc = new GridBagConstraints(
                0, 0, //GridX, GridY
                2, 1, //GridWidth, GridHeight
                1, 1, //WeightX, WeightY
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                new Insets(0, 10, 0, 10), 3, 3); //Insets, IpadX, IpadY
        mainPanel.add(datasetNameLabel, gbc);

        datasetName = new JTextField("");
        gbc = new GridBagConstraints(
                2, 0,
                2, 1,
                1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 10, 0, 10), 3, 3);
        mainPanel.add(datasetName, gbc);
        // </editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Metadata key fields">
        int gbcy = 1;
        for (final MetadataContainer.MetadataKey metadataKey : MetadataContainer.MetadataKey.values()) {
            if (!metadataKey.dataset) {
                continue;
            }

            //Field label
            JLabel keyLabel = new JLabel(metadataKey.toString() + ": ");
            gbc = new GridBagConstraints();
            gbc = new GridBagConstraints(
                    0, gbcy, //GridX, GridY
                    2, 1, //GridWidth, GridHeight
                    1, 1, //WeightX, WeightY
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                    new Insets(0, 10, 0, 10), 3, 3);                              //Insets, IpadX, IpadY
            mainPanel.add(keyLabel, gbc);

            //Field textfield, which can be a textfield or a combobox.
            JComponent keyValue;
            if (metadataKey.settable) {
                final JTextField tobeValue = new JTextField(metadataKey.getDefaultValue(), 20);
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

                    public void changeColor() {
                        if (!metadataKey.getDefaultValue().equals(tobeValue.getText())) {
                            tobeValue.setForeground(Color.BLACK);
                        } else {
                            tobeValue.setForeground(Color.LIGHT_GRAY);
                        }
                    }
                });
                tobeValue.addFocusListener(new FocusListener() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        if (metadataKey.getDefaultValue().equals(tobeValue.getText())) {
                            tobeValue.setText("");
                        }
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        if ("".equals(tobeValue.getText())) {
                            tobeValue.setText(metadataKey.getDefaultValue());
                        }
                    }
                });
                keyValue = tobeValue;
            } else {
                keyValue = new JComboBox(metadataKey.getSetOptions());
            }

            gbc = new GridBagConstraints();
            gbc = new GridBagConstraints(
                    2, gbcy++, //GridX, GridY
                    2, 1, //GridWidth, GridHeight
                    1, 1, //WeightX, WeightY
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                    new Insets(0, 10, 0, 10), 3, 3);                              //Insets, IpadX, IpadY
            mainPanel.add(keyValue, gbc);

            keyToText.put(metadataKey, keyValue);
        }
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Folder selection">
        JLabel loc = new JLabel("Dataset location");
        gbc = new GridBagConstraints();
        gbc = new GridBagConstraints(
                0, gbcy, //GridX, GridY
                2, 1, //GridWidth, GridHeight
                1, 1, //WeightX, WeightY
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                new Insets(0, 10, 0, 10), 3, 3);                              //Insets, IpadX, IpadY
        mainPanel.add(loc, gbc);
        locText = new JTextField("C:\\Users\\niels\\Documents\\Archie\\Testset\\testset");
        gbc = new GridBagConstraints();
        gbc = new GridBagConstraints(
                2, gbcy, //GridX, GridY
                1, 1, //GridWidth, GridHeight
                1, 1, //WeightX, WeightY
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                new Insets(0, 10, 0, 10), 3, 3);                              //Insets, IpadX, IpadY
        mainPanel.add(locText, gbc);
        choose = new JButton("Choose");
        choose.addActionListener(this);
        gbc = new GridBagConstraints();
        gbc = new GridBagConstraints(
                3, gbcy++, //GridX, GridY
                1, 1, //GridWidth, GridHeight
                1, 1, //WeightX, WeightY
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                new Insets(0, 10, 0, 10), 3, 3);                              //Insets, IpadX, IpadY
        mainPanel.add(choose, gbc);

//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Bottom buttons">
        //Bottom panel containing generation and cancellation buttons
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        cancel = new JButton("Cancel");
        cancel.addActionListener(this);
        gbc = new GridBagConstraints();
        gbc = new GridBagConstraints(
                1, 0, //GridX, GridY
                1, 1, //GridWidth, GridHeight
                .1, 1, //WeightX, WeightY
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, //Anchor, Fill
                new Insets(0, 0, 0, 0), 0, 0);                              //Insets, IpadX, IpadY
        bottomPanel.add(cancel, gbc);

        JPanel test = new JPanel();

        gbc = new GridBagConstraints();
        gbc = new GridBagConstraints(
                2, 0, //GridX, GridY
                2, 1, //GridWidth, GridHeight
                .5, 1, //WeightX, WeightY
                GridBagConstraints.CENTER, GridBagConstraints.NONE, //Anchor, Fill
                new Insets(0, 0, 0, 0), 0, 0);                              //Insets, IpadX, IpadY
        bottomPanel.add(test, gbc);

        generate = new JButton("Generate");
        generate.addActionListener(this);
        gbc = new GridBagConstraints();
        gbc = new GridBagConstraints(
                4, 0, //GridX, GridY
                1, 1, //GridWidth, GridHeight
                .1, 1, //WeightX, WeightY
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, //Anchor, Fill
                new Insets(0, 0, 0, 0), 0, 0);                              //Insets, IpadX, IpadY
        bottomPanel.add(generate, gbc);

//</editor-fold>
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        Border paneBorder = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Dataset initialization");
        scrollPane.setBorder(paneBorder);
        //scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        //weird fix for random jumps while resizing
        scrollPane.setPreferredSize(new Dimension(1, 1));

//        this.add(scrollPane, BorderLayout.CENTER);
//        this.add(bottomPanel, BorderLayout.PAGE_END);
        gbc = new GridBagConstraints(0, 0, 1, 1, 1, .95, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 0);
        this.add(scrollPane, gbc);
        gbc = new GridBagConstraints(0, 1, 1, 1, 1, .05, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 0);
        this.add(bottomPanel, gbc);

        //datasetName.requestFocus();
//        datasetName.requestFocusInWindow();
//        generate.requestFocusInWindow();
        gainFocus(datasetName);
    }
    
    public void gainFocus(JComponent comp){
        comp.requestFocusInWindow();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == choose) {
            int status = fileChooser.showOpenDialog(this);
            if (status == JFileChooser.APPROVE_OPTION) {
                locText.setText(fileChooser.getSelectedFile().toString());
            }
            return;
        } else if (e.getSource() == cancel) {
            parent.goToHome();
        } else if (e.getSource() == generate) {
            if ("".equals(datasetName.getText())) {
                JOptionPane.showMessageDialog(this, "The name of a dataset can not be empty.", "Dataset name", JOptionPane.PLAIN_MESSAGE);
                datasetName.setBackground(new Color(255, 100, 100));
                gainFocus(datasetName);
                return;
            }

            //Start generating the metadata and UI.
            Path path = Paths.get(locText.getText());

            int fileCount = FileUtils.listFilesAndDirs(path.toFile(), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE).size();
            
            //Counting number of files
//            int fileCount = 0;
//            if (path.toFile().isDirectory()) {
//                Stack<File> dirs = new Stack();
//                dirs.add(path.toFile());
//
//                while (!dirs.empty()) {
//                    File dir = dirs.pop();
//                    if (dir.listFiles() == null) {
//                        continue;
//                    }
//                    for (File f : dir.listFiles()) {
//                        if (f.isDirectory()) {
//                            dirs.push(f);
//                        } else {
//                            fileCount++;
//                        }
//                    }
//                }
//            }

            //Setting the mainpanel to a progresspanel
            
            parent.working = parent.WorkingOnItPanel(fileCount);
            parent.ChangeMainPanel(parent.working);

            //Storing initial data
            for (Map.Entry<MetadataContainer.MetadataKey, JComponent> keyComponent : keyToText.entrySet()) {
                if (keyComponent.getKey().settable) {
                    dataII.initInfo.put(keyComponent.getKey(), ((JTextField) keyComponent.getValue()).getText());
                } else {
                    dataII.initInfo.put(keyComponent.getKey(), ((JComboBox) keyComponent.getValue()).getSelectedItem().toString());
                }
            }

            //Setting the mainpanel to a metadatachanger
            parent.metadatachanger = new MetadataChanger(datasetName.getText(), path, false, true, dataII, (ProgressPanel) parent.working);
            parent.ChangeMainPanel(parent.metadatachanger);
            parent.export.setEnabled(true);
            
            
            //CHECK THIS
            parent.validate();
            parent.pack();
        }
    }
}
