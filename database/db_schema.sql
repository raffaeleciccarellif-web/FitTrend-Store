-- ============================================================
--  FitTrend Store — Schema Database MySQL
--  File    : database/db_schema.sql
--  DBMS    : MySQL 8.x
--  Charset : utf8mb4 / utf8mb4_unicode_ci
--  Engine  : InnoDB (supporto FK, transazioni, CHECK)
--
--  UTILIZZO:
--    mysql -u root -p < database/db_schema.sql
--
--  Lo script è idempotente: può essere rieseguito in sviluppo
--  senza conflitti (DROP DATABASE IF EXISTS garantisce un
--  ambiente pulito ad ogni esecuzione).
-- ============================================================


-- ============================================================
--  RESET — Ambiente pulito per sviluppo
--  ATTENZIONE: in produzione rimuovere o commentare il DROP.
-- ============================================================
DROP DATABASE IF EXISTS fittrend_store;

CREATE DATABASE fittrend_store
    CHARACTER SET  utf8mb4
    COLLATE        utf8mb4_unicode_ci;

USE fittrend_store;


-- ============================================================
--  TABELLA 1 — Utente
--
--  Contiene tutti gli utenti registrati (clienti e admin).
--  - is_admin = 0 → cliente normale
--  - is_admin = 1 → amministratore del negozio
--  - password_hash: SHA-256 hex (64 char); VARCHAR(256) lascia
--    margine per altri algoritmi futuri (bcrypt, Argon2, ecc.)
-- ============================================================
CREATE TABLE Utente (
    id            INT           NOT NULL AUTO_INCREMENT,
    nome          VARCHAR(50)   NOT NULL,
    cognome       VARCHAR(50)   NOT NULL,
    email         VARCHAR(100)  NOT NULL,
    password_hash VARCHAR(256)  NOT NULL,
    is_admin      TINYINT(1)    NOT NULL DEFAULT 0,

    PRIMARY KEY (id),
    UNIQUE  KEY uq_utente_email (email)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Utenti registrati: clienti (is_admin=0) e amministratori (is_admin=1)';


-- ============================================================
--  TABELLA 2 — Categoria
--
--  La categoria è una entità di primo livello, non un semplice
--  campo testuale su Prodotto. Questo permette di:
--    - navigare il catalogo per categoria (filtri)
--    - aggiungere/rinominare categorie senza toccare i prodotti
--    - associare descrizioni e metadati alla categoria
-- ============================================================
CREATE TABLE Categoria (
    id          INT          NOT NULL AUTO_INCREMENT,
    nome        VARCHAR(50)  NOT NULL,
    descrizione VARCHAR(255),

    PRIMARY KEY (id),
    UNIQUE KEY uq_categoria_nome (nome)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Categorie merceologiche dei prodotti';


-- ============================================================
--  TABELLA 3 — Prodotto
--
--  I prodotti NON vengono mai eliminati fisicamente dal DB.
--  Usare is_deleted = 1 per rimuoverli dalla vetrina (soft
--  delete), preservando l'integrità referenziale con gli
--  ordini già effettuati.
--
--  categoria_id → FK su Categoria: ogni prodotto appartiene
--  a una sola categoria (molti-a-uno).
--
--  immagine: percorso relativo alla root web, es.:
--    "images/products/tappetino-yoga.jpg"
-- ============================================================
CREATE TABLE Prodotto (
    id                   INT            NOT NULL AUTO_INCREMENT,
    nome                 VARCHAR(100)   NOT NULL,
    descrizione          TEXT           NOT NULL,
    prezzo               NUMERIC(10,2)  NOT NULL,
    categoria_id         INT            NOT NULL,
    immagine             VARCHAR(256)   NOT NULL,
    quantita_disponibile INT            NOT NULL DEFAULT 0,
    is_deleted           TINYINT(1)     NOT NULL DEFAULT 0,

    PRIMARY KEY (id),
    CONSTRAINT fk_prodotto_categoria
        FOREIGN KEY (categoria_id) REFERENCES Categoria(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    -- CHECK: il prezzo non può mai essere negativo
    CONSTRAINT chk_prodotto_prezzo
        CHECK (prezzo >= 0),

    -- CHECK: la giacenza non può essere negativa
    CONSTRAINT chk_prodotto_quantita
        CHECK (quantita_disponibile >= 0)

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Catalogo prodotti; eliminazione logica tramite is_deleted=1';


-- ============================================================
--  TABELLA 4 — `Ordine`
--
--  Backtick necessari: "Ordine" è una keyword riservata
--  in alcune versioni di MySQL.
--
--  Lo stato modella il ciclo di vita dell'ordine:
--    in_elaborazione → in_consegna → consegnato
--                                  ↘ annullato
--
--  Lo stato 'annullato' evita la cancellazione fisica:
--  l'ordine rimane in DB per tracciabilità e rimborsi.
--
--  ultime_cifre_carta: opzionale, solo quando il metodo di
--  pagamento è 'carta'; mai memorizzare dati completi della
--  carta (PCI-DSS). Gestire la validazione nel backend.
-- ============================================================
CREATE TABLE `Ordine` (
    id                  INT            NOT NULL AUTO_INCREMENT,
    utente_id           INT            NOT NULL,
    data_ordine         TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    totale              NUMERIC(10,2)  NOT NULL,
    indirizzo_spedizione TEXT          NOT NULL,
    citta_spedizione    VARCHAR(80)    NOT NULL,
    cap_spedizione      VARCHAR(10)    NOT NULL,
    metodo_pagamento    VARCHAR(30)    NOT NULL,
    ultime_cifre_carta  VARCHAR(4)     NULL,
    stato               VARCHAR(30)    NOT NULL DEFAULT 'in_elaborazione',

    PRIMARY KEY (id),
    CONSTRAINT fk_ordine_utente
        FOREIGN KEY (utente_id) REFERENCES Utente(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    -- CHECK: il totale non può essere negativo
    CONSTRAINT chk_ordine_totale
        CHECK (totale >= 0),

    -- CHECK: solo valori di stato ammessi
    CONSTRAINT chk_ordine_stato
        CHECK (stato IN ('in_elaborazione', 'in_consegna', 'consegnato', 'annullato'))

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Ordini effettuati dagli utenti; eliminazione logica via stato=annullato';


-- ============================================================
--  TABELLA 5 — Dettaglio_Ordine
--
--  Ogni riga rappresenta una linea (prodotto × quantità)
--  all'interno di un ordine.
--
--  SCELTA FONDAMENTALE: nome_prodotto_acquisto e
--  prezzo_acquisto sono de-normalizzati intenzionalmente.
--  Motivo: il nome e il prezzo di un prodotto possono
--  cambiare nel tempo; salvandoli qui si "fotografa" il
--  valore esatto al momento dell'acquisto, garantendo la
--  correttezza storica degli ordini e degli scontrini.
--
--  prodotto_id → FK mantenuta per consentire navigazione
--  al prodotto corrente (es. "acquista di nuovo"), ma
--  la verità dell'ordine è in nome_prodotto_acquisto e
--  prezzo_acquisto.
-- ============================================================
CREATE TABLE Dettaglio_Ordine (
    id                      INT            NOT NULL AUTO_INCREMENT,
    ordine_id               INT            NOT NULL,
    prodotto_id             INT            NOT NULL,
    nome_prodotto_acquisto  VARCHAR(100)   NOT NULL,
    quantita                INT            NOT NULL,
    prezzo_acquisto         NUMERIC(10,2)  NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_dettaglio_ordine
        FOREIGN KEY (ordine_id) REFERENCES `Ordine`(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_dettaglio_prodotto
        FOREIGN KEY (prodotto_id) REFERENCES Prodotto(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    -- CHECK: quantità minima 1
    CONSTRAINT chk_dettaglio_quantita
        CHECK (quantita > 0),

    -- CHECK: il prezzo pagato non può essere negativo
    CONSTRAINT chk_dettaglio_prezzo
        CHECK (prezzo_acquisto >= 0)

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Righe di dettaglio di ogni ordine; prezzo e nome de-normalizzati per storicità';


-- ============================================================
--  TABELLA 6 — Rimborso
--
--  Gestisce le richieste di rimborso associate a un ordine.
--  Ogni ordine può avere AL MASSIMO un rimborso (UNIQUE su
--  ordine_id).
--
--  Ciclo di vita del rimborso:
--    richiesto → approvato  → completato
--             ↘ rifiutato
--
--  NOTA APPLICATIVA (gestita dal backend, non dal DB):
--    La regola "un rimborso può essere richiesto solo per
--    ordini in stato 'consegnato' o 'annullato'" NON è
--    implementata con un vincolo FK o trigger, ma sarà
--    verificata dalla Servlet / DAO prima dell'INSERT.
--    Questo mantiene la logica applicativa nel layer Java
--    e non nel database, coerentemente con l'architettura
--    MVC Model 2 del progetto.
--
--  data_elaborazione: NULL finché il rimborso è 'richiesto';
--  impostato a timestamp corrente quando cambia stato.
-- ============================================================
CREATE TABLE Rimborso (
    id                  INT            NOT NULL AUTO_INCREMENT,
    ordine_id           INT            NOT NULL,
    data_richiesta      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_elaborazione   TIMESTAMP      NULL,
    importo             NUMERIC(10,2)  NOT NULL,
    motivo              TEXT,
    stato               VARCHAR(30)    NOT NULL DEFAULT 'richiesto',

    PRIMARY KEY (id),
    -- UNIQUE garantisce max 1 rimborso per ordine
    UNIQUE KEY uq_rimborso_ordine (ordine_id),
    CONSTRAINT fk_rimborso_ordine
        FOREIGN KEY (ordine_id) REFERENCES `Ordine`(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    -- CHECK: l'importo rimborsato non può essere negativo
    CONSTRAINT chk_rimborso_importo
        CHECK (importo >= 0),

    -- CHECK: solo stati ammessi nel ciclo di vita del rimborso
    CONSTRAINT chk_rimborso_stato
        CHECK (stato IN ('richiesto', 'approvato', 'rifiutato', 'completato'))

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Rimborsi: max 1 per ordine; logica di ammissibilità gestita dal backend Java';


-- ============================================================
--  SEZIONE DML — Dati iniziali (seed)
-- ============================================================


-- ------------------------------------------------------------
--  CATEGORIE
--  Quattro macro-categorie merceologiche del negozio.
--  Gli id vengono usati come riferimento nei prodotti qui sotto.
--    id=1 → Accessori
--    id=2 → Abbigliamento
--    id=3 → Home Workout
--    id=4 → Idratazione
-- ------------------------------------------------------------
INSERT INTO Categoria (nome, descrizione) VALUES
    ('Accessori',     'Guanti, fasce, tappetini e accessori per l''allenamento'),
    ('Abbigliamento', 'Magliette tecniche, leggings e abbigliamento sportivo'),
    ('Home Workout',  'Attrezzi per allenarsi a casa: manubri, kettlebell e altro'),
    ('Idratazione',   'Borracce termiche, shaker proteici e accessori per l''idratazione');


-- ------------------------------------------------------------
--  UTENTE ADMIN DI TEST
--
--  email   : admin@fittrend.it
--  password: admin123A  (solo per sviluppo — cambiare in prod)
--  hash    : SHA-256 hex di "admin123A"
--            = 47f30f1318c94e068d04f2521d498c2173999e43a20c6aaf2666c911d78194d4
--  is_admin: 1
-- ------------------------------------------------------------
INSERT INTO Utente (nome, cognome, email, password_hash, is_admin) VALUES
    ('Admin', 'FitTrend', 'admin@fittrend.it',
     '47f30f1318c94e068d04f2521d498c2173999e43a20c6aaf2666c911d78194d4', 1);


-- ------------------------------------------------------------
--  PRODOTTI DI ESEMPIO — 8 articoli fitness
--
--  Distribuzione per categoria:
--    Accessori    (id=1): Tappetino Yoga, Fasce Elastiche
--    Abbigliamento(id=2): Maglietta Tecnica, Leggings Sportivi
--    Home Workout (id=3): Manubri Regolabili, Kettlebell 12kg
--    Idratazione  (id=4): Borraccia Termica, Shaker Proteico
--
--  immagine: percorso relativo da usare con ${pageContext.request.contextPath}
--  Esempio: /images/products/tappetino-yoga.jpg
-- ------------------------------------------------------------
INSERT INTO Prodotto (nome, descrizione, prezzo, categoria_id, immagine, quantita_disponibile, is_deleted) VALUES

    -- ---- Accessori (categoria_id = 1) ----
    (
        'Tappetino Yoga Antiscivolo',
        'Tappetino professionale 183×61 cm, spessore 6 mm, superficie antiscivolo su entrambi i lati. '
        'Ideale per yoga, pilates e stretching. Materiale ecologico TPE, lavabile.',
        24.99,
        1,
        'images/products/tappetino-yoga.jpg',
        50,
        0
    ),
    (
        'Fasce Elastiche Resistance Band — Set 5 livelli',
        'Set da 5 fasce di resistenza progressiva (leggera, media, forte, molto forte, extra forte). '
        'In lattice naturale, ideali per riabilitazione, attivazione muscolare e allenamento funzionale.',
        19.90,
        1,
        'images/products/fasce-elastiche.jpg',
        80,
        0
    ),

    -- ---- Abbigliamento (categoria_id = 2) ----
    (
        'Maglietta Tecnica FitTrend',
        'T-shirt tecnica in tessuto traspirante Dry-Fit al 100% poliestere riciclato. '
        'Trattamento anti-odore, costine laterali per libertà di movimento. '
        'Disponibile in diverse taglie S-XXL.',
        29.99,
        2,
        'images/products/maglietta-tecnica.jpg',
        120,
        0
    ),
    (
        'Leggings Sportivi FitTrend Pro',
        'Leggings a vita alta in tessuto compressivo 4-way stretch. '
        'Tasca laterale porta-smartphone, cuciture piatte anti-sfregamento. '
        'Perfetti per corsa, crossfit e yoga.',
        39.90,
        2,
        'images/products/leggings-sportivi.jpg',
        75,
        0
    ),

    -- ---- Home Workout (categoria_id = 3) ----
    (
        'Manubri Regolabili 2–24 kg — Coppia',
        'Coppia di manubri regolabili da 2 a 24 kg ciascuno con sistema di bloccaggio a selettore rapido. '
        'Sostituisce 15 paia di manubri tradizionali. Struttura in acciaio con rivestimento anti-rumore.',
        189.00,
        3,
        'images/products/manubri-regolabili.jpg',
        20,
        0
    ),
    (
        'Kettlebell 12 kg',
        'Kettlebell in ghisa verniciata da 12 kg con manico ergonomico sabbiato per una presa sicura. '
        'Base piatta antiscivolo. Ideale per swing, turkish get-up e circuiti HIIT.',
        44.90,
        3,
        'images/products/kettlebell-12kg.jpg',
        35,
        0
    ),

    -- ---- Idratazione (categoria_id = 4) ----
    (
        'Borraccia Termica 750 ml',
        'Borraccia in acciaio inox 18/8 a doppia parete sottovuoto. '
        'Mantiene le bevande fredde 24 h e calde 12 h. '
        'Bocca larga, tappo sport-cap con cannuccia integrata, BPA-free.',
        22.50,
        4,
        'images/products/borraccia-termica.jpg',
        100,
        0
    ),
    (
        'Shaker Proteico FitTrend 700 ml',
        'Shaker da 700 ml con griglia mixing interna per frullati senza grumi. '
        'Scala di misurazione stampata, coperchio a vite anti-perdita, sportello a scatto. '
        'Lavabile in lavastoviglie, BPA-free.',
        12.90,
        4,
        'images/products/shaker-proteico.jpg',
        150,
        0
    );


-- ============================================================
--  FINE SCRIPT
--  Verificare l'esito con:
--    SHOW TABLES;
--    SELECT * FROM Categoria;
--    SELECT * FROM Prodotto;
--    SELECT email, is_admin FROM Utente;
-- ============================================================
