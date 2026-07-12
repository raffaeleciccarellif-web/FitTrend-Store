package model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * Rappresenta un ordine effettuato da un utente.
 * Il campo 'stato' è gestito tramite le costanti statiche di questa classe
 * e corrisponde ai valori stringa minuscoli del database.
 */
public class Ordine {

    // ── Costanti di stato (valori DB minuscoli) ─────────────────────────────
    public static final String STATO_IN_ELABORAZIONE = "in_elaborazione";
    public static final String STATO_IN_CONSEGNA     = "in_consegna";
    public static final String STATO_CONSEGNATO      = "consegnato";
    public static final String STATO_ANNULLATO       = "annullato";

    // ── Transizioni ammesse: chiave=stato corrente, valore=stati successivi leciti ──
    // Usate in OrdineDAO.doUpdateStato() per la whitelist delle transizioni
    public static java.util.Map<String, java.util.Set<String>> TRANSIZIONI_AMMESSE;
    static {
        java.util.Map<String, java.util.Set<String>> map = new java.util.HashMap<>();
        map.put(STATO_IN_ELABORAZIONE, new java.util.HashSet<>(java.util.Arrays.asList(STATO_IN_CONSEGNA, STATO_ANNULLATO)));
        map.put(STATO_IN_CONSEGNA,     new java.util.HashSet<>(java.util.Arrays.asList(STATO_CONSEGNATO, STATO_ANNULLATO)));
        map.put(STATO_CONSEGNATO,      java.util.Collections.emptySet());
        map.put(STATO_ANNULLATO,       java.util.Collections.emptySet());
        TRANSIZIONI_AMMESSE = java.util.Collections.unmodifiableMap(map);
    }

    // ── Campi ────────────────────────────────────────────────────────────────
    private int id;
    private int utenteId;
    private Timestamp dataOrdine;
    private BigDecimal totale;
    private String indirizzoSpedizione;
    private String cittaSpedizione;
    private String capSpedizione;
    private String metodoPagamento;
    private String ultimeCifreCarta;   // nullable
    private String stato;
    private List<DettaglioOrdine> dettagli; // caricato on-demand dal DAO

    // ── Costruttori ──────────────────────────────────────────────────────────
    public Ordine() {}

    // ── Getter / Setter ──────────────────────────────────────────────────────
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUtenteId() { return utenteId; }
    public void setUtenteId(int utenteId) { this.utenteId = utenteId; }

    public Timestamp getDataOrdine() { return dataOrdine; }
    public void setDataOrdine(Timestamp dataOrdine) { this.dataOrdine = dataOrdine; }

    public BigDecimal getTotale() { return totale; }
    public void setTotale(BigDecimal totale) { this.totale = totale; }

    public String getIndirizzoSpedizione() { return indirizzoSpedizione; }
    public void setIndirizzoSpedizione(String indirizzoSpedizione) { this.indirizzoSpedizione = indirizzoSpedizione; }

    public String getCittaSpedizione() { return cittaSpedizione; }
    public void setCittaSpedizione(String cittaSpedizione) { this.cittaSpedizione = cittaSpedizione; }

    public String getCapSpedizione() { return capSpedizione; }
    public void setCapSpedizione(String capSpedizione) { this.capSpedizione = capSpedizione; }

    public String getMetodoPagamento() { return metodoPagamento; }
    public void setMetodoPagamento(String metodoPagamento) { this.metodoPagamento = metodoPagamento; }

    public String getUltimeCifreCarta() { return ultimeCifreCarta; }
    public void setUltimeCifreCarta(String ultimeCifreCarta) { this.ultimeCifreCarta = ultimeCifreCarta; }

    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }

    public List<DettaglioOrdine> getDettagli() { return dettagli; }
    public void setDettagli(List<DettaglioOrdine> dettagli) { this.dettagli = dettagli; }

    /**
     * Restituisce l'etichetta leggibile dello stato per la vista.
     * Usata nella JSP con ${ordine.statoLabel}.
     */
    public String getStatoLabel() {
        if (stato == null) return "";
        return switch (stato) {
            case STATO_IN_ELABORAZIONE -> "In Elaborazione";
            case STATO_IN_CONSEGNA     -> "In Consegna";
            case STATO_CONSEGNATO      -> "Consegnato";
            case STATO_ANNULLATO       -> "Annullato";
            default                    -> stato;
        };
    }
}
