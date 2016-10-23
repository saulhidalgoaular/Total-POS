package totalpos;

/**
 *
 * @author Saúl Hidalgo
 */
public class Column {
    private String name;
    private String fieldName;
    private String myClass;

    protected Column(String name, String fieldName, String myClass) {
        this.name = name;
        this.fieldName = fieldName;
        this.myClass = myClass;
    }

    protected String getFieldName() {
        return fieldName;
    }

    protected String getMyClass() {
        return myClass;
    }

    protected String getName() {
        return name;
    }
}
