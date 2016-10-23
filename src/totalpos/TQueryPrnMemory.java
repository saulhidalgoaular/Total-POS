package totalpos;

import com.sun.jna.Structure;

/**
 *
 * @author shidalgo
 */
public class TQueryPrnMemory extends Structure{
    public byte[] DateTimeRptZ;
    public byte[] DateTimeLastVta;
    public byte[] DateTimeLastDev;
    public byte[] DateTimeLastNoFiscal;
    public byte[] DateTimeLastRptZ;
    public byte[] DateTimeLastMemRptZ;
    public byte[] CounterLastVta;
    public byte[] CounterLastDev;
    public byte[] CounterLastNoFiscal;
    public byte[] CounterLastRptZ;
    public byte[] CounterLastMemRptZ;
    public byte[] VtaA;
    public byte[] VtaB ;
    public byte[] VtaC;
    public byte[] VtaE;
    public byte[] IvaVtaA;
    public byte[] IvaVtaB;
    public byte[] IvaVtaC;
    public byte[] DscVtaA;
    public byte[] DscVtaB;
    public byte[] DscVtaC;
    public byte[] DscVtaE;
    public byte[] RcgVtaA;
    public byte[] RcgVtaB;
    public byte[] RcgVtaC;
    public byte[] RcgVtaE;
    public byte[] DevA;
    public byte[] DevB;
    public byte[] DevC;
    public byte[] DevE;
    public byte[] IvaDevA;
    public byte[] IvaDevB;
    public byte[] IvaDevC;
    public byte[] DscDevA;
    public byte[] DscDevB;
    public byte[] DscDevC;
    public byte[] DscDevE;
    public byte[] RcgDevA;
    public byte[] RcgDevB;
    public byte[] RcgDevC;
    public byte[] RcgDevE;
    public byte[] FPago1;
    public byte[] FPago2;
    public byte[] FPago3;
    public byte[] FPago4;
    public byte[] FPago5;
    public byte[] FPago6;
    public byte[] CounterVta;
    public byte[] CounterDev;
    public byte[] CounterNoFiscal;
    public byte[] AnulVtaA;
    public byte[] AnulVtaB;
    public byte[] AnulVtaC;
    public byte[] AnulVtaE;
    public byte[] AnulDevA;
    public byte[] AnulDevB;
    public byte[] AnulDevC;
    public byte[] AnulDevE;

    TQueryPrnMemory(){
        DateTimeRptZ = new byte[23];
        DateTimeLastVta = new byte[23];
        DateTimeLastDev = new byte[23];
        DateTimeLastNoFiscal = new byte[23];
        DateTimeLastRptZ = new byte[23];
        DateTimeLastMemRptZ = new byte[23];
        CounterLastVta = new byte[14];
        CounterLastDev = new byte[14];
        CounterLastNoFiscal = new byte[14];
        CounterLastRptZ = new byte[14];
        CounterLastMemRptZ = new byte[14];
        VtaA = new byte[14];
        VtaB = new byte[14];
        VtaC = new byte[14];
        VtaE = new byte[14];
        IvaVtaA = new byte[14];
        IvaVtaB = new byte[14];
        IvaVtaC = new byte[14];
        DscVtaA = new byte[14];
        DscVtaB = new byte[14];
        DscVtaC = new byte[14];
        DscVtaE = new byte[14];
        RcgVtaA = new byte[14];
        RcgVtaB = new byte[14];
        RcgVtaC = new byte[14];
        RcgVtaE = new byte[14];
        DevA = new byte[14];
        DevB = new byte[14];
        DevC = new byte[14];
        DevE = new byte[14];
        IvaDevA = new byte[14];
        IvaDevB = new byte[14];
        IvaDevC = new byte[14];
        DscDevA = new byte[14];
        DscDevB = new byte[14];
        DscDevC = new byte[14];
        DscDevE = new byte[14];
        RcgDevA = new byte[14];
        RcgDevB = new byte[14];
        RcgDevC = new byte[14];
        RcgDevE = new byte[14];
        FPago1 = new byte[14];
        FPago2 = new byte[14];
        FPago3 = new byte[14];
        FPago4 = new byte[14];
        FPago5 = new byte[14];
        FPago6 = new byte[14];
        CounterVta = new byte[14];
        CounterDev = new byte[14];
        CounterNoFiscal = new byte[14];
        AnulVtaA = new byte[14];
        AnulVtaB = new byte[14];
        AnulVtaC = new byte[14];
        AnulVtaE = new byte[14];
        AnulDevA = new byte[14];
        AnulDevB = new byte[14];
        AnulDevC = new byte[14];
        AnulDevE = new byte[14];
    }
}
