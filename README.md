# FitTrend Store

> Progetto universitario — Tecnologie Software per il Web (TSW)
> E-commerce dedicato a fitness, palestra e allenamento in casa.

---

## Descrizione

**FitTrend Store** è un'applicazione web di e-commerce che permette agli appassionati di palestra e fitness di acquistare articoli sportivi: accessori da palestra, piccoli attrezzi, abbigliamento sportivo e prodotti di tendenza (shaker, guanti, fasce, tappetini, manubri regolabili, ecc.).

---

## Stack Tecnologico

| Layer | Tecnologia |
|---|---|
| **Server** | Apache Tomcat 10 / 11 (Jakarta EE 10) |
| **Backend** | Java Servlet (`jakarta.servlet.*`) |
| **View** | JSP + JSTL (`jakarta.tags.core`) + Expression Language (EL) |
| **Persistenza** | JDBC con `PreparedStatement` + `try-with-resources` |
| **Connessione DB** | DataSource JNDI (configurato in Tomcat via `context.xml`) |
| **Pattern** | DAO (Data Access Object) |
| **Database** | MySQL 8.x |
| **Frontend** | HTML5, CSS3 (Vanilla), JavaScript (ES6+) |
| **Comunicazione asincrona** | AJAX + JSON |
| **Versioning** | Git |

### Tecnologie NON utilizzate
Spring, Spring Boot, Hibernate, JPA, ORM, React, Angular, Vue, Node.js, Bootstrap obbligatorio, REST framework esterni.

---

## Architettura — MVC Model 2

```
Client (Browser)
       │
       ▼  HTTP Request
  [ Servlet ]  ──── legge parametri, valida, usa DAO
  (control/)         │
       │              ▼
       │         [ DAO / Model ]  ──── JDBC ──── MySQL
       │         (dao/, model/)
       │
       ▼  forward / redirect
    [ JSP ]  ──── EL + JSTL (no scriptlet)
  (WEB-INF/view/)
       │
       ▼  HTTP Response
Client (Browser)
```

**Regole fondamentali:**
- Il client chiama sempre una Servlet, **mai** una JSP direttamente.
- Le JSP applicative si trovano **esclusivamente** in `web/WEB-INF/view/` (non accessibili via URL diretto).
- La Servlet valida i parametri lato server prima di usarli.
- Nessun CSS o JavaScript inline nelle JSP.
- Usare `<c:out>` per stampare dati dinamici (protezione XSS).

---

## Struttura delle Cartelle

```
FitTrend-Store/
│
├── src/                          # Sorgenti Java
│   ├── control/                  # Servlet (MVC Controller)
│   │   └── [*Servlet.java]
│   ├── model/                    # JavaBean / classi di dominio
│   │   └── [Product.java, User.java, ...]
│   └── dao/                      # DAO + DbManager
│       └── [ProductDAO.java, UserDAO.java, DbManager.java, ...]
│
├── web/                          # Risorse web (deploy root)
│   ├── WEB-INF/
│   │   ├── view/                 # JSP (SOLO qui, mai fuori)
│   │   │   └── [*.jsp]
│   │   └── web.xml               # Deployment descriptor (Jakarta EE 10)
│   ├── META-INF/
│   │   └── context.xml           # JNDI DataSource (Tomcat)
│   ├── styles/                   # File CSS
│   │   └── [main.css, ...]
│   ├── scripts/                  # File JavaScript
│   │   └── [cart.js, validation.js, ...]
│   ├── images/                   # Immagini statiche
│   │   └── [logo.png, ...]
│   └── index.jsp                 # Entry point → forward a /home
│
├── database/
│   └── db_schema.sql             # DDL + DML MySQL
│
├── .agents/
│   └── AGENTS.md                 # Regole fisse per l'AI (contesto progetto)
│
└── README.md
```

---

## Mapping Servlet Ufficiali

| URL Pattern | Servlet Class | Descrizione |
|---|---|---|
| `/home` | `control.HomeServlet` | Home page |
| `/catalogo` | `control.CatalogoServlet` | Lista, dettaglio, ricerca prodotti |
| `/login` | `control.LoginServlet` | Autenticazione utente (GET/POST) |
| `/logout` | `control.LogoutServlet` | Invalidazione sessione |
| `/registrazione` | `control.RegistrazioneServlet` | Registrazione nuovo utente |
| `/carrello` | `control.CarrelloServlet` | Gestione carrello (AJAX/JSON) |
| `/ordini` | `control.OrdiniServlet` | Storico e gestione ordini |
| `/admin` | `control.AdminServlet` | Area amministrazione (protetta) |

> I mapping sono registrati tramite annotazione `@WebServlet` sulla classe oppure in `web/WEB-INF/web.xml`.

---

## Regola Critica sulle JSP

> ⚠️ **NESSUNA JSP applicativa deve essere collocata fuori da `web/WEB-INF/view/`.**

L'unica eccezione ammessa è `web/index.jsp`, che non contiene logica applicativa
e si limita a fare un forward immediato alla `HomeServlet` (`<jsp:forward page="/home" />`).

---

## Configurazione DataSource JNDI

Il DataSource è configurato in `web/META-INF/context.xml` con nome JNDI `jdbc/FitTrendDB`.

Lookup in `DbManager.java`:
```java
Context ctx = new InitialContext();
DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/FitTrendDB");
```

---

## Setup Locale

1. Creare il database MySQL: `CREATE DATABASE fittrend_db CHARACTER SET utf8mb4;`
2. Eseguire `database/db_schema.sql` per creare le tabelle.
3. Aggiornare le credenziali DB in `web/META-INF/context.xml`.
4. Copiare il driver `mysql-connector-j-*.jar` in `$TOMCAT_HOME/lib/`.
5. Fare il deploy su Tomcat 10/11 e avviare.
6. Aprire `http://localhost:8080/FitTrend-Store/`.

---

## Convenzioni di Naming

| Elemento | Convenzione | Esempio |
|---|---|---|
| Servlet | `NomeAzioneServlet` | `LoginServlet`, `CarrelloServlet` |
| DAO | `NomeEntitàDAO` | `ProductDAO`, `UserDAO` |
| Model/Bean | PascalCase | `Product`, `User`, `Order` |
| JSP | kebab-case | `product-list.jsp`, `cart.jsp` |
| CSS | kebab-case | `main.css`, `product-card.css` |
| JS | camelCase | `cart.js`, `validation.js` |
| URL Servlet | `/nomeRisorsa` | `/login`, `/catalogo`, `/carrello` |
