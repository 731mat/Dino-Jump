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
public class Houby extends VlastnostiKomponentu{

    public Houby(VykresliVrstvu l) {
        super(l);
        this.sirka = 35;
        this.color = Color.RED;
        this.nazevSouboru = "houby";
        nahrajObrazek();
    }

    @Override
    public void aktualizace() {
        this.xPozice-=this.rychlost;
        
    }

    @Override
    public void render(Graphics g) {
        g.setColor(this.color);
        //g.fillRect(this.xPozice-27, this.yPozice-39, this.sirka, this.sirka);
        g.drawImage(this.orientace?imageL:imageP, this.xPozice-25, this.yPozice-38, null);
    }
    
}
