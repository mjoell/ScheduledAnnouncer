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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;



/**
 * Scheduled AnnouncerPlugin for Bukkit.
 *
 * @author MiHo
 */
public class AnnouncerPlugin extends JavaPlugin {

    /**
     * Messages to be announced.
     */
    protected List<String> announcementMessages;

    /**
     * The tag used for the broadcast.
     */
    protected String announcementPrefix;

    /**
     * Period used for announcing.
     */
    protected long announcementInterval;

    /**
     * Flag if the plugin is enabled.
     */
    protected boolean enabled;

    /**
     * Flag if the plugin should output the announcements randomly.
     */
    protected boolean random;
    
    /**
     * Flag if the plugin should output the onPlayerLogin announcement.
     */
    protected boolean motdEnabled;
    
    /**
     * The onPlayerLogin announcement.
     */
    protected String motd;
    
    /**
     * Flag to show prefix on motd or not
     */
    protected boolean motdPrefixEnabled;
    
    /**
     * Thread used to announcing.
     */
    private AnnouncerThread announcerThread;

    /**
     * The logger used to output logging information.
     */
    private Logger logger;
    
    /**
     * The listener used to register events.
     */
    private AnnouncerEventListener listener;
    /**
     * Allocates a new AnnouncerPlugin plugin. Any initialisation code is here. NOTE: Event registration should be done
     * in onEnable not here as all events are unregistered when a plugin is disabled
     */
    public AnnouncerPlugin() {
        super();

        announcerThread = new AnnouncerThread(this);
    }

    /**
     * Called when enabling the plugin.
     */
    public void onEnable() {

        logger = getServer().getLogger();

        // Create default config if not exist yet.
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
        }

        // Load configuration.
        reloadConfiguration();

        // Register the schedule.
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler
            .scheduleSyncRepeatingTask(this, announcerThread, announcementInterval * 20, announcementInterval * 20);
        
        // Register the event listener.
		listener = new AnnouncerEventListener(this);
		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(this.listener, this);

        // Register command executor.
        AnnouncerCommandExecutor announcerCommandExecutor = new AnnouncerCommandExecutor(this);
        getCommand("announce").setExecutor(announcerCommandExecutor);
        getCommand("announcer").setExecutor(announcerCommandExecutor);
        getCommand("an").setExecutor(announcerCommandExecutor);

        // Logging.
        logger.info(String.format("%s is enabled!", getDescription().getFullName()));
        
