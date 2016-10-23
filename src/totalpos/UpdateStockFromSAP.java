package totalpos;

import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.JFrame;
import net.n3.nanoxml.XMLException;
import webservice.TotalPosWebService;
import webservice.TotalPosWebServiceService;

/**
 *
 * @author Saúl Hidalgo
 */
public class UpdateStockFromSAP implements Doer{

    public Working workingFrame;
    public String mode;

    public UpdateStockFromSAP(String mode) {
        this.mode = mode;
    }

    public void updateStockFromSAP() {
        System.out.println("[" + Shared.now() + "] UpdateStockFromSAP " + Shared.lineNumber() +  " Update from SAP");
        workingFrame = new Working((JFrame) Shared.getMyMainWindows());
        WaitSplash ws = new WaitSplash(this);

        Shared.centerFrame(workingFrame);
        workingFrame.setVisible(true);

        ws.execute();
        //doIt();
    }

    private void updatePrices(Connection c, TotalPosWebService ws) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, XMLException{
        System.out.println("[" + Shared.now() + "] UpdateStockFromSAP " + Shared.lineNumber() +  " Actualizar precios");
        String myDay = Shared.getConfig("lastPriceUpdate");
        String newPrices = ws.listNewPriceFromDate(myDay, Shared.getConfig("storePrefix")+Shared.getConfig("storeName"), Shared.getConfig("Z"));
        System.out.println("newPrices = " + newPrices);

        ConnectionDrivers.setPrices(c,newPrices);
        ConnectionDrivers.setLastUpdateNow();
    }

    private void updateFlagC(Connection c, TotalPosWebService ws) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, XMLException{
        System.out.println("[" + Shared.now() + "] UpdateStockFromSAP " + Shared.lineNumber() +  " Update flagc");
        String myDay = ConnectionDrivers.getLastFlagc();

        String daysFlagc = ws.getFlagC(Shared.getConfig("storePrefix")+Shared.getConfig("storeName"), myDay);

        ConnectionDrivers.updateFlagc(daysFlagc, c);
    }
    
    @Override
    public void doIt() {

        System.out.println("[" + Shared.now() + "] UpdateStockFromSAP " + Shared.lineNumber() +  " Operando");
        Connection c = null;
        try {
            Shared.createBackup("articulo precio codigo_de_barras costo movimiento_inventario detalles_movimientos");

            TotalPosWebService ws = new TotalPosWebServiceService().getTotalPosWebServicePort();

            c = ConnectionDrivers.cpds.getConnection();
            c.setAutoCommit(false);

            if ( mode.equals("MM") || mode.equals("MMBackground") ){
                
                String ansListMM = ws.listMMwithPrices(Shared.getConfig("storePrefix")+Shared.getConfig("storeName"), Shared.getConfig("Z"), ConnectionDrivers.getLastMM());
                //String ansListMM = ws.listMM("4900458128");
                System.out.println("[" + Shared.now() + "] UpdateStockFromSAP " + Shared.lineNumber() +  " ansListMM = " + ansListMM );

                String itemsNeeded = ConnectionDrivers.createNewMovement(c, ansListMM);
                ansListMM = null;
                System.out.println("[" + Shared.now() + "] UpdateStockFromSAP " + Shared.lineNumber() +  " itemsNeeded = " + itemsNeeded);
                
                // Update prices too
                updatePrices(c,ws);

                // flagsC
                updateFlagC(c, ws);

                // Descriptions
                updateDescriptions(c, ws);

                System.out.println("[" + Shared.now() + "] UpdateStockFromSAP " + Shared.lineNumber() +  " Listo!");
            }else if ( mode.equals("Prices")){
                updatePrices(c,ws);
            }else if ( mode.equals("initialStock") ){
                
                String ansListMM = ws.getInitialStockWithPrices(Shared.getConfig("storePrefix")+Shared.getConfig("storeName"), Shared.getConfig("Z"));
                System.out.println("[" + Shared.now() + "] UpdateStockFromSAP " + Shared.lineNumber() +  "  ansListMM = " + ansListMM );
                ConnectionDrivers.getInitialStock(c, ansListMM);

                ConnectionDrivers.disableInitialStock(c);

                System.out.println("[" + Shared.now() + "] UpdateStockFromSAP " + Shared.lineNumber() +  " Listo!");
            }else if ( mode.equals("profitWorkers")){
                System.out.println("[" + Shared.now() + "] UpdateStockFromSAP " + Shared.lineNumber() +  " Profit DB Name " + Shared.getConfig("profitDatabase"));
                String ans = ws.listEmployCode(Shared.getConfig("storeNameProfit"), Shared.getConfig("profitDatabase"));
                System.out.println("[" + Shared.now() + "] UpdateStockFromSAP " + Shared.lineNumber() +  " Ans = " + ans);
                ConnectionDrivers.updateEmployees(ans);
            }

            System.out.println("[" + Shared.now() + "] UpdateStockFromSAP " + Shared.lineNumber() +  " Haciendo el commit...");
            c.commit();
            System.out.println("[" + Shared.now() + "] UpdateStockFromSAP " + Shared.lineNumber() +  " Terminado commit Exitoso!");

            if ( !mode.equals("MMBackground") ){
                MessageBox msg = new MessageBox(MessageBox.SGN_SUCCESS, "Actualizado!");
                msg.show(Shared.getMyMainWindows());
            }else{
                System.exit(0);
            }
        } catch (Exception ex) {
            System.out.println("[" + Shared.now() + "] UpdateStockFromSAP " + Shared.lineNumber() +  " Comenzo la exception");
            try {
                System.out.println("[" + Shared.now() + "] UpdateStockFromSAP " + Shared.lineNumber() +  " Haciendo Rollback");
                c.rollback();
                System.out.println("[" + Shared.now() + "] UpdateStockFromSAP " + Shared.lineNumber() +  " Reversado!");
                MessageBox msg = new MessageBox(MessageBox.SGN_DANGER, "Ha ocurrido un error. No se ha guardado ningun cambio.", ex);
                msg.show(Shared.getMyMainWindows());
            } catch (Exception ex1) {
                // We are in problems :(
                MessageBox msg = new MessageBox(MessageBox.SGN_DANGER, "Ha ocurrido un error. No se ha guardado ningun cambio.", ex);
                msg.show(Shared.getMyMainWindows());
                System.out.println("[" + Shared.now() + "] UpdateStockFromSAP " + Shared.lineNumber() +  "  Ha ocurrido un error. Haciendo Roll back..." + ex1.getMessage());
            }
        }finally{
            try {
                c.close();
            } catch (SQLException ex) {
                System.out.println("[" + Shared.now() + "] UpdateStockFromSAP " + Shared.lineNumber() +  " Ha ocurrido un error cerrando la conexion. " + ex.getMessage());
            }
        }
    }

    @Override
    public void close() {
        workingFrame.setVisible(false);
    }

    private void updateDescriptions(Connection c, TotalPosWebService ws) throws ClassNotFoundException, ClassNotFoundException, InstantiationException, IllegalAccessException, XMLException, SQLException {
        String myDay = Shared.getConfig("lastPriceUpdate");
        
        String descriptions = ws.readDescriptions(myDay);

        ConnectionDrivers.updateItems(descriptions, c);
    }

}
