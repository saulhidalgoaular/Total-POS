package totalpos;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

/**
 *
 * @author shidalgo
 */
public class Bottom extends JPanel{

    private Image wallpaper;

    protected Bottom(Image wallpaper) {
        super();
        this.wallpaper = wallpaper;
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(wallpaper, 0, 0, getWidth(), getHeight(),this);
        setOpaque(false);
        super.paint(g);
    }

}
