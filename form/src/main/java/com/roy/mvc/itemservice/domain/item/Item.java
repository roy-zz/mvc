package com.roy.mvc.itemservice.domain.item;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
public class Item {
    private Long id;
    private String itemName;
    private Integer price;
    private Integer quantity;
    private Boolean open;
    private List<String> regions = Collections.emptyList();
    private ItemType itemType;
    private String deliveryCode;

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
