package com.massivecraft.massivebooks.entity;

import org.bukkit.inventory.ItemStack;

import com.massivecraft.massivebooks.BookUtil;
import com.massivecraft.mcore.store.Entity;

public class MBook extends Entity<MBook, String>
{	
	// -------------------------------------------- //
	// META
	// -------------------------------------------- //
	
	public static MBook get(Object oid)
	{
		return MBookColl.get().get(oid);
	}
	
	//----------------------------------------------//
	// OVERRIDE
	//----------------------------------------------//
	
	@Override
	public MBook load(MBook that)
	{
		this.item = that.item;
		
		return this;
	}
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	private ItemStack item = null;
	public ItemStack getItem()
	{
		return fixItem(this.item);
	}
	public void setItem(ItemStack item)
	{
		this.item = fixItem(item);
		this.changed();
	}
	
	// -------------------------------------------- //
	// UTIL
	// -------------------------------------------- //
	
	public static ItemStack fixItem(ItemStack item)
	{
		if (!BookUtil.hasBookMeta(item)) return null;
		item = item.clone();
		item.setAmount(1);
		return item;
	}
	
}