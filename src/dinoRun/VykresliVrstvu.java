package dinoRun;

import Komponenty.Cesta;
import Komponenty.Dino;
import Komponenty.Houby;
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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 * @author Matěj Hloušek
 */
public class VykresliVrstvu extends Canvas implements Runnable, KeyListener, ActionListener {

    int[] terenCesty = new int[300];
    // pole pro cesty a komponenty
    ArrayList<Cesta> poleCest;
    ArrayList<VlastnostiKomponentu> komponenty;
    ArrayList<VlastnostiKomponentu> veciNaCeste;
    Dino hrac1 = new Dino(this);
    Dino hrac2 = new Dino(this);

    // booleany pro tlačítka
    boolean dTL1, nTL1, nTLPovoleni1, pTL1, lTL1;
    boolean dTL2, nTL2, nTLPovoleni2, pTL2, lTL2;
    int score = 0;

    /**
     * Metoda sloužící k inicializaci
     */
    public VykresliVrstvu() {
        super(); // zavolá canvas
        this.setSize(new Dimension(1200, 600)); // nystavení velikosti canvasu
        this.poleCest = new ArrayList<>();
        this.komponenty = new ArrayList<>();
        this.veciNaCeste = new ArrayList<>();
        this.naplnPole();
        addKeyListener(this);
        setFocusable(true);

        hrac1.setNazevSouboru("dinoRuzova");
        hrac1.nahrajObrazek();
        hrac2.setNazevSouboru("dinoZelena");
        hrac2.nahrajObrazek();
    }

    /**
     * hlavní smyčka hry
     */
    @Override
    public void run() {
        while (!renderMenu()) {

        }

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
            while (hrac1.getXPozice() > -10 && hrac2.getXPozice() > -10 && hrac1.getZivot() > 0 && hrac2.getZivot() > 0) {
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
                    System.out.println("smycky: " + smycka + ", FPS: " + fps + " pocet objektu: "
                            + (this.poleCest.size() + this.komponenty.size() + this.veciNaCeste.size()));
                    fps = 0;
                    smycka = 0;
                    if(score%5 == 0){vlozeniHub();}

                    this.score++;
                }
            }//while hry

