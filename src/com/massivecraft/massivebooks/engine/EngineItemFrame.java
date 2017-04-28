package com.massivecraft.massivebooks.engine;

import com.massivecraft.massivebooks.BookUtil;
import com.massivecraft.massivebooks.Lang;
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
import org.bukkit.inventory.meta.ItemMeta;

public class EngineItemFrame extends Engine
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static EngineItemFrame i = new EngineItemFrame();
	public static EngineItemFrame get() { return i; }
	
	// -------------------------------------------- //
	// ITEM FRAME: LOAD AND DISPLAYNAME
	// -------------------------------------------- //
	
	// Can be cancelled but we don't care :P
	// NOTE: Placed at low so that the content update on LOWEST will run before.
	@EventHandler(priority = EventPriority.LOW)
	public void itemFrameLoadAndDisplayname(PlayerInteractEntityEvent event)
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
		
		// ... check it the player is sneaking ...
		final Player player = event.getPlayer();
		if (MUtil.isntPlayer(player)) return;
		final boolean sneaking = player.isSneaking();
		
		if (itemFrameLoad(event, item, player, sneaking)) return;
		if (itemFrameDisplayname(event, item, player, sneaking)) return;
		
		// Add more here if you want to in the future :P
	}
	
	private static boolean itemFrameLoad(PlayerInteractEntityEvent event, ItemStack itemInFrame, Player player, boolean sneaking)
	{
		// If loading is allowed ...
		if (sneaking && !MConf.get().itemFrameLoadIfSneakTrue) return false;
		if (!sneaking && !MConf.get().itemFrameLoadIfSneakFalse) return false;
		
		// ... and there is something with BookMeta in the item frame ...
		if (!BookUtil.hasBookMeta(itemInFrame)) return false;
		
		// ... then cancel to stop rotation ...
		event.setCancelled(true);
		
		// ... now do different stuff depending on what item the player is holding ...
		ItemStack itemInHand = InventoryUtil.getWeapon(player);
		if (itemInHand == null) return true;
	
		// ... if the player is holding a similar item ...
		if (itemInHand.isSimilar(itemInFrame))
		{
			// ... do unload ...
			ItemStack target = new ItemStack(itemInHand);
			BookUtil.clear(target);
			InventoryUtil.setWeapon(player, target);
			
			// ... and inform.
			player.sendMessage(Lang.getFrameUnload(itemInHand));
		}
		// ... else if the player is holding a clear book and quill ...
		else if (BookUtil.isCleared(itemInHand))
		{
			// Has right to copy?
			if (!BookUtil.hasCopyPerm(itemInFrame, player, true)) return true;
			
			// ... do load ...
			ItemStack target = new ItemStack(itemInFrame);
			target.setAmount(itemInHand.getAmount());
			InventoryUtil.setWeapon(player, target);
			
			// ... and inform.
			player.sendMessage(Lang.getFrameLoad(target));
		}
		// ... else if the player is holding an educated but invalid guess ...
		else if (itemInHand.getType() == Material.WRITTEN_BOOK || itemInHand.getType() == Material.BOOK_AND_QUILL || itemInHand.getType() == Material.BOOK)
		{
			// ... do help.
			player.sendMessage(Lang.getFrameHelp());
		}
		
		// ... and return true which means that no displayname info should be sent.
		return true;
	}
	
	private static boolean itemFrameDisplayname(PlayerInteractEntityEvent event, ItemStack itemInFrame, Player player, boolean sneaking)
	{
		// If displayname is allowed ...
		if (sneaking && !MConf.get().itemFrameDisplaynameIfSneakTrue) return false;
		if (!sneaking && !MConf.get().itemFrameDisplaynameIfSneakFalse) return false;
		
		// ... and there is something with displayname in the item frame ...
		if (!itemInFrame.hasItemMeta()) return false;
		ItemMeta meta = itemInFrame.getItemMeta();
		if (!meta.hasDisplayName()) return false;
		String displayname = meta.getDisplayName();
		
		// ... and inform on what the frame contains.
		player.sendMessage(Lang.getFrameContains(displayname));
		return true;
	}
	
}
