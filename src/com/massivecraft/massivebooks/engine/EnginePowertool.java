package com.massivecraft.massivebooks.engine;

import com.massivecraft.massivebooks.BookUtil;
import com.massivecraft.massivebooks.Const;
import com.massivecraft.massivebooks.Lang;
import com.massivecraft.massivebooks.event.MassiveBooksPowertoolReplaceLinesEvent;
import com.massivecraft.massivebooks.predicate.PredicateLineShouldExecute;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.collections.MassiveList;
import com.massivecraft.massivecore.util.InventoryUtil;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.regex.Pattern;

public class EnginePowertool extends Engine
{
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //
	
	private final static String START = "{";
	private final static String END = "}";
	
	private final static String ESC_START = "\\"+START;
	private final static String ESC_END = "\\"+END;
	
	final static Pattern pattern = Pattern.compile(ESC_START+"([^"+ESC_START+ESC_END+"]+)"+ESC_END);
	
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static EnginePowertool i = new EnginePowertool();
	public static EnginePowertool get() { return i; }
	
	// -------------------------------------------- //
	// LISTENER: INTERACT EVENTS
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.LOW)
	public void handleInteractEvent(PlayerInteractEvent interactEvent)
	{
		if (interactEvent.getAction() != Action.LEFT_CLICK_AIR && interactEvent.getAction() != Action.LEFT_CLICK_BLOCK) return;
		handleInteractEvent(interactEvent, null);
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void handleInteractEvent(PlayerInteractEntityEvent interactEntityEvent)
	{
		// Ignore Off Hand
		if (isOffHand(interactEntityEvent)) return;
		
		// TODO: Do we even want to use this? Perhaps a sniper-edit search implementation will be better? 
		handleInteractEvent(null, interactEntityEvent);
	}
	
	private static void handleInteractEvent(final PlayerInteractEvent interactEvent, final PlayerInteractEntityEvent interactEntityEvent)
	{
		// Get some data
		final Player player;
		final Cancellable cancellable;
		if (interactEvent != null)
		{
			player = interactEvent.getPlayer();
			cancellable = interactEvent;
		}
		else if (interactEntityEvent != null)
		{
			player = interactEntityEvent.getPlayer();
			cancellable = interactEntityEvent;
		}
		else
		{
			throw new NullPointerException("It's ok for either interactEvent or interactEntityEvent to be null, but not both at the same time!");
		}
		
		// If the player is holding a written book ...
		final ItemStack item = InventoryUtil.getWeapon(player);
		if (item == null) return;
		final Material itemType = item.getType();
		if (itemType != Material.WRITTEN_BOOK) return;
		
		// ... and that written book is a powertool ...
		if (!BookUtil.containsFlag(item, Const.POWERTOOL)) return;
		
		// ... cancel the event since we are trying to use the powertool for sure ...
		cancellable.setCancelled(true);
		
		// ... extract the rawlines from the powertool ...
		List<String> rawLines = getRawlines(item);
		
		// ... run the replace event on the lines ...
		MassiveBooksPowertoolReplaceLinesEvent event = new MassiveBooksPowertoolReplaceLinesEvent(interactEvent, interactEntityEvent, rawLines);
		event.run();
		
		// ... did we have an error? ...
		if (event.getError() != null)
		{
			player.sendMessage(event.getError());
			return;
		}
		
		// ... how nice! now lets try to run these chats/commands ...
		List<String> lines = event.getReplacement();
		int lineIndex = 0;
		for (String line : lines)
		{
			lineIndex++;
			if (!PredicateLineShouldExecute.get().apply(line)) continue;
			try
			{
				player.chat(line);
				player.sendMessage(Lang.getPowertoolRan(lineIndex, line));
			}
			catch (Exception e)
			{
				player.sendMessage(Lang.getPowertoolRan(lineIndex, line, e.getMessage()));
				break;
			}
		}
	}
	
	// -------------------------------------------- //
	// UTILITIES AND SUB-LOGIC
	// -------------------------------------------- //
	
	private static List<String> getRawlines(ItemStack item)
	{
		List<String> ret = new MassiveList<>();
		
		List<String> pages = BookUtil.getPages(item);
		if (pages == null) return ret;
		
		for (String page : pages)
		{
			if (page == null) continue;
			for (String line : Txt.PATTERN_NEWLINE.split(page))
			{
				line = line.trim();
				// There seems to be a bug in Spigot 1.9
				// This bug adds black colors around newlines.
				// Since colors should not be part of chat window output
				// We just strip all colors from each line.
				line = ChatColor.stripColor(line);
				ret.add(line);
			}
		}
		
		return ret;
	}
	
}
