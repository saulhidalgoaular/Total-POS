package totalpos;

import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 *
 * @author Saúl Hidalgo
 */
public class Parameter {
    private String type;
    private String formName;
    private String fieldName;
    private JLabel label;
    private JComponent textField;
    private String positions;

    public Parameter(String type, String formName, String fieldName, String positions, JLabel label, JComponent textField) {
        this.type = type;
        this.formName = formName;
        this.fieldName = fieldName;
        this.label = label;
        this.textField = textField;
        this.positions = positions;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFormName() {
        return formName;
    }

    public JLabel getLabel() {
        return label;
    }

    public JComponent getTextField() {
        return textField;
    }

    public String getType() {
        return type;
    }

    public void setLabel(JLabel label) {
        this.label = label;
    }

    public void setTextField(JComponent textField) {
        this.textField = textField;
    }

    public String getPositions() {
        return positions;
    }
    
}
