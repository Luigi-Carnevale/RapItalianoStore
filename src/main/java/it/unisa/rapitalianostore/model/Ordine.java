package it.unisa.rapitalianostore.model;

import java.sql.Timestamp;
import java.util.List;

public class Ordine {
    private int id;
    private int idUtente;          // 👈 aggiunto
    private String emailUtente;    // 👈 aggiunto
    private Timestamp dataOrdine;
    private double totale;

    private String indirizzoSpedizione;
    private String metodoPagamento;

    private Utente utente;  // opzionale, se vuoi collegare direttamente l'oggetto Utente
    private List<OrdineDettaglio> dettagli;

    public Ordine() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdUtente() { return idUtente; }
    public void setIdUtente(int idUtente) { this.idUtente = idUtente; }

    public String getEmailUtente() { return emailUtente; }
    public void setEmailUtente(String emailUtente) { this.emailUtente = emailUtente; }

    public Timestamp getDataOrdine() { return dataOrdine; }
    public void setDataOrdine(Timestamp dataOrdine) { this.dataOrdine = dataOrdine; }

    public double getTotale() { return totale; }
    public void setTotale(double totale) { this.totale = totale; }

    public String getIndirizzoSpedizione() { return indirizzoSpedizione; }
    public void setIndirizzoSpedizione(String indirizzoSpedizione) { this.indirizzoSpedizione = indirizzoSpedizione; }

    public String getMetodoPagamento() { return metodoPagamento; }
    public void setMetodoPagamento(String metodoPagamento) { this.metodoPagamento = metodoPagamento; }

    public Utente getUtente() { return utente; }
    public void setUtente(Utente utente) { this.utente = utente; }

    public List<OrdineDettaglio> getDettagli() { return dettagli; }
    public void setDettagli(List<OrdineDettaglio> dettagli) { this.dettagli = dettagli; }
}
