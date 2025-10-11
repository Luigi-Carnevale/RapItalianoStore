package it.unisa.rapitalianostore.dao;

import it.unisa.rapitalianostore.model.*;
import it.unisa.rapitalianostore.utils.DBManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecensioneDAO {

    // 🔹 Trova tutte le recensioni di un prodotto
    public List<Recensione> findByProdotto(int idProdotto) {
        List<Recensione> recensioni = new ArrayList<>();
        String sql = "SELECT r.*, u.id_utente, u.email " +
                     "FROM recensioni r JOIN utenti u ON r.id_utente = u.id_utente " +
                     "WHERE r.id_prodotto = ? ORDER BY r.data_recensione DESC";

        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProdotto);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Recensione r = new Recensione();
                r.setId(rs.getInt("id_recensione"));
                r.setValutazione(rs.getInt("valutazione"));   // ✅ coerente con servlet
                r.setCommento(rs.getString("commento"));
                r.setDataRecensione(rs.getTimestamp("data_recensione"));

                Utente u = new Utente();
                u.setId(rs.getInt("id_utente"));
                u.setEmail(rs.getString("email"));
                r.setUtente(u);

                recensioni.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recensioni;
    }

    // 🔹 Salva una recensione
    public boolean save(Recensione r) {
        String sql = "INSERT INTO recensioni (id_prodotto, id_utente, valutazione, commento) VALUES (?, ?, ?, ?)";

        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

        	ps.setInt(1, r.getProdotto().getId());
        	ps.setInt(2, r.getUtente().getId());
            ps.setInt(3, r.getValutazione());
            ps.setString(4, r.getCommento());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
