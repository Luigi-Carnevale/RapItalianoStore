package it.unisa.rapitalianostore.tools;

import org.mindrot.jbcrypt.BCrypt;

public class GenHash {
  // Uso: java GenHash <password>  -> stampa l'hash BCrypt da salvare nel database
  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("Uso: GenHash <password>");
      System.exit(1);
    }
    String plain = args[0];
    String hash  = BCrypt.hashpw(plain, BCrypt.gensalt(12)); // cost 12
    System.out.println(hash);
  }
}
