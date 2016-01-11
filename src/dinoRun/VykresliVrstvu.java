package dinoRun;

import Komponenty.Cesta;
import Komponenty.Dino;
import Komponenty.Mrak;
import Komponenty.VlastnostiKomponentu;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JOptionPane;

/**
 *
 * @author Matěj Hloušek
 */
public class VykresliVrstvu extends Canvas implements Runnable, KeyListener, ActionListener {

    ArrayList<Cesta> poleCest;
    ArrayList<VlastnostiKomponentu> komponenty;
    Dino hrac = new Dino(this);
    boolean dTL, nTL,nTLPovoleni, pTL, lTL;
    int score=0;

    public VykresliVrstvu() {
        super(); // zavolá canvas
        this.setSize(new Dimension(800, 600)); // nystavení velikosti canvasu
        this.poleCest = new ArrayList<Cesta>();
        this.komponenty = new ArrayList<VlastnostiKomponentu>();
        this.naplnPole();
        addKeyListener(this);
        setFocusable(true);
    }

    @Override
    public void run() {

        // nekonečná smyčka hry
        while (true) {
            // vytvoření časových proměnných
            long posledniCasSmycky = System.nanoTime();
            long poslednyCasVypisu = System.currentTimeMillis();
            double pocetSmycekZaSekundu = 0;
            double nanoSecSmycky = Math.pow(10, 9) / 60;//chci 60 smyček za sekundu a 10na9 je 1 nanosekunda -> kolik má být smycek za 1 sekundu
            int fps = 0;
            int smycka = 0;

            // smyčka hry kokud nepropíchnu všechny balonky
            while (hrac.getXPozice() > -10) {
                // výpočet času na smyšku
                long aktualniCasSmycky = System.nanoTime();
                pocetSmycekZaSekundu += (aktualniCasSmycky - posledniCasSmycky) / nanoSecSmycky;
                posledniCasSmycky = aktualniCasSmycky;

                // zpracování smycek které se nestihnou zpracovat
                // z důvodu rychlosti hry na výkonu jakéhokoliv pc
                // na každém pc proběhne za 1 s stejně smyček
                while (pocetSmycekZaSekundu >= 1) {
                    smycka++;
                    pocetSmycekZaSekundu--;
                    this.aktualizace();
                }

                fps++;
                // vyrendruje canvas
                this.render();

                //každou sekundu přidá score a vypíše na obrazovku
                if (System.currentTimeMillis() - poslednyCasVypisu > 1000) {
                    poslednyCasVypisu += 1000;
                    System.out.println("smycky: " + smycka + ", FPS: " + fps + " pocet objektu: " + this.poleCest.size());
                    fps = 0;
                    smycka = 0;

                    this.score++;
                }
                            
            }//while hry

            // když je smyčka hry ukončena vypíšu dialogové okno se score a možnost opakování
            if (true) {
                int reply = JOptionPane.showConfirmDialog(null, "Tvoje skóré je: " + this.score + "s\n chceš hru restartovat ?", "hraj znovu", JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION) {
                    this.hrac.setXPozice(300);
                    dTL = false;
                    nTL = false;
                    pTL = false;
                    lTL = false;
                    //this.score = 0;
                } else {
                    System.exit(0);
                }
            }

        }// hlavní while
    }

    // vytvoření vlákna
    public void start() {
        Thread vlakno1;
        vlakno1 = new Thread(this);
        vlakno1.start();
    }

    private void aktualizace() {
        hrac.aktualizace();
        pohyb();
        for (Cesta e : this.poleCest) {   // něco jako foreach - prochází pole a každé si ulozi do proměnné e(předem definované vlastnosti) a vyrendruje
            e.aktualizace();
            if (e.getXPozice() < -10) {
                this.poleCest.remove(e);

                Cesta neupraveny = new Cesta(this);
                neupraveny.setXPozice(800);

                int predchozi = this.poleCest.size() - 1;
                Random rand = new Random();

                neupraveny.setYPozice(poleCest.get(predchozi).getYPozice() + (this.score%2 == 1 ? 1: -1));
                neupraveny.setColor(Color.BLACK);
                this.poleCest.add(neupraveny); // vložení nepřítele do arrylistu 
                break;
            }
        }
        
        for (VlastnostiKomponentu e : this.komponenty) {   // něco jako foreach - prochází pole a každé si ulozi do proměnné e(předem definované vlastnosti) a vyrendruje
            e.aktualizace();
            if (e.getXPozice() < -10) {
                this.komponenty.remove(e);

                Mrak neupraveny = new Mrak(this);
                neupraveny.setXPozice(800);

                int predchozi = this.komponenty.size() - 1;
                Random rand = new Random();
                //neupraveny.setYPozice(poleCest.get(predchozi).getYPozice() + (this.score%2 == 1 ? 1: -1));
                neupraveny.setYPozice(komponenty.get(predchozi).getYPozice() + (this.score%2 == 1 ? 20: -20));
                
                this.komponenty.add(neupraveny); // vložení nepřítele do arrylistu 
                break;
            }
        }

    }

