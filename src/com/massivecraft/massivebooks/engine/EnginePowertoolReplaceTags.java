package com.massivecraft.massivebooks.engine;

import com.massivecraft.massivebooks.Lang;
import com.massivecraft.massivebooks.event.MassiveBooksPowertoolReplaceTagEvent;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.mixin.MixinDisplayName;
import com.massivecraft.massivecore.util.IdUtil;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class EnginePowertoolReplaceTags extends Engine
{
	// -------------------------------------------- //
	// INSTANCE
	// -------------------------------------------- //
	
	private static EnginePowertoolReplaceTags i = new EnginePowertoolReplaceTags();
	public static EnginePowertoolReplaceTags get() { return i; }
	
	// -------------------------------------------- //
	// LISTENER
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onReplaceTagEvent(MassiveBooksPowertoolReplaceTagEvent event)
	{
		String[] tagParts = event.getTag().split("[.]", 2);
		String subTag = tagParts.length >= 2 ? tagParts[1] : "";
		
		switch (tagParts[0])
		{
			case "me":
				handleMeTags(event, subTag);
				break;
			case "you":
				handleYouTags(event, subTag);
				break;
			case "block":
				handleBlockTags(event, subTag);
				break;
		}
	}
	
	private static void handleMeTags(MassiveBooksPowertoolReplaceTagEvent event, String subTag)
	{
		Player me = event.getPlayer();
		
		switch (subTag)
		{
			case "name":
				event.setReplacement(me.getName());
				break;
			case "id":
				event.setReplacement(String.valueOf(me.getEntityId()));
				break;
			case "displayname":
				event.setReplacement(MixinDisplayName.get().getDisplayName(me, me));
				break;
		}
	}
	
	private static void handleYouTags(MassiveBooksPowertoolReplaceTagEvent event, String subTag)
	{
		Player me;
		Player you = IdUtil.getAsPlayer(event.getEntityClicked());
		
		if (you == null)
		{
			event.setError(Lang.POWERTOOL_REQUIRES_YOU);
			return;
		}
		
		switch (subTag)
		{
			case "name":
				event.setReplacement(you.getName());
				break;
			case "id":
				event.setReplacement(String.valueOf(you.getEntityId()));
				break;
			case "displayname":
				me = event.getPlayer();
				event.setReplacement(MixinDisplayName.get().getDisplayName(you, me));
				break;
		}
	}
	
	private static void handleBlockTags(MassiveBooksPowertoolReplaceTagEvent event, String subTag)
	{
		Block block = event.getBlockClicked();
		
		if (block == null)
		{
			event.setError(Lang.POWERTOOL_REQUIRES_BLOCK);
			return;
		}
		
		Integer coordElement = null;
		
		switch (subTag)
		{
			case "x":
				coordElement = block.getX();
				break;
			case "y":
				coordElement = block.getY();
				break;
			case "z":
				coordElement = block.getZ();
				break;
		}
		
		if (coordElement == null) return;
		
		event.setReplacement(String.valueOf(coordElement));
	}
	
}
