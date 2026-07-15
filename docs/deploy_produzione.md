# FitTrend Store - Deploy in Produzione e Checklist

Questo documento descrive la procedura standard per il deploy dell'applicazione FitTrend Store su un server Tomcat standalone e la relativa checklist per validare le funzionalità post-rilascio.

## 1. Ripristino del Database

L'applicazione richiede il ripristino o la creazione del database MySQL in ambiente di produzione.

1. Avviare il server MySQL.
2. Eseguire lo script SQL fornito: `database/db_schema.sql`.
   * **ATTENZIONE**: Lo script contiene il comando `DROP DATABASE IF EXISTS fittrend_store;`. Eseguendo questo script, **tutti i dati di test e gli utenti preesistenti verranno eliminati in modo irreversibile**.
3. Assicurarsi che l'esecuzione vada a buon fine. Verificare che il comando `SHOW TABLES;` restituisca esattamente le seguenti **6 tabelle**:
   * `Categoria`
   * `Utente`
   * `Prodotto`
   * `Ordine`
   * `Dettaglio_Ordine`
   * `Rimborso`

## 2. Configurazione JNDI

Il progetto si basa su un DataSource configurato tramite JNDI per la connessione al database.

1. Aprire il file di configurazione di Tomcat `[TOMCAT_HOME]/conf/context.xml` (o inserire il `<Resource>` direttamente nel `META-INF/context.xml` del progetto).
2. Assicurarsi che i parametri puntino al database corretto e *non contengano credenziali personali* da sviluppatore nel repository git. Un esempio di configurazione JNDI standard:

```xml
<Resource name="jdbc/FitTrendDB" 
          auth="Container" 
          type="javax.sql.DataSource" 
          maxTotal="20" 
          maxIdle="10" 
          maxWaitMillis="-1" 
          username="root" 
          password="your_production_password" 
          driverClassName="com.mysql.cj.jdbc.Driver" 
          url="jdbc:mysql://localhost:3306/fittrend_store?serverTimezone=UTC"/>
```

*(Nota: usa l'infrastruttura di credenziali del server di produzione)*

## 3. Build del pacchetto WAR

Per eseguire il deploy indipendentemente dall'IDE:

1. Da riga di comando (o usando gli strumenti di build del tuo IDE come artefatto standalone), compila il progetto. Se non utilizzi Maven o Gradle, l'IDE può pacchettizzare la cartella `web/` in un file `.war` includendo i compilati `.class` in `WEB-INF/classes`.
2. Nomina il file generato `FitTrendStore.war` o `ROOT.war` (per servirlo dalla root del server).

## 4. Deploy su Tomcat Standalone

1. Copiare il file `.war` nella directory `[TOMCAT_HOME]/webapps/` del server Tomcat.
2. Avviare o riavviare il server Tomcat (`bin/startup.sh` o `bin/startup.bat`).
3. Tomcat esploderà in automatico l'archivio creando la cartella dell'applicazione.
4. L'applicazione sarà raggiungibile all'indirizzo configurato (es: `http://localhost:8080/FitTrendStore/home`).

---

## 5. Checklist di Validazione Flussi (Nuovo DB)

Una volta completato il deploy, verificare il corretto funzionamento dei flussi che fanno affidamento sul nuovo schema del database.

### Catalogo e Prodotti
- [ ] **Catalogo filtra per Categoria**: Verificare che nel catalogo sia possibile filtrare la lista dei prodotti selezionando una specifica categoria tramite l'ID derivato dalla tabella `Categoria`.
- [ ] **Immagini caricate dal path DB**: Confermare che le immagini dei prodotti siano visibili (ad es. `images/products/tappetino.jpg` salvato su DB unito al `contextPath`).

### Ordini e Stock
- [ ] **Nuovo Ordine**: Completare il checkout. Verificare che l'ordine sia creato correttamente e abbia lo stato iniziale `in_elaborazione`.
- [ ] **Progressione Stato**: Accedere come Admin e verificare la possibilità di passare l'ordine a `in_consegna` e, successivamente, a `consegnato`.
- [ ] **Annullamento Ordine**: Testare l'annullamento di un ordine. Verificare che le quantità dei prodotti (`stock`) vengano *ripristinate* correttamente sul database.

### Storicità dei Dati
- [ ] **Invarianza Dati Storici**: Modificare il prezzo o il nome di un prodotto dal pannello Admin. Controllare un vecchio ordine contentente quel prodotto e confermare che il prezzo e il nome registrati nell'ordine non cambino, poiché storicizzati in `Dettaglio_Ordine`.

### Gestione Rimborsi
- [ ] **Singola Richiesta Rimborso**: Verificare che per un singolo ordine (con stato appropriato) il cliente possa richiedere *un solo* rimborso. Ulteriori tentativi di richiesta per lo stesso ordine devono essere bloccati.
- [ ] **Approvazione/Rifiuto Rimborso**: Da Admin, approvare un rimborso. Lo stato deve progredire regolarmente (es. `richiesto` -> `approvato` -> `completato`). Testare similmente lo stato `rifiutato`.
