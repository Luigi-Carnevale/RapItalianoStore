package it.unisa.rapitalianostore.model;

public class Prodotto {
    private int id;
    private String titolo;
    private String descrizione; // ✅ nuovo campo
    private double prezzo;
    private String immagine;
    private int idArtista;

    // Relazione con Artista
    private Artista artista;

    public Prodotto() {}

    // Getter e Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }

    public String getDescrizione() { return descrizione; }  // ✅ nuovo getter
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; } // ✅ nuovo setter

    public double getPrezzo() { return prezzo; }
    public void setPrezzo(double prezzo) { this.prezzo = prezzo; }

    public String getImmagine() { return immagine; }
    public void setImmagine(String immagine) { this.immagine = immagine; }

    public int getIdArtista() { return idArtista; }
    public void setIdArtista(int idArtista) { this.idArtista = idArtista; }

    public Artista getArtista() { return artista; }
    public void setArtista(Artista artista) { this.artista = artista; }
}
