package com.massivecraft.massivebooks.cmd;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.massivecraft.massivebooks.BookUtil;
import com.massivecraft.massivebooks.ConfServer;
import com.massivecraft.massivebooks.Lang;
import com.massivecraft.massivebooks.MassiveBooks;
import com.massivecraft.massivebooks.Perm;
import com.massivecraft.massivebooks.entity.MConf;
import com.massivecraft.mcore.cmd.arg.ARInteger;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.cmd.req.ReqIsPlayer;
import com.massivecraft.mcore.util.InventoryUtil;

public class CmdBookCopy extends MassiveBooksCommand
{
	public CmdBookCopy()
	{
		super();
		this.addAliases(ConfServer.aliasesBookCopy);
		this.addOptionalArg("times", "1");
		this.addRequirements(ReqHasPerm.get(Perm.COPY.node));
		this.addRequirements(ReqIsPlayer.get());
	}
	
	@Override
	public void perform()
	{
		// Get item arg
		ItemStack item = this.arg(ARBookInHand.getWritten());
		if (item == null) return;
		BookUtil.updateBook(item);
		
		item = item.clone();
		item.setAmount(1);
		
		// Get times arg
		Integer times = this.arg(0, ARInteger.get(), 1);
		if (times == null) return;
		if (times <= 0)
		{
			sendMessage(Lang.TIMES_MUST_BE_POSITIVE);
			return;
		}

		// Check other-perm if another author
		if (!BookUtil.isAuthorEquals(item, sender) && !Perm.COPY_OTHER.has(sender, true)) return;
		
		// What do we require?
		double moneyRequired = times * MConf.get().getCopyCost(me);
		int booksRequired = times;
		int inksacsRequired = times;
		int feathersRequired = times;
		int roomRequired = times;
		
		if (me.getGameMode() == GameMode.CREATIVE)
		{
			moneyRequired = 0;
			booksRequired = 0;
			inksacsRequired = 0;
			feathersRequired = 0;
			// roomRequired = 0; // Nope, since the room always is required. It's not really a resource cost.
		}
		
		// The inventory in question
		Inventory inventory = me.getInventory();
		
		// Check ...
		
		// ... money (this is only a preliminary check)
		if (MassiveBooks.get().economy != null)
		{
			if (!MassiveBooks.get().economy.has(me.getName(), me.getWorld().getName(), moneyRequired))
			{
				double moneyPossesed = MassiveBooks.get().economy.getBalance(me.getName(), me.getWorld().getName());
				double moneyMissing = moneyRequired - moneyPossesed;
				this.sendCheckFailMessage(Lang.RESOURCE_MONEY, moneyRequired, moneyPossesed, moneyMissing);
				return;
			}
		}
		
		// ... books (the actual check)
		int booksPossesed = InventoryUtil.countSimilar(inventory, new ItemStack(Material.BOOK));
		if (!this.checkResource(Lang.RESOURCE_BOOKS, booksRequired, booksPossesed)) return;
		
		// ... inksacs (the actual check)
		int inksacsPossesed = InventoryUtil.countSimilar(inventory, new ItemStack(Material.INK_SACK));
		if (!this.checkResource(Lang.RESOURCE_INKSACS, inksacsRequired, inksacsPossesed)) return;
		
		// ... feathers (the actual check)
		int feathersPossesed = InventoryUtil.countSimilar(inventory, new ItemStack(Material.FEATHER));
		if (!this.checkResource(Lang.RESOURCE_FEATHERS, feathersRequired, feathersPossesed)) return;
		
		// ... room (the actual check)
		int roomPossesed = InventoryUtil.roomLeft(me.getInventory(), item, roomRequired);
		if (!this.checkResource(Lang.RESOURCE_ROOM, roomRequired, roomPossesed)) return;
		
		// Remove ...
		
		// ... money (real check here)
		if (MassiveBooks.get().economy != null)
		{
			if (!MassiveBooks.get().economy.withdrawPlayer(me.getName(), me.getWorld().getName(), moneyRequired).transactionSuccess())
			{
				sendMessage(String.format(Lang.FAILED_TO_REMOVE_X, Lang.RESOURCE_MONEY));
				return;
			}
		}
		
		// ... books (assumed to succeed)
		inventory.removeItem(new ItemStack(Material.BOOK, booksRequired));
		
		// ... inksacs (assumed to succeed)
		inventory.removeItem(new ItemStack(Material.INK_SACK, inksacsRequired));
		
		// ... feathers (assumed to succeed)
		inventory.removeItem(new ItemStack(Material.FEATHER, feathersRequired));
		
		// ... room (assumed to succeed)
		// (add book copies)
		InventoryUtil.addItemTimes(me.getInventory(), item, times);
		
		// Inform		
		sendMessage(Lang.getSuccessCopyCopies(times));
		sendMessage(Lang.getSuccessCopyResources(moneyRequired, booksRequired, inksacsRequired, feathersRequired));
	}
	
	// -------------------------------------------- //
	// CHECK UTILITIES
	// -------------------------------------------- //
	
	public boolean checkResource(String resourceName, Integer required, Integer possessed)
	{
		int missing = required - possessed;
		if (missing <= 0) return true;
		this.sendCheckFailMessage(resourceName, required, possessed, missing);
		return false;
	}
	
	public void sendCheckFailMessage(String resourceName, Object required, Object possessed, Object missing)
	{
		String notEnoughMessage = String.format(Lang.NOT_ENOUGH_X, resourceName); 
		this.sendMessage(notEnoughMessage);
		String reportMessage = String.format(Lang.REQUIRED_X_POSSESSED_Y_MISSING_Z, required, possessed, missing);
		this.sendMessage(reportMessage);
	}
	
}
