package it.unisa.rapitalianostore.tools;

import org.mindrot.jbcrypt.BCrypt;

public class GenHash {
  public static void main(String[] args) {
    String plain = "Admin@123"; // metti la tua password
    String hash  = BCrypt.hashpw(plain, BCrypt.gensalt(12)); // cost 12
    System.out.println(hash);
  }
}
