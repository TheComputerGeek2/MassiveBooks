package com.massivecraft.massivebooks.engine;

import com.massivecraft.massivebooks.entity.MConf;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.util.InventoryUtil;
import com.massivecraft.massivecore.util.MUtil;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class EngineItemFrameRotate extends Engine
{
	// -------------------------------------------- //
	// INSTANCE
	// -------------------------------------------- //
	
	private static EngineItemFrameRotate i = new EngineItemFrameRotate();
	public static EngineItemFrameRotate get() { return i; }
	
	// -------------------------------------------- //
	// LISTENERS
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void itemFrameRotate(PlayerInteractEntityEvent event)
	{
		// Ignore Off Hand
		if (isOffHand(event)) return;
		
		// If a player is interacting with an item frame ...
		Entity entity = event.getRightClicked();
		if (!(entity instanceof ItemFrame)) return;
		ItemFrame itemFrame = (ItemFrame)entity;
		
		// ... and that item frame contains something ...
		ItemStack item = itemFrame.getItem();
		if (InventoryUtil.isNothing(item)) return;
		
		// ... possibly ...
		final Player player = event.getPlayer();
		if (MUtil.isntPlayer(player)) return;
		final boolean sneaking = player.isSneaking();
		if (sneaking && MConf.get().itemFrameRotateIfSneakTrue) return;
		if (!sneaking && MConf.get().itemFrameRotateIfSneakFalse) return;
		
		// ... stop rotation.
		event.setCancelled(true);
	}
	
}
