package com.massivecraft.massivebooks.engine;

import com.massivecraft.massivebooks.Lang;
import com.massivecraft.massivebooks.event.MassiveBooksPowertoolReplaceLineEvent;
import com.massivecraft.massivebooks.event.MassiveBooksPowertoolReplaceLinesEvent;
import com.massivecraft.massivebooks.event.MassiveBooksPowertoolReplaceTagEvent;
import com.massivecraft.massivebooks.predicate.PredicateLineShouldExecute;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.collections.MassiveList;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.List;
import java.util.regex.Matcher;

public class EnginePowertoolLineReplace extends Engine
{
	// -------------------------------------------- //
	// INSTANCE
	// -------------------------------------------- //
	
	private static EnginePowertoolLineReplace i = new EnginePowertoolLineReplace();
	public static EnginePowertoolLineReplace get() { return i; }
	
	// -------------------------------------------- //
	// LISTENER: REPLACE LINES/LINE
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onReplaceLinesEventNormal(MassiveBooksPowertoolReplaceLinesEvent event)
	{
		List<String> replacement = new MassiveList<>();
		int lineIndex = 0;
		for (String line : event.getLines())
		{
			lineIndex++;
			MassiveBooksPowertoolReplaceLineEvent innerEvent = new MassiveBooksPowertoolReplaceLineEvent(event.getInteractEvent(), event.getInteractEntityEvent(), line);
			innerEvent.run();
			if (innerEvent.hasError())
			{
				event.setError(Lang.getPowertoolIssueAtLine(lineIndex, innerEvent.getError()));
				event.setCancelled(true);
				return;
			}
			replacement.add(innerEvent.getReplacement());
		}
		event.setReplacement(replacement);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onReplaceLinesEvent(MassiveBooksPowertoolReplaceLinesEvent event)
	{
		if (event.hasError()) return;
		if (!shouldAnyLineBeExecuted(event.getReplacement()))
		{
			event.setError(Lang.POWERTOOL_NO_RUNNABLE_LINES);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onReplaceLineEvent(MassiveBooksPowertoolReplaceLineEvent event)
	{
		// We build the replacement in this string buffer
		StringBuffer replacementBuffer = new StringBuffer();
		
		// A matcher to match all the tags in the line
		Matcher matcher = EnginePowertool.pattern.matcher(event.getLine());
		
		// For each tag we find
		while (matcher.find())
		{
			// The fullmatch is something like "{me.name}"
			//String fullmatch = matcher.group(0);
			
			// The tag is something like "me.name"
			String tag = matcher.group(1);
			
			// Run the tag-replace-event
			MassiveBooksPowertoolReplaceTagEvent tagEvent = new MassiveBooksPowertoolReplaceTagEvent(event.getInteractEvent(), event.getInteractEntityEvent(), tag);
			tagEvent.run();
			
			// I can haz replacement?
			String tagReplacement = tagEvent.getReplacement();
			
			// No replacement?
			if (tagReplacement == null && !tagEvent.hasError())
			{
				tagEvent.setError(Lang.getPowertoolUnknownTag(tag));
			}
			
			// Did we fail
			if (tagEvent.hasError())
			{
				event.setError(tagEvent.getError());
				event.setCancelled(true);
				return;
			}
			
			// Append the replacement
			matcher.appendReplacement(replacementBuffer, tagReplacement);
		}
		
		// Append the rest
		matcher.appendTail(replacementBuffer);
		
		// And finally we set the replacement
		event.setReplacement(replacementBuffer.toString());
	}
	
	private static boolean shouldAnyLineBeExecuted(Iterable<String> lines)
	{
		if (lines == null) return false;
		for (String line : lines)
		{
			if (PredicateLineShouldExecute.get().apply(line)) return true;
		}
		return false;
	}
	
}
