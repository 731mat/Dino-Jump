package dinoRun;

import Komponenty.Cesta;
import Komponenty.Dino;
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
 
public class VykresliVrstvu extends Canvas implements Runnable, KeyListener, ActionListener  {

    ArrayList<Cesta> poleCest;
    Dino hrac = new Dino(this);
    
    
    
    public VykresliVrstvu() {
        super(); // zavolá canvas
        this.setSize(new Dimension(800, 600)); // nystavení velikosti canvasu
        this.poleCest = new ArrayList<Cesta>();
        this.naplnPole();
        addKeyListener(this);
        setFocusable(true);
    }

    @Override
    public void run() {
        
        // nekonečná smyčka hry
        while(true){
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
                    //this.score++;
    
                }
            }//while hry
            
            
            // když je smyčka hry ukončena vypíšu dialogové okno se score a možnost opakování
            
            if (true) {
                int reply = JOptionPane.showConfirmDialog(null, "Tvoje skóré je: " + 10 + "s\n chceš hru restartovat ?", "hraj znovu", JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION) {
                    this.hrac.setXPozice(300);
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
        for (Cesta e : this.poleCest) {   // něco jako foreach - prochází pole a každé si ulozi do proměnné e(předem definované vlastnosti) a vyrendruje
            e.aktualizace();
            if(e.getXPozice() < -10)
            {             
                this.poleCest.remove(e);
                
                Cesta neupraveny = new Cesta(this);
                neupraveny.setXPozice(800);
                
                int predchozi = this.poleCest.size()-1;
                Random rand = new Random();
                
                neupraveny.setYPozice(poleCest.get(predchozi).getYPozice()+(rand.nextInt(2)==0? 5:-5));
                neupraveny.setColor(Color.BLACK);
                this.poleCest.add(neupraveny); // vložení nepřítele do arrylistu 
                break;
            }
           
            
            
            if(e.getYPozice() < hrac.getYPozice()+hrac.getSirka() && hrac.getXPozice() > e.getXPozice() && hrac.getXPozice() < e.getXPozice()+e.getSirka() ){
                e.setColor(Color.yellow);
            }else
            {
                e.setColor(Color.BLACK);
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
            g.fillRect(e.getXPozice(), e.getYPozice()-1000, 10, 1000);
        }
        hrac.render(g);
        
        // nystavení barvy pro text
        g.setColor(Color.black);
        g.drawString("tvoje skóre: " +20, 20, 20); // výpis score
        g.dispose(); // ukončení kreslení
        buffer.show(); // vyměnění bafru a vykreslení najednou

    }

    
     public void naplnPole() {
        for (int i = 0; i < 810; i+=5) {
            Cesta neupraveny = new Cesta(this);
            neupraveny.setXPozice(i);
            neupraveny.setYPozice(400);
            int barva = poleCest.size()%2;
            neupraveny.setColor(barva ==1? Color.RED: Color.BLACK);
            this.poleCest.add(neupraveny); // vložení nepřítele do arrylistu 
            System.out.println(this.poleCest.size());
        }
    }

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyPressed(KeyEvent ke) {
       int max =0;
        for(Cesta e: this.poleCest){
           if(e.getYPozice() < hrac.getYPozice()+hrac.getSirka() && hrac.getXPozice() > e.getXPozice() && hrac.getXPozice() < e.getXPozice()+e.getSirka() ){
                max = e.getYPozice();
            }else
            {
                max = 0;
            }
        }
        
        if(ke.getKeyCode() == KeyEvent.VK_RIGHT){
            hrac.setXPozice(hrac.getXPozice()+10);
         }
        
        
         if(ke.getKeyCode() == KeyEvent.VK_LEFT ){
            hrac.setXPozice(hrac.getXPozice()-10);
         }
        if(ke.getKeyCode() == KeyEvent.VK_UP){
            if(hrac.getYPozice() > max)
            {
                hrac.setYPozice(hrac.getYPozice()-10);
            }
        }
        if(ke.getKeyCode() == KeyEvent.VK_DOWN){
            hrac.setYPozice(hrac.getYPozice()+10);
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
    }
}


