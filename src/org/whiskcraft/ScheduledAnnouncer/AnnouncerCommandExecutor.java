/*     */ package org.whiskcraft.ScheduledAnnouncer;
/*     */ 
/*     */ import java.util.List;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.command.Command;
/*     */ import org.bukkit.command.CommandExecutor;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.plugin.PluginDescriptionFile;
/*     */ 
/*     */ class AnnouncerCommandExecutor
/*     */   implements CommandExecutor
/*     */ {
/*     */   private static final int ENTRIES_PER_PAGE = 7;
/*     */   private final AnnouncerPlugin plugin;
/*     */ 
/*     */   AnnouncerCommandExecutor(AnnouncerPlugin plugin)
/*     */   {
/*  45 */     this.plugin = plugin;
/*     */   }
/*     */ 
/*     */   public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
/*     */   {
/*     */     boolean success;
/*     */     boolean success;
/*  60 */     if ((args.length == 0) || (args[0].equalsIgnoreCase("version")) || (args[0].equalsIgnoreCase("info"))) {
/*  61 */       success = onVersionCommand(sender, command, label, args);
/*     */     }
/*     */     else
/*     */     {
/*     */       boolean success;
/*  62 */       if ("help".equalsIgnoreCase(args[0])) {
/*  63 */         success = onHelpCommand(sender, command, label, args);
/*     */       }
/*     */       else
/*     */       {
/*     */         boolean success;
/*  64 */         if ("add".equalsIgnoreCase(args[0])) {
/*  65 */           success = onAddCommand(sender, command, label, args);
/*     */         }
/*     */         else
/*     */         {
/*     */           boolean success;
/*  66 */           if (("broadcast".equalsIgnoreCase(args[0])) || ("now".equalsIgnoreCase(args[0]))) {
/*  67 */             success = onBroadcastCommand(sender, command, label, args);
/*     */           }
/*     */           else
/*     */           {
/*     */             boolean success;
/*  68 */             if ("list".equalsIgnoreCase(args[0])) {
/*  69 */               success = onListCommand(sender, command, label, args);
/*     */             }
/*     */             else
/*     */             {
/*     */               boolean success;
/*  70 */               if ("delete".equalsIgnoreCase(args[0])) {
/*  71 */                 success = onDeleteCommand(sender, command, label, args);
/*     */               }
/*     */               else
/*     */               {
/*     */                 boolean success;
/*  72 */                 if ("interval".equalsIgnoreCase(args[0])) {
/*  73 */                   success = onIntervalCommand(sender, command, label, args);
/*     */                 }
/*     */                 else
/*     */                 {
/*     */                   boolean success;
/*  74 */                   if ("prefix".equalsIgnoreCase(args[0])) {
/*  75 */                     success = onPrefixCommand(sender, command, label, args);
/*     */                   }
/*     */                   else
/*     */                   {
/*     */                     boolean success;
/*  76 */                     if ("random".equalsIgnoreCase(args[0])) {
/*  77 */                       success = onRandomCommand(sender, command, label, args);
/*     */                     }
/*     */                     else
/*     */                     {
/*     */                       boolean success;
/*  78 */                       if ("enable".equalsIgnoreCase(args[0])) {
/*  79 */                         success = onEnableCommand(sender, command, label, args);
/*     */                       }
/*     */                       else
/*     */                       {
/*     */                         boolean success;
/*  80 */                         if ("reload".equalsIgnoreCase(args[0]))
/*  81 */                           success = onReloadCommand(sender, command, label, args);
/*     */                         else
/*  83 */                           success = false; 
/*     */                       }
/*     */                     }
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*  86 */     if (!success) {
/*  87 */       sender.sendMessage(ChatColor.RED + "Invalid arguments! " + 
/*  88 */         "Use '/announce help' to get a list of valid commands.");
/*     */     }
/*     */ 
/*  91 */     return true;
/*     */   }
/*     */ 
/*     */   boolean onVersionCommand(CommandSender sender, Command command, String label, String[] args)
/*     */   {
/* 104 */     sender.sendMessage(
/* 105 */       String.format("%s === %s [Version %s] === ", new Object[] { ChatColor.LIGHT_PURPLE, this.plugin.getDescription().getName(), 
/* 106 */       this.plugin.getDescription().getVersion() }));
/* 107 */     sender.sendMessage(String.format("Author: %s", new Object[] { this.plugin.getDescription().getAuthors().get(0) }));
/* 108 */     sender.sendMessage(String.format("Website: %s", new Object[] { this.plugin.getDescription().getWebsite() }));
/* 109 */     sender.sendMessage(String.format("Version: %s", new Object[] { this.plugin.getDescription().getVersion() }));
/* 110 */     sender.sendMessage("Features:");
/* 111 */     sender.sendMessage("- InGame Configuration");
/* 112 */     sender.sendMessage("- Permissions Support");
/* 113 */     sender.sendMessage("");
/* 114 */     sender.sendMessage(ChatColor.GRAY + "Use '/announce help' to get a list of valid commands.");
/*     */ 
/* 116 */     return true;
/*     */   }
/*     */ 
/*     */   boolean onHelpCommand(CommandSender sender, Command command, String label, String[] args)
/*     */   {
/* 129 */     sender.sendMessage(String.format("%s === %s [Version %s] === ", new Object[] { ChatColor.LIGHT_PURPLE, 
/* 130 */       this.plugin.getDescription().getName(), this.plugin.getDescription().getVersion() }));
/* 131 */     if (sender.hasPermission("announcer.add")) {
/* 132 */       sender.sendMessage(ChatColor.GRAY + "/announce add <message>" + ChatColor.WHITE + 
/* 133 */         " - Adds a new announcement");
/*     */     }
/* 135 */     if (sender.hasPermission("announcer.broadcast")) {
/* 136 */       sender.sendMessage(ChatColor.GRAY + "/announce broadcast [<index>]" + ChatColor.WHITE + 
/* 137 */         " - Broadcast an announcement NOW");
/*     */     }
/* 139 */     if (sender.hasPermission("announcer.delete")) {
/* 140 */       sender.sendMessage(ChatColor.GRAY + "/announce delete <index>" + ChatColor.WHITE + 
/* 141 */         " - Removes the announcement with the passed index");
/*     */     }
/* 143 */     if (sender.hasPermission("announcer.moderate")) {
/* 144 */       sender.sendMessage(ChatColor.GRAY + "/announce enable [true|false]" + ChatColor.WHITE + 
/* 145 */         " - Enables or disables the announcer.");
/* 146 */       sender.sendMessage(ChatColor.GRAY + "/announce interval <seconds>" + ChatColor.WHITE + 
/* 147 */         " - Sets the seconds between the announcements.");
/* 148 */       sender.sendMessage(ChatColor.GRAY + "/announce prefix <message>" + ChatColor.WHITE + 
/* 149 */         " - Sets the prefix for all announcements.");
/* 150 */       sender.sendMessage(ChatColor.GRAY + "/announce list" + ChatColor.WHITE + " - Lists all announcements");
/* 151 */       sender.sendMessage(ChatColor.GRAY + "/announce random [true|false]" + ChatColor.WHITE + 
/* 152 */         " - Enables or disables the random announcing mode.");
/*     */     }
/* 154 */     if (sender.hasPermission("announcer.admin")) {
/* 155 */       sender.sendMessage(ChatColor.GRAY + "/announce reload" + ChatColor.WHITE + " - Reloads the config.yml");
/*     */     }
/*     */ 
/* 158 */     return true;
/*     */   }
/*     */ 
/*     */   boolean onAddCommand(CommandSender sender, Command command, String label, String[] args)
/*     */   {
/* 171 */     if (sender.hasPermission("announcer.add")) {
/* 172 */       if (args.length > 1) {
/* 173 */         StringBuilder messageToAnnounce = new StringBuilder();
/* 174 */         for (int index = 1; index < args.length; index++) {
/* 175 */           messageToAnnounce.append(args[index]);
/* 176 */           messageToAnnounce.append(" ");
/*     */         }
/* 178 */         this.plugin.addAnnouncement(messageToAnnounce.toString());
/*     */ 
/* 180 */         sender.sendMessage(ChatColor.GREEN + "Added announcement successfully!");
/*     */ 
/* 182 */         if (args.length > 100)
/* 183 */           sender.sendMessage(ChatColor.RED + "This message is too long!");
/*     */       }
/*     */       else
/*     */       {
/* 187 */         sender.sendMessage(ChatColor.RED + "You need to pass a message to announce!");
/*     */       }
/*     */ 
/* 190 */       return true;
/*     */     }
/* 192 */     return false;
/*     */   }
/*     */ 
/*     */   boolean onBroadcastCommand(CommandSender sender, Command command, String label, String[] args)
/*     */   {
/* 206 */     if (sender.hasPermission("announcer.broadcast")) {
/* 207 */       if (args.length == 2)
/*     */         try {
/* 209 */           int index = Integer.parseInt(args[1]);
/*     */ 
/* 211 */           if ((index > 0) && (index <= this.plugin.numberOfAnnouncements())) {
/* 212 */             this.plugin.announce(index); break label193;
/*     */           }
/* 214 */           sender.sendMessage(
/* 215 */             ChatColor.RED + "There isn't any announcement with the passed index!");
/* 216 */           sender.sendMessage(
/* 217 */             ChatColor.RED + "Use '/announce list' to view all available announcements.");
/*     */         }
/*     */         catch (NumberFormatException e) {
/* 220 */           sender.sendMessage(ChatColor.RED + "Index must be a integer!");
/*     */         }
/* 222 */       else if (args.length == 1)
/* 223 */         this.plugin.announce();
/*     */       else {
/* 225 */         sender.sendMessage(ChatColor.RED + "Invalid number of arguments! Use /announce help to view the help!");
/*     */       }
/*     */ 
/* 228 */       label193: return true;
/*     */     }
/* 230 */     return false;
/*     */   }
/*     */ 
/*     */   boolean onListCommand(CommandSender sender, Command command, String label, String[] args)
/*     */   {
/* 244 */     if (sender.hasPermission("announcer.moderate")) {
/* 245 */       if ((args.length == 1) || (args.length == 2)) {
/* 246 */         int page = 1;
/* 247 */         if (args.length == 2) {
/*     */           try {
/* 249 */             page = Integer.parseInt(args[1]);
/*     */           } catch (NumberFormatException e) {
/* 251 */             sender.sendMessage(ChatColor.RED + "Invalid page number!");
/*     */           }
/*     */         }
/* 254 */         sender.sendMessage(ChatColor.GREEN + String.format(" === Announcements [Page %d/%d] ===", new Object[] { Integer.valueOf(page), 
/* 255 */           Integer.valueOf(this.plugin.announcementMessages.size() / 7 + 1) }));
/*     */ 
/* 257 */         int indexStart = Math.abs(page - 1) * 7;
/* 258 */         int indexStop = Math.min(page * 7, this.plugin.announcementMessages.size());
/*     */ 
/* 260 */         for (int index = indexStart + 1; index <= indexStop; index++)
/* 261 */           sender.sendMessage(String.format("%d - %s", new Object[] { Integer.valueOf(index), ChatColorHelper.replaceColorCodes(
/* 262 */             this.plugin.getAnnouncement(index)) }));
/*     */       }
/*     */       else {
/* 265 */         sender.sendMessage(
/* 266 */           ChatColor.RED + "Invalid number of arguments! Use '/announce help' to view the help.");
/*     */       }
/*     */ 
/* 269 */       return true;
/*     */     }
/* 271 */     return false;
/*     */   }
/*     */ 
/*     */   boolean onDeleteCommand(CommandSender sender, Command command, String label, String[] args)
/*     */   {
/* 285 */     if (sender.hasPermission("announcer.delete")) {
/* 286 */       if (args.length == 2)
/*     */         try {
/* 288 */           int index = Integer.parseInt(args[1]);
/*     */ 
/* 290 */           if ((index > 0) && (index <= this.plugin.numberOfAnnouncements())) {
/* 291 */             sender.sendMessage(String.format("%sRemoved announcement: '%s'", new Object[] { ChatColor.GREEN, 
/* 292 */               this.plugin.getAnnouncement(index) }));
/* 293 */             this.plugin.removeAnnouncement(index); break label210;
/*     */           }
/* 295 */           sender.sendMessage(
/* 296 */             ChatColor.RED + "There isn't any announcement with the passed index!");
/* 297 */           sender.sendMessage(
/* 298 */             ChatColor.RED + "Use '/announce list' to view all available announcements.");
/*     */         }
/*     */         catch (NumberFormatException e) {
/* 301 */           sender.sendMessage(ChatColor.RED + "Index must be a integer!");
/*     */         }
/*     */       else {
/* 304 */         sender.sendMessage(ChatColor.RED + "Too many arguments! Use '/announce help' to view the help.");
/*     */       }
/*     */ 
/* 307 */       label210: return true;
/*     */     }
/* 309 */     return false;
/*     */   }
/*     */ 
/*     */   boolean onIntervalCommand(CommandSender sender, Command command, String label, String[] args)
/*     */   {
/* 323 */     if (sender.hasPermission("announcer.moderate")) {
/* 324 */       if (args.length == 2)
/*     */         try {
/* 326 */           this.plugin.setAnnouncementInterval(Integer.parseInt(args[1]));
/*     */ 
/* 328 */           sender.sendMessage(
/* 329 */             ChatColor.GREEN + "Set interval of scheduled announcements successfully!");
/*     */         } catch (NumberFormatException e) {
/* 331 */           sender.sendMessage(ChatColor.RED + "Interval must be a number!");
/*     */         } catch (ArithmeticException e) {
/* 333 */           sender.sendMessage(ChatColor.RED + "Interval must be greater than 0!");
/*     */         }
/* 335 */       else if (args.length == 1)
/* 336 */         sender.sendMessage(String.format("%sPeriod duration is %d", new Object[] { ChatColor.LIGHT_PURPLE, 
/* 337 */           Long.valueOf(this.plugin.getAnnouncementInterval()) }));
/*     */       else {
/* 339 */         sender.sendMessage(
/* 340 */           ChatColor.RED + "Too many arguments! Use '/announce help' to view the help!");
/*     */       }
/*     */ 
/* 343 */       return true;
/*     */     }
/* 345 */     return false;
/*     */   }
/*     */ 
/*     */   boolean onPrefixCommand(CommandSender sender, Command command, String label, String[] args)
/*     */   {
/* 359 */     if (sender.hasPermission("announcer.moderate")) {
/* 360 */       if (args.length > 1) {
/* 361 */         StringBuilder prefixBuilder = new StringBuilder();
/* 362 */         for (int index = 1; index < args.length; index++) {
/* 363 */           prefixBuilder.append(args[index]);
/* 364 */           prefixBuilder.append(" ");
/*     */         }
/* 366 */         this.plugin.setAnnouncementPrefix(prefixBuilder.toString());
/*     */ 
/* 368 */         sender.sendMessage(ChatColor.GREEN + "Set prefix for all announcements successfully!");
/*     */       } else {
/* 370 */         sender.sendMessage(String.format("%sPrefix is %s", new Object[] { ChatColor.LIGHT_PURPLE, 
/* 371 */           ChatColorHelper.replaceColorCodes(this.plugin.getAnnouncementPrefix()) }));
/*     */       }
/*     */ 
/* 374 */       return true;
/*     */     }
/* 376 */     return false;
/*     */   }
/*     */ 
/*     */   boolean onRandomCommand(CommandSender sender, Command command, String label, String[] args)
/*     */   {
/* 390 */     if (sender.hasPermission("announcer.moderate")) {
/* 391 */       if (args.length == 2) {
/* 392 */         if ("true".equalsIgnoreCase(args[1])) {
/* 393 */           this.plugin.setRandom(true);
/* 394 */           sender.sendMessage(ChatColor.GREEN + "Random mode enabled!");
/* 395 */         } else if ("false".equalsIgnoreCase(args[1])) {
/* 396 */           this.plugin.setRandom(false);
/* 397 */           sender.sendMessage(ChatColor.GREEN + "Sequential mode enabled!");
/*     */         } else {
/* 399 */           sender.sendMessage(ChatColor.RED + "Use true or false to enable or disable! " + 
/* 400 */             "Use '/announce help' to view the help.");
/*     */         }
/* 402 */       } else if (args.length == 1) {
/* 403 */         if (this.plugin.isRandom())
/* 404 */           sender.sendMessage(ChatColor.LIGHT_PURPLE + "Random mode is enabled.");
/*     */         else
/* 406 */           sender.sendMessage(ChatColor.LIGHT_PURPLE + "Sequential mode is enabled.");
/*     */       }
/*     */       else {
/* 409 */         sender.sendMessage(
/* 410 */           ChatColor.RED + "Invalid number of arguments! Use '/announce help' to view the help.");
/*     */       }
/*     */ 
/* 413 */       return true;
/*     */     }
/* 415 */     return false;
/*     */   }
/*     */ 
/*     */   boolean onEnableCommand(CommandSender sender, Command command, String label, String[] args)
/*     */   {
/* 429 */     if (sender.hasPermission("announcer.moderate")) {
/* 430 */       if (args.length == 2) {
/* 431 */         if ("true".equalsIgnoreCase(args[1])) {
/* 432 */           this.plugin.setAnnouncerEnabled(true);
/* 433 */           sender.sendMessage(ChatColor.GREEN + "Announcer enabled!");
/* 434 */         } else if ("false".equalsIgnoreCase(args[1])) {
/* 435 */           this.plugin.setAnnouncerEnabled(false);
/* 436 */           sender.sendMessage(ChatColor.GREEN + "Announcer disabled!");
/*     */         } else {
/* 438 */           sender.sendMessage(ChatColor.RED + "Use ture or false to enable or disable! " + 
/* 439 */             "Use '/announce help' to view the help.");
/*     */         }
/* 441 */       } else if (args.length == 1) {
/* 442 */         if (this.plugin.isRandom())
/* 443 */           sender.sendMessage(ChatColor.LIGHT_PURPLE + "Announcer is enabled.");
/*     */         else
/* 445 */           sender.sendMessage(ChatColor.LIGHT_PURPLE + "Announcer is disabled.");
/*     */       }
/*     */       else {
/* 448 */         sender.sendMessage(
/* 449 */           ChatColor.RED + "Invalid number of arguments! Use '/announce help' to view the help.");
/*     */       }
/*     */ 
/* 452 */       return true;
/*     */     }
/* 454 */     return false;
/*     */   }
/*     */ 
/*     */   boolean onReloadCommand(CommandSender sender, Command command, String label, String[] args)
/*     */   {
/* 468 */     if (sender.hasPermission("announcer.moderate")) {
/* 469 */       if (args.length == 1) {
/* 470 */         this.plugin.reloadConfiguration();
/* 471 */         sender.sendMessage(ChatColor.LIGHT_PURPLE + "Configuration reloaded.");
/*     */       } else {
/* 473 */         sender.sendMessage(ChatColor.RED + "Any arguments needed! Use '/announce help' to view the help.");
/*     */       }
/* 475 */       return true;
/*     */     }
/* 477 */     return false;
/*     */   }
/*     */ }

/* Location:           C:\Users\casdorph.gavin.casdorphgavin\Downloads\ScheduledAnnouncer.jar
 * Qualified Name:     org.whiskcraft.ScheduledAnnouncer.AnnouncerCommandExecutor
 * JD-Core Version:    0.6.0
 */