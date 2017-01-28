//License
package archie_v1.UI;

import archie_v1.fileHelpers.FolderHelper;
import archie_v1.fileHelpers.MetadataKey;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

/**
 *
 * @author niels
 */
public class NewDataset extends JPanel implements ActionListener {

    private JButton generate, cancel;
    private MainFrame parent;
    private MetadataChangerFields fields;
    private FolderHelper datasetHelper;

    public NewDataset(MainFrame parent) {
        this.parent = parent;
        datasetHelper = new FolderHelper(Paths.get("C:\\Users\\niels\\Documents\\Archie\\Testset\\testset"), true, true);
        createUI();
    }

    private void createUI() {
        this.setLayout(new GridBagLayout());

        //Panel setup
        GridBagConstraints gbc;
        fields = new MetadataChangerFields(datasetHelper, true);

        gbc = new GridBagConstraints(0, 0, 1, 1, 1, .95, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 0);
        this.add(fields, gbc);

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
                new Insets(0, 0, 0, 0), 0, 0);                      //Insets, IpadX, IpadY
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

        gbc = new GridBagConstraints(0, 1, 1, 1, 1, .05, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 0);
        this.add(bottomPanel, gbc);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cancel) {
            parent.goToHome();
        } else if (e.getSource() == generate) {
            for (Map.Entry<MetadataKey, JComponent> metadataKeyTextEntry : fields.labelText.entrySet()) {
                String value = (metadataKeyTextEntry.getKey().unrestricted) ? ((ArchieTextField) metadataKeyTextEntry.getValue()).getText() : ((JComboBox) metadataKeyTextEntry.getValue()).getSelectedItem().toString();
                datasetHelper.setRecord(metadataKeyTextEntry.getKey(), value, false);
            }

            String datasetName = datasetHelper.metadataMap.get(MetadataKey.DatasetTitle);
            if (datasetName == null || "".equals(datasetName)) {
                JOptionPane.showMessageDialog(this, "The name of a dataset can not be empty.", "Dataset name", JOptionPane.PLAIN_MESSAGE);
                return;
            }
            //more input testing?

            //Start generating the metadata and UI.
            Path path = Paths.get(fields.datasetLocationField.getText());
            int fileCount = FileUtils.listFilesAndDirs(path.toFile(), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE).size();

            //Setting the mainpanel to a progresspanel
            parent.working = parent.WorkingOnItPanel(fileCount);
            parent.ChangeMainPanel(parent.working);
            parent.revalidate();

            //Setting the mainpanel to a metadatachanger
            parent.metadatachanger = new MetadataChanger(datasetName, path, false, true, (ProgressPanel) parent.working);
            parent.ChangeMainPanel(parent.metadatachanger);
            parent.export.setEnabled(true);

            //CHECK THIS
            parent.validate();
            parent.pack();
        }
    }
}
