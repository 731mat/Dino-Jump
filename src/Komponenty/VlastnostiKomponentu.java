/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Komponenty;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import dinoRun.VykresliVrstvu;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author student
 */
abstract public class VlastnostiKomponentu {

    protected int xPozice;
    protected int yPozice;
    protected Color color;
    protected int sirka;
    protected int vyska;
    protected int zivot;
    protected boolean orientace;
    protected String nazevSouboru;
    protected VykresliVrstvu map;
    protected BufferedImage imageP = null;
    protected BufferedImage imageL = null;
    

    public VlastnostiKomponentu(VykresliVrstvu l) {
        this.map = l;

    }

    //abstraktví metoda třídy - neobsahuje žádné tělo 
    // odvozená třída musí tuto metodu předdefinovat

    abstract public void aktualizace();

    abstract public void render(Graphics g);

    // vrátí a nastaví pozici X
    public int getXPozice() {
        return xPozice;
    }

    public void setXPozice(int xPozice) {
        this.orientace = this.xPozice < xPozice;
        this.xPozice = xPozice;
    }

    // vrátí a nastaví pozici Y
    public int getYPozice() {
        return yPozice;
    }

    public void setYPozice(int yPozice) {
        this.yPozice = yPozice;
    }

    // vrátí a nastaví šířku objektu
    public int getSirka() {
        return sirka;
    }

    public void setSirka(int sirka) {
        this.sirka = sirka;
    }

    // vrátí a nastaví výšku objektu
    public int getVyska() {
        return vyska;
    }

    public void setVyska(int vyska) {
        this.vyska = vyska;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getZivot() {
        return zivot;
    }

    public void setZivot(int zivot) {
        this.zivot = (zivot < 101 &&  zivot > -1)? zivot: this.zivot;
    }

    public String getNazevSouboru() {
        return nazevSouboru;
    }

    public void setNazevSouboru(String nazevSouboru) {
        this.nazevSouboru = nazevSouboru;
    }

    public boolean isOrientace() {
        return orientace;
    }

    public void setOrientace(boolean orientace) {
        this.orientace = orientace;
    }
    
    
    public void nahrajObrazek() {
        try {
            this.imageL = ImageIO.read(new File("obrazky/dinoHra/"+this.nazevSouboru+"-01.png"));
            this.imageP = ImageIO.read(new File("obrazky/dinoHra/"+this.nazevSouboru+"-02.png"));
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
}
