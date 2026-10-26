# Rap Italiano Store

E-commerce di album del rap italiano, realizzato come progetto per l'esame di **Tecnologie Software per il Web** del Corso di
Laurea in Informatica dell'Università degli Studi di Salerno (ottobre – novembre 2025).

## Funzionalità

- **Catalogo e ricerca**: album con scheda prodotto, pagina degli artisti, ricerca.
- **Carrello**: aggiunta e aggiornamento delle quantità via AJAX (JSON), anche dalla pagina del prodotto; per gli utenti registrati
  il carrello è salvato nel database.
- **Account**: registrazione con email e username, login, password salvate con hash **BCrypt**.
- **Checkout**: dati di spedizione validati, conferma dell'ordine e storico degli ordini dell'utente.
- **Recensioni** dei prodotti con voto da 1 a 5.
- **Area amministratore**, protetta da un filtro sul ruolo: gestione di prodotti, artisti, utenti e ordini (con filtri).

## Architettura

- Pattern **MVC**: servlet come controller (`control`), JavaBean come modello (`model`), viste JSP con JSTL in
  `WEB-INF/views` (non raggiungibili direttamente dal browser).
- Accesso ai dati con il pattern **DAO** e query parametrizzate (`PreparedStatement`) su un `DataSource` JNDI con pool di
  connessioni di Tomcat (`META-INF/context.xml`).
- **Filtri**: codifica UTF-8 su tutte le richieste, login obbligatorio per le pagine riservate, controllo del ruolo admin.
- Front-end in HTML, CSS e JavaScript: validazione dei form, carrello e checkout via AJAX.

```
src/main/java/it/unisa/rapitalianostore/
├── control/   servlet (catalogo, prodotto, ricerca, carrello, checkout, ordini, recensioni, login, area admin)
├── dao/       accesso al database (artisti, prodotti, utenti, carrello, ordini, recensioni)
├── model/     JavaBean
├── filters/   codifica, login obbligatorio, area admin
├── tools/     GenHash (hash BCrypt di una password)
└── utils/     DBManager (DataSource JNDI)
src/main/webapp/   viste JSP, CSS, JavaScript, immagini
RapItaShop.sql     database, tabelle e dati di esempio
```

## Tecnologie

Java 21 · Servlet 4.0, JSP e JSTL · Apache Tomcat 9 · MySQL (Connector/J) · jBCrypt · Gson · HTML, CSS, JavaScript (AJAX) ·
progetto Eclipse (Dynamic Web Project)

## Come avviarlo in locale

1. Esegui `RapItaShop.sql` in MySQL: crea il database, le tabelle e i dati di esempio.
2. In `src/main/webapp/META-INF/context.xml` imposta utente e password del tuo MySQL.
3. Importa il progetto in Eclipse come Dynamic Web Project e avvialo su Apache Tomcat 9.
4. Per entrare nell'area amministratore c'è un utente **di prova**, `admin@rapitalianostore.it` (la password è indicata nello
   script SQL). Per usare una password tua: `java GenHash <password>` stampa l'hash BCrypt da salvare nella tabella `utenti`.

## Licenza

GNU Affero General Public License v3 – vedi [`LICENSE`](LICENSE).

## Autore

Luigi Carnevale – [carnevale-luigi.it](https://carnevale-luigi.it)
