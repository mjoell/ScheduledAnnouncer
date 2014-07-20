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
