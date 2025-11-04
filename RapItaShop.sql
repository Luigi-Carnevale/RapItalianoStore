-- ========================================
-- DATABASE: RapItaShop (schema finale)
-- ========================================
DROP DATABASE IF EXISTS RapItaShop;
CREATE DATABASE RapItaShop
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;

USE RapItaShop;

-- ========================================
-- 1) ARTISTI
-- ========================================
CREATE TABLE artisti (
    id_artista INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    genere VARCHAR(50),
    bio TEXT,
    immagine VARCHAR(255)
) ENGINE=InnoDB;

-- ========================================
-- 2) PRODOTTI
-- ========================================
CREATE TABLE prodotti (
    id_prodotto INT AUTO_INCREMENT PRIMARY KEY,
    titolo VARCHAR(150) NOT NULL,
    prezzo DECIMAL(10,2) NOT NULL,
    immagine VARCHAR(255),
    descrizione TEXT,
    id_artista INT,
    FOREIGN KEY (id_artista) REFERENCES artisti(id_artista)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ========================================
-- 3) UTENTI
-- ========================================

CREATE TABLE utenti (
    id_utente INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,  
    password VARCHAR(255) NOT NULL,
    ruolo ENUM('cliente', 'admin') DEFAULT 'cliente'
) ENGINE=InnoDB;

-- ========================================
-- 4) RECENSIONI
-- ========================================
CREATE TABLE recensioni (
    id_recensione INT AUTO_INCREMENT PRIMARY KEY,
    id_prodotto INT NOT NULL,
    id_utente INT NOT NULL,
    valutazione INT NOT NULL CHECK (valutazione BETWEEN 1 AND 5),
    commento TEXT,
    data_recensione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_prodotto) REFERENCES prodotti(id_prodotto)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_utente) REFERENCES utenti(id_utente)
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_recensioni_prodotto (id_prodotto),
    INDEX idx_recensioni_utente (id_utente)
) ENGINE=InnoDB;

-- ========================================
-- 5) ORDINI
-- ========================================
CREATE TABLE ordini (
    id_ordine INT AUTO_INCREMENT PRIMARY KEY,
    id_utente INT NOT NULL,
    data_ordine TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    totale DECIMAL(10,2) NOT NULL,
    indirizzo_spedizione VARCHAR(255) NOT NULL,
    metodo_pagamento ENUM('carta','paypal','bonifico') NOT NULL DEFAULT 'carta',
    FOREIGN KEY (id_utente) REFERENCES utenti(id_utente)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ========================================
-- 6) DETTAGLI ORDINI
-- ========================================
CREATE TABLE ordine_dettagli (
    id_dettaglio INT AUTO_INCREMENT PRIMARY KEY,
    id_ordine INT NOT NULL,
    id_prodotto INT NULL,
    quantita INT NOT NULL DEFAULT 1,
    prezzo_unitario DECIMAL(10,2) NOT NULL,
    titolo_prodotto VARCHAR(255) NOT NULL,
    immagine_prodotto VARCHAR(255),
    FOREIGN KEY (id_ordine) REFERENCES ordini(id_ordine)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_prodotto) REFERENCES prodotti(id_prodotto)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ========================================
-- 7) CARRELLO PERSISTENTE PER UTENTE
-- ========================================
CREATE TABLE IF NOT EXISTS carrello_item (
    id_item INT AUTO_INCREMENT PRIMARY KEY,
    id_utente INT NOT NULL,
    id_prodotto INT NOT NULL,
    quantita INT NOT NULL DEFAULT 1,
    UNIQUE KEY unq_utente_prodotto (id_utente, id_prodotto),  -- necessario per UPSERT
    CONSTRAINT fk_car_item_user FOREIGN KEY (id_utente) REFERENCES utenti(id_utente) ON DELETE CASCADE,
    CONSTRAINT fk_car_item_prod FOREIGN KEY (id_prodotto) REFERENCES prodotti(id_prodotto) ON DELETE CASCADE,
    INDEX idx_car_utente (id_utente),     -- indice per carichi veloci del carrello
    INDEX idx_car_prodotto (id_prodotto)  -- indice utile su join/controlli
) ENGINE=InnoDB;

-- ========================================
-- DATI DI BASE
-- ========================================

-- ARTISTI
INSERT INTO artisti (nome, genere, bio, immagine) VALUES
('Fabri Fibra', 'Rap', 'La leggenda del rap italiano, il nostro Eminem', 'fibra.jpg'), 
('Marracash', 'Rap', 'Uno dei maggiori artisti della scena rap italiana.', 'marracash.jpg'),
('Clementino', 'Rap', 'Una leggenda del rap italiano e napoletano', 'clementino.jpg'), 
('Gue', 'Rap', 'Uno dei più forti rapper italiani, se non il più forte', 'gue.jpg'),
('Salmo', 'Rap', 'Rap italiano con influenze elettroniche e rock.', 'salmo.jpg');

-- PRODOTTI
INSERT INTO prodotti (titolo, prezzo, immagine, descrizione, id_artista) VALUES
('Turbe Giovanili', 18.50, 'TurbeGiovanili.jpg', 'Album di debutto di Fabri Fibra', 1), 
('Persona', 19.99, 'Persona.jpg', 'Album iconico di Marracash', 2),
('Grande Anima', 22.50, 'GrandeAnima.jpg', 'Progetto speciale di Clementino', 3),
('Mr Fini', 16.99, 'MrFini.jpg', 'Album famoso di Gue Pequeno', 4), 
('Hellvisback', 17.50, 'Hellvisback.jpg', 'Successo di Salmo', 5);

-- Utente admin di default (con username)
INSERT INTO utenti (email, username, password, ruolo)  
VALUES ('admin@rapitalianostore.it', 'admin', '$2a$12$TwaIt3fcovWfqujX1yqOS.X5GGY0z7s2ptIn3W5M/J3jynwmtuVLe', 'admin');

