package com.proelioservices.ScheduledAnnouncer;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class AnnouncerEventListener implements Listener {
	
	private final AnnouncerPlugin plugin;
	
	public AnnouncerEventListener(AnnouncerPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	public void onPlayerJoin(PlayerJoinEvent event) {
		
		if(plugin.motdEnabled == true)
		{
			if(event.getPlayer().hasPermission(AnnouncerPermissions.RECEIVER))
			{
				plugin.onLoginMessage(event.getPlayer());
				return;
			}
		}
		else
		{
			return;
		}
	}
}
