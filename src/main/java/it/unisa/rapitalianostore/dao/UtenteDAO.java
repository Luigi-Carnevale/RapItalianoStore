package it.unisa.rapitalianostore.dao;

import it.unisa.rapitalianostore.model.Utente;
import it.unisa.rapitalianostore.utils.DBManager;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtenteDAO {

    // Trova utente tramite email (ora legge anche username) 
    public Utente findByEmail(String email) {
        Utente utente = null;
        String sql = "SELECT id_utente, email, username, password, ruolo FROM utenti WHERE email = ?"; 

        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                utente = new Utente();
                utente.setId(rs.getInt("id_utente"));
                utente.setEmail(rs.getString("email"));
                utente.setUsername(rs.getString("username")); 
                utente.setPassword(rs.getString("password"));
                utente.setRuolo(rs.getString("ruolo"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return utente;
    }

    // Trova per username (utile per validazione)
    public Utente findByUsername(String username) {
        String sql = "SELECT id_utente, email, username, password, ruolo FROM utenti WHERE username = ?";
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Utente u = new Utente();
                    u.setId(rs.getInt("id_utente"));
                    u.setEmail(rs.getString("email"));
                    u.setUsername(rs.getString("username"));
                    u.setPassword(rs.getString("password"));
                    u.setRuolo(rs.getString("ruolo"));
                    return u;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // verifica esistenza email
    public boolean existsByEmail(String email) {
        String sql = "SELECT 1 FROM utenti WHERE email = ? LIMIT 1";
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // verifica esistenza username
    public boolean existsByUsername(String username) {
        String sql = "SELECT 1 FROM utenti WHERE username = ? LIMIT 1";
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // Controlla se la password inserita corrisponde all’hash nel DB
    public boolean checkPassword(String plainPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Genera hash della password
    public String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    // 🔹 Salva un nuovo utente (ORA con username) 
    public boolean save(Utente utente) {
        String sql = "INSERT INTO utenti (email, username, password, ruolo) VALUES (?, ?, ?, ?)"; 

        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, utente.getEmail());
            ps.setString(2, utente.getUsername());   
            ps.setString(3, utente.getPassword());
            ps.setString(4, utente.getRuolo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // gestisci violazioni UNIQUE
            e.printStackTrace();
            return false;
        }
    }

    // Metodi per la gestione admin (aggiunti username nei select) 
    public List<Utente> findAll() {
        List<Utente> utenti = new ArrayList<>();
        String sql = "SELECT id_utente, email, username, password, ruolo FROM utenti"; 

        try (Connection con = DBManager.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Utente u = new Utente();
                u.setId(rs.getInt("id_utente"));
                u.setEmail(rs.getString("email"));
                u.setUsername(rs.getString("username")); 
                u.setPassword(rs.getString("password"));
                u.setRuolo(rs.getString("ruolo"));
                utenti.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return utenti;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM utenti WHERE id_utente = ?";
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateRole(int id, String ruolo) {
        String sql = "UPDATE utenti SET ruolo = ? WHERE id_utente = ?";
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ruolo);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