    // vyrendrování canvasu
    private void render() {
        // vytvoření třech bafrů pro canvas
        BufferStrategy buffer = this.getBufferStrategy();
        if (buffer == null) {
            this.createBufferStrategy(3);
            return;
        }
        // nastavení do kterého bafru budu kreslit
        Graphics g = buffer.getDrawGraphics();

        // kdyby náhodou jsme chtěli bílé pozadí
        g.setColor(Color.GREEN);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        // překreslení pozadí // vymazání canvasu
        for (Cesta e : this.poleCest) {   // něco jako foreach - prochází pole a každé si ulozi do proměnné e(předem definované vlastnosti) a vyrendruje
            e.render(g);
            g.setColor(Color.BLUE);
            g.fillRect(e.getXPozice(), e.getYPozice() - 1000, 2, 1000);
        }
        
        for (VlastnostiKomponentu e : this.komponenty) {
        e.render(g);
        }
        
        hrac.render(g);

        // nystavení barvy pro text
        g.setColor(Color.black);
        g.drawString("tvoje skóre: " + this.score, 20, 20); // výpis score
        g.dispose(); // ukončení kreslení
        buffer.show(); // vyměnění bafru a vykreslení najednou

    }

    public void naplnPole() {
        for (int i = 0; i < 810; i += 2) {
            Cesta neupraveny = new Cesta(this);
            neupraveny.setXPozice(i);
            neupraveny.setYPozice(400);
            int barva = poleCest.size() % 2;
            neupraveny.setColor(barva == 1 ? Color.RED : Color.BLACK);
            this.poleCest.add(neupraveny); // vložení nepřítele do arrylistu 
            System.out.println(this.poleCest.size());
        }
        for(int i = 0; i < 810; i += 100){
            Mrak neupraveny = new Mrak(this);
            neupraveny.setXPozice(i);
            neupraveny.setYPozice(150);
            this.komponenty.add(neupraveny);
        }
    }

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    public void keyPressed(KeyEvent ke) {
        switch (ke.getKeyCode()) {
            case KeyEvent.VK_UP:
                nTL = true;
                break;
            case KeyEvent.VK_DOWN:
                dTL = true;
                break;
            case KeyEvent.VK_LEFT:
                lTL = true;
                break;
            case KeyEvent.VK_RIGHT:
                pTL = true;
                break;
        }
    }

    public void keyReleased(KeyEvent ke) {
        switch (ke.getKeyCode()) {
            case KeyEvent.VK_UP:
                nTL = false;
                break;
            case KeyEvent.VK_DOWN:
                dTL = false;
                break;
            case KeyEvent.VK_LEFT:
                lTL = false;
                break;
            case KeyEvent.VK_RIGHT:
                pTL = false;
                break;
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
    }

    public void pohyb() {
        int max = 0;
        for (Cesta e : this.poleCest) {
            // vizualni detekce dotyku 
            if (e.getYPozice() < hrac.getYPozice() + hrac.getSirka() && hrac.getXPozice() > e.getXPozice() && hrac.getXPozice() < e.getXPozice() + e.getSirka()) {
                e.setColor(Color.yellow);

            } else {
                e.setColor(Color.BLACK);
            }

            if (hrac.getXPozice() > e.getXPozice() && hrac.getXPozice() < e.getXPozice() + e.getSirka()) {
                max = e.getYPozice() - hrac.getYPozice();
               
            }
        }

        if (dTL && max >10) {
            hrac.setYPozice(hrac.getYPozice() + 10);
        }
        
        nTLPovoleni =  max < 50 ? true:nTLPovoleni;
        if (nTL && nTLPovoleni) {
            hrac.setYPozice(hrac.getYPozice() - 10);
            nTLPovoleni =  max > 100 ? false:nTLPovoleni;
            
        }
        if (pTL) {
            hrac.setXPozice(hrac.getXPozice() + 5);
        }

        if (lTL) {
            hrac.setXPozice(hrac.getXPozice() - 5);
        }
        
        hrac.setYPozice(max < -1 ? hrac.getYPozice()+max : hrac.getYPozice());
        hrac.setYPozice(max > 0 ? hrac.getYPozice() + 10 : hrac.getYPozice()); // udržení panacka na trase
 
        

    }
}
