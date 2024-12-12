package com.epam.training.gen.ai.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents an item in the inventory.
 */
@Data
@AllArgsConstructor
public class InventoryItem {
    int id;
    String description;
    int currentStock;
    int maximumStock;

    /**
     * Increases the stock of a specific item.
     *
     * @param amount units to increase
     * @return the actual amount of units increased.
     */
    public int increaseStock(int amount) {
        int itemsToAdd = (maximumStock - currentStock) < amount ? maximumStock - currentStock : amount;
        if (itemsToAdd == 0) {
            throw new ArithmeticException("Maximum stock is reached on item " + id);
        }

        currentStock += itemsToAdd;

        return itemsToAdd;
    }

    /**
     * Reduces the stock of a specific item.
     *
     * @param amount units to reduce
     * @return the actual amount of units reduced.
     */
    public int reduceStock(int amount) {
        int itemsToRemove = currentStock < amount ? currentStock : amount;
        if (itemsToRemove == 0) {
            throw new ArithmeticException("There is no stock for item " + id);
        }

        currentStock -= itemsToRemove;

        return itemsToRemove;
    }
}
