/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Komponenty;

import java.awt.Color;
import java.awt.Graphics;
import dinoRun.MotoCross;
import dinoRun.VykresliVrstvu;

/**
 *
 * @author Hloušek Matěj
 */
public class Cesta extends VlastnostiKomponentu {

    public Cesta(VykresliVrstvu l) {
        super(l);
        this.color = Color.GREEN;
        this.sirka = 2;
    }

    @Override
    public void aktualizace() {
        this.xPozice-=this.rychlost*2;
    }

    
    @Override
    public void render(Graphics g) {
        g.setColor(this.color);
        g.fillRect(this.xPozice, this.yPozice, this.sirka, this.sirka);
    }
    
}
