package totalpos;

import java.sql.Date;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Saúl Hidalgo
 */
public class Movement {
    private String id;
    private Date date;
    private String description;
    private String code;
    private String storeId;
    private List<ItemQuant> items = new LinkedList<ItemQuant>();

    public Movement(String id, Date date, String description, String code, String storeId , List<ItemQuant> items) {
        this.id = id;
        this.description = description;
        this.code = code;
        this.storeId = storeId;
        this.date = date;
        this.items = items;
    }

    public String getCode() {
        return code;
    }

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public List<ItemQuant> getItems() {
        return items;
    }

    public String getStoreId() {
        return storeId;
    }

}
