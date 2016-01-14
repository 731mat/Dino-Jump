package Komponenty;

import dinoRun.VykresliVrstvu;
import java.awt.Color;
import java.awt.Graphics;
import java.io.File;


/**
 *
 * @author Hloušek Matěj
 */
public class Dino extends VlastnostiKomponentu {
    
    public Dino(VykresliVrstvu l) {
        super(l);
        
        this.color = Color.GRAY;
        this.sirka = 40;
        this.xPozice = 200;
        this.yPozice = 0;
        this.zivot = 100;
        this.nazevSouboru = "dinoCervena";
        nahrajObrazek();
    }
    
    @Override
    public void aktualizace() {
        this.xPozice-=1;
    }

    
    @Override
    public void render(Graphics g) {
        g.setColor(this.color);
       // g.fillRect(this.xPozice-25, this.yPozice-38, this.sirka, this.sirka);
        g.drawImage(this.orientace?imageL:imageP, this.xPozice-25, this.yPozice-38, null);
        
        // délka zeleného proužku - životů
        int x = (int)((zivot*40)/100);
        
        // rendrování ukazatele životů
        g.setColor(Color.BLACK);
        g.fillRect(this.xPozice-20, this.yPozice-55, 40, 5);
        // rendrování ukazatele životů
        g.setColor(Color.GREEN);
        g.fillRect(this.xPozice-20, this.yPozice-55, x, 5);
    }
}
