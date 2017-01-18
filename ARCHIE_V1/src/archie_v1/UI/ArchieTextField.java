//*http://stackoverflow.com/questions/10506789/how-to-display-faint-gray-ghost-text-in-a-jtextfield

package archie_v1.UI;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author N.C. Mulder <n.c.mulder at students.uu.nl>
 */
public class ArchieTextField extends JTextField implements FocusListener, DocumentListener, PropertyChangeListener{
    
    Color ghostColor = Color.LIGHT_GRAY;
    Font ghostFont = new Font(this.getFont().getFontName(), Font.ITALIC, this.getFont().getSize());
    Color normalColor = Color.BLACK;
    Font normalFont = this.getFont();
    
    String ghostText = "";
    private boolean isEmpty;
    
    public ArchieTextField(){
        super();
        actionInit();
    }
    
    public ArchieTextField(int length){
        super(length);
        actionInit();
    }
    
    public ArchieTextField(int length, String hinttext){
        super(length);
        this.ghostText = hinttext;
        actionInit();
        updateState();
        if (!this.hasFocus()) {
                focusLost(null);
        }
    }
    
    public ArchieTextField(String text){
        super(text);
        actionInit();
    }
    
    public ArchieTextField(String text, String hinttext){
        super(text);
        this.ghostText = hinttext;
        actionInit();
    }
    
    public ArchieTextField(String text, int length){
        super(text, length);
        actionInit();
    }
    
    public ArchieTextField(String text, int length, String hinttext){
        super(text, length);
        actionInit();
        this.ghostText = hinttext;
    }
    
    private void registerListeners(){
        getDocument().addDocumentListener(this);
        addPropertyChangeListener(this);
    }
    
    private void unregisterListeners(){
        getDocument().removeDocumentListener(this);
        removePropertyChangeListener(this);
    }
    
    private void actionInit(){
        addFocusListener(this);
        registerListeners();
    }
    
    private void updateState(){
        isEmpty = getText().length() == 0;
    }

    @Override
    public void focusGained(FocusEvent e) {
        if(isEmpty){
            unregisterListeners();
            setText("");
            setForeground(normalColor);
            setFont(normalFont);
            registerListeners();
        } else {
            selectAll();
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        if(isEmpty){
            unregisterListeners();
            setText(ghostText);
            setForeground(ghostColor);
            setFont(ghostFont);
            registerListeners();
        } else {
            select(0,0);
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        updateState();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        updateState();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        updateState();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        updateState();
    }
    
    @Override
    public String getText(){
        if(super.getText().equals(ghostText))
            return "";
        else 
            return super.getText();
    }
}
