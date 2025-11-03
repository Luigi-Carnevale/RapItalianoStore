package it.unisa.rapitalianostore.dao;

import it.unisa.rapitalianostore.model.Artista;
import it.unisa.rapitalianostore.model.Prodotto;
import it.unisa.rapitalianostore.model.Recensione;
import it.unisa.rapitalianostore.utils.DBManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdottoDAO {

    // Trova tutti i prodotti con join sugli artisti
    public List<Prodotto> findAll() {
        List<Prodotto> lista = new ArrayList<>();
        String sql = "SELECT p.*, " +
                     "a.id_artista AS a_id, a.nome AS a_nome, a.genere AS a_genere, " +
                     "a.bio AS a_bio, a.immagine AS a_immagine " +
                     "FROM prodotti p LEFT JOIN artisti a ON p.id_artista = a.id_artista " +
                     "ORDER BY p.id_prodotto ASC"; // ASC per avere ID1 in cima

        try (Connection con = DBManager.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // Alias
    public List<Prodotto> findAllWithArtisti() {
        return findAll(); // stesso ordinamento ASC
    }

    // Trova prodotti filtrati
    public List<Prodotto> findFiltered(String q, String prezzo, String artistaId) {
        List<Prodotto> prodotti = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT p.*, a.id_artista AS a_id, a.nome AS a_nome, a.genere AS a_genere, " +
            "a.bio AS a_bio, a.immagine AS a_immagine " +
            "FROM prodotti p LEFT JOIN artisti a ON p.id_artista = a.id_artista WHERE 1=1"
        );

        if (q != null && !q.isBlank()) {
            sql.append(" AND p.titolo LIKE ?");
        }
        if (prezzo != null && !prezzo.isBlank()) {
            switch (prezzo) {
                case "low" -> sql.append(" AND p.prezzo < 10");
                case "mid" -> sql.append(" AND p.prezzo BETWEEN 10 AND 20");
                case "high" -> sql.append(" AND p.prezzo > 20");
            }
        }
        if (artistaId != null && !artistaId.isBlank()) {
            sql.append(" AND p.id_artista = ?");
        }

        sql.append(" ORDER BY p.id_prodotto ASC"); // ordinamento crescente

        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int idx = 1;
            if (q != null && !q.isBlank()) {
                ps.setString(idx++, "%" + q + "%");
            }
            if (artistaId != null && !artistaId.isBlank()) {
                ps.setInt(idx++, Integer.parseInt(artistaId));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                prodotti.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return prodotti;
    }

    // Trova un prodotto per ID (con join artista)
    public Prodotto findById(int id) {
        Prodotto p = null;
        String sql = "SELECT p.*, a.id_artista AS a_id, a.nome AS a_nome, a.genere AS a_genere, " +
                     "a.bio AS a_bio, a.immagine AS a_immagine " +
                     "FROM prodotti p LEFT JOIN artisti a ON p.id_artista = a.id_artista " +
                     "WHERE p.id_prodotto = ?";

        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                p = mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return p;
    }

    // Salva un nuovo prodotto
    public boolean save(Prodotto prodotto) {
        String sql = "INSERT INTO prodotti (titolo, prezzo, immagine, descrizione, id_artista) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { 

            ps.setString(1, prodotto.getTitolo());
            ps.setDouble(2, prodotto.getPrezzo());
            ps.setString(3, prodotto.getImmagine());
            ps.setString(4, prodotto.getDescrizione());

            // id_artista opzionale -> setNull quando 0 o assente
            if (prodotto.getIdArtista() > 0) {
                ps.setInt(5, prodotto.getIdArtista());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            int updated = ps.executeUpdate();
            if (updated > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {     // prendo l'ID generato
                    if (keys.next()) {
                        prodotto.setId(keys.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    // Aggiorna un prodotto esistente
    public boolean update(Prodotto prodotto) {
        String sql = "UPDATE prodotti SET titolo = ?, prezzo = ?, immagine = ?, descrizione = ?, id_artista = ? WHERE id_prodotto = ?";

        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, prodotto.getTitolo());
            ps.setDouble(2, prodotto.getPrezzo());
            ps.setString(3, prodotto.getImmagine());
            ps.setString(4, prodotto.getDescrizione());

            // id_artista opzionale -> setNull quando 0 o assente
            if (prodotto.getIdArtista() > 0) {
                ps.setInt(5, prodotto.getIdArtista());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            ps.setInt(6, prodotto.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Elimina un prodotto
    public boolean delete(int id) {
        String sql = "DELETE FROM prodotti WHERE id_prodotto = ?";

        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Trova recensioni di un prodotto
    public List<Recensione> findRecensioniByProdotto(int idProdotto) {
        RecensioneDAO dao = new RecensioneDAO();
        return dao.findByProdotto(idProdotto);
    }

    // Utility per mappare riga prodotto + artista
    private Prodotto mapRow(ResultSet rs) throws SQLException {
        Prodotto p = new Prodotto();

        p.setId(rs.getInt("id_prodotto"));
        p.setTitolo(rs.getString("titolo"));
        p.setPrezzo(rs.getDouble("prezzo"));
        p.setImmagine(rs.getString("immagine"));
        p.setDescrizione(rs.getString("descrizione"));

        // se id_artista DB è NULL -> 0 nella model
        Object idArtistaObj = rs.getObject("id_artista");
        p.setIdArtista(idArtistaObj == null ? 0 : rs.getInt("id_artista"));

        // Artista LEFT JOIN: può essere null -> creo comunque oggetto "vuoto"
        Artista a = new Artista();
        a.setId(rs.getInt("a_id"));
        a.setNome(rs.getString("a_nome"));
        a.setGenere(rs.getString("a_genere"));
        a.setBio(rs.getString("a_bio"));
        a.setImmagine(rs.getString("a_immagine"));

        p.setArtista(a);
        return p;
    }
}
