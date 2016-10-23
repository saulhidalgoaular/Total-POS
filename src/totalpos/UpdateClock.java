package totalpos;

import java.sql.SQLException;
import java.sql.Time;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.JFrame;

/**
 *
 * @author Saúl Hidalgo
 */
public class UpdateClock extends Thread{

    private long lastOperationTime;

    public UpdateClock() {
        lastOperationTime = Calendar.getInstance().getTimeInMillis();
    }

    public void actioned(){
        lastOperationTime = Calendar.getInstance().getTimeInMillis();
    }

    @Override
    public void run(){
        int checking = 1;

        // This delay might solve an unknown problem. =(
        try {
            Thread.currentThread().sleep(10000);
        } catch (InterruptedException ex) {
            MessageBox msb = new MessageBox(MessageBox.SGN_IMPORTANT, "Problema desconocido",ex);
            msb.show(null);
            Shared.reload();
        }

        while(Shared.getUser() != null ){
            ++checking;

            try {
                if ( !Shared.isOffline && checking % Integer.parseInt(Shared.getConfig("secondsToChangeMsg2Pos")) == 0
                        && Shared.getMyMainWindows() instanceof MainRetailWindows ){
                    MainRetailWindows m = (MainRetailWindows) Shared.getMyMainWindows();

                    if ( m.msg2pos == null ) break; //little patch. List has not been loaded yet!
                    // I am just a noob =(

                    m.setMsgIndex( (m.getMsgIndex()+1) % m.msg2pos.size());
                    m.updateMsg();
                }

                if ( !Shared.isOffline && checking % Integer.parseInt(Shared.getConfig("secondsToShiftMsg")) == 0 && Shared.getMyMainWindows() instanceof MainRetailWindows ){
                    MainRetailWindows m = (MainRetailWindows) Shared.getMyMainWindows();
                    m.increaseShiftValue();
                }

                if ( !Shared.isOffline && checking % Integer.parseInt(Shared.getConfig("secondsToCheckTurn")) == 0 && Shared.getMyMainWindows() instanceof MainRetailWindows ){
                    try {
                        List<Assign> as = ConnectionDrivers.listAssignsTurnPosRightNow();
                        boolean toContinue = false;
                        for (Assign assign : as) {
                            if (assign.getPos().equals(Shared.getFileConfig("myId")) && assign.isOpen()) {
                                toContinue = true;
                                break; // for performance ...  =D!
                            }
                        }
                        if (!toContinue) {
                            MessageBox msg = new MessageBox(MessageBox.SGN_IMPORTANT, "El turno de venta se ha vencido.");
                            msg.show(null);
                            Shared.reload();
                            break;
                        }
                    } catch (SQLException ex) {
                        MessageBox msg = new MessageBox(MessageBox.SGN_DANGER, "Problemas con la base de datos");
                        msg.show(null);
                        Shared.reload();
                        break;
                    }
                }

                if ( !Shared.isOffline && checking % Integer.parseInt(Shared.getConfig("secondsToUpdateCountdown")) == 0 && Shared.getMyMainWindows() instanceof MainRetailWindows ){
                    try {
                        List<Assign> as = ConnectionDrivers.listAssignsTurnPosRightNow();
                        for (Assign assign : as) {
                            if (assign.getPos().equals(Shared.getFileConfig("myId")) && assign.isOpen()) {
                                Turn cur = Shared.getTurn(ConnectionDrivers.listTurns(), assign.getTurn());
                                String diff = ConnectionDrivers.getDiff( cur.getFin() );
                                String[] toks = diff.split(":");
                                int leaving = Integer.parseInt(toks[1]);
                                if ( toks[0].equals("00") && leaving < 5 ){
                                    MainRetailWindows mrw = (MainRetailWindows) Shared.getMyMainWindows();
                                    if ( leaving > 0 ){
                                        mrw.yourTurnIsFinishingLabel.setText("Su turno se va a vencer en " + leaving + " minuto" + (leaving>1?"s":"") + ".");
                                    }else{
                                        mrw.yourTurnIsFinishingLabel.setText("Su turno se va a vencer en pocos segundos.");
                                    }
                                    mrw.yourTurnIsFinishingLabel.setVisible(true);
                                }
                                break; // for performance ...  =D!
                            }
                        }
                    } catch (SQLException ex) {
                        MessageBox msg = new MessageBox(MessageBox.SGN_DANGER, "Problemas con la base de datos",ex);
                        msg.show(null);
                        Shared.reload();
                        break;
                    } catch (ParseException ex){
                        MessageBox msg = new MessageBox(MessageBox.SGN_DANGER, "Problemas con la base de datos",ex);
                        msg.show(null);
                        Shared.reload();
                        break;
                    }
                }

                if ( !Shared.isOffline && checking % (Integer.parseInt(Shared.getConfig("Sincronizacion Caja Servidor"))) == 0
                        && Shared.getMyMainWindows() instanceof MainRetailWindows){
                    if ( !Shared.isOffline ){
                        try {
                            for (String table : Shared.getConfig("tablesToMirrorAtDay").split(",")) {
                                ConnectionDrivers.mirrorTableFastMode(table);
                            }
                        } catch (Exception ex) {
                            MessageBox msg = new MessageBox(MessageBox.SGN_DANGER, "Problemas con la base de datos");
                            msg.show(null);
                            Shared.reload();
                            break;
                        }
                    }

                }

                /*if ( !Shared.isOffline && checking % (Integer.parseInt(Shared.getConfig("Sincronizacion SAP"))) == 0
                        && Shared.getMyMainWindows() instanceof MainWindows ){
                    
                    ClosingDay c = new ClosingDay(Constants.sdfDay2DB.format(ConnectionDrivers.getDate()), false);
                    c.doIt();
                }*/

                if ( Calendar.getInstance().getTimeInMillis() - lastOperationTime > Long.valueOf(Shared.getConfig("idleTime"))){

                    MessageBox msg = new MessageBox(MessageBox.SGN_WARNING, "El sistema ha permanecido mucho tiempo sin uso. Requiere contraseña.");
                    msg.show(Shared.getMyMainWindows());
                    PasswordNeeded pn = new PasswordNeeded((JFrame)Shared.getMyMainWindows(), true, Shared.getUser());
                    Shared.centerFrame(pn);
                    pn.setVisible(true);
                    if ( pn.isPasswordOk() ){
                        lastOperationTime = Calendar.getInstance().getTimeInMillis();
                    }else{
                        Shared.reload();
                        break;
                    }
                }

                Thread thisThread = Thread.currentThread();

                try {
                    thisThread.sleep(1000);
                } catch (InterruptedException ex) {
                    MessageBox msb = new MessageBox(MessageBox.SGN_IMPORTANT, "Problema desconocido",ex);
                    msb.show(null);
                    Shared.reload();
                    break;
                }

                Date d = new Date(Calendar.getInstance().getTimeInMillis());

                if ( Shared.getMyMainWindows() instanceof MainWindows ){
                    MainWindows m = (MainWindows)Shared.getMyMainWindows();
                    m.whatTimeIsIt.setText(Shared.sdfDateHour.format(d));
                } else if ( Shared.getMyMainWindows() instanceof MainRetailWindows ){
                    MainRetailWindows m = (MainRetailWindows)Shared.getMyMainWindows();
                    m.whatTimeIsIt.setText(Shared.sdfDateHour.format(d));
                }
            }catch(Exception ex){
                System.err.println(ex.getMessage());
            }
        }
    }

}
