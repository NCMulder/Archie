//License
package archie_v1.UI;

import archie_v1.fileHelpers.FileHelper;
import archie_v1.fileHelpers.MetadataKey;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

/**
 * The AddablePanel is the part of the MetadataChanger responsible
 * for addable metadata, such as creators and contributors.
 *
 * @author N.C. Mulder <n.c.mulder at students.uu.nl>
 */
public class AddablePanel extends JPanel implements ActionListener {

    public MetadataKey[] Values;
    String singleTitle, pluralTitle;
    public int height = 0;
    JComponent mainPanel;

    JButton addItem;
    HashMap<JButton, Integer> removeButtons;

    HashMap<Integer, JPanel> valuePanels;
    public ArrayList<String[]> valueArray;

    private float xweight;
    private FileHelper fileHelper;

    public AddablePanel(String singleTitle, String pluralTitle, MetadataKey[] Values, FileHelper fileHelper) {
        this.singleTitle = singleTitle;
        this.pluralTitle = pluralTitle;
        this.Values = Values;
        this.fileHelper = fileHelper;
        removeButtons = new HashMap();
        valuePanels = new HashMap();

        this.valueArray = new ArrayList();
        if (fileHelper.metadataMap.get(Values[0]) != null) {
            for (int i = 0; i < fileHelper.metadataMap.getOrDefault(Values[0], "").split(";", 0).length; i++) {
                String[] arrayEntry = new String[Values.length];
                for (int j = 0; j < Values.length; j++) {
                    arrayEntry[j] = fileHelper.metadataMap.get(Values[j]).split(";")[i];
                }
                valueArray.add(arrayEntry);
            }
        }

        createPanel();
    }

    private void resetMainPanel() {
        this.remove(mainPanel);
        createPanel();
        revalidate();
    }

    private void createPanel() {
        this.setLayout(new GridBagLayout());
        Border paneBorder = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), pluralTitle);
        this.setBorder(paneBorder);

        mainPanel = Values();

        GridBagConstraints gbc = new GridBagConstraints(
                0, 0, //GridX, GridY
                5, height, //GridWidth, GridHeight
                1, height / (height + 1), //WeightX, WeightY
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, //Anchor, Fill
                new Insets(0, 0, 0, 0), 0, 0); //Insets, IpadX, IpadY
        this.add(mainPanel, gbc);
    }

    private JComponent Values() {
        JPanel gridPane = new JPanel(new GridLayout(0, 1));

        if (!valueArray.isEmpty()) {
            xweight = .8333f / (Values.length);
            String[] mainKeys = valueArray.get(0);
            height = valueArray.size();

            for (int i = 0; i < height; i++) {
                //This six is arbitrary, should be dynamically settable; however, we want all columns equally.
                JPanel singleValue = new JPanel(new GridLayout(0, 6));

                for (int j = 0; j < Values.length; j++) {
                    String labelText = valueArray.get(i)[j].trim();
                    JLabel label = new JLabel(labelText);
                    singleValue.add(label);
                }

                for (int j = Values.length; j < 5; j++) {
                    JLabel label = new JLabel();
                    singleValue.add(label);
                }

                JButton remove = new JButton("Remove");
                remove.addActionListener(this);
                singleValue.add(remove);

                gridPane.add(singleValue);

                valuePanels.put(i, singleValue);
                removeButtons.put(remove, i);
            }
        }

        JPanel addPanel = new JPanel(new GridLayout(0, 1));

        addItem = new JButton("Add " + singleTitle);
        addItem.addActionListener(this);
        addPanel.add(addItem);

        height++;

        gridPane.add(addPanel);

        JScrollPane scrollPane = new JScrollPane(gridPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        return gridPane;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int f = removeButtons.getOrDefault(e.getSource(), -1);
        //Todo: only do this on save from above.
        if (f != -1) {
            valueArray.remove(f);
            resetMainPanel();
        } else if (e.getSource() == addItem) {
            SetPart sp = new SetPart(Values[0]);
            String title = singleTitle + " addition";
            Object[] buttons = {"Add", "Cancel"};
            
            int result = JOptionPane.showOptionDialog(this, sp, title, JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, buttons[0]);
            
            if (result == JOptionPane.OK_OPTION) {
                HashMap<MetadataKey, String> map = sp.getInfo();
                String[] values = new String[Values.length];
                for(int i = 0; i < Values.length; i++){
                    String value = map.get(Values[i]);
                    if(value == null || value.equals(""))
                        value = " ";
                    values[i] = value;
                }
                valueArray.add(values);
                resetMainPanel();
            }
        }
    }
}
