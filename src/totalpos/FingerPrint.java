
package totalpos;

/**
 *
 * @author Saúl Hidalgo
 */
public class FingerPrint {
    private String employId;
    private byte[] bytesArray;

    public FingerPrint(String employId, byte[] bytesArray) {
        this.employId = employId;
        this.bytesArray = bytesArray;
    }

    public byte[] getBytesArray() {
        return bytesArray;
    }

    public String getEmployId() {
        return employId;
    }

}
