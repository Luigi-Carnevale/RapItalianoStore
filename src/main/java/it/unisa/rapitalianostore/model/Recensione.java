package it.unisa.rapitalianostore.model;

import java.sql.Timestamp;

public class Recensione {
    private int id;
    private int valutazione; // da 1 a 5 (prima "valutazione")
    private String commento;
    private Timestamp dataRecensione;

    // Relazioni
    private int idUtente;   // 🔹 utile per insert/update veloci
    private int idProdotto; // 🔹 utile per insert/update veloci
    private Utente utente;  // oggetto utente (join)
    private Prodotto prodotto; // oggetto prodotto (join)

    public Recensione() {}

    // Getter e Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getValutazione() { return valutazione; }
    public void setValutazione(int valutazione) { this.valutazione = valutazione; }

    public String getCommento() { return commento; }
    public void setCommento(String commento) { this.commento = commento; }

    public Timestamp getDataRecensione() { return dataRecensione; }
    public void setDataRecensione(Timestamp dataRecensione) { this.dataRecensione = dataRecensione; }

    public int getIdUtente() { return idUtente; }
    public void setIdUtente(int idUtente) { this.idUtente = idUtente; }

    public int getIdProdotto() { return idProdotto; }
    public void setIdProdotto(int idProdotto) { this.idProdotto = idProdotto; }

    public Utente getUtente() { return utente; }
    public void setUtente(Utente utente) { this.utente = utente; }

    public Prodotto getProdotto() { return prodotto; }
    public void setProdotto(Prodotto prodotto) { this.prodotto = prodotto; }
}
