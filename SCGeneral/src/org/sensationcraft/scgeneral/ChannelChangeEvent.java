package org.sensationcraft.scgeneral;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author DarkSeraphim
 */
public class ChannelChangeEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();

	private final String name;
	private final ChatChannel ch;

	public ChannelChangeEvent(final String name, final ChatChannel channel)
	{
		this.name = name;
		this.ch = channel;
	}

	public String getPlayerName()
	{
		return this.name;
	}

	public ChatChannel getChannel()
	{
		return this.ch;
	}

	public HandlerList getHandlers()
	{
		return ChannelChangeEvent.handlers;
	}

	public static HandlerList getHandlerList()
	{
		return ChannelChangeEvent.handlers;
	}
}
