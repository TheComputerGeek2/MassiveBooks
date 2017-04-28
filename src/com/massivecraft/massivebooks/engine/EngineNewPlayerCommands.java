package com.massivecraft.massivebooks.engine;

import com.massivecraft.massivebooks.Lang;
import com.massivecraft.massivebooks.MassiveBooks;
import com.massivecraft.massivebooks.entity.MConf;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.mixin.MixinPlayed;
import com.massivecraft.massivecore.util.MUtil;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

public class EngineNewPlayerCommands extends Engine
{
	// -------------------------------------------- //
	// INSTANCE
	// -------------------------------------------- //
	
	private static EngineNewPlayerCommands i = new EngineNewPlayerCommands();
	public static EngineNewPlayerCommands get() { return i; }
	
	// -------------------------------------------- //
	// LISTENERS
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void newPlayerCommands(PlayerJoinEvent event)
	{
		// If a player is joining the server for the first time ...
		final Player player = event.getPlayer();
		if (MUtil.isntPlayer(player)) return;
		if (MixinPlayed.get().hasPlayedBefore(player)) return;
		
		// ... and we are using new player commands ...
		if (!MConf.get().usingNewPlayerCommands) return;
		
		// ... prepare a task ...
		Runnable task = new Runnable()
		{
			@Override
			public void run()
			{
				final ConsoleCommandSender consoleSender = Bukkit.getConsoleSender();
				MassiveBooks.get().log(Lang.getNewPlayerCommandsForX(player, consoleSender));
				for (String cmd : MConf.get().newPlayerCommands)
				{
					cmd = Txt.removeLeadingCommandDust(cmd);
					cmd = cmd.replace("{p}", player.getName());
					cmd = cmd.replace("{player}", player.getName());
					Bukkit.getServer().dispatchCommand(consoleSender, cmd);
				}
			}
		};
		
		// ... and run it either now or later.
		if (MConf.get().usingNewPlayerCommandsDelayTicks)
		{
			Bukkit.getScheduler().scheduleSyncDelayedTask(MassiveBooks.get(), task, MConf.get().newPlayerCommandsDelayTicks);
		}
		else
		{
			task.run();
		}
	}
	
}
