/*     */ package org.whiskcraft.ScheduledAnnouncer;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.util.List;
/*     */ import java.util.logging.Logger;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.command.PluginCommand;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ import org.bukkit.plugin.PluginDescriptionFile;
/*     */ import org.bukkit.plugin.java.JavaPlugin;
/*     */ import org.bukkit.scheduler.BukkitScheduler;
/*     */ 
/*     */ public class AnnouncerPlugin extends JavaPlugin
/*     */ {
/*     */   protected List<String> announcementMessages;
/*     */   protected String announcementPrefix;
/*     */   protected long announcementInterval;
/*     */   protected boolean enabled;
/*     */   protected boolean random;
/*     */   private AnnouncerThread announcerThread;
/*     */   private Logger logger;
/*     */ 
/*     */   public AnnouncerPlugin()
/*     */   {
/*  76 */     this.announcerThread = new AnnouncerThread(this);
/*     */   }
/*     */ 
/*     */   public void onEnable()
/*     */   {
/*  84 */     this.logger = getServer().getLogger();
/*     */ 
/*  89 */     if (!new File(getDataFolder(), "config.yml").exists()) {
/*  90 */       saveDefaultConfig();
/*     */     }
/*     */ 
/*  94 */     reloadConfiguration();
/*     */ 
/*  97 */     BukkitScheduler scheduler = getServer().getScheduler();
/*  98 */     scheduler
/*  99 */       .scheduleSyncRepeatingTask(this, this.announcerThread, this.announcementInterval * 20L, this.announcementInterval * 20L);
/*     */ 
/* 102 */     AnnouncerCommandExecutor announcerCommandExecutor = new AnnouncerCommandExecutor(this);
/* 103 */     getCommand("announce").setExecutor(announcerCommandExecutor);
/* 104 */     getCommand("announcer").setExecutor(announcerCommandExecutor);
/*     */ 
/* 107 */     this.logger.info(String.format("%s is enabled!\n", new Object[] { getDescription().getFullName() }));
/*     */   }
/*     */ 
/*     */   public void onDisable()
/*     */   {
/* 115 */     this.logger.info(String.format("%s is disabled!\n", new Object[] { getDescription().getFullName() }));
/*     */   }
/*     */ 
/*     */   public void announce()
/*     */   {
/* 122 */     this.announcerThread.run();
/*     */   }
/*     */ 
/*     */   public void announce(int index)
/*     */   {
/* 131 */     announce((String)this.announcementMessages.get(index - 1));
/*     */   }
/*     */ 
/*     */   public void announce(String line)
/*     */   {
/* 140 */     String[] messages = line.split("&n");
/* 141 */     for (String message : messages)
/* 142 */       if (message.startsWith("/"))
/*     */       {
/* 144 */         getServer().dispatchCommand(getServer().getConsoleSender(), message.substring(1)); } else {
/* 145 */         if (getServer().getOnlinePlayers().length <= 0)
/*     */           continue;
/* 147 */         String messageToSend = ChatColorHelper.replaceColorCodes(String.format("%s%s", new Object[] { this.announcementPrefix, message }));
/* 148 */         getServer().broadcast(messageToSend, "announcer.receiver");
/*     */       }
/*     */   }
/*     */ 
/*     */   public void saveConfiguration()
/*     */   {
/* 157 */     getConfig().set("announcement.messages", this.announcementMessages);
/* 158 */     getConfig().set("announcement.interval", Long.valueOf(this.announcementInterval));
/* 159 */     getConfig().set("announcement.prefix", this.announcementPrefix);
/* 160 */     getConfig().set("announcement.enabled", Boolean.valueOf(this.enabled));
/* 161 */     getConfig().set("announcement.random", Boolean.valueOf(this.random));
/* 162 */     saveConfig();
/*     */   }
/*     */ 
/*     */   public void reloadConfiguration()
/*     */   {
/* 169 */     reloadConfig();
/* 170 */     this.announcementPrefix = getConfig().getString("announcement.prefix", "&c[Announcement] ");
/* 171 */     this.announcementMessages = getConfig().getStringList("announcement.messages");
/* 172 */     this.announcementInterval = getConfig().getInt("announcement.interval", 1000);
/* 173 */     this.enabled = getConfig().getBoolean("announcement.enabled", true);
/* 174 */     this.random = getConfig().getBoolean("announcement.random", false);
/*     */   }
/*     */ 
/*     */   public String getAnnouncementPrefix()
/*     */   {
/* 181 */     return this.announcementPrefix;
/*     */   }
/*     */ 
/*     */   public void setAnnouncementPrefix(String announcementPrefix)
/*     */   {
/* 190 */     this.announcementPrefix = announcementPrefix;
/* 191 */     saveConfig();
/*     */   }
/*     */ 
/*     */   public long getAnnouncementInterval()
/*     */   {
/* 198 */     return this.announcementInterval;
/*     */   }
/*     */ 
/*     */   public void setAnnouncementInterval(long announcementInterval)
/*     */   {
/* 207 */     this.announcementInterval = announcementInterval;
/* 208 */     saveConfiguration();
/*     */ 
/* 211 */     BukkitScheduler scheduler = getServer().getScheduler();
/* 212 */     scheduler.cancelTasks(this);
/* 213 */     scheduler
/* 214 */       .scheduleSyncRepeatingTask(this, this.announcerThread, announcementInterval * 20L, announcementInterval * 20L);
/*     */   }
/*     */ 
/*     */   public void addAnnouncement(String message)
/*     */   {
/* 223 */     this.announcementMessages.add(message);
/* 224 */     saveConfiguration();
/*     */   }
/*     */ 
/*     */   public String getAnnouncement(int index)
/*     */   {
/* 234 */     return (String)this.announcementMessages.get(index - 1);
/*     */   }
/*     */ 
/*     */   public int numberOfAnnouncements()
/*     */   {
/* 241 */     return this.announcementMessages.size();
/*     */   }
/*     */ 
/*     */   public void removeAnnouncements()
/*     */   {
/* 248 */     this.announcementMessages.clear();
/* 249 */     saveConfiguration();
/*     */   }
/*     */ 
/*     */   public void removeAnnouncement(int index)
/*     */   {
/* 258 */     this.announcementMessages.remove(index - 1);
/* 259 */     saveConfiguration();
/*     */   }
/*     */ 
/*     */   public boolean isAnnouncerEnabled()
/*     */   {
/* 265 */     return this.enabled;
/*     */   }
/*     */ 
/*     */   public void setAnnouncerEnabled(boolean enabled) {
/* 269 */     this.enabled = enabled;
/* 270 */     saveConfiguration();
/*     */   }
/*     */ 
/*     */   public boolean isRandom() {
/* 274 */     return this.random;
/*     */   }
/*     */ 
/*     */   public void setRandom(boolean random) {
/* 278 */     this.random = random;
/* 279 */     saveConfiguration();
/*     */   }
/*     */ }

/* Location:           C:\Users\casdorph.gavin.casdorphgavin\Downloads\ScheduledAnnouncer.jar
 * Qualified Name:     org.whiskcraft.ScheduledAnnouncer.AnnouncerPlugin
 * JD-Core Version:    0.6.0
 */