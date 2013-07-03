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
		
		if(plugin.loginAnnouncementEnabled == true)
		{
			if(event.getPlayer().hasPermission(AnnouncerPermissions.MODERATOR))
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