            // když je smyčka hry ukončena vypíšu dialogové okno se score a možnost opakování
            if (true) {
                int reply = JOptionPane.showConfirmDialog(null, "Tvoje skóré je: " + this.score + "s\n chceš hru restartovat ?", "hraj znovu", JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION) {
                    this.hrac1.setXPozice(300);
                    this.hrac2.setXPozice(300);
                    this.hrac1.setZivot(100);
                    this.hrac2.setZivot(100);
                    dTL1 = nTL1 = pTL1 = lTL1 = false;
                    dTL2 = nTL2 = pTL2 = lTL2 = false;
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

    /**
     * Metoda zjišťuje kompletní aktualizaci všech objektů
     */
    private void aktualizace() {
        // aktualzace hráčů
        hrac1.aktualizace();
        hrac2.aktualizace();

        //aktualizace pohybu - zmačknuté tlačítka + kolize
        pohyb();

        // aktualizace každého objektu cesty
        for (Cesta e : this.poleCest) {
            e.aktualizace();

            // pokud nějý box cesty přesáhne hodnotu -10 => nebude vidět
            // >> bude smazán a následně vložen na konec pole => na začátek cesty
            if (e.getXPozice() < -10) {
                this.poleCest.remove(e);

                Cesta neupraveny = new Cesta(this);
                neupraveny.setXPozice(this.getWidth());
                int predchozi = this.poleCest.size() - 1;
                Random rand = new Random();
                if (poleCest.get(predchozi).getYPozice() > 500) {
                    neupraveny.setYPozice(poleCest.get(predchozi).getYPozice() - (rand.nextInt(50) % 2 == 1 ? 1 : -1));
                } else {
                    if (poleCest.get(predchozi).getYPozice() < 450) {
                        neupraveny.setYPozice(poleCest.get(predchozi).getYPozice() + ( rand.nextInt(50) % 2 == 1 ? 1 : -1));
                    } else {
                        neupraveny.setYPozice(poleCest.get(predchozi).getYPozice() - (rand.nextInt(2) / 2 == 1 ? 1 : -1));
                    }
                }

                neupraveny.setColor(Color.BLACK);
                this.poleCest.add(neupraveny); // vložení nepřítele do arrylistu 
                break;
            }
        }

        // render komponentů
        // totožné s  aktualizací cesty
        for (VlastnostiKomponentu e : this.komponenty) {
            e.aktualizace();

            if (e.getXPozice() < -10) {
                this.komponenty.remove(e);

                Mrak neupraveny = new Mrak(this);
                neupraveny.setXPozice(this.getWidth());

                int predchozi = this.komponenty.size() - 1;
                Random rand = new Random();
                neupraveny.setYPozice(komponenty.get(predchozi).getYPozice() + (this.score % 2 == 1 ? 20 : -20));

                this.komponenty.add(neupraveny); // vložení nepřítele do arrylistu 
                break;
            }
        }

        // render komponentů
        // totožné s  aktualizací cesty
        for (VlastnostiKomponentu e : this.veciNaCeste) {
            e.aktualizace();

            if (e.getXPozice() < -10) {
                this.veciNaCeste.remove(e);                
                vlozeniHub();
                break;
            }
        }
        kolizeSnecimNaCeste();

    }

    /**
     * Metoda zjišťuje kompletní rendrování canvasu pomocí 3 bafrů použití bafrů
     * k vůli pomalému vykreslování při každém vyvolání funkce se jen buffery
     * vymění a nevykresluje se do aktualního buff.
     */
    private boolean renderMenu() {
        // vytvoření třech bafrů pro canvas
        BufferStrategy buffer = this.getBufferStrategy();
        if (buffer == null) {
            this.createBufferStrategy(3);
            return false;
        }
        BufferedImage dino = null;
        BufferedImage pozadi = null;
        try {
            dino = ImageIO.read(new File("obrazky/dinoHra/menuDino.png"));
            pozadi = ImageIO.read(new File("obrazky/dinoHra/" + "-02.png"));
        } catch (IOException ex) {
            System.out.println(ex);
        }

        // nastavení do kterého bafru budu kreslit
        Graphics g = buffer.getDrawGraphics();

        // zalené pozadí
        g.setColor(Color.CYAN);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.drawImage(dino, 0, 130, this);

        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(this.getWidth() / 3, this.getHeight() / 4, 100, 30);
        g.setColor(Color.black);
        g.drawString("Hrát", this.getWidth() / 3 + 5, this.getHeight() / 4 + 20); // výpis score

        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(this.getWidth() / 3, this.getHeight() / 4 + 35, 100, 30);
        g.setColor(Color.black);
        g.drawString("Hrač 1", this.getWidth() / 3 + 5, this.getHeight() / 4 + 20 + 35); // výpis score
        
        g.setColor(Color.BLUE);
        g.fillRect(this.getWidth() / 3+ 125, this.getHeight() / 4 + 35, 20, 20);
        g.setColor(Color.RED);
        g.fillRect(this.getWidth() / 3+ 155, this.getHeight() / 4 + 35, 20, 20);
        g.setColor(Color.ORANGE);
        g.fillRect(this.getWidth() / 3+ 185, this.getHeight() / 4 + 35, 20, 20);
        g.setColor(Color.PINK);
        g.fillRect(this.getWidth() / 3+ 215, this.getHeight() / 4 + 35, 20, 20);
        g.setColor(Color.GREEN);
        g.fillRect(this.getWidth() / 3+ 245, this.getHeight() / 4 + 35, 20, 20);

        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(this.getWidth() / 3, this.getHeight() / 4 + 75, 100, 30);
        g.setColor(Color.black);
        g.drawString("Hrač 2", this.getWidth() / 3 + 5, this.getHeight() / 4 + 20 + 75); // výpis score
        
        g.setColor(Color.BLUE);
        g.fillRect(this.getWidth() / 3+ 125, this.getHeight() / 4 + 75, 20, 20);
        g.setColor(Color.RED);
        g.fillRect(this.getWidth() / 3+ 155, this.getHeight() / 4 + 75, 20, 20);
        g.setColor(Color.ORANGE);
        g.fillRect(this.getWidth() / 3+ 185, this.getHeight() / 4 + 75, 20, 20);
        g.setColor(Color.PINK);
        g.fillRect(this.getWidth() / 3+ 215, this.getHeight() / 4 + 75, 20, 20);
        g.setColor(Color.GREEN);
        g.fillRect(this.getWidth() / 3+ 245, this.getHeight() / 4 + 75, 20, 20);
        
        
        
        
        

        // nastavení barvy pro text
        g.dispose(); // ukončení kreslení
        buffer.show(); // vyměnění bafru a vykreslení najednou
        return false;
    }

    /**
     * Metoda zjišťuje kompletní rendrování canvasu pomocí 3 bafrů použití bafrů
     * k vůli pomalému vykreslování při každém vyvolání funkce se jen buffery
     * vymění a nevykresluje se do aktualního buff.
     */
    private void render() {
        // vytvoření třech bafrů pro canvas
        BufferStrategy buffer = this.getBufferStrategy();
        if (buffer == null) {
            this.createBufferStrategy(3);
            return;
        }

        // nastavení do kterého bafru budu kreslit
        Graphics g = buffer.getDrawGraphics();

        // zalené pozadí
        g.setColor(Color.GREEN);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        // vykreslení každé kostičky cesty
        // dokreslení oblohy (modré)
        for (Cesta e : this.poleCest) {
            e.render(g);
            // obloha
            g.setColor(new Color(179, 236, 255));
            g.fillRect(e.getXPozice(), e.getYPozice() - 1000, 2, 1000);
        }

        // vykreslení komponentů
        for (VlastnostiKomponentu e : this.komponenty) {
            e.render(g);
        }

        // vykreslení věcí na cestě
        for (VlastnostiKomponentu e : this.veciNaCeste) {
            e.render(g);
        }

        // vykreslení hráčů
        hrac1.render(g);
        hrac2.render(g);

        // nastavení barvy pro text
        g.setColor(Color.black);
        g.drawString("tvoje skóre: " + this.score, 20, 20); // výpis score
        g.dispose(); // ukončení kreslení
        buffer.show(); // vyměnění bafru a vykreslení najednou
    }

    /**
     * Metoda zjišťuje prvotní naplnění herního pole jak objekty cesty tak
     * objekty komponentů cesty
     */
    public final void naplnPole() {
        // 800- šířka okna + 10 - kvůli kolizím
        for (int i = 0; i < this.getWidth() + 10; i += 2) {
            Cesta neupraveny = new Cesta(this);
            neupraveny.setXPozice(i);
            neupraveny.setYPozice(400);
            int barva = poleCest.size() % 2;
            neupraveny.setColor(barva == 1 ? Color.RED : Color.BLACK);
            this.poleCest.add(neupraveny); // vložení nepřítele do arrylistu 
        }
        for (int i = 0; i < this.getWidth() + 10; i += 100) {
            Mrak neupraveny = new Mrak(this);
            neupraveny.setXPozice(i);
            neupraveny.setYPozice(150);
            this.komponenty.add(neupraveny);
        }
        for (int i = 0; i < this.getWidth() + 10; i += 400) {
            Random rand = new Random();
            Houby neupraveny = new Houby(this);
            neupraveny.setXPozice(i + rand.nextInt(20));
            neupraveny.setYPozice(400);
            neupraveny.setOrientace(rand.nextInt(3) == 1 ? true : false);
            this.veciNaCeste.add(neupraveny);
        }

        for (int i = 0; i < terenCesty.length; i++) {
        }
    }

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    /**
     * Metoda zjišťuje funkci tlačítek pokud je tlačítko zmáčknuto tak to hodí
     * TRUE do příslušné proměnné daného tlačítka
     *
     * @param ke
     */
    @Override
    public void keyPressed(KeyEvent ke) {
        switch (ke.getKeyCode()) {

            // HRAC 1 => šipky
            case KeyEvent.VK_UP:
                nTL1 = true;
                break;
            case KeyEvent.VK_DOWN:
                dTL1 = true;
                break;
            case KeyEvent.VK_LEFT:
                lTL1 = true;
                break;
            case KeyEvent.VK_RIGHT:
                pTL1 = true;
                break;

            // HRAC 2 => WASD
            case KeyEvent.VK_W:
                nTL2 = true;
                break;
            case KeyEvent.VK_S:
                dTL2 = true;
                break;
            case KeyEvent.VK_A:
                lTL2 = true;
                break;
            case KeyEvent.VK_D:
                pTL2 = true;
                break;
        }
    }

    /**
     * Metoda zjišťuje funkci tlačítek pokud je tlačítko puštěno tak to hodí
     * FALSE do příslušné proměnné daného tlačítka
     *
     * @param ke
     */
    @Override
    public void keyReleased(KeyEvent ke) {
        switch (ke.getKeyCode()) {

            // HRAC 1 => šipky
            case KeyEvent.VK_UP:
                nTL1 = false;
                break;
            case KeyEvent.VK_DOWN:
                dTL1 = false;
                break;
            case KeyEvent.VK_LEFT:
                lTL1 = false;
                break;
            case KeyEvent.VK_RIGHT:
                pTL1 = false;
                break;

            // HRAC 2 => WASD
            case KeyEvent.VK_W:
                nTL2 = false;
                break;
            case KeyEvent.VK_S:
                dTL2 = false;
                break;
            case KeyEvent.VK_A:
                lTL2 = false;
                break;
            case KeyEvent.VK_D:
                pTL2 = false;
                break;
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
    }

    /**
     * Metoda zjišťuje kolize mezi hráči zajišťuje pohyb plus + udržení se na
     * hrací cestě
     */
    public void pohyb() {
        int max1 = 0; // maximalní výška výskoku
        int max2 = 0;
        boolean chyba1 = true;  // chybová proměnná kvůli neurčení nad kterým boxem cesty se nachází
        boolean chyba2 = true;

        // for prochází pole cest
        for (Cesta e : this.poleCest) {
            //  HRAC 1
            // když zjistí že objekt hráče je na nějakém boxu cesty tak ten box zvýrazní
            // vizualni detekce dotyku 
            if (e.getYPozice() < hrac1.getYPozice() + hrac1.getSirka() && hrac1.getXPozice() > e.getXPozice() && hrac1.getXPozice() < e.getXPozice() + e.getSirka()) {
                e.setColor(Color.yellow);

            } else {
                e.setColor(Color.BLACK);
            }

            //HRAC 2
            if (e.getYPozice() < hrac2.getYPozice() + hrac2.getSirka() && hrac2.getXPozice() > e.getXPozice() && hrac2.getXPozice() < e.getXPozice() + e.getSirka()) {
                e.setColor(Color.RED);

            } else {
                e.setColor(Color.BLACK);
            }

            //  HRAC 1
            // když zjistí že objekt hráče je nad nějakým boxem cesty tak ten box zvýrazní
            // a rozdíl výšky hrace a boxu cesty zapise do proměnné max
            if (hrac1.getXPozice() > e.getXPozice() && hrac1.getXPozice() < e.getXPozice() + e.getSirka() + 1) {
                max1 = e.getYPozice() - hrac1.getYPozice();
                chyba1 = false;
            }
            //HRAC 2
            if (hrac2.getXPozice() > e.getXPozice() - 1 && hrac2.getXPozice() < e.getXPozice() + e.getSirka()) {
                max2 = e.getYPozice() - hrac2.getYPozice();
                chyba2 = false;
            }
        }

        //HRAC 1 - šipky
        // reáguje na zmáčknuté tlačítka
        //tlačítko dolů
        if (dTL1 && max1 > 10) {
            hrac1.setYPozice(hrac1.getYPozice() + 10);
        }

        //tlačítko nahoru
        if (!chyba1) {
            // zjistí zda se dotkl země 3- z důvodů odchylky
            nTLPovoleni1 = max1 < 3 ? true : nTLPovoleni1;
        }
        if (nTL1 && !chyba1 && nTLPovoleni1) {
            hrac1.setYPozice(hrac1.getYPozice() - 15);
            // pokud je hrac 100 pixelů vysoko tak vypne stoupání
            nTLPovoleni1 = max1 > 100 ? false : nTLPovoleni1;
        }

        //tlacitko DOPRAVA
        if (pTL1) {
            hrac1.setXPozice(hrac1.getXPozice() + 5);
        }

        //tlacitko DOLEVA
        if (lTL1) {
            hrac1.setXPozice(hrac1.getXPozice() - 5);
        }

        // HRAC 2  - WASD
        // tlačitko S
        if (dTL2 && max2 > 10) {
            hrac2.setYPozice(hrac2.getYPozice() + 10);
        }

        // tlačitko W
        if (!chyba2) {
            nTLPovoleni2 = max2 < 3 ? true : nTLPovoleni2;
        }

        if (nTL2 && !chyba2 && nTLPovoleni2) {
            hrac2.setYPozice(hrac2.getYPozice() - 15);
            nTLPovoleni2 = max2 > 100 ? false : nTLPovoleni2;
        }

        // tlačitko P
        if (pTL2) {
            hrac2.setXPozice(hrac2.getXPozice() + 5);
        }

        // tlačitko L
        if (lTL2) {
            hrac2.setXPozice(hrac2.getXPozice() - 5);
        }

        //if(chyba1){return;}
        //if(chyba2){return;}
        // HRAC 1
        // pokud je objekt postavy níž než cesta zak ho dá na cestu
        // -1 == hodnota o trerou ho vysune nahoru
        hrac1.setYPozice(max1 < -1 ? hrac1.getYPozice() - 1 : hrac1.getYPozice());
        hrac1.setYPozice(max1 < -5 ? hrac1.getYPozice() - 4 : hrac1.getYPozice());
        // pokud je objekt postavy víš než cesta zak ho dá na cestu
        hrac1.setYPozice(max1 > 0 ? hrac1.getYPozice() + 10 : hrac1.getYPozice()); // udržení panacka na trase

        // HRAC 2
        hrac2.setYPozice(max2 < -1 ? hrac2.getYPozice() - 1 : hrac2.getYPozice());
        hrac2.setYPozice(max2 < -5 ? hrac2.getYPozice() - 4 : hrac2.getYPozice());
        hrac2.setYPozice(max2 > 0 ? hrac2.getYPozice() + 10 : hrac2.getYPozice()); // udržení panacka na trase
    }

    public void kolizeSnecimNaCeste() {
        int zivot1 = 0;
        int zivot2 = 0;
        for (VlastnostiKomponentu e : this.veciNaCeste) {

            // HRAČ 1
            //ověření pokud levým spodním rohem nezasahuje do objektu
            if ((e.getXPozice() - 27 < hrac1.getXPozice() - 25) && (e.getXPozice() - 27 + e.getSirka() > hrac1.getXPozice() - 25)
                    && hrac1.getYPozice() + hrac1.getSirka() > e.getYPozice()) {
                zivot1 = e.isOrientace() ? 1 : 2;
            }
            //ověření pokud pravým spodním rohem nezasahuje do objektu
            if ((e.getXPozice() - 27 < hrac1.getXPozice() - 25 + hrac1.getSirka()) && (e.getXPozice() - 27 + e.getSirka() > hrac1.getXPozice() - 25 + hrac1.getSirka())
                    && hrac1.getYPozice() + hrac1.getSirka() > e.getYPozice()) {
                zivot1 = e.isOrientace() ? 1 : 2;
            }

            //ověření pokud prostředním spodním bodem nezasahuje do objektu
            // kdyby náhodou byl objekt užší než hráč
            if ((e.getXPozice() - 27 < hrac1.getXPozice() - 25 + hrac1.getSirka() / 2) && (e.getXPozice() - 27 + e.getSirka() > hrac1.getXPozice() - 25 + hrac1.getSirka() / 2)
                    && hrac1.getYPozice() + hrac1.getSirka() > e.getYPozice()) {
                zivot1 = e.isOrientace() ? 1 : 2;
            }

            // HRAC 2 
            if ((e.getXPozice() - 27 < hrac2.getXPozice() - 25) && (e.getXPozice() - 27 + e.getSirka() > hrac2.getXPozice() - 25)
                    && hrac2.getYPozice() + hrac2.getSirka() > e.getYPozice()) {
                zivot2 = e.isOrientace() ? 1 : 2;
            }

            if ((e.getXPozice() - 27 < hrac2.getXPozice() - 25 + hrac2.getSirka()) && (e.getXPozice() - 27 + e.getSirka() > hrac2.getXPozice() - 25 + hrac2.getSirka())
                    && hrac2.getYPozice() + hrac2.getSirka() > e.getYPozice()) {
                zivot2 = e.isOrientace() ? 1 : 2;
            }

            if ((e.getXPozice() - 27 < hrac2.getXPozice() - 25 + hrac2.getSirka() / 2) && (e.getXPozice() - 27 + e.getSirka() > hrac2.getXPozice() - 25 + hrac2.getSirka() / 2)
                    && hrac2.getYPozice() + hrac2.getSirka() > e.getYPozice()) {
                zivot2 = e.isOrientace() ? 1 : 2;
            }
            if (zivot1 == 1) {
                hrac1.setZivot(hrac1.getZivot() + 30);
                veciNaCeste.remove(e);
                vlozeniHub();
                return;
            }
            if (zivot2 == 1) {
                hrac2.setZivot(hrac2.getZivot() + 30);
                veciNaCeste.remove(e);
                vlozeniHub();
                return;
            }
        }
        System.out.println(zivot1);
        switch (zivot1) {
            case 0:
                hrac1.getZivot();
                break;
            case 1:
                hrac1.setZivot(hrac1.getZivot() + 80);
                break;
            case 2:

                hrac1.setZivot(hrac1.getZivot() - 1);
                break;
        }
        switch (zivot2) {
            case 0:
                hrac2.getZivot();
                break;
            case 2:
                hrac2.setZivot(hrac2.getZivot() - 1);
                break;
        }
    }
    
    
    public void nastaveniRychlosti(){
        hrac1.setRychlost(hrac1.getRychlost()+1);
        hrac2.setRychlost(hrac2.getRychlost()+1);
        for (VlastnostiKomponentu e : this.veciNaCeste) {
            e.setRychlost(e.getRychlost()+1);
        }
        for (VlastnostiKomponentu e : this.komponenty) {
            e.setRychlost(e.getRychlost()+1);
        }
        for (VlastnostiKomponentu e : this.poleCest) {
            e.setRychlost(e.getRychlost()+1);
        }
    }
    public void vlozeniHub(){
                Houby neupraveny = new Houby(this);
                neupraveny.setXPozice(this.getWidth());
                Random rand = new Random();
                neupraveny.setOrientace(rand.nextInt(3) == 1 ? true : false);

                int predchozi = this.poleCest.size() - 1;
                neupraveny.setYPozice(poleCest.get(predchozi).getYPozice());

                this.veciNaCeste.add(neupraveny); // vložení nepřítele do arrylistu 
    }


}
