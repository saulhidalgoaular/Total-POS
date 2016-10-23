package totalpos;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Saúl Hidalgo.
 */
public class Presence {
    private Employ employ;
    private List<Timestamp> fingerPrints;

    public Presence(Employ employ) {
        this.employ = employ;
        this.fingerPrints = new LinkedList<Timestamp>();
    }

    public Employ getEmploy() {
        return employ;
    }

    public List<Timestamp> getFingerPrints() {
        return Collections.unmodifiableList(fingerPrints);
    }

    public void addFingerPrint( Timestamp t ){
        if ( fingerPrints.size() < 4 ){
            fingerPrints.add(t);
        }
    }

}
