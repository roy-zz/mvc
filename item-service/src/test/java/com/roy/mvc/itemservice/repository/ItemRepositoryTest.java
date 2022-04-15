package com.roy.mvc.itemservice.repository;

import com.roy.mvc.itemservice.domain.Item;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRepositoryTest {

    ItemRepository itemRepository = new ItemRepository();
    @AfterEach
    void afterEach() {
        itemRepository.clearStore();
    }
    @Test
    void save() {
        // GIVEN
        Item item = new Item("itemA", 10000, 10);
        // WHEN
        Item savedItem = itemRepository.save(item);
        // THEN
        Item findItem = itemRepository.findById(item.getId());
        assertThat(findItem).isEqualTo(savedItem);
    }
    @Test
    void findAll() {
        // GIVEN
        Item item1 = new Item("item1", 10000, 10);
        Item item2 = new Item("item2", 20000, 20);
        itemRepository.save(item1);
        itemRepository.save(item2);
        // WHEN
        List<Item> result = itemRepository.findAll();
        // THEN
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(item1, item2);
    }
    @Test
    void updateItem() {
        // GIVEN
        Item item = new Item("item1", 10000, 10);
        Item savedItem = itemRepository.save(item);
        Long itemId = savedItem.getId();
        // WHEN
        Item updateParam = new Item("item2", 20000, 30);
        itemRepository.update(itemId, updateParam);
        Item findItem = itemRepository.findById(itemId);
        // THEN
        assertThat(findItem.getItemName()).isEqualTo(updateParam.getItemName());
        assertThat(findItem.getPrice()).isEqualTo(updateParam.getPrice());
        assertThat(findItem.getQuantity()).isEqualTo(updateParam.getQuantity());
    }

}
