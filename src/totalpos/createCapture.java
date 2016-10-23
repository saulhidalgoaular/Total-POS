
package totalpos;

import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.processing.DPFPEnrollment;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import java.sql.SQLException;

/**
 *
 * @author shidalgo
 */
public class createCapture extends fingerPrintReader{

    private DPFPEnrollment enroller = DPFPGlobal.getEnrollmentFactory().createEnrollment();
    public boolean isOk = false;
    private Employ employ;

    public createCapture(String employId) {
        super();
        try {
            super.setState("Captura de Nueva Huella");
            updateStatus();
            employ = ConnectionDrivers.getEmploy(employId);
            isOk = (employ != null);
            if ( isOk ){
                super.setNameLabel(employ.getName());
                super.setFontSize2Name(20);
            }
        } catch (SQLException ex) {
            MessageBox msb = new MessageBox(MessageBox.SGN_DANGER, "Error desconocido", ex);
            msb.show(Shared.getMyMainWindows());
        }
    }

    @Override
    protected void process(DPFPSample sample) {
        super.process(sample);
        DPFPFeatureSet features = extractFeatures(sample, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);

        try {
            if ( features != null ){
                enroller.addFeatures(features);            
            }
        } catch (DPFPImageQualityException ex) {
            MessageBox msb = new MessageBox(MessageBox.SGN_IMPORTANT, "No se ha guardado la huella. Intente de nuevo", ex);
            msb.show(this);
        }finally{
            try {
                updateStatus();
                switch (enroller.getTemplateStatus()) {
                    case TEMPLATE_STATUS_READY:
                        stop();
                        byte[] template = enroller.getTemplate().serialize();
                        ConnectionDrivers.saveTemplate(employ.getCode(), template);
                        MessageBox msb = new MessageBox(MessageBox.SGN_SUCCESS, "Guardado satisfactoriamente");
                        msb.show(this);
                        this.dispose();
                        break;
                    case TEMPLATE_STATUS_FAILED:
                        enroller.clear();
                        stop();
                        updateStatus();
                        start();
                        break;
                }
            } catch (SQLException ex) {
                MessageBox msb = new MessageBox(MessageBox.SGN_SUCCESS, "Error al guardar la huella");
                msb.show(this);
            }
        }

    }

    private void updateStatus() {
        int n = enroller.getFeaturesNeeded();
        if ( n == 0 ){
            super.setTitleLabel("Listo");
        }else{
            super.setTitleLabel("Faltan " + enroller.getFeaturesNeeded() + " Captaciones.");
        }
    }

}
