package model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Ordine {

    public static final String STATO_IN_ELABORAZIONE = "in_elaborazione";
    public static final String STATO_IN_CONSEGNA     = "in_consegna";
    public static final String STATO_CONSEGNATO      = "consegnato";
    public static final String STATO_ANNULLATO       = "annullato";

    public static final Map<String, Set<String>> TRANSIZIONI_AMMESSE;
    static {
        Map<String, Set<String>> map = new HashMap<>();
        map.put(STATO_IN_ELABORAZIONE, new HashSet<>(Arrays.asList(STATO_IN_CONSEGNA, STATO_ANNULLATO)));
        map.put(STATO_IN_CONSEGNA,     new HashSet<>(Arrays.asList(STATO_CONSEGNATO, STATO_ANNULLATO)));
        map.put(STATO_CONSEGNATO,      Collections.emptySet());
        map.put(STATO_ANNULLATO,       Collections.emptySet());
        TRANSIZIONI_AMMESSE = Collections.unmodifiableMap(map);
    }

    private int id;
    private int utenteId;
    private Timestamp dataOrdine;
    private BigDecimal totale;
    private String indirizzoSpedizione;
    private String cittaSpedizione;
    private String capSpedizione;
    private String metodoPagamento;
    private String ultimeCifreCarta;
    private String stato;
    private List<DettaglioOrdine> dettagli;

    public Ordine() {}

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
