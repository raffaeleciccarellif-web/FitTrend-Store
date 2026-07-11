package model;

import java.math.BigDecimal;

public class Prodotto {

    private int id;
    private String nome;
    private String descrizione;
    private BigDecimal prezzo;
    private int categoriaId;
    private String categoriaNome;
    private String immagine;
    private int quantitaDisponibile;
    private boolean deleted;

    public Prodotto() {
    }

    public Prodotto(int id, String nome, String descrizione, BigDecimal prezzo,
                    int categoriaId, String categoriaNome, String immagine,
                    int quantitaDisponibile, boolean deleted) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.prezzo = prezzo;
        this.categoriaId = categoriaId;
        this.categoriaNome = categoriaNome;
        this.immagine = immagine;
        this.quantitaDisponibile = quantitaDisponibile;
        this.deleted = deleted;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public BigDecimal getPrezzo() { return prezzo; }
    public void setPrezzo(BigDecimal prezzo) { this.prezzo = prezzo; }

    public int getCategoriaId() { return categoriaId; }
    public void setCategoriaId(int categoriaId) { this.categoriaId = categoriaId; }

    public String getCategoriaNome() { return categoriaNome; }
    public void setCategoriaNome(String categoriaNome) { this.categoriaNome = categoriaNome; }

    public String getImmagine() { return immagine; }
    public void setImmagine(String immagine) { this.immagine = immagine; }

    public int getQuantitaDisponibile() { return quantitaDisponibile; }
    public void setQuantitaDisponibile(int quantitaDisponibile) { this.quantitaDisponibile = quantitaDisponibile; }

    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}
