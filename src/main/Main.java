/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.IOException;
import komunikacija.KomunikacijaSaKlijentom;

/**
 *
 * @author Bojana Komljenovic
 */
public class Main {
    
    public static void main(String[] args) {
        try {
            KomunikacijaSaKlijentom startServer = new KomunikacijaSaKlijentom();
            startServer.start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
