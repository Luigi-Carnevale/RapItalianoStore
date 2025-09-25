package it.unisa.rapitalianostore.dao;

import it.unisa.rapitalianostore.model.Artista;
import it.unisa.rapitalianostore.utils.DBManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArtistaDAO {

    public List<Artista> findAll() {
        List<Artista> artisti = new ArrayList<>();
        String sql = "SELECT * FROM artisti";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                artisti.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return artisti;
    }

    // 🔎 Trova artisti filtrando per nome (parziale, case-insensitive)
    public List<Artista> findByNome(String q) {
        List<Artista> artisti = new ArrayList<>();
        String sql = "SELECT * FROM artisti WHERE LOWER(nome) LIKE LOWER(?)";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + q + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                artisti.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return artisti;
    }

    public Artista findById(int id) {
        String sql = "SELECT * FROM artisti WHERE id_artista=?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean save(Artista a) {
        String sql = "INSERT INTO artisti (nome, genere, bio, immagine) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, a.getNome());
            ps.setString(2, a.getGenere());
            ps.setString(3, a.getBio());
            ps.setString(4, a.getImmagine());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Artista a) {
        String sql = "UPDATE artisti SET nome=?, genere=?, bio=?, immagine=? WHERE id_artista=?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, a.getNome());
            ps.setString(2, a.getGenere());
            ps.setString(3, a.getBio());
            ps.setString(4, a.getImmagine());
            ps.setInt(5, a.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM artisti WHERE id_artista=?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Artista mapRow(ResultSet rs) throws SQLException {
        Artista a = new Artista();
        a.setId(rs.getInt("id_artista"));
        a.setNome(rs.getString("nome"));
        a.setGenere(rs.getString("genere"));
        a.setBio(rs.getString("bio"));
        a.setImmagine(rs.getString("immagine"));
        return a;
    }
}
