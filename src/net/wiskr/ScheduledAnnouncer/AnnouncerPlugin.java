/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wiskr.ScheduledAnnouncer;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 *
 * @author Whisk
 */
public class AnnouncerPlugin extends JavaPlugin
{
  protected List<String> announcementMessages;
  protected String announcementPrefix;
  protected String motdMesage;
  protected long announcementInterval;
  protected boolean enabled;
  protected boolean random;
  protected boolean motd;
  private AnnouncerThread announcerThread;
  private Logger logger;

  public AnnouncerPlugin()
  {
    this.announcerThread = new AnnouncerThread(this);
  }

    @Override
  public void onEnable()
  {
    this.logger = getServer().getLogger();

    if (!new File(getDataFolder(), "config.yml").exists()) {
      saveDefaultConfig();
    }

    reloadConfiguration();

    BukkitScheduler scheduler = getServer().getScheduler();
    scheduler
      .scheduleSyncRepeatingTask(this, this.announcerThread, this.announcementInterval * 20L, this.announcementInterval * 20L);

    AnnouncerCommandExecutor announcerCommandExecutor = new AnnouncerCommandExecutor(this);
    getCommand("announce").setExecutor(announcerCommandExecutor);
    getCommand("announcer").setExecutor(announcerCommandExecutor);

    this.logger.info(String.format("%s is enabled!\n", new Object[] { getDescription().getFullName() }));
  }

    @Override
  public void onDisable()
  {
    this.logger.info(String.format("%s is disabled!\n", new Object[] { getDescription().getFullName() }));
  }

  public void announce()
  {
    this.announcerThread.run();
  }

  public void announce(int index)
  {
    announce((String)this.announcementMessages.get(index - 1));
  }

  public void announce(String line)
  {
    String[] messages = line.split("&n");
    for (String message : messages)
      if (message.startsWith("/"))
      {
        getServer().dispatchCommand(getServer().getConsoleSender(), message.substring(1)); } else {
        if (getServer().getOnlinePlayers().length <= 0)
          continue;
        String messageToSend = ChatColorHelper.replaceColorCodes(String.format("%s%s", new Object[] { this.announcementPrefix, message }));
        getServer().broadcast(messageToSend, "announcer.receiver");
      }
  }

  public void saveConfiguration()
  {
    getConfig().set("announcement.messages", this.announcementMessages);
    getConfig().set("announcement.interval", Long.valueOf(this.announcementInterval));
    getConfig().set("announcement.prefix", this.announcementPrefix);
    getConfig().set("announcement.enabled", Boolean.valueOf(this.enabled));
    getConfig().set("announcement.random", Boolean.valueOf(this.random));
    saveConfig();
  }

  public void reloadConfiguration()
  {
    reloadConfig();
    this.announcementPrefix = getConfig().getString("announcement.prefix", "&c[Announcement] ");
    this.announcementMessages = getConfig().getStringList("announcement.messages");
    this.announcementInterval = getConfig().getInt("announcement.interval", 1000);
    this.enabled = getConfig().getBoolean("announcement.enabled", true);
    this.random = getConfig().getBoolean("announcement.random", false);
  }

  public String getAnnouncementPrefix()
  {
    return this.announcementPrefix;
  }

  public void setAnnouncementPrefix(String announcementPrefix)
  {
    this.announcementPrefix = announcementPrefix;
    saveConfig();
  }

  public long getAnnouncementInterval()
  {
    return this.announcementInterval;
  }

  public void setAnnouncementInterval(long announcementInterval)
  {
    this.announcementInterval = announcementInterval;
    saveConfiguration();

    BukkitScheduler scheduler = getServer().getScheduler();
    scheduler.cancelTasks(this);
    scheduler
      .scheduleSyncRepeatingTask(this, this.announcerThread, announcementInterval * 20L, announcementInterval * 20L);
  }

  public void addAnnouncement(String message)
  {
    this.announcementMessages.add(message);
    saveConfiguration();
  }

  public String getAnnouncement(int index)
  {
    return (String)this.announcementMessages.get(index - 1);
  }

  public int numberOfAnnouncements()
  {
    return this.announcementMessages.size();
  }

  public void removeAnnouncements()
  {
    this.announcementMessages.clear();
    saveConfiguration();
  }

  public void removeAnnouncement(int index)
  {
    this.announcementMessages.remove(index - 1);
    saveConfiguration();
  }

  public boolean isAnnouncerEnabled()
  {
    return this.enabled;
  }

  public void setAnnouncerEnabled(boolean enabled) {
    this.enabled = enabled;
    saveConfiguration();
  }

  public boolean isRandom() {
    return this.random;
  }

  public void setRandom(boolean random) {
    this.random = random;
    saveConfiguration();
  }
  
  public boolean isMotdEnabled()
  {
      return this.enabled;
  }
  
  public void setMotdEnabled()
  {
      this.enabled = enabled;
      saveConfiguration();
  }
}