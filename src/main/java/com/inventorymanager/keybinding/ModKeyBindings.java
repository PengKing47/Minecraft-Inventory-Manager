package com.inventorymanager.keybinding;

import org.lwjgl.glfw.GLFW;


import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.slot.SlotActionType;

public class ModKeyBindings {

    public static int currentRow = 9;
    
    public static final KeyBinding CYCLE_KEY = new KeyBinding(
        "key.inventorymanager.cycle_key", 
        InputUtil.Type.KEYSYM, 
        GLFW.GLFW_KEY_GRAVE_ACCENT,
        KeyBinding.INVENTORY_CATEGORY
    );


    public static void registerKeys(){
        KeyBindingHelper.registerKeyBinding(CYCLE_KEY);
        InitializeCycleInventory();
        
    }


    private static void InitializeCycleInventory(){

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            MinecraftClient CLIENT = MinecraftClient.getInstance();

            while (CYCLE_KEY.wasPressed()) {
                for(int i = 0; i < 9; i++){
                    CLIENT.interactionManager.clickSlot(
                    CLIENT.player.playerScreenHandler.syncId,
                    i + currentRow,
                    i,
                    SlotActionType.SWAP,
                    CLIENT.player
                    );
                }
                currentRow += 9;
                if(currentRow >= 35){
                    currentRow = 9;
                }
            }
        });
    }
}