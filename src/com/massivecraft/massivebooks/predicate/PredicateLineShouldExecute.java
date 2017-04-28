package com.massivecraft.massivebooks.predicate;

import com.massivecraft.massivecore.predicate.Predicate;

public class PredicateLineShouldExecute implements Predicate<String>
{
	// -------------------------------------------- //
	// INSTANCE
	// -------------------------------------------- //
	
	private static PredicateLineShouldExecute i = new PredicateLineShouldExecute();
	public static PredicateLineShouldExecute get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public boolean apply(String type)
	{
		if (type == null) return false;
		if (type.isEmpty()) return false;
		if (type.startsWith("#")) return false;
		return true;
	}
	
}
