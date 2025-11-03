package it.unisa.rapitalianostore.model;

public class Utente {
    private int id;
    private String email;
    private String username;   
    private String password;
    private String ruolo;

    public Utente() {}

    public Utente(int id, String email, String password, String ruolo) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.ruolo = ruolo;
    }

    // Getter e Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // getter/setter username
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRuolo() { return ruolo; }
    public void setRuolo(String ruolo) { this.ruolo = ruolo; }
}
