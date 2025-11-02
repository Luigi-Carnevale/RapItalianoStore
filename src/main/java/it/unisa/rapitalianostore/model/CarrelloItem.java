package it.unisa.rapitalianostore.model;

public class CarrelloItem {
    private Prodotto prodotto;
    private int quantita;

    public CarrelloItem(Prodotto prodotto) {
        this.prodotto = prodotto;
        this.quantita = 1;
    }

    public Prodotto getProdotto() { return prodotto; }
    public void setProdotto(Prodotto prodotto) { this.prodotto = prodotto; }

    public int getQuantita() { return quantita; }
    public void setQuantita(int quantita) { this.quantita = Math.max(1, quantita); } // MOD: clamp minimo

    public void incrementaQuantita() { this.quantita++; }
    public void decrementaQuantita() { if (this.quantita > 1) this.quantita--; }

    public double getTotale() {
        return prodotto.getPrezzo() * quantita;
    }
}
