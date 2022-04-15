package com.roy.mvc.itemservice.repository;

import com.roy.mvc.itemservice.domain.Item;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ItemRepository {

    private static final Map<Long, Item> STORE = new ConcurrentHashMap<>();
    private static final AtomicLong SEQUENCE = new AtomicLong(0L);

    public Item save(Item item) {
        item.setId(SEQUENCE.getAndIncrement());
        STORE.put(item.getId(), item);
        return item;
    }

    public Item findById(Long id) {
        return STORE.get(id);
    }

    public List<Item> findAll() {
        return new ArrayList<>(STORE.values());
    }

    public void update(Long itemId, Item param) {
        Item savedItem = findById(itemId);
        savedItem.setItemName(param.getItemName());
        savedItem.setPrice(param.getPrice());
        savedItem.setQuantity(param.getQuantity());
    }

    public void clearStore() {
        STORE.clear();
    }

}
