package totalpos;

/**
 *
 * @author Saúl Hidalgo.
 */
public class Store {

    private String id;
    private String description;

    public Store(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }
}
