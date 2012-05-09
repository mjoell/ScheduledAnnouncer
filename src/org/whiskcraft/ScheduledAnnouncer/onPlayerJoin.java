package org.whiskcraft.ScheduledAnnouncer;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class onPlayerJoin implements Listener {
	
	String string = "Hello World!";
	this.getConfig().set("announcement.MOTD", stringValue);
	
	private final AnnouncerPlugin plugin;
	
	public void onPlayerLogin(PlayerLoginEvent event) {
		event.getPlayer().sendMessage(string);
		
	}

}
