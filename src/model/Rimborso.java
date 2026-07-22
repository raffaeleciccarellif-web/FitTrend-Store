package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Rimborso {

    public static final String STATO_RICHIESTO  = "richiesto";
    public static final String STATO_APPROVATO  = "approvato";
    public static final String STATO_RIFIUTATO  = "rifiutato";
    public static final String STATO_COMPLETATO = "completato";

    private int id;
    private int ordineId;
    private Timestamp dataRichiesta;
    private Timestamp dataElaborazione;
    private BigDecimal importo;
    private String motivo;
    private String stato;

    private int utenteId;
    private String utenteEmail;

    public Rimborso() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrdineId() { return ordineId; }
    public void setOrdineId(int ordineId) { this.ordineId = ordineId; }

    public Timestamp getDataRichiesta() { return dataRichiesta; }
    public void setDataRichiesta(Timestamp dataRichiesta) { this.dataRichiesta = dataRichiesta; }

    public Timestamp getDataElaborazione() { return dataElaborazione; }
    public void setDataElaborazione(Timestamp dataElaborazione) { this.dataElaborazione = dataElaborazione; }

    public BigDecimal getImporto() { return importo; }
    public void setImporto(BigDecimal importo) { this.importo = importo; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }

    public int getUtenteId() { return utenteId; }
    public void setUtenteId(int utenteId) { this.utenteId = utenteId; }

    public String getUtenteEmail() { return utenteEmail; }
    public void setUtenteEmail(String utenteEmail) { this.utenteEmail = utenteEmail; }

    // Etichetta leggibile dello stato per la JSP (${rimborso.statoLabel})
    public String getStatoLabel() {
        if (stato == null) return "";
        return switch (stato) {
            case STATO_RICHIESTO  -> "Richiesto";
            case STATO_APPROVATO  -> "Approvato";
            case STATO_RIFIUTATO  -> "Rifiutato";
            case STATO_COMPLETATO -> "Completato";
            default               -> stato;
        };
    }
}
