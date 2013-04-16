package com.massivecraft.massivebooks.entity;

import com.massivecraft.massivebooks.ConfServer;
import com.massivecraft.massivebooks.Const;
import com.massivecraft.massivebooks.MassiveBooks;
import com.massivecraft.mcore.store.MStore;
import com.massivecraft.mcore.store.SenderColl;

public class MPlayerColl extends SenderColl<MPlayer>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static MPlayerColl i = new MPlayerColl();
	public static MPlayerColl get() { return i; }
	private MPlayerColl()
	{
		super(Const.COLLECTION_BASENAME_PLAYER, MPlayer.class, MStore.getDb(ConfServer.dburi), MassiveBooks.get());
	}
	
}
