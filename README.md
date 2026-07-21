# FitTrend Store

> Progetto universitario — Tecnologie Software per il Web (TSW)
> E-commerce dedicato a fitness, palestra e allenamento in casa.

---

## Descrizione

**FitTrend Store** è un'applicazione web di e-commerce che permette agli appassionati di palestra e fitness di acquistare articoli sportivi: accessori da palestra, piccoli attrezzi, abbigliamento sportivo e prodotti di tendenza (shaker, guanti, fasce, tappetini, manubri regolabili, ecc.).

---

## Stack Tecnologico

<table>
  <thead>
    <tr>
      <th>Layer</th>
      <th>Tecnologia</th>
    </tr>
  </thead>
  <tbody>
    <tr><td><strong>Server</strong></td><td>Apache Tomcat 10 / 11 (Jakarta EE 10)</td></tr>
    <tr><td><strong>Backend</strong></td><td>Java Servlet (<code>jakarta.servlet.*</code>)</td></tr>
    <tr><td><strong>View</strong></td><td>JSP + JSTL (<code>jakarta.tags.core</code>) + Expression Language (EL)</td></tr>
    <tr><td><strong>Persistenza</strong></td><td>JDBC con <code>PreparedStatement</code> + <code>try-with-resources</code></td></tr>
    <tr><td><strong>Connessione DB</strong></td><td>DataSource JNDI (configurato in Tomcat via <code>context.xml</code>)</td></tr>
    <tr><td><strong>Pattern</strong></td><td>DAO (Data Access Object)</td></tr>
    <tr><td><strong>Database</strong></td><td>MySQL</td></tr>
    <tr><td><strong>Frontend</strong></td><td>HTML5, CSS3 (Vanilla), JavaScript (ES6+)</td></tr>
    <tr><td><strong>Comunicazione asincrona</strong></td><td>AJAX + JSON</td></tr>
    <tr><td><strong>Versioning</strong></td><td>Git</td></tr>
  </tbody>
</table>