        try 
        {
        	MetricsLite metricsLite = new MetricsLite(this);
        	metricsLite.start();
        }
        catch(IOException e)
        {
        	logger.info(String.format("%s was unable to send stats to mcstats.org", getDescription().getFullName()));
        }
    }

    /**
     * Called when disabling the plugin.
     */
    public void onDisable() {
        // Logging.
        logger.info(String.format("%s is disabled!", getDescription().getFullName()));
    }

    /**
     * Broadcasts an announcement.
     */
    public void announce() {
        announcerThread.run();
    }

    /**
     * Broadcasts an announcement.
     *
     * @param index 1 based index. (Like in the list output.)
     */
    public void announce(int index) {
        announce(announcementMessages.get(index - 1));
    }

    /**
     * Broadcasts an announcement.
     *
     * @param line the messages to promote.
     */
    public void announce(String line) {
        String[] messages = line.split("%n");
        for (String message : messages) {
            if (message.startsWith("/")) {
                // Execute the command, cause it's a command:
                getServer().dispatchCommand(getServer().getConsoleSender(), message.substring(1));
            } else if (getServer().getOnlinePlayers().size() > 0) {
            	// Get position of key in array
            	int pos = -1;
            	for(int i = 0; i < messages.length; i++) {
            		if(messages[i] == message) {
            			pos = i;
            		}
            	}
            	
            	String messageToSend = null;
            	
            	// If first message, show prefix,
            	if(pos == 0) messageToSend = ChatColorHelper.replaceColorCodes(String.format("%s%s", announcementPrefix, message));
            	// else, don't show the prefix
            	if(pos != 0) messageToSend = ChatColorHelper.replaceColorCodes(String.format("%s", message));
            	
                messageToSend = messageToSend.replaceAll("%maxCount%", Integer.toString(getServer().getMaxPlayers()));
                messageToSend = messageToSend.replaceAll("%playerCount%", Integer.toString(getServer().getOnlinePlayers().size()));
                getServer().broadcast(messageToSend, AnnouncerPermissions.RECEIVER);
            }
        }
    }

    /**
     * Saves the announcements.
     */
    public void saveConfiguration() {
        getConfig().set("announcement.messages", announcementMessages);
        getConfig().set("announcement.interval", announcementInterval);
        getConfig().set("announcement.prefix", announcementPrefix);
        getConfig().set("announcement.enabled", enabled);
        getConfig().set("announcement.random", random);
        getConfig().set("announcement.motdEnabled", motdEnabled);
        getConfig().set("announcement.motd", motd);
        getConfig().set("announcement.motd-show-prefix", motdPrefixEnabled);
        saveConfig();
    }

    /**
     * Reloads the configuration.
     */
    public void reloadConfiguration() {
        reloadConfig();
        announcementPrefix = getConfig().getString("announcement.prefix", "&c[Announcement] ");
        announcementMessages = getConfig().getStringList("announcement.messages");
        announcementInterval = getConfig().getInt("announcement.interval", 1000);
        enabled = getConfig().getBoolean("announcement.enabled", true);
        random = getConfig().getBoolean("announcement.random", false);
        motdEnabled = getConfig().getBoolean("announcement.motdEnabled", false);
        motdPrefixEnabled = getConfig().getBoolean("announcement.motdPrefixEnabled", false);
        motd = getConfig().getString("announcement.motd", "");
    }

    /**
     * @return prefix used for all announcements.
     */
    public String getAnnouncementPrefix() {
        return announcementPrefix;
    }

    /**
     * Sets the prefix used for all announcements.
     *
     * @param announcementPrefix the prefix to use for all announcements.
     */
    public void setAnnouncementPrefix(String announcementPrefix) {
        this.announcementPrefix = announcementPrefix;
        saveConfiguration();
    }

    /**
     * @return the announcement period.
     */
    public long getAnnouncementInterval() {
        return announcementInterval;
    }

    /**
     * Sets the announcement period.
     *
     * @param announcementInterval the period to set.
     */
    public void setAnnouncementInterval(long announcementInterval) {
        this.announcementInterval = announcementInterval;
        saveConfiguration();

        // Register the schedule
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.cancelTasks(this);
        scheduler
            .scheduleSyncRepeatingTask(this, announcerThread, announcementInterval * 20, announcementInterval * 20);
    }

    /**
     * Adds a new announcement.
     *
     * @param message the message to announce.
     */
    public void addAnnouncement(String message) {
        announcementMessages.add(message);
        saveConfiguration();
    }

    /**
     * Returns the Announcement with the passed index.
     *
     * @param index 1 based index, like in /announce list.
     * @return the announcement string.
     */
    public String getAnnouncement(int index) {
        return announcementMessages.get(index - 1);
    }

    /**
     * @return the number of announcements.
     */
    public int numberOfAnnouncements() {
        return announcementMessages.size();
    }

    /**
     * Removes all announcements.
     */
    public void removeAnnouncements() {
        announcementMessages.clear();
        saveConfiguration();
    }

    /**
     * Removes the announcement with the passed index.
     *
     * @param index the index which selects the announcement to remove.
     */
    public void removeAnnouncement(int index) {
        announcementMessages.remove(index - 1);
        saveConfiguration();
    }

    /**
     * @return if announcer is enabled or disabled.
     */
    public boolean isAnnouncerEnabled() {
        return enabled;
    }

    /**
     * Set whether or not the announcer is enabled.
     * @param enabled
     */
    public void setAnnouncerEnabled(boolean enabled) {
        this.enabled = enabled;
        saveConfiguration();
    }

    /**
     * @return if random is enabled or disabled
     */
    public boolean isRandom() {
        return random;
    }
    
    /**
     * @param random
     */
    public void setRandom(boolean random) {
        this.random = random;
        saveConfiguration();
    }
    
    /**
     * @param messageToSay
     */
    public void sayMessage(String messageToSay) {
    	messageToSay = messageToSay.replace("%playerCount%", Integer.toString(getServer().getOnlinePlayers().size()));
    	messageToSay = messageToSay.replace("%maxCount%", Integer.toString(getServer().getMaxPlayers()));
    	
        String messageToSend = ChatColorHelper.replaceColorCodes(String.format("%s%s", announcementPrefix, messageToSay));
        getServer().broadcast(messageToSend, AnnouncerPermissions.RECEIVER);
    }
    
    /**
     * @param player
     */
    public void sendMotd(Player player)
    {
    	if(this.isMotdPrefixEnabled()) {
        	String messageToSend = ChatColorHelper.replaceColorCodes(String.format("%s%s", announcementPrefix, motd));
        	player.sendMessage(messageToSend);
    	} else {
        	String messageToSend = ChatColorHelper.replaceColorCodes(String.format("%s", motd));
        	player.sendMessage(messageToSend);
    	}
    }
    
    /**
     * @param enabled
     */
    public void setMotdEnabled(boolean enabled) {
        this.motdEnabled = enabled;
        saveConfiguration();
    }
    
    /**
     * @param enabled
     */
    public void setMotdPrefixEnabled(boolean enabled) {
    	this.motdPrefixEnabled = enabled;
    	saveConfiguration();
    }
    
    /**
     * @param motd
     */
    public void setMotd(String motd) {
        this.motd = motd;
        saveConfiguration();
    }
    
    /**
     * @return the motd
     */
    public String getMotd() {
    	return motd;
    }
    
    /**
     * @return if the motd is enabled or disabled
     */
    public boolean isMotdEnabled()
    {
    	return motdEnabled;
    }
    
    /**
     * @return if the motd prefix is enabled or disabled
     */
    public boolean isMotdPrefixEnabled() {
    	return motdPrefixEnabled;
    }
}
