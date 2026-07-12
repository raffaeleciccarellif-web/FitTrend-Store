package model;

import java.math.BigDecimal;

/**
 * Rappresenta una singola riga di dettaglio di un ordine.
 * nome_prodotto_acquisto e prezzo_acquisto sono de-normalizzati:
 * fotografano il valore esatto al momento dell'acquisto (storicità).
 */
public class DettaglioOrdine {

    private int id;
    private int ordineId;
    private int prodottoId;
    private String nomeProdottoAcquisto; // valore storico al momento dell'ordine
    private int quantita;
    private BigDecimal prezzoAcquisto;   // valore storico al momento dell'ordine

    // ── Costruttori ──────────────────────────────────────────────────────────
    public DettaglioOrdine() {}

    // ── Getter / Setter ──────────────────────────────────────────────────────
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrdineId() { return ordineId; }
    public void setOrdineId(int ordineId) { this.ordineId = ordineId; }

    public int getProdottoId() { return prodottoId; }
    public void setProdottoId(int prodottoId) { this.prodottoId = prodottoId; }

    public String getNomeProdottoAcquisto() { return nomeProdottoAcquisto; }
    public void setNomeProdottoAcquisto(String nomeProdottoAcquisto) { this.nomeProdottoAcquisto = nomeProdottoAcquisto; }

    public int getQuantita() { return quantita; }
    public void setQuantita(int quantita) { this.quantita = quantita; }

    public BigDecimal getPrezzoAcquisto() { return prezzoAcquisto; }
    public void setPrezzoAcquisto(BigDecimal prezzoAcquisto) { this.prezzoAcquisto = prezzoAcquisto; }

    /**
     * Calcola il subtotale di questa riga: prezzo_acquisto × quantita.
     */
    public BigDecimal getSubtotale() {
        if (prezzoAcquisto == null) return BigDecimal.ZERO;
        return prezzoAcquisto.multiply(new BigDecimal(quantita));
    }
}
