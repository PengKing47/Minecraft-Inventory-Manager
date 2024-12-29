package com.inventorymanager.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenPos;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.AbstractCraftingRecipeBookWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.inventorymanager.InventoryManager;

@Environment(EnvType.CLIENT)
@Mixin(InventoryScreen.class)
public class SortButtonMixin extends RecipeBookScreen<PlayerScreenHandler>{

	private float mouseX;
	private float mouseY;

	private final ButtonTextures BUTTON_TEXTURES = new ButtonTextures(
		Identifier.of(InventoryManager.MOD_ID, "sort_button/button"), 
		Identifier.of(InventoryManager.MOD_ID, "sort_button/button_highlighted")
	);
	final MinecraftClient client = MinecraftClient.getInstance();
	HashMap<Item, Integer> itemMap = getItemMap();

	TexturedButtonWidget sortedButton = new TexturedButtonWidget(this.getRecipeBookButtonPos().x() + 25, this.getRecipeBookButtonPos().y(), 20, 18, BUTTON_TEXTURES, button -> {

		this.stackItems();

		Item[] inventory = new Item[27];
		for(int i = 9; i < 36; i++){
			inventory[i-9] = client.player.getInventory().getStack(i).getItem();
		}

		for(int i = 0; i < inventory.length; i++){
			for(int j = 0; j < inventory.length-1; j++){
				if(itemMap.get(inventory[j]) > itemMap.get(inventory[j+1])){
					this.swapItems(j+9, j+10);

					Item temp = inventory[j];
					inventory[j] = inventory[j+1];
					inventory[j+1] = temp;
				}
			}
		}
		

	});

	public SortButtonMixin(PlayerEntity player) {
		super(
			player.playerScreenHandler, new AbstractCraftingRecipeBookWidget(player.playerScreenHandler), player.getInventory(), Text.translatable("container.crafting")
		);
		this.titleX = 97;
	}

	@Inject(at = @At("TAIL"), method = "init")
	private void init(CallbackInfo info) {

		this.addDrawableChild(sortedButton);
		
	}

	private void swapItems(int slot1, int slot2){
		this.onMouseClick(this.handler.getSlot(slot1), slot1, 8, SlotActionType.SWAP);
		this.onMouseClick(this.handler.getSlot(slot2), slot2, 8, SlotActionType.SWAP);
		this.onMouseClick(this.handler.getSlot(slot1), slot1, 8, SlotActionType.SWAP);
	}

	private void stackItems(){
		for(int i = 9; i < 36; i++){
			for(int j = 9; j < 36; j++){
				if(this.canStack(client.player.getInventory().getStack(i), client.player.getInventory().getStack(j))){
					this.onMouseClick(this.handler.getSlot(i), i, 8, SlotActionType.SWAP);
					this.onMouseClick(this.handler.getSlot(j), j, 0, SlotActionType.QUICK_MOVE);
					this.onMouseClick(this.handler.getSlot(i), i, 8, SlotActionType.SWAP);
				}
			}
		}
	}

	private boolean canStack(ItemStack stack1, ItemStack stack2){
		if(!stack1.getItem().equals(stack2.getItem())){
			return false;
		}
		int maxStack = stack1.getMaxCount();
		return stack1.getCount() + stack2.getCount() <= maxStack;
	}

	@Override
	public ScreenPos getRecipeBookButtonPos() {
		return new ScreenPos(this.x + 104, this.height / 2 - 22);
	}

	@Override
	public void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
		int i = this.x;
		int j = this.y;
		context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND_TEXTURE, i, j, 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 256, 256);
		InventoryScreen.drawEntity(context, i + 26, j + 8, i + 75, j + 78, 30, 0.0625F, this.mouseX, this.mouseY, this.client.player);
		sortedButton.setPosition(this.getRecipeBookButtonPos().x() + 25, this.getRecipeBookButtonPos().y());
		sortedButton.setFocused(false);
	}


	private static HashMap<Item, Integer> getItemMap(){

		HashMap<Item, Integer> itemMap = new HashMap<>();
		ArrayList<Item> itemList = new ArrayList<>();

		Registries.BLOCK.stream().forEach(block -> {
            itemList.add(block.asItem());
			
        });
        Registries.ITEM.stream().forEach(item -> {
            itemList.add(item);
        });

		for(int i = 0; i < itemList.size(); i++){
			itemMap.put(itemList.get(i), i);
		}

		itemMap.put(Items.AIR, Integer.MAX_VALUE);

		return itemMap;

	}

}