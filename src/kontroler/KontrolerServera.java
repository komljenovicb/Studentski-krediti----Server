/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kontroler;

import dbb.DBBroker;
import domen.Banka;
import domen.IspunjavanjeUslovaKonkursa;
import domen.Konkurs;
import domen.KoriscenjeKreditaUPrethodnojGodini;
import domen.PreliminarnaRangLista;
import domen.SpisakPrijavljenih;
import domen.StavkaPreliminarneRangListe;
import domen.Student;
import domen.TipKonkursa;
import java.util.ArrayList;
import niti.NitKlijenta;

/**
 *
 * @author Bojana Komljenovic
 */
public class KontrolerServera {

    private static KontrolerServera instanca;
    private ArrayList<NitKlijenta> klijenti;

    private KontrolerServera() {
        klijenti = new ArrayList<>();
    }

    public static KontrolerServera getInstanca() {
        if (instanca == null) {
            instanca = new KontrolerServera();
        }
        return instanca;
    }

    // 1
    public ArrayList<Konkurs> vratiKonkurse() {
        ArrayList<Konkurs> konkursi = new ArrayList<>();
        konkursi = DBBroker.getInstanca().vratiKonkurse();
        return konkursi;
    }

    public int vratiMaxID() {
        int id = DBBroker.getInstanca().vratiMaxIDKonkursa();
        return id;
    }

    public ArrayList<NitKlijenta> getKlijenti() {
        return klijenti;
    }

    public boolean dodajKonkurs(Konkurs k) {
        return DBBroker.getInstanca().dodajKonkurs(k);
    }

    public ArrayList<Banka> vratiBanke() {
        return DBBroker.getInstanca().vratiBanke();
    }

    public boolean izmeniKonkurs(Konkurs kon) {
        return DBBroker.getInstanca().izmeniKonkurs(kon);
    }

    public boolean sacuvajPRL(PreliminarnaRangLista prl) {
        return DBBroker.getInstanca().sacuvajPRL(prl);
    }

    public ArrayList<PreliminarnaRangLista> vratiPRL() {
        return DBBroker.getInstanca().vratiPRL();
    }

    public ArrayList<IspunjavanjeUslovaKonkursa> vratiIUK() {
        return DBBroker.getInstanca().vratiIUK();
    }

    public ArrayList<KoriscenjeKreditaUPrethodnojGodini> vratiKKU() {
        return DBBroker.getInstanca().vratiKKU();
    }

    public ArrayList<SpisakPrijavljenih> vratiSpisakPrijavljenih() {
        return DBBroker.getInstanca().vratiSpisakPrijavljenih();
    }

    public int vratiID() {
        return DBBroker.getInstanca().vratiID();
    }

    public ArrayList<StavkaPreliminarneRangListe> vratiStavkePRL(int id) {
        return DBBroker.getInstanca().vratiStavkePRL(id);
    }

    public boolean izmeniPRL(PreliminarnaRangLista prl) {
        return DBBroker.getInstanca().izmeniPRL(prl);
    }

    public boolean obrisiPRL(PreliminarnaRangLista r) {
        return DBBroker.getInstanca().obrisiPRL(r);
    }

    public ArrayList<TipKonkursa> vratiTipoveKonkursa() {
        return DBBroker.getInstanca().vratiTipoveKonkursa();
    }

    public void obrisiKonkurs(Konkurs konkurs) {
        DBBroker.getInstanca().obrisiKonkurs(konkurs);
    }

    public boolean izmeniStudenta(Student s) {
        return DBBroker.getInstanca().izmeniStudenta(s);
    }

    public boolean izmeniNaziv(TipKonkursa naziv) {
        return DBBroker.getInstanca().izmeniNaziv(naziv);
    }

    public boolean dodajStavku(StavkaPreliminarneRangListe sp) {
        return DBBroker.getInstanca().dodajStavku(sp);
    }

}
