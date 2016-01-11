/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dinoRun;

import java.awt.BorderLayout;
import javax.swing.JFrame;

/**
 *
 * @author student
 */
public class MotoCross extends JFrame {

    public static final String nazevOkna = "DinoJump";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        MotoCross hra = new MotoCross();  // vytvoření instannce třídy
        hra.init();

    }

    public void init() {
        // metoda okna 
        VykresliVrstvu vrstva = new VykresliVrstvu();
        this.add(vrstva);  // přidání komponentu
        this.pack();
        this.setTitle(MotoCross.nazevOkna); // nazev okna
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // zavření
        this.setLayout(new BorderLayout());
        this.setVisible(true); // nystavení viditelnosti
        vrstva.start();
    }
}