### Tecnologie NON utilizzate
Spring, Spring Boot, Hibernate, JPA, ORM, React, Angular, Vue, Node.js, Bootstrap, REST framework esterni.

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
│   ├── control/                  # Servlet (MVC Controller) + Filter
│   │   ├── HomeServlet.java
│   │   ├── CatalogoServlet.java
│   │   ├── DettaglioProdottoServlet.java
│   │   ├── CarrelloServlet.java
│   │   ├── CheckoutServlet.java
│   │   ├── LoginServlet.java
│   │   ├── LogoutServlet.java
│   │   ├── RegistrazioneServlet.java
│   │   ├── OrdiniServlet.java
│   │   ├── RimborsoServlet.java
│   │   ├── AdminProdottiServlet.java
│   │   ├── AdminOrdiniServlet.java
│   │   ├── AdminRimborsiServlet.java
│   │   ├── AuthFilter.java
│   │   └── AuthHelper.java
│   ├── model/                    # JavaBean / classi di dominio
│   │   ├── Prodotto.java
│   │   ├── Categoria.java
│   │   ├── Utente.java
│   │   ├── Ordine.java
│   │   ├── DettaglioOrdine.java
│   │   ├── Carrello.java
│   │   ├── ItemCarrello.java
│   │   └── Rimborso.java
│   └── dao/                      # DAO + DbManager + Utility
│       ├── DbManager.java
│       ├── ProdottoDAO.java
│       ├── CategoriaDAO.java
│       ├── UtenteDAO.java
│       ├── OrdineDAO.java
│       ├── DettaglioOrdineDAO.java
│       ├── RimborsoDAO.java
│       └── PasswordUtil.java
│
├── web/                          # Risorse web (deploy root)
│   ├── WEB-INF/
│   │   ├── view/                 # JSP (SOLO qui, mai fuori)
│   │   │   ├── home.jsp
│   │   │   ├── catalogo.jsp
│   │   │   ├── dettaglio.jsp
│   │   │   ├── carrello.jsp
│   │   │   ├── checkout.jsp
│   │   │   ├── login.jsp
│   │   │   ├── registrazione.jsp
│   │   │   ├── ordini.jsp
│   │   │   ├── rimborsi.jsp
│   │   │   ├── admin_prodotti.jsp (alias admin_catalogo.jsp)
│   │   │   ├── admin_ordini.jsp
│   │   │   ├── admin_rimborsi.jsp
│   │   │   ├── header.jsp
│   │   │   ├── footer.jsp
│   │   │   └── paginazione.jsp
│   │   └── web.xml               # Deployment descriptor (Jakarta EE 10)
│   ├── META-INF/
│   │   └── context.xml           # JNDI DataSource (Tomcat)
│   ├── styles/                   # File CSS
│   │   ├── main.css
│   │   ├── catalogo.css
│   │   └── carrello.css
│   ├── scripts/                  # File JavaScript
│   │   ├── cart.js
│   │   ├── checkout-validation.js
│   │   ├── login-validation.js
│   │   ├── registrazione-validation.js
│   │   └── admin-products-validation.js
│   ├── images/                   # Immagini statiche
│   └── index.jsp                 # Entry point → forward a /home
│
├── database/
│   └── db_schema.sql             # DDL + DML MySQL
│
└── README.md
```

---

## Mapping Servlet Ufficiali

<table>
  <thead>
    <tr>
      <th>URL Pattern</th>
      <th>Servlet Class</th>
      <th>Descrizione</th>
    </tr>
  </thead>
  <tbody>
    <tr><td><code>/home</code></td><td><code>control.HomeServlet</code></td><td>Home page</td></tr>
    <tr><td><code>/catalogo</code></td><td><code>control.CatalogoServlet</code></td><td>Lista prodotti, ricerca e filtri per categoria</td></tr>
    <tr><td><code>/prodotto</code></td><td><code>control.DettaglioProdottoServlet</code></td><td>Dettaglio singolo prodotto</td></tr>
    <tr><td><code>/carrello</code></td><td><code>control.CarrelloServlet</code></td><td>Gestione carrello (AJAX / JSON)</td></tr>
    <tr><td><code>/checkout</code></td><td><code>control.CheckoutServlet</code></td><td>Finalizzazione ordine</td></tr>
    <tr><td><code>/login</code></td><td><code>control.LoginServlet</code></td><td>Autenticazione utente (GET / POST)</td></tr>
    <tr><td><code>/logout</code></td><td><code>control.LogoutServlet</code></td><td>Invalidazione sessione</td></tr>
    <tr><td><code>/registrazione</code></td><td><code>control.RegistrazioneServlet</code></td><td>Registrazione nuovo utente</td></tr>
    <tr><td><code>/ordini</code></td><td><code>control.OrdiniServlet</code></td><td>Storico ordini utente</td></tr>
    <tr><td><code>/rimborsi</code></td><td><code>control.RimborsoServlet</code></td><td>Richiesta e gestione rimborsi utente</td></tr>
    <tr><td><code>/admin/prodotti</code></td><td><code>control.AdminProdottiServlet</code></td><td>Gestione catalogo (area admin, protetta)</td></tr>
    <tr><td><code>/admin/ordini</code></td><td><code>control.AdminOrdiniServlet</code></td><td>Gestione ordini (area admin, protetta)</td></tr>
    <tr><td><code>/admin/rimborsi</code></td><td><code>control.AdminRimborsiServlet</code></td><td>Gestione rimborsi (area admin, protetta)</td></tr>
  </tbody>
</table>

> I mapping sono registrati tramite annotazione `@WebServlet` su ogni classe.
> L'accesso alle aree `/admin/*` è protetto da `AuthFilter`.

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
2. Eseguire `database/db_schema.sql` per creare le tabelle e il dato iniziale.
3. Aggiornare le credenziali DB in `web/META-INF/context.xml`.
4. Copiare il driver `mysql-connector-j-*.jar` in `$TOMCAT_HOME/lib/`.
5. Fare il deploy su Tomcat 10/11 e avviare.
6. Aprire `http://localhost:8080/FitTrend-Store/`.

---

## Convenzioni di Naming

<table>
  <thead>
    <tr>
      <th>Elemento</th>
      <th>Esempio</th>
    </tr>
  </thead>
  <tbody>
    <tr><td>Servlet</td><td><code>LoginServlet</code>, <code>CarrelloServlet</code></td></tr>
    <tr><td>DAO</td><td><code>ProdottoDAO</code>, <code>UtenteDAO</code></td></tr>
    <tr><td>Model/Bean</td><td><code>Prodotto</code>, <code>Utente</code>, <code>Ordine</code></td></tr>
    <tr><td>JSP</td><td><code>admin_ordini.jsp</code>, <code>carrello.jsp</code></td></tr>
    <tr><td>CSS</td><td><code>main.css</code>, <code>carrello.css</code></td></tr>
    <tr><td>JS</td><td><code>cart.js</code>, <code>checkout-validation.js</code></td></tr>
    <tr><td>URL Servlet</td><td><code>/login</code>, <code>/catalogo</code>, <code>/carrello</code></td></tr>
  </tbody>
</table>