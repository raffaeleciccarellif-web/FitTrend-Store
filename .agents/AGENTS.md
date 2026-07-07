# FitTrend Store — Contesto Fisso del Progetto

## Descrizione
Progetto universitario TSW in Java Web per un e-commerce chiamato **FitTrend Store**.
E-commerce dedicato a palestra, fitness e allenamento in casa: accessori, piccoli attrezzi,
abbigliamento sportivo, shaker, guanti, fasce, tappetini, manubri regolabili, ecc.

---

## Stack Tecnologico Obbligatorio
- **Backend**: Java Servlet, JSP, JDBC, DAO, DataSource JNDI
- **Database**: MySQL
- **Frontend**: HTML, CSS vanilla, JavaScript, AJAX/JSON
- **Server**: Tomcat 10/11 con Jakarta Servlet API
- **JSTL**: Jakarta JSTL (`jakarta.tags.core`)

## Tecnologie VIETATE
Non usare mai: Spring, Spring Boot, Hibernate, JPA, ORM, React, Angular, Vue, Node.js,
Bootstrap (obbligatorio), REST framework esterni o librerie non necessarie non approvate dal corso.

---

## Architettura MVC Model 2 — Struttura dei Package e delle Directory

### Java Source (`src/`)
| Package | Contenuto |
|---|---|
| `control` | Servlet (`*Servlet.java`) |
| `model` | JavaBean e classi del modello di dominio |
| `dao` | DAO, DbManager, utility di persistenza JDBC |

### Web Resources (`web/`)
| Percorso | Contenuto |
|---|---|
| `web/WEB-INF/view/` | JSP (accessibili SOLO via forward da Servlet) |
| `web/styles/` | File CSS |
| `web/scripts/` | File JavaScript |
| `web/images/` | Immagini statiche |

### Database
| Percorso | Contenuto |
|---|---|
| `database/db_schema.sql` | Script DDL e DML MySQL |

---

## Regole Architetturali Obbligatorie

### Flusso MVC
1. Il client chiama sempre una **Servlet** (mai la JSP direttamente via URL).
2. La Servlet: legge i parametri → valida lato server → usa DAO/Model → imposta attributi request/session.
3. La Servlet fa **forward** alla JSP oppure **redirect** a un'altra Servlet.
4. Le JSP non sono mai accessibili direttamente tramite URL (devono stare in `WEB-INF/view/`).

### Regole JSP
- **Vietato** l'uso di scriptlet Java (`<% %>` o `<%= %>`).
- Usare esclusivamente **EL (Expression Language)** e **JSTL** per i dati dinamici.
- Tag obbligatorio: `<%@ taglib prefix="c" uri="jakarta.tags.core" %>`.
- Usare `<c:out>` per stampare dati dinamici (prevenzione XSS).
- **Vietato** CSS o JavaScript inline nelle JSP.

### Regole Servlet
- Import obbligatori: `jakarta.servlet.*`, `jakarta.servlet.http.*`, `jakarta.servlet.annotation.WebServlet`.
- Validazione server-side sempre obbligatoria, anche se esiste la validazione JavaScript client-side.
- Annotazione `@WebServlet` per la mappatura URL.

### Regole JDBC / DAO
- Usare **DataSource JNDI** (configurato in Tomcat) per ottenere le connessioni.
- Usare sempre **PreparedStatement** (mai Statement con concatenazione di stringhe).
- Usare sempre **try-with-resources** per Connection, PreparedStatement e ResultSet.
- Nessuna logica SQL nelle Servlet: tutta la persistenza è delegata ai DAO.

### Regole CSS / JS
- CSS in `web/styles/`, JS in `web/scripts/`.
- Niente CSS o JS inline nelle JSP o nelle Servlet.
- AJAX deve usare `fetch` o `XMLHttpRequest` e rispondere con JSON dalla Servlet.

---

## Stato Attuale del Progetto (snapshot iniziale)

```
FitTrend-Store/
├── src/
│   └── control/          (vuoto — nessuna Servlet ancora creata)
├── web/
│   ├── WEB-INF/
│   │   ├── view/         (vuoto — nessuna JSP ancora creata)
│   │   └── web.xml       (scheletro base, versione 4.0 da aggiornare a Jakarta EE 10)
│   ├── images/           (vuoto)
│   ├── scripts/          (vuoto)
│   ├── styles/           (vuoto)
│   └── index.jsp         (placeholder "Funziona tutto!")
├── database/             (vuoto — db_schema.sql da creare)
└── README.md
```

> **Nota**: il `web.xml` usa ancora il namespace `javax` (Jakarta EE 8/JCP).
> Va aggiornato al namespace `jakarta` (Jakarta EE 10) compatibile con Tomcat 10+.

---

## Convenzioni di Naming

| Elemento | Convenzione | Esempio |
|---|---|---|
| Servlet | `NomeAzioneServlet` nel package `control` | `LoginServlet`, `ProductServlet` |
| DAO | `NomeEntitàDAO` nel package `dao` | `ProductDAO`, `UserDAO` |
| Model/Bean | PascalCase nel package `model` | `Product`, `User`, `Order` |
| JSP | camelCase o kebab-case in `WEB-INF/view/` | `productList.jsp`, `cart.jsp` |
| CSS | kebab-case in `web/styles/` | `main.css`, `product-card.css` |
| JS | camelCase in `web/scripts/` | `cart.js`, `validation.js` |
| URL Servlet | `/NomeRisorsa` o `/azione` | `/login`, `/products`, `/cart` |

---

## Checklist di Verifica per Ogni Componente Generato
Prima di consegnare qualsiasi file, verificare:
- [ ] Nessuno scriptlet `<% %>` nelle JSP.
- [ ] Tutti gli import usano `jakarta.*` (non `javax.*`).
- [ ] Ogni accesso al DB usa PreparedStatement e try-with-resources.
- [ ] La Servlet valida i parametri lato server prima di usarli.
- [ ] Le JSP non sono accessibili direttamente (sono in `WEB-INF/view/`).
- [ ] CSS e JS sono file separati, non inline.
- [ ] Il DataSource è ottenuto via JNDI.
