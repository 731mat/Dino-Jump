/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Komponenty;

import dinoRun.VykresliVrstvu;
import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

/**
 *
 * @author Matěj
 */
public class Mrak extends VlastnostiKomponentu{
    
    public Mrak (VykresliVrstvu l) {
        super(l);
        this.color = Color.GRAY;
        this.sirka = 50;
    }

    @Override
    public void aktualizace() {
        this.xPozice-=1;
    }

    
    @Override
    public void render(Graphics g) {
        g.setColor(this.color);
        Random rand = new Random();
  
        g.fillOval(this.xPozice, this.yPozice, this.sirka, this.sirka);
        g.fillOval(this.xPozice-20, this.yPozice+10, this.sirka+20, this.sirka-10);
        g.fillOval(this.xPozice-20, this.yPozice+10, this.sirka-20, this.sirka+10);
    }
    
}
