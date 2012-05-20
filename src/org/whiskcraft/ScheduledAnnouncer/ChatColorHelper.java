/*    */ package org.whiskcraft.ScheduledAnnouncer;
/*    */ 
/*    */ import org.bukkit.ChatColor;
/*    */ 
/*    */ public class ChatColorHelper
/*    */ {
/*    */   public static String replaceColorCodes(String message)
/*    */   {
/* 32 */     for (ChatColor color : ChatColor.values()) {
/* 33 */       message = message.replaceAll(String.format("&%c", new Object[] { Character.valueOf(color.getChar()) }), color.toString());
/*    */     }
/*    */ 
/* 36 */     return message;
/*    */   }
/*    */ }

/* Location:           C:\Users\casdorph.gavin.casdorphgavin\Downloads\ScheduledAnnouncer.jar
 * Qualified Name:     org.whiskcraft.ScheduledAnnouncer.ChatColorHelper
 * JD-Core Version:    0.6.0
 */