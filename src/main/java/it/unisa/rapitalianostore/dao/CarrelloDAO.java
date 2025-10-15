package it.unisa.rapitalianostore.dao;

import it.unisa.rapitalianostore.utils.DBManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarrelloDAO {

    /** Aggiunge (o incrementa) un item nel carrello utente */
    public void aggiungiItem(int idUtente, int idProdotto, int quantita) {
        // Se esiste, aggiorno la quantità; altrimenti inserisco
        final String upsertSql =
                "INSERT INTO carrello_item (id_utente, id_prodotto, quantita) " +
                "VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE quantita = quantita + VALUES(quantita)";

        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(upsertSql)) {
            ps.setInt(1, idUtente);
            ps.setInt(2, idProdotto);
            ps.setInt(3, Math.max(1, quantita));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Imposta la quantità esatta per un item; se <=0 rimuove l’item */
    public void aggiornaQuantita(int idUtente, int idProdotto, int quantita) {
        if (quantita <= 0) {
            rimuoviItem(idUtente, idProdotto);
            return;
        }
        final String sql = "UPDATE carrello_item SET quantita=? WHERE id_utente=? AND id_prodotto=?";
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, quantita);
            ps.setInt(2, idUtente);
            ps.setInt(3, idProdotto);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Rimuove un singolo item dal carrello */
    public void rimuoviItem(int idUtente, int idProdotto) {
        final String sql = "DELETE FROM carrello_item WHERE id_utente=? AND id_prodotto=?";
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUtente);
            ps.setInt(2, idProdotto);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Svuota completamente il carrello dell’utente */
    public void svuotaCarrello(int idUtente) {
        final String sql = "DELETE FROM carrello_item WHERE id_utente=?";
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUtente);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Carica gli item del carrello con info di prodotto (titolo, prezzo, immagine) */
    public List<CarrelloItemDTO> caricaItems(int idUtente) {
        final String sql =
                "SELECT c.id_prodotto, c.quantita, p.titolo, p.prezzo, p.immagine " +
                "FROM carrello_item c " +
                "JOIN prodotti p ON p.id_prodotto = c.id_prodotto " +
                "WHERE c.id_utente = ?";

        List<CarrelloItemDTO> items = new ArrayList<>();
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUtente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CarrelloItemDTO dto = new CarrelloItemDTO();
                    dto.setIdProdotto(rs.getInt("id_prodotto"));
                    dto.setQuantita(rs.getInt("quantita"));
                    dto.setTitolo(rs.getString("titolo"));
                    dto.setPrezzo(rs.getDouble("prezzo"));
                    dto.setImmagine(rs.getString("immagine"));
                    items.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    /** DTO semplice per non dipendere dalle tue model class */
    public static class CarrelloItemDTO {
        private int idProdotto;
        private int quantita;
        private String titolo;
        private double prezzo;
        private String immagine;

        public int getIdProdotto() { return idProdotto; }
        public void setIdProdotto(int idProdotto) { this.idProdotto = idProdotto; }

        public int getQuantita() { return quantita; }
        public void setQuantita(int quantita) { this.quantita = quantita; }

        public String getTitolo() { return titolo; }
        public void setTitolo(String titolo) { this.titolo = titolo; }

        public double getPrezzo() { return prezzo; }
        public void setPrezzo(double prezzo) { this.prezzo = prezzo; }

        public String getImmagine() { return immagine; }
        public void setImmagine(String immagine) { this.immagine = immagine; }
    }
}
