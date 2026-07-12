package model;

import java.math.BigDecimal;

// Bean DettaglioOrdine: riga di un ordine con dati storici de-normalizzati
// nome_prodotto_acquisto e prezzo_acquisto fotografano il valore al momento dell'acquisto
public class DettaglioOrdine {

    private int id;
    private int ordineId;
    private int prodottoId;
    private String nomeProdottoAcquisto;
    private int quantita;
    private BigDecimal prezzoAcquisto;

    public DettaglioOrdine() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrdineId() { return ordineId; }
    public void setOrdineId(int ordineId) { this.ordineId = ordineId; }

    public int getProdottoId() { return prodottoId; }
    public void setProdottoId(int prodottoId) { this.prodottoId = prodottoId; }

    public String getNomeProdottoAcquisto() { return nomeProdottoAcquisto; }
    public void setNomeProdottoAcquisto(String n) { this.nomeProdottoAcquisto = n; }

    public int getQuantita() { return quantita; }
    public void setQuantita(int quantita) { this.quantita = quantita; }

    public BigDecimal getPrezzoAcquisto() { return prezzoAcquisto; }
    public void setPrezzoAcquisto(BigDecimal prezzoAcquisto) { this.prezzoAcquisto = prezzoAcquisto; }

    // Subtotale della riga: prezzo_acquisto * quantita
    public BigDecimal getSubtotale() {
        if (prezzoAcquisto == null) return BigDecimal.ZERO;
        return prezzoAcquisto.multiply(new BigDecimal(quantita));
    }
}
