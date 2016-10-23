package totalpos;

/**
 *
 * @author Saul Hidalgo
 */
public class Profile {
    private String id;
    private String description;

    public Profile(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || this.id == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Profile other = (Profile) obj;
        if (!this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

}
