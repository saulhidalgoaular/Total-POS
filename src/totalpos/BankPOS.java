package totalpos;

/**
 *
 * @author Saúl Hidalgo
 */
public class BankPOS {
    private String id;
    private String descripcion;
    private String lot;
    private String posId;
    private String kind;

    protected BankPOS(String id, String descripcion, String lot, String posId, String kind) {
        this.id = id;
        this.descripcion = descripcion;
        this.lot = lot;
        this.posId = posId;
        this.kind = kind;
    }

    protected String getDescripcion() {
        return descripcion;
    }

    protected String getId() {
        return id;
    }

    protected String getKind() {
        return kind;
    }

    protected String getLot() {
        return lot;
    }

    protected String getPosId() {
        return posId;
    }
}
