package model;

import java.math.BigDecimal;

public class ItemCarrello {
    private int idProdotto;
    private String nomeProdotto;
    private BigDecimal prezzoCorrente;
    private String immagine;
    private int quantita;
    private int quantitaDisponibile;

    public ItemCarrello() {
    }

    public ItemCarrello(int idProdotto, String nomeProdotto, BigDecimal prezzoCorrente, String immagine, int quantita, int quantitaDisponibile) {
        this.idProdotto = idProdotto;
        this.nomeProdotto = nomeProdotto;
        this.prezzoCorrente = prezzoCorrente;
        this.immagine = immagine;
        this.quantita = quantita;
        this.quantitaDisponibile = quantitaDisponibile;
    }

    public int getIdProdotto() {
        return idProdotto;
    }

    public int getId() {
        return idProdotto;
    }

    public void setIdProdotto(int idProdotto) {
        this.idProdotto = idProdotto;
    }

    public String getNomeProdotto() {
        return nomeProdotto;
    }

    public String getNome() {
        return nomeProdotto;
    }

    public void setNomeProdotto(String nomeProdotto) {
        this.nomeProdotto = nomeProdotto;
    }

    public BigDecimal getPrezzoCorrente() {
        return prezzoCorrente;
    }

    public BigDecimal getPrezzo() {
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

    public int getQuantitaDisponibile() {
        return quantitaDisponibile;
    }

    public void setQuantitaDisponibile(int quantitaDisponibile) {
        this.quantitaDisponibile = quantitaDisponibile;
    }

    public BigDecimal getSubtotale() {
        if (prezzoCorrente == null) {
            return BigDecimal.ZERO;
        }
        return prezzoCorrente.multiply(new BigDecimal(quantita));
    }
}
