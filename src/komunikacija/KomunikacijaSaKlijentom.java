/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package komunikacija;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import kontroler.KontrolerServera;
import niti.NitKlijenta;

/**
 *
 * @author Bojana Komljenovic
 */
public class KomunikacijaSaKlijentom extends Thread {

    private final ServerSocket ss;

    public KomunikacijaSaKlijentom() throws IOException {
        ss = new ServerSocket(9000);
    }

    @Override
    public void run() {
        try {
            System.out.println("Server je pokrenut!");
            while(true) {
                Socket socket = ss.accept();
                System.out.println("Klijent se povezao sa serverom!");
                NitKlijenta nk = new NitKlijenta(socket);
                KontrolerServera.getInstanca().getKlijenti().add(nk);
                nk.start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
