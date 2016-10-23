package totalpos;

import java.sql.Date;

/**
 *
 * @author Saúl Hidalgo
 */
public class Assign {
    private String turn;
    private String pos;
    private Date date;
    private boolean open;

    protected Assign(String turn, String pos, Date date, boolean open) {
        this.turn = turn;
        this.pos = pos;
        this.date = date;
        this.open = open;
    }
    protected Date getDate() {
        return date;
    }

    protected boolean isOpen() {
        return open;
    }

    protected String getPos() {
        return pos;
    }

    protected String getTurn() {
        return turn;
    }
}
