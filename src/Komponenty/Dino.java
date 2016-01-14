package Komponenty;

import dinoRun.VykresliVrstvu;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Hloušek Matěj
 */
public class Dino extends VlastnostiKomponentu {
    protected BufferedImage image = null;
    
    public Dino(VykresliVrstvu l) {
        super(l);
        this.color = Color.GRAY;
        this.sirka = 20;
        this.xPozice = 200;
        this.yPozice = 0;
        try {
            this.image = ImageIO.read(new File("obrazky/dino.png"));
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
    @Override
    public void aktualizace() {
        this.xPozice-=1;
    }

    
    @Override
    public void render(Graphics g) {
        g.setColor(this.color);
        g.fillRect(this.xPozice, this.yPozice-10, this.sirka-20, this.sirka);
        g.drawImage(image, this.xPozice-25, this.yPozice-56, null);
    }
}
