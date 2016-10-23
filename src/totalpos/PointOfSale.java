package totalpos;

/**
 *
 * @author Saul Hidalgo
 */
public class PointOfSale {
    private String id;
    private String description;
    private String printer;
    private boolean enabled;

    public PointOfSale(String id, String description, String printer, boolean enabled) {
        this.id = id;
        this.description = description;
        this.printer = printer;
        this.enabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getId() {
        return id;
    }

    public String getPrinter() {
        return printer;
    }
}
