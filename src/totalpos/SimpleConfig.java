package totalpos;

/**
 *
 * @author Saúl Hidalgo
 */
public class SimpleConfig {
    private String key;
    private String value;
    private String name;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public SimpleConfig(String key, String value, String name) {
        this.key = key;
        this.value = value;
        this.name = name;
    }
}
