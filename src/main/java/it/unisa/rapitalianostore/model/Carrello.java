package it.unisa.rapitalianostore.model;

import java.util.*;
import it.unisa.rapitalianostore.dao.CarrelloDAO; // solo per il tipo nel metodo replaceAllFromDTOs (fully qualified nel corpo)

/**
 * Carrello in sessione (guest/loggato).
 * 
 * aggiunte utility per merge/persistenza (hasItems, toMap, replaceAllFromDTOs).
 */
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
            item.setQuantita(Math.max(1, quantita)); // clamp minimo 1 in sessione
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

    /* =======================
       utilità per persistenza
       ======================= */

    /** vero/falso se il carrello contiene almeno un item con quantita > 0 */
    public boolean hasItems() {
        for (CarrelloItem it : items.values()) {
            if (it.getQuantita() > 0) return true;
        }
        return false;
    }

    /** restituisce una mappa {idProdotto -> quantita} (comoda per DAO.replaceAll o merge) */
    public Map<Integer, Integer> toMap() {
        Map<Integer, Integer> map = new LinkedHashMap<>();
        for (CarrelloItem it : items.values()) {
            if (it.getProdotto() == null) continue;
            int q = Math.max(0, it.getQuantita());
            if (q > 0) map.put(it.getProdotto().getId(), q);
        }
        return map;
    }

    /** rimpiazza tutto il contenuto a partire da una lista DTO (es. caricata dal DB) */
    public void replaceAllFromDTOs(List<it.unisa.rapitalianostore.dao.CarrelloDAO.CarrelloItemDTO> dtos) {
        items.clear();
        if (dtos == null) return;
        for (it.unisa.rapitalianostore.dao.CarrelloDAO.CarrelloItemDTO dto : dtos) {
            Prodotto p = new Prodotto();
            p.setId(dto.getIdProdotto());
            p.setTitolo(dto.getTitolo());
            p.setPrezzo(dto.getPrezzo());
            p.setImmagine(dto.getImmagine());
            CarrelloItem ci = new CarrelloItem(p);
            ci.setQuantita(dto.getQuantita());
            items.put(p.getId(), ci);
        }
    }
}
