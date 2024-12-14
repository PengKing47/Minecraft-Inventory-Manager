package com.inventorymanager;

import com.inventorymanager.keybinding.ModKeyBindings;

import net.fabricmc.api.ClientModInitializer;

public class InventoryManagerClientInitializer implements ClientModInitializer{

    @Override
    public void onInitializeClient() {
        ModKeyBindings.registerKeys();
    }
    
}
