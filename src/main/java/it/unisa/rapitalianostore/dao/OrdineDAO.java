package it.unisa.rapitalianostore.dao;

import it.unisa.rapitalianostore.model.*;
import it.unisa.rapitalianostore.utils.DBManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdineDAO {

    // =====================================================
    // Salvataggio nuovo ordine
    // =====================================================
    public boolean salvaOrdine(Utente utente, Carrello carrello, String indirizzo, String metodoPagamento) {
        String sqlOrdine =
            "INSERT INTO ordini (id_utente, totale, indirizzo_spedizione, metodo_pagamento) VALUES (?, ?, ?, ?)";
        String sqlDettaglio =
            "INSERT INTO ordine_dettagli " +
            "(id_ordine, id_prodotto, quantita, prezzo_unitario, titolo_prodotto, immagine_prodotto) " +
            "VALUES (?,?,?,?,?,?)";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement psOrd = conn.prepareStatement(sqlOrdine, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement psDet = conn.prepareStatement(sqlDettaglio)) {

            conn.setAutoCommit(false);

            psOrd.setInt(1, utente.getId());
            psOrd.setDouble(2, carrello.getTotale());
            psOrd.setString(3, indirizzo);
            psOrd.setString(4, metodoPagamento);
            psOrd.executeUpdate();

            int idOrdine;
            try (ResultSet rs = psOrd.getGeneratedKeys()) {
                if (!rs.next()) {
                    conn.rollback();
                    return false;
                }
                idOrdine = rs.getInt(1);
            }

            for (CarrelloItem item : carrello.getItems()) {
                psDet.setInt(1, idOrdine);
                psDet.setObject(2, item.getProdotto() != null ? item.getProdotto().getId() : null, Types.INTEGER);
                psDet.setInt(3, item.getQuantita());
                psDet.setDouble(4, item.getProdotto().getPrezzo());
                psDet.setString(5, item.getProdotto().getTitolo());
                psDet.setString(6, item.getProdotto().getImmagine());
                psDet.addBatch();
            }
            psDet.executeBatch();

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // =====================================================
    // Ordini di un utente con dettagli prodotti
    // =====================================================
    public List<Ordine> findByUtenteWithDettagli(int idUtente) {
        String sql = "SELECT * FROM ordini WHERE id_utente = ? ORDER BY data_ordine DESC";
        List<Ordine> ordini = new ArrayList<>();
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUtente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ordine o = mapOrdineBase(rs);
                    o.setDettagli(findDettagliByOrdine(o.getId()));
                    ordini.add(o);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ordini;
    }

    // =====================================================
    // Lista completa di tutti gli ordini (per admin, con email cliente)
    // =====================================================
    public List<Ordine> findAll() {
        String sql = "SELECT o.*, u.email AS email_utente " +
                     "FROM ordini o JOIN utenti u ON o.id_utente = u.id_utente " +
                     "ORDER BY o.data_ordine DESC";
        List<Ordine> ordini = new ArrayList<>();
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Ordine o = mapOrdineBase(rs);
                o.setEmailUtente(rs.getString("email_utente"));
                o.setDettagli(findDettagliByOrdine(o.getId()));
                ordini.add(o);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ordini;
    }

    // =====================================================
    // Filtri per admin (date e email cliente)
    // =====================================================
    public List<Ordine> findByFilter(String dataInizio, String dataFine, String clienteEmail) {
        StringBuilder sql = new StringBuilder(
            "SELECT o.*, u.email AS email_utente " +
            "FROM ordini o JOIN utenti u ON o.id_utente = u.id_utente WHERE 1=1"
        );
        List<Object> params = new ArrayList<>();

        if (dataInizio != null && !dataInizio.isEmpty()) {
            sql.append(" AND o.data_ordine >= ?");
            params.add(Timestamp.valueOf(dataInizio + " 00:00:00"));
        }
        if (dataFine != null && !dataFine.isEmpty()) {
            sql.append(" AND o.data_ordine <= ?");
            params.add(Timestamp.valueOf(dataFine + " 23:59:59"));
        }
        if (clienteEmail != null && !clienteEmail.isEmpty()) {
            sql.append(" AND LOWER(u.email) LIKE ?");
            params.add("%" + clienteEmail.toLowerCase() + "%");
        }

        sql.append(" ORDER BY o.data_ordine DESC");

        List<Ordine> ordini = new ArrayList<>();
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ordine o = mapOrdineBase(rs);
                    o.setEmailUtente(rs.getString("email_utente"));
                    o.setDettagli(findDettagliByOrdine(o.getId()));
                    ordini.add(o);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ordini;
    }

    // =====================================================
    // Dettagli di un ordine
    // =====================================================
    public List<OrdineDettaglio> findDettagliByOrdine(int idOrdine) {
        String sqlDet =
            "SELECT id_dettaglio, id_prodotto, quantita, prezzo_unitario, titolo_prodotto, immagine_prodotto " +
            "FROM ordine_dettagli WHERE id_ordine = ?";
        List<OrdineDettaglio> dettagli = new ArrayList<>();
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sqlDet)) {
            ps.setInt(1, idOrdine);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrdineDettaglio d = new OrdineDettaglio();
                    d.setId(rs.getInt("id_dettaglio"));
                    d.setIdProdotto((Integer) rs.getObject("id_prodotto") == null ? 0 : rs.getInt("id_prodotto"));
                    d.setQuantita(rs.getInt("quantita"));
                    d.setPrezzoUnitario(rs.getDouble("prezzo_unitario"));
                    d.setTitoloProdotto(rs.getString("titolo_prodotto"));
                    d.setImmagine(rs.getString("immagine_prodotto"));
                    dettagli.add(d);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dettagli;
    }

    // =====================================================
    // Mapper ordine base (senza dettagli)
    // =====================================================
    private Ordine mapOrdineBase(ResultSet rs) throws SQLException {
        Ordine o = new Ordine();
        o.setId(rs.getInt("id_ordine"));
        o.setIdUtente(rs.getInt("id_utente"));  // id utente
        o.setDataOrdine(rs.getTimestamp("data_ordine"));
        o.setTotale(rs.getDouble("totale"));
        o.setIndirizzoSpedizione(rs.getString("indirizzo_spedizione"));
        o.setMetodoPagamento(rs.getString("metodo_pagamento"));
        return o;
    }
}
