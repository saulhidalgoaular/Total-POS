package totalpos;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JDesktopPane;

/**
 *
 * @author Saúl Hidalgo
 */
public class MdiPanel extends JDesktopPane{
    private Image wallpaper;

    public MdiPanel(Image wallpaper) {
        super();
        this.wallpaper = wallpaper;
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(wallpaper, 0, 0, getWidth(), getHeight(), this);

        setOpaque(false);
        super.paint(g);
    }

    public void setWallpaper(Image w){
        wallpaper = w;
        this.repaint();
    }

}
