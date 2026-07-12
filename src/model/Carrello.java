package model;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;

public class Carrello {
    // Usiamo LinkedHashMap per mantenere l'ordine di inserimento dei prodotti nel carrello
    private LinkedHashMap<Integer, ItemCarrello> items;

    public Carrello() {
        this.items = new LinkedHashMap<>();
    }

    public void add(ItemCarrello item) {
        int id = item.getIdProdotto();
        if (items.containsKey(id)) {
            ItemCarrello esistente = items.get(id);
            esistente.setQuantita(esistente.getQuantita() + item.getQuantita());
        } else {
            items.put(id, item);
        }
    }

    public void update(int idProdotto, int quantita) {
        if (quantita <= 0) {
            remove(idProdotto);
        } else if (items.containsKey(idProdotto)) {
            items.get(idProdotto).setQuantita(quantita);
        }
    }

    public void remove(int idProdotto) {
        items.remove(idProdotto);
    }

    public void svuota() {
        items.clear();
    }

    public Collection<ItemCarrello> getItems() {
        return items.values();
    }

    public int getNumeroTotaleArticoli() {
        int totale = 0;
        for (ItemCarrello item : items.values()) {
            totale += item.getQuantita();
        }
        return totale;
    }

    public BigDecimal getTotaleProvvisorio() {
        BigDecimal totale = BigDecimal.ZERO;
        for (ItemCarrello item : items.values()) {
            totale = totale.add(item.getSubtotale());
        }
        return totale;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
