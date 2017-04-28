package com.massivecraft.massivebooks;

import com.massivecraft.massivebooks.cmd.CmdBook;
import com.massivecraft.massivebooks.engine.EngineAutoUpdate;
import com.massivecraft.massivebooks.engine.EngineItemFrameRotate;
import com.massivecraft.massivebooks.engine.EngineItemFrame;
import com.massivecraft.massivebooks.engine.EngineNewPlayerCommands;
import com.massivecraft.massivebooks.engine.EnginePowertoolReplaceTags;
import com.massivecraft.massivebooks.engine.EnginePowertool;
import com.massivecraft.massivebooks.engine.EnginePowertoolLineReplace;
import com.massivecraft.massivebooks.entity.MBookColl;
import com.massivecraft.massivebooks.entity.MConfColl;
import com.massivecraft.massivecore.MassivePlugin;

public class MassiveBooks extends MassivePlugin 
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static MassiveBooks i;
	public static MassiveBooks get() { return i; }
	public MassiveBooks() { MassiveBooks.i = this; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void onEnableInner()
	{
		// Activate
		this.activate(
			// Coll
			MConfColl.class,
			MBookColl.class,
		
			// Command
			CmdBook.class,
		
			// Engine
			EngineNewPlayerCommands.class,
			EngineItemFrame.class,
			EngineItemFrameRotate.class,
			EngineAutoUpdate.class,
			EnginePowertool.class,
			EnginePowertoolLineReplace.class,
			EnginePowertoolReplaceTags.class
		);
	}
	
}
