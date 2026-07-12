package model;

import java.math.BigDecimal;

public class ItemCarrello {
    private int idProdotto;
    private String nomeProdotto;
    private BigDecimal prezzoCorrente;
    private String immagine;
    private int quantita;

    public ItemCarrello() {
    }

    public ItemCarrello(int idProdotto, String nomeProdotto, BigDecimal prezzoCorrente, String immagine, int quantita) {
        this.idProdotto = idProdotto;
        this.nomeProdotto = nomeProdotto;
        this.prezzoCorrente = prezzoCorrente;
        this.immagine = immagine;
        this.quantita = quantita;
    }

    public int getIdProdotto() {
        return idProdotto;
    }

    public void setIdProdotto(int idProdotto) {
        this.idProdotto = idProdotto;
    }

    public String getNomeProdotto() {
        return nomeProdotto;
    }

    public void setNomeProdotto(String nomeProdotto) {
        this.nomeProdotto = nomeProdotto;
    }

    public BigDecimal getPrezzoCorrente() {
        return prezzoCorrente;
    }

    public void setPrezzoCorrente(BigDecimal prezzoCorrente) {
        this.prezzoCorrente = prezzoCorrente;
    }

    public String getImmagine() {
        return immagine;
    }

    public void setImmagine(String immagine) {
        this.immagine = immagine;
    }

    public int getQuantita() {
        return quantita;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    public BigDecimal getSubtotale() {
        if (prezzoCorrente == null) {
            return BigDecimal.ZERO;
        }
        return prezzoCorrente.multiply(new BigDecimal(quantita));
    }
}
