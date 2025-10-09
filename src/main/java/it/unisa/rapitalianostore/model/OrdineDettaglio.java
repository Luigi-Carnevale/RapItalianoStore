package it.unisa.rapitalianostore.model;

public class OrdineDettaglio {
    private int id;
    private int idProdotto;
    private int quantita;
    private double prezzoUnitario;

    // Campi extra per la view
    private String titoloProdotto;
    private String immagine; 

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdProdotto() { return idProdotto; }
    public void setIdProdotto(int idProdotto) { this.idProdotto = idProdotto; }

    public int getQuantita() { return quantita; }
    public void setQuantita(int quantita) { this.quantita = quantita; }

    public double getPrezzoUnitario() { return prezzoUnitario; }
    public void setPrezzoUnitario(double prezzoUnitario) { this.prezzoUnitario = prezzoUnitario; }

    public String getTitoloProdotto() { return titoloProdotto; }
    public void setTitoloProdotto(String titoloProdotto) { this.titoloProdotto = titoloProdotto; }

    public String getImmagine() { return immagine; }
    public void setImmagine(String immagine) { this.immagine = immagine; }
}
