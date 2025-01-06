package com.epam.training.gen.ai.plugin;

import com.epam.training.gen.ai.model.InventoryItem;
import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class InventoryPlugin {
    private final Map<Integer, InventoryItem> inventory;

    @DefineKernelFunction(name = "get_inventory", description = "Get all items from the inventory and their current stock")
    public List<InventoryItem> getInventory() {
        log.info("Called the get Inventory: {}", inventory.values());
        return new ArrayList<>(inventory.values());
    }

    @DefineKernelFunction(name = "increase_item_stock", description = "Increases the stock of an item in the inventory")
    public InventoryItem increaseItemStock(
            @KernelFunctionParameter(name = "id", description = "The Item ID to change") int id,
            @KernelFunctionParameter(name = "amount", description = "The amount to increase. It should be positive") int amount
    ) {
        log.info("Adding {} elements to the stock of item {}.", amount, id);
        if (!inventory.containsKey(id)) {
            throw new IllegalArgumentException("Item " + id + " not found in the inventory");
        }
        InventoryItem item = inventory.get(id);
        int itemsAdded = item.increaseStock(amount);
        log.info("Inventory Item {} was increase in {} elements", id, itemsAdded);

        return item;
    }

    @DefineKernelFunction(name = "reduce_item_stock", description = "Reduces the stock of an item in the inventory")
    public InventoryItem reduceItemStock(
            @KernelFunctionParameter(name = "id", description = "The Item ID to change") int id,
            @KernelFunctionParameter(name = "amount", description = "The amount to reduce. It should be positive") int amount
    ) {
        log.info("Removing {} elements to the stock of item {}.", amount, id);
        if (!inventory.containsKey(id)) {
            throw new IllegalArgumentException("Item " + id + " not found in the inventory");
        }
        InventoryItem item = inventory.get(id);
        int itemsRemoved = item.reduceStock(amount);
        log.info("Inventory Item {} was reduced in {} elements", id, itemsRemoved);

        return item;
    }
}
