package it.unisa.rapitalianostore.model;

import java.util.ArrayList;
import java.util.List;

public class Artista {
    private int id;
    private String nome;
    private String genere;
    private String bio;
    private String immagine;

    // 🔴 Nuovo campo: lista degli album associati all'artista
    private List<Prodotto> album = new ArrayList<>();

    public Artista() {}

    public Artista(int id, String nome, String genere, String bio, String immagine) {
        this.id = id;
        this.nome = nome;
        this.genere = genere;
        this.bio = bio;
        this.immagine = immagine;
    }

    // Getter e Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getGenere() { return genere; }
    public void setGenere(String genere) { this.genere = genere; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getImmagine() { return immagine; }
    public void setImmagine(String immagine) { this.immagine = immagine; }

    // Getter/Setter per album
    public List<Prodotto> getAlbum() { return album; }
    public void setAlbum(List<Prodotto> album) { this.album = album; }
}
