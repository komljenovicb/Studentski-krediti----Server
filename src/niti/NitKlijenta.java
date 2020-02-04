/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package niti;

import domen.Banka;
import domen.IspunjavanjeUslovaKonkursa;
import domen.Konkurs;
import domen.KoriscenjeKreditaUPrethodnojGodini;
import domen.PreliminarnaRangLista;
import domen.SpisakPrijavljenih;
import domen.StavkaPreliminarneRangListe;
import domen.Student;
import domen.TipKonkursa;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import kontroler.KontrolerServera;
import transfer.TransferObjekatOdgovor;
import transfer.TransferObjekatZahtev;

/**
 *
 * @author Bojana Komljenovic
 */
public class NitKlijenta extends Thread {

    Socket s;

    public NitKlijenta(Socket s) {
        this.s = s;
    }

    @Override
    public void run() {
        while (true) {
            try {
                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
                TransferObjekatZahtev toz = (TransferObjekatZahtev) ois.readObject();
                TransferObjekatOdgovor too = new TransferObjekatOdgovor();
                switch (toz.getOperacija()) {
                    case 1:
                        ArrayList<Konkurs> konkursi = new ArrayList<>();
                        konkursi = KontrolerServera.getInstanca().vratiKonkurse();
                        too.setRezultat(konkursi);
                        break;
                    case 2:
                        int max = KontrolerServera.getInstanca().vratiMaxID();
                        too.setRezultat(max);
                        break;
                    case 3:
                        Konkurs k = (Konkurs) toz.getZahtev();
                        boolean ok = KontrolerServera.getInstanca().dodajKonkurs(k);
                        if (ok) {
                            too.setRezultat("Konkurs je uspesno unet!");
                        } else {
                            too.setRezultat("Neuspesan unos konkursa!");
                        }
                        break;
                    case 4:
                        ArrayList<Banka> banke = new ArrayList<>();
                        banke = KontrolerServera.getInstanca().vratiBanke();
                        too.setRezultat(banke);
                        break;
                    case 5:
                        Konkurs kon = (Konkurs) toz.getZahtev();
                        boolean uspesno = KontrolerServera.getInstanca().izmeniKonkurs(kon);
                        if (uspesno) {
                            too.setRezultat("Konkurs je uspesno izmenjen!");
                        } else {
                            too.setRezultat("Neuspesna izmena konkursa!");
                        }
                        break;
                    case 6:
                        PreliminarnaRangLista prl = (PreliminarnaRangLista) toz.getZahtev();
                        boolean sacuvano = KontrolerServera.getInstanca().sacuvajPRL(prl);
                        if (sacuvano) {
                            too.setRezultat("Preliminarna rang lista je uspesno kreirana!");
                        } else {
                            too.setRezultat("Neuspesno kreiranje PRL!");
                        }
                        break;
                    case 7:
                        ArrayList<PreliminarnaRangLista> rangListe = KontrolerServera.getInstanca().vratiPRL();
                        too.setRezultat(rangListe);
                        break;
                    case 8:
                        ArrayList<IspunjavanjeUslovaKonkursa> iuk = KontrolerServera.getInstanca().vratiIUK();
                        too.setRezultat(iuk);
                        break;
                    case 9:
                        ArrayList<KoriscenjeKreditaUPrethodnojGodini> kku
                                = KontrolerServera.getInstanca().vratiKKU();
                        too.setRezultat(kku);
                        break;
                    case 10:
                        System.out.println("Sifra prl: " + toz.getZahtev() + "");
                        int sifra = (int) toz.getZahtev();
                        ArrayList<StavkaPreliminarneRangListe> stavke
                                = KontrolerServera.getInstanca().vratiStavkePRL(sifra);
                        too.setRezultat(stavke);
                        break;
                    case 11:
                        ArrayList<SpisakPrijavljenih> prijavljeni
                                = KontrolerServera.getInstanca().vratiSpisakPrijavljenih();
                        too.setRezultat(prijavljeni);
                        break;
                    case 12:
                        int id
                                = KontrolerServera.getInstanca().vratiID();
                        too.setRezultat(id);
                        break;
                    case 13:
                        PreliminarnaRangLista pr = (PreliminarnaRangLista) toz.getZahtev();
                        boolean izmenjena = KontrolerServera.getInstanca().izmeniPRL(pr);
                        if (izmenjena) {
                            too.setRezultat("Uspesna izmena PRL");
                        } else {
                            too.setRezultat("Neuspesna izmena PRL");
                        }
                        break;
                    case 14:
                        PreliminarnaRangLista r = (PreliminarnaRangLista) toz.getZahtev();
                        boolean obrisana = KontrolerServera.getInstanca().obrisiPRL(r);
                        if (obrisana) {
                            too.setRezultat("Uspesno obrisana PRL!");
                        } else {
                            too.setRezultat("Neuspesno brisanje PRL!");
                        }
                        break;
                    case 15:
                        ArrayList<TipKonkursa> tipoviKonkursa = KontrolerServera.getInstanca().vratiTipoveKonkursa();
                        too.setRezultat(tipoviKonkursa);
                        break;
                    case 16:
                        Konkurs konkurs = (Konkurs) toz.getZahtev();
                        KontrolerServera.getInstanca().obrisiKonkurs(konkurs);
                        break;
                    case 17:
                        Student s = (Student) toz.getZahtev();
                        boolean uspesnaIzmena = KontrolerServera.getInstanca().izmeniStudenta(s);
                        if (uspesnaIzmena) {
                            too.setRezultat("Informacije o studentu su uspesno izmenjene!");
                        } else {
                            too.setRezultat("Neuspesna izmena informacija o studentu!");
                        }
                        break;
                    case 18:
                        TipKonkursa tk = (TipKonkursa) toz.getZahtev();
                        boolean promenjenNaziv = KontrolerServera.getInstanca().izmeniNaziv(tk);
                        if (promenjenNaziv) {
                            too.setRezultat("Naziv je uspesno promenjen!");
                        } else {
                            too.setRezultat("Neuspesna izmena naziva!");
                        }
                        break;
                    case 20:
                        StavkaPreliminarneRangListe sp = (StavkaPreliminarneRangListe) toz.getZahtev();
                        boolean unetaStavka = KontrolerServera.getInstanca().dodajStavku(sp);
                        if (unetaStavka) {
                            too.setRezultat("Stavka rang liste je uspesno uneta!");
                        } else {
                            too.setRezultat("Neuspesan unos stavke rang liste!");
                        }

                }
                posajiOdgovor(too);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void posajiOdgovor(TransferObjekatOdgovor too) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            oos.writeObject(too);
        } catch (IOException ex) {
            Logger.getLogger(NitKlijenta.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
