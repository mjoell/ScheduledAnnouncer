/*
 * Copyright (C) 2011-2012 Mi.Ho.
 * Maintained by MJoell - http://mjoell.com
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details. You should have received a copy of the GNU General Public
 * License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */

package com.mjoell.ScheduledAnnouncer;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class AnnouncerEventListener implements Listener {
	
	private final AnnouncerPlugin plugin;
	
	public AnnouncerEventListener(AnnouncerPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	/**
	 * @param event		On player join for sendMotd()
	 */
	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event) {
		
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
		{
			public void run()
			{
				if(plugin.motdEnabled == true)
				{
					if(event.getPlayer().hasPermission(AnnouncerPermissions.RECEIVER))
					{
						plugin.sendMotd(event.getPlayer());
						return;
					}
				}
				else
				{
					return;
				}
			}
		}, 3); 
	}
}
