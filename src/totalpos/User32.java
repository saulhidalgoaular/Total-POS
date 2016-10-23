package totalpos;

import com.sun.jna.Library;

/**
 *
 * @author shidalgo
 */
public interface User32 extends Library{
    public boolean BlockInput(boolean fBlockIt);
    public boolean EnableWindow(int hWnd, boolean bEnable);
}
