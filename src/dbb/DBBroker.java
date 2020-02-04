/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbb;

import domen.Banka;
import domen.Fakultet;
import domen.IspunjavanjeUslovaKonkursa;
import domen.Konkurs;
import domen.KoriscenjeKreditaUPrethodnojGodini;
import domen.Mesto;
import domen.PreliminarnaRangLista;
import domen.PrijavaZaKonkurs;
import domen.SpisakPrijavljenih;
import domen.StavkaPreliminarneRangListe;
import domen.StavkaSpiska;
import domen.Student;
import domen.TipKonkursa;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bojana Komljenovic
 */
public class DBBroker {

    private static DBBroker instanca;
    private Connection konekcija;

    private DBBroker() {
        try {
            konekcija
                    = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "bojana", "bojana");
            konekcija.setAutoCommit(false);
        } catch (SQLException ex) {
            System.err.println("Greska prilikom konekcije na bazu!");
            ex.printStackTrace();
        }
    }

    public static DBBroker getInstanca() {
        if (instanca == null) {
            instanca = new DBBroker();
        }
        return instanca;
    }

    public void commit() {
        try {
            konekcija.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void rollback() {
        try {
            konekcija.rollback();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public ArrayList<Konkurs> vratiKonkurse() {

        ArrayList<Konkurs> konkursi = new ArrayList<>();

        String upit = "select * from konkurs k inner join tip_konkursa tk on k.tip_id = tk.tip_id inner join banka b on b.banka_id = k.banka_id";

        System.out.println(upit);

        try {

            Statement stat = konekcija.createStatement();
            ResultSet rs = stat.executeQuery(upit);

            while (rs.next()) {

                int id = rs.getInt("konkurs_id");
                String rokPodnosenja = rs.getString("rok_podosenja_dok");
                String skolskaGodina = rs.getString("skolska_godina");
                int tip_id = rs.getInt("tip_id");
                String naziv_tipa = rs.getString("naziv_tipa_konkursa");
                int idBanke = rs.getInt("banka_id");
                String nazivBanke = rs.getString("naziv_banke");
                String ovlascenoLice = rs.getString("ovlasceno_lice");
                System.out.println("Iz baze: " + id + " " + naziv_tipa);

                Konkurs k = new Konkurs(id, skolskaGodina, rokPodnosenja, new TipKonkursa(tip_id, naziv_tipa), new Banka(idBanke, nazivBanke, ovlascenoLice));
                konkursi.add(k);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return konkursi;
    }

    public int vratiMaxIDKonkursa() {

        int id = -1;

        String upit = "select max(k.konkurs_id) as max from konkurs k inner join tip_konkursa tk on k.tip_id = tk.tip_id";

        System.out.println(upit);

        try {

            Statement stat = konekcija.createStatement();
            ResultSet rs = stat.executeQuery(upit);

            while (rs.next()) {
                id = rs.getInt("max");
                System.out.println("Max id konkursa je: " + id);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return ++id;
    }

    public boolean dodajKonkurs(Konkurs k) {

        String upit = "INSERT INTO konkurs (KONKURS_ID, SKOLSKA_GODINA, TIP_ID, BANKA_ID, ROK_PODOSENJA_DOK) VALUES (?, ?, ?, ?, ?)";
        System.out.println(upit);

        try {
            PreparedStatement ps = konekcija.prepareStatement(upit);
            ps.setInt(1, k.getKonkursID());
            ps.setString(2, k.getSkolskaGodina());
            ps.setInt(3, k.getTipKonkursa().getTipID());
            ps.setInt(4, k.getBanka().getBankaID());
            ps.setString(5, k.getRokPodnosenjaDokumentacije());
            System.out.println(k.getKonkursID() + "|" + k.getSkolskaGodina() + "|" + k.getTipKonkursa().getTipID() + "" + k.getBanka().getBankaID() + "|" + k.getRokPodnosenjaDokumentacije() + "|" + k.getTipKonkursa().getNazivTipaKonkursa());
            ps.executeUpdate();
            konekcija.commit();
            return true;

        } catch (SQLException ex) {
            System.out.println("Neuspesan unos konkursa!");
            return false;
        }

    }

    public ArrayList<Banka> vratiBanke() {

        ArrayList<Banka> banke = new ArrayList<>();
        String upit = "select * from banka";
        System.out.println(upit);

        try {

            Statement stat = konekcija.createStatement();
            ResultSet rs = stat.executeQuery(upit);

            while (rs.next()) {

                int id = rs.getInt("banka_id");
                String nazivBanke = rs.getString("naziv_banke");
                String ovlascenoLice = rs.getString("ovlasceno_lice");

                Banka b = new Banka(id, nazivBanke, ovlascenoLice);
                banke.add(b);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return banke;
    }

    private int vratiIDTipaKonkursa(String nazivTipaKonkursa) {

        String upit = "select * from tip_konkursa where naziv_tipa_konkursa = '" + nazivTipaKonkursa + "'";
        System.out.println(upit);

        int id = -1;

        try {
            Statement stat = konekcija.createStatement();
            ResultSet rs = stat.executeQuery(upit);
            while (rs.next()) {
                id = rs.getInt("tip_id");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBBroker.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("TIP_ID: " + id);

        return id;

    }

    public boolean izmeniKonkurs(Konkurs kon) {
        String upit = "update konkurs set skolska_godina = ?, tip_id = ?, banka_id = ?, rok_podosenja_dok = ? where konkurs_id = " + kon.getKonkursID();
        System.out.println(upit);
        try {
            PreparedStatement ps = konekcija.prepareStatement(upit);
            ps.setString(1, kon.getSkolskaGodina());
            ps.setInt(2, kon.getTipKonkursa().getTipID());
            ps.setInt(3, kon.getBanka().getBankaID());
            ps.setString(4, kon.getRokPodnosenjaDokumentacije());
            ps.executeUpdate();
            konekcija.commit();

            return true;
        } catch (SQLException ex) {
            System.out.println("Neuspesna izmena konkursa!");
            return false;
        }
    }

    public boolean sacuvajPRL(PreliminarnaRangLista prl) {

        int id = prl.getSifraPRL();

        try {

            String upit = "insert into preliminarna_rang_lista(sifra_prl, opis_prl, datum_objavljivanja_prl) values(?,?,?)";
            System.out.println(prl.getSifraPRL() + "" + prl.getOpisPRL() + "" + prl.getDatumObjavljivanjaPRL());
            System.out.println(upit);

            PreparedStatement ps = konekcija.prepareStatement(upit);
            ps.setInt(1, id);
            ps.setString(2, prl.getOpisPRL());
            ps.setDate(3, new Date(prl.getDatumObjavljivanjaPRL().getTime()));
            ps.executeUpdate();

            upit = "insert into stavka_preliminarne_rang_liste(sifra_prl, rbr_stavke_prl, napomena_prl, iuk_id, kk_id) values(?,?,?,?,?)";
            System.out.println(upit);
            ps = konekcija.prepareStatement(upit);

            for (StavkaPreliminarneRangListe st : prl.getListaStavki()) {

                ps.setInt(1, id);
                ps.setInt(2, st.getRbrStavke());
                ps.setString(3, st.getNapomenaPRL());
                ps.setInt(4, st.getIuk().getIukID());
                ps.setInt(5, st.getKku().getKkID());
                ps.executeUpdate();
            }

            ps.close();
            konekcija.commit();
            return true;

        } catch (SQLException ex) {
            System.out.println("Cuvanje PRL nije uspelo!");
            return false;
        }

    }

    public boolean izmeniPRL(PreliminarnaRangLista prl) {

        try {

            String upit = "update preliminarna_rang_lista set opis_prl = ?, datum_objavljivanja_prl = ? where sifra_prl = " + prl.getSifraPRL();
            System.out.println(prl.getSifraPRL() + "" + prl.getOpisPRL() + "" + prl.getDatumObjavljivanjaPRL());
            System.out.println(upit);

            PreparedStatement ps = konekcija.prepareStatement(upit);
            ps.setString(1, prl.getOpisPRL());
            ps.setDate(2, new Date(prl.getDatumObjavljivanjaPRL().getTime()));
            ps.executeUpdate();
            for (StavkaPreliminarneRangListe st : prl.getListaStavki()) {

                System.out.println("Status stavke: " + st.getStatus() + "" + st.getNapomenaPRL() + "" + st.getRbrStavke() + "" + st.getIuk());

                if (st.getStatus() != null) {

                    if (st.getStatus().equals("insert")) {
                        upit = "insert into stavka_preliminarne_rang_liste(sifra_prl, rbr_stavke_prl, napomena_prl, iuk_id, kk_id) values(?,?,?,?,?)";
                        System.out.println("Sifra PRL: " + st.getSifraPRL() + ", rbr_stavke: " + st.getRbrStavke());
                        System.out.println(upit);
                        ps = konekcija.prepareStatement(upit);
                        ps.setInt(1, prl.getSifraPRL());
                        ps.setInt(2, st.getRbrStavke());
                        ps.setString(3, st.getNapomenaPRL());
                        ps.setInt(4, st.getIuk().getIukID());
                        ps.setInt(5, st.getKku().getKkID());
                        ps.executeUpdate();
                    }

                    if (st.getStatus().equals("update")) {
                        upit = "update stavka_preliminarne_rang_liste set napomena_prl = ?, iuk_id = ?, kk_id = ? where sifra_prl = " + prl.getSifraPRL() + " and rbr_stavke_prl = " + st.getRbrStavke();
                        System.out.println(upit);
                        ps = konekcija.prepareStatement(upit);
                        ps.setString(1, st.getNapomenaPRL());
                        ps.setInt(2, st.getIuk().getIukID());
                        ps.setInt(3, st.getKku().getKkID());
                        ps.executeUpdate();
                    }
                }
            }

            konekcija.commit();
            ps.close();
            return true;

        } catch (SQLException ex) {
            System.out.println("Izmena PRL nije uspela!");
            return false;
        }

    }

    public ArrayList<PreliminarnaRangLista> vratiPRL() {

        ArrayList<PreliminarnaRangLista> prl = new ArrayList<>();
        String upit = "select * from preliminarna_rang_lista";

        System.out.println(upit);
        try {
            Statement stat = konekcija.createStatement();
            ResultSet rs = stat.executeQuery(upit);
            while (rs.next()) {

                int id = rs.getInt("sifra_prl");
                String opis = rs.getString("opis_prl");
                Date datumObjavljivanja = rs.getDate("datum_objavljivanja_prl");

                PreliminarnaRangLista p = new PreliminarnaRangLista(id, opis, datumObjavljivanja, null);
                prl.add(p);

            }
        } catch (SQLException ex) {
            Logger.getLogger(DBBroker.class.getName()).log(Level.SEVERE, null, ex);
        }

        return prl;
    }

    public ArrayList<IspunjavanjeUslovaKonkursa> vratiIUK() {

        ArrayList<IspunjavanjeUslovaKonkursa> lista = new ArrayList<>();
        String upit = "select * from ispunjavanje_uslova_konkursa";

        System.out.println(upit);
        try {
            Statement stat = konekcija.createStatement();
            ResultSet rs = stat.executeQuery(upit);
            while (rs.next()) {

                int id = rs.getInt("iuk_id");
                String opis = rs.getString("opis_iuk");

                IspunjavanjeUslovaKonkursa iuk = new IspunjavanjeUslovaKonkursa(id, opis);
                lista.add(iuk);

            }
        } catch (SQLException ex) {
            Logger.getLogger(DBBroker.class.getName()).log(Level.SEVERE, null, ex);
        }

        return lista;
    }

    public ArrayList<KoriscenjeKreditaUPrethodnojGodini> vratiKKU() {

        ArrayList<KoriscenjeKreditaUPrethodnojGodini> lista = new ArrayList<>();
        String upit = "select * from koriscenje_kredita_u_pr_godini";

        System.out.println(upit);
        try {
            Statement stat = konekcija.createStatement();
            ResultSet rs = stat.executeQuery(upit);
            while (rs.next()) {

                int id = rs.getInt("kk_id");
                String opis = rs.getString("opis_kk");

                KoriscenjeKreditaUPrethodnojGodini kk = new KoriscenjeKreditaUPrethodnojGodini(id, opis);
                lista.add(kk);

            }
        } catch (SQLException ex) {
            Logger.getLogger(DBBroker.class.getName()).log(Level.SEVERE, null, ex);
        }

        return lista;
    }

    public ArrayList<SpisakPrijavljenih> vratiSpisakPrijavljenih() {

        String upit = "select * from spisak_prijavljenih sp inner join fakultet f on f.fakultet_id = sp.fakultet_id inner join mesto m on m.mesto_id = sp.mesto_id";
        System.out.println(upit);

        ArrayList<SpisakPrijavljenih> lista = new ArrayList<>();

        try {
            Statement stat = konekcija.createStatement();
            ResultSet rs = stat.executeQuery(upit);
            while (rs.next()) {

                int rbr = rs.getInt("rbr");
                String skolskaGodina = rs.getString("skolska_godina");
                int mestoID = rs.getInt("mesto_id");
                String nazivMesta = rs.getString("naziv_mesta");
                int fakultetID = rs.getInt("fakultet_id");
                String nazivFakulteta = rs.getString("naziv_fakulteta");

                ArrayList<StavkaSpiska> listaStavki = vratiStavkeSpiska(rbr);

                SpisakPrijavljenih sp = new SpisakPrijavljenih(rbr, skolskaGodina, new Fakultet(fakultetID, nazivFakulteta, null), new Mesto(mestoID, nazivMesta, null), listaStavki);
                lista.add(sp);

            }
        } catch (SQLException ex) {
            Logger.getLogger(DBBroker.class.getName()).log(Level.SEVERE, null, ex);
        }

        return lista;
    }

    private ArrayList<StavkaSpiska> vratiStavkeSpiska(int rbr) {

        String upit = "select * from stavka_spiska sp inner join prijava_za_konkurs p on sp.prijava_id = p.prijava_id inner join student s on s.jmbg = p.jmbg inner join mesto m on s.izdata_lk = m.mesto_id inner join mesto r on s.rodjen = r.mesto_id where rbr_spiska = " + rbr;
        ArrayList<StavkaSpiska> lista = new ArrayList<>();

        System.out.println(upit);
        try {
            Statement stat = konekcija.createStatement();
            ResultSet rs = stat.executeQuery(upit);
            while (rs.next()) {

                int rbrStavkeSpiska = rs.getInt("rbr_stavke_spiska");
                String primedba = rs.getString("primedba");
                int prijavaID = rs.getInt("prijava_id");
                Date datumPotpisivanja = rs.getDate("datum_potpisivanja");
                String jmbg = rs.getString("jmbg");
                String imePrezime = rs.getString("ime_prezime");
                boolean sluzioVojniRok = rs.getBoolean("sluzio_vojni_rok");
                boolean osetljivaDrustvenaGrupa = rs.getBoolean("osetljiva_drustvena_grupa");
                double uspeh = rs.getDouble("uspeh");
                int espb = rs.getInt("espb");
                Date datumRodjenja = rs.getDate("datum_rodjenja");
                Date datumUpisaFakulteta = rs.getDate("datum_upisa_fakulteta");
                double prosecanPrihod = rs.getDouble("prosecni_prihod");
                String brojLK = rs.getString("broj_licne_karte");
                String kontaktTelefon = rs.getString("kontakt_telefon");
                int mestoID = rs.getInt("izdata_lk");
                String nazivMesta = rs.getString("naziv_mesta");
                int mestoRodjenjaID = rs.getInt("rodjen");
                String nazivMestaRodjenja = rs.getString("naziv_mesta");
                

                StavkaSpiska sp
                        = new StavkaSpiska(rbr, rbrStavkeSpiska, primedba,
                                new PrijavaZaKonkurs(prijavaID,
                                        datumPotpisivanja, null,
                                        new Student(jmbg,
                                                imePrezime,
                                                sluzioVojniRok,
                                                osetljivaDrustvenaGrupa,
                                                uspeh,
                                                espb,
                                                datumRodjenja,
                                                datumUpisaFakulteta,
                                                prosecanPrihod,
                                                brojLK,
                                                kontaktTelefon,
                                                new Mesto(mestoRodjenjaID, nazivMestaRodjenja, null),
                                                new Mesto(mestoID, nazivMesta, null))));
                lista.add(sp);

            }
        } catch (SQLException ex) {
            Logger.getLogger(DBBroker.class.getName()).log(Level.SEVERE, null, ex);
        }

        return lista;

    }

    public int vratiID() {
        int id = -1;

        String upit = "select max(pr.sifra_prl) as max from preliminarna_rang_lista pr";

        System.out.println(upit);

        try {

            Statement stat = konekcija.createStatement();
            ResultSet rs = stat.executeQuery(upit);

            while (rs.next()) {
                id = rs.getInt("max");
                System.out.println("Max id konkursa je: " + id);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return ++id;
    }

    private void kreirajStavku(int sifraPRL, StavkaPreliminarneRangListe sp) {

        String upit = "insert into stavka_preliminarne_rang_liste values (?,?,?,?,?)";
        System.out.println("" + sifraPRL + " " + sp.getRbrStavke() + " " + sp.getIuk().getIukID());
        System.out.println(upit);
        try {
            PreparedStatement ps = konekcija.prepareStatement(upit);
            ps.setInt(1, sifraPRL);
            ps.setInt(2, sp.getRbrStavke());
            ps.setString(3, sp.getNapomenaPRL());
            ps.setInt(4, sp.getIuk().getIukID());
            ps.setInt(5, sp.getKku().getKkID());
            ps.executeQuery();
        } catch (SQLException ex) {
            System.out.println("Neuspesan unos stavke!");
        }
    }

    public ArrayList<StavkaPreliminarneRangListe> vratiStavkePRL(int id) {

        String upit = "select * from stavka_preliminarne_rang_liste sprl inner join koriscenje_kredita_u_pr_godini kk on sprl.kk_id = kk.kk_id inner join ispunjavanje_uslova_konkursa iuk on iuk.iuk_id = sprl.iuk_id inner join spisak_prijavljenih sp on sp.rbr = sprl.rbr_stavke_prl inner join mesto m on m.mesto_id = sp.mesto_id inner join fakultet f on f.fakultet_id = sp.fakultet_id where sifra_prl = " + id;
        System.out.println(upit);

        ArrayList<StavkaPreliminarneRangListe> lista = new ArrayList<>();

        try {
            Statement stat = konekcija.createStatement();
            ResultSet rs = stat.executeQuery(upit);
            while (rs.next()) {

                int rbrStavke = rs.getInt("rbr_stavke_prl");
                String napomenaPRL = rs.getString("napomena_prl");
                java.util.Date datumObjavljivanjaPRL = rs.getDate("datum_objavljivanja_prl");
                // koriscenje kredita u prethodnoj godini
                int idKK = rs.getInt("kk_id");
                String opisKK = rs.getString("opis_kk");
                KoriscenjeKreditaUPrethodnojGodini kk = new KoriscenjeKreditaUPrethodnojGodini(idKK, opisKK);
                // ispunjavanje uslova konkursa
                int iukID = rs.getInt("iuk_id");
                String opisIUK = rs.getString("opis_iuk");
                IspunjavanjeUslovaKonkursa iuk = new IspunjavanjeUslovaKonkursa(iukID, opisIUK);
                double ukupno = rs.getDouble("ukupno_poena_prl");
                // spisak prijavljenih              

                int rbr = rs.getInt("rbr");
                String skolskaGodina = rs.getString("skolska_godina");
                int mestoID = rs.getInt("mesto_id");
                String nazivMesta = rs.getString("naziv_mesta");
                int fakultetID = rs.getInt("fakultet_id");
                String nazivFakulteta = rs.getString("naziv_fakulteta");

                ArrayList<StavkaSpiska> listaStavki = vratiStavkeSpiska(rbr);

                SpisakPrijavljenih s = new SpisakPrijavljenih(rbr, skolskaGodina, new Fakultet(fakultetID, nazivFakulteta, null), new Mesto(mestoID, nazivMesta, null), listaStavki);

                StavkaPreliminarneRangListe sp = new StavkaPreliminarneRangListe(id, rbrStavke, napomenaPRL, ukupno, iuk, kk, s, null);
                lista.add(sp);

            }
        } catch (SQLException ex) {
            Logger.getLogger(DBBroker.class.getName()).log(Level.SEVERE, null, ex);
        }

        return lista;
    }

    public boolean obrisiPRL(PreliminarnaRangLista r) {

        String upit = "delete from preliminarna_rang_lista where sifra_prl = " + r.getSifraPRL();
        System.out.println(upit);
        try {
            Statement stat = konekcija.createStatement();
            stat.executeUpdate(upit);
            konekcija.commit();
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    public ArrayList<TipKonkursa> vratiTipoveKonkursa() {
        ArrayList<TipKonkursa> tk = new ArrayList<>();
        String upit = "select * from tip_konkursa";
        System.out.println(upit);

        try {

            Statement stat = konekcija.createStatement();
            ResultSet rs = stat.executeQuery(upit);

            while (rs.next()) {

                int id = rs.getInt("tip_id");
                String nazivTK = rs.getString("naziv_tipa_konkursa");

                TipKonkursa t = new TipKonkursa(id, nazivTK);
                tk.add(t);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return tk;
    }

    public void obrisiKonkurs(Konkurs konkurs) {

        String upit = "delete from konkurs where konkurs_id = " + konkurs.getKonkursID();
        System.out.println(upit);
        try {
            Statement stat = konekcija.createStatement();
            stat.executeUpdate(upit);
            stat.close();
            konekcija.commit();
        } catch (SQLException ex) {
        }

    }

    public boolean izmeniStudenta(Student s) {
        try {

            String upit = "update student set espb = ?, prosecni_prihod = ?, uspeh = ? where jmbg = '" + s.getJmbg() + "'";
            System.out.println(upit);

            PreparedStatement ps = konekcija.prepareStatement(upit);
            ps.setInt(1, s.getEspb());
            ps.setDouble(2, s.getProsecanPrihodPoClanu());
            ps.setDouble(3, s.getUspeh());
            ps.executeUpdate();
            konekcija.commit();
            return true;
        } catch (SQLException ex) {
            System.out.println("Izmena studenta nije uspela!");
            return false;
        }
    }

    public boolean izmeniNaziv(TipKonkursa tk) {
        try {

            String upit = "update tip_konkursa set naziv_tipa_konkursa = ? where tip_id = " + tk.getTipID();
            System.out.println(upit);

            PreparedStatement ps = konekcija.prepareStatement(upit);
            ps.setString(1, tk.getNazivTipaKonkursa());
            ps.executeUpdate();
            konekcija.commit();
            return true;
        } catch (SQLException ex) {
            System.out.println("Izmena naziva nije uspela!");
            return false;
        }
    }

    public boolean dodajStavku(StavkaPreliminarneRangListe sp) {

        try {

            System.out.println("Stavka: " + sp.getRbrStavke() + " " + sp.getKku() + " " + sp.getIuk() + " " + sp.getNapomenaPRL());

            String upit = "insert into stavka_preliminarne_rang_liste(rbr_stavke_prl, napomena, iuk_id, kk_id) values (?,?,?,?) where sifra_prl = " + sp.getSifraPRL();
            System.out.println(upit);

            PreparedStatement ps = konekcija.prepareStatement(upit);
            ps.setInt(1, sp.getRbrStavke());
            ps.setString(2, sp.getNapomenaPRL());
            ps.setInt(3, sp.getIuk().getIukID());
            ps.setInt(4, sp.getKku().getKkID());

            ps.executeUpdate();
            konekcija.commit();
            return true;

        } catch (SQLException ex) {
            System.out.println("Stavka nije dodata!");
            return false;
        }

    }

}
