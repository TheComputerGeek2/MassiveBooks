package com.massivecraft.massivebooks.engine;

import com.massivecraft.massivebooks.BookUtil;
import com.massivecraft.massivebooks.Lang;
import com.massivecraft.massivebooks.Perm;
import com.massivecraft.massivebooks.entity.MPlayer;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.mixin.MixinActual;
import com.massivecraft.massivecore.util.IdUtil;
import com.massivecraft.massivecore.util.MUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class EngineAutoUpdate extends Engine
{
	// -------------------------------------------- //
	// INSTANCE
	// -------------------------------------------- //
	
	private static EngineAutoUpdate i = new EngineAutoUpdate();
	public static EngineAutoUpdate get() { return i; }
	
	// -------------------------------------------- //
	// AUTOUPDATE: JOIN WARN OR TOGGLE
	// -------------------------------------------- //
	
	// Can't be cancelled
	@EventHandler(priority = EventPriority.NORMAL)
	public void autoupdateJoinWarnOrToggle(PlayerJoinEvent event)
	{
		if (!MixinActual.get().isActualJoin(event)) return;
		
		final Player player = event.getPlayer();
		if (MUtil.isntPlayer(player)) return;
		MPlayer mplayer = MPlayer.get(player);
		if (mplayer.isUsingAutoUpdate()) return;
		
		if (Perm.AUTOUPDATE.has(player, false))
		{
			// Warn
			player.sendMessage(Lang.AUTOUPDATE_JOINWARN);
		}
		else
		{
			// Toggle
			mplayer.setUsingAutoUpdate(true, true, false);
		}
		
	}
	
	// -------------------------------------------- //
	// AUTOUPDATE: PERFORM
	// -------------------------------------------- //
	// MassiveBooks autoupdates books. What does that mean?
	// The item displayname is updated to contain the display name of the author player.
	// Sice the player displayname may change so must the displayname of the book.
	// Additionally books that have the same title as a saved book recieves complete content updates.
	// This way you can always be sure those special books of yours never exists in multiple versions.
	// Saved a new version of the server rule book? All old instances will be updated on the fly.
	
	// Can be cancelled but we don't care :P
	@EventHandler(priority = EventPriority.LOWEST)
	public void updatePerform(PlayerInteractEntityEvent event)
	{
		// Ignore Off Hand
		if (isOffHand(event)) return;
		
		Entity entity = event.getRightClicked();
		if (!(entity instanceof ItemFrame)) return;
		ItemFrame itemFrame = (ItemFrame)entity;
		
		final Player player = event.getPlayer();
		if (MUtil.isntPlayer(player)) return;
		MPlayer mplayer = MPlayer.get(player);
		if (!mplayer.isUsingAutoUpdate()) return;
		
		BookUtil.updateBook(itemFrame);
	}
	
	// Can't be cancelled
	@EventHandler(priority = EventPriority.LOWEST)
	public void updatePerform(PlayerItemHeldEvent event)
	{
		final Player player = event.getPlayer();
		if (MUtil.isntPlayer(player)) return;
		MPlayer mplayer = MPlayer.get(player);
		if (!mplayer.isUsingAutoUpdate()) return;
		
		BookUtil.updateBooks(player);
	}
	
	// Can be cancelled but we don't care :P
	@EventHandler(priority = EventPriority.LOWEST)
	public void updatePerform(PlayerPickupItemEvent event)
	{
		final Player player = event.getPlayer();
		if (MUtil.isntPlayer(player)) return;
		MPlayer mplayer = MPlayer.get(player);
		if (!mplayer.isUsingAutoUpdate()) return;
		
		BookUtil.updateBook(event.getItem());
	}
	
	// Can be cancelled but we don't care :P
	@EventHandler(priority = EventPriority.LOWEST)
	public void updatePerform(InventoryClickEvent event)
	{
		final Player player = IdUtil.getAsPlayer(event.getWhoClicked());
		if (MUtil.isntPlayer(player)) return;
		
		MPlayer mplayer = MPlayer.get(player);
		if (!mplayer.isUsingAutoUpdate()) return;
		
		BookUtil.updateBooks(event.getInventory());
		BookUtil.updateBooks(event.getWhoClicked());
	}
	
	// Can be cancelled but we don't care :P
	@EventHandler(priority = EventPriority.LOWEST)
	public void updatePerform(InventoryOpenEvent event)
	{
		final Player player = IdUtil.getAsPlayer(event.getPlayer());
		if (MUtil.isntPlayer(player)) return;
		
		MPlayer mplayer = MPlayer.get(player);
		if (!mplayer.isUsingAutoUpdate()) return;
		
		BookUtil.updateBooks(event.getInventory());
		BookUtil.updateBooks(player);
	}
	
}
