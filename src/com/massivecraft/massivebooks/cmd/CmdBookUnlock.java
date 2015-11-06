package com.massivecraft.massivebooks.cmd;

import org.bukkit.inventory.ItemStack;

import com.massivecraft.massivebooks.BookUtil;
import com.massivecraft.massivebooks.Lang;
import com.massivecraft.massivebooks.Perm;
import com.massivecraft.massivebooks.cmd.type.TypeBookInHand;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;

public class CmdBookUnlock extends MassiveBooksCommand
{
	public CmdBookUnlock()
	{
		// Aliases
		this.addAliases("unlock");
		
		// Requirements
		this.addRequirements(RequirementHasPerm.get(Perm.UNLOCK.node));
		this.addRequirements(RequirementIsPlayer.get());
	}
	
	@Override
	public void perform() throws MassiveException
	{
		ItemStack item = TypeBookInHand.getEither().read(sender);
		
		if (BookUtil.isUnlocked(item))
		{
			message(Lang.getSameUnlock(item));
			return;
		}
		
		if (!BookUtil.isAuthorEquals(item, sender) && !Perm.UNLOCK_OTHER.has(sender, true)) return;
		
		ItemStack before = item.clone();
		BookUtil.unlock(item);
		me.setItemInHand(item);
		
		message(Lang.getAlterUnlock(before));
	}

}
