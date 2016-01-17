/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Komponenty;

import dinoRun.VykresliVrstvu;
import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Matěj
 */
public class Tlacitko extends VlastnostiKomponentu{

    public Tlacitko(VykresliVrstvu l,String nazev,int pX,int pY, int S, int V,Color ba) {
        super(l);
        this.jmeno = nazev;
        this.xPozice = pX;
        this.yPozice = pY;
        this.sirka = S;
        this.vyska = V;
        this.color = ba;
        
    }

    @Override
    public void aktualizace() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void render(Graphics g) {
        g.setColor(this.color);
        g.fillRect(this.xPozice, this.yPozice, this.sirka, this.vyska);
        g.setColor(Color.BLACK);
        g.drawString(this.jmeno, this.xPozice+5, this.yPozice+2);
    }
    
}
