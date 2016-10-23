package totalpos;

import com.sun.jna.NativeLong;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;

/**
 *
 * @author Saúl Hidalgo.
 */
public class TQueryPrnStatus extends Structure{
    public byte[] UltSecX ;
    public byte[] PrnStatusHdw;
    public byte[] ErrStatus;
    public byte[] PaperStatus;
    public byte[] SlipStatus;
    public byte[] PrnID;
    public byte[] TCounter;
    public byte[] PrnMdlMrk;
    public byte[] Rif;
    public byte[] MemoryNumber;
    public byte[] IvaA;
    public byte[] IvaB;
    public byte[] IvaC;
    public byte PrnStatusApp;
    public byte LineCounter;
    public byte AutStatus;
    public int UltZ;

    TQueryPrnStatus(){
        UltSecX = new byte[3];
        PrnStatusHdw = new byte[3];
        ErrStatus = new byte[3];
        PaperStatus = new byte[3];
        SlipStatus = new byte[3];
        PrnID = new byte[11];
        TCounter = new byte[7];
        PrnMdlMrk = new byte[11];
        Rif = new byte[16];
        MemoryNumber = new byte[3];
        IvaA = new byte[8];
        IvaB = new byte[8];
        IvaC = new byte[8];
    }

}
