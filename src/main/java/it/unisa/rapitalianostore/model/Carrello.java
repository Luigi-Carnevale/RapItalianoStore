package it.unisa.rapitalianostore.model;

import java.util.*;

public class Carrello {
    private Map<Integer, CarrelloItem> items = new HashMap<>();

    public void aggiungiProdotto(Prodotto prodotto) {
        CarrelloItem item = items.get(prodotto.getId());
        if (item == null) {
            items.put(prodotto.getId(), new CarrelloItem(prodotto));
        } else {
            item.incrementaQuantita();
        }
    }

    public void rimuoviProdotto(int idProdotto) {
        items.remove(idProdotto);
    }

    public void aggiornaQuantita(int idProdotto, int quantita) {
        CarrelloItem item = items.get(idProdotto);
        if (item != null) {
            item.setQuantita(quantita);
        }
    }

    public Collection<CarrelloItem> getItems() {
        return items.values();
    }

    public double getTotale() {
        return items.values().stream().mapToDouble(CarrelloItem::getTotale).sum();
    }

    public boolean isVuoto() {
        return items.isEmpty();
    }
}
