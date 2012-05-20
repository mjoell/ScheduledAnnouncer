/*    */ package org.whiskcraft.ScheduledAnnouncer;
/*    */ 
/*    */ import java.util.Random;
/*    */ 
/*    */ class AnnouncerThread extends Thread
/*    */ {
/* 28 */   private final Random randomGenerator = new Random();
/*    */   private final AnnouncerPlugin plugin;
/* 38 */   private int lastAnnouncement = 0;
/*    */ 
/*    */   public AnnouncerThread(AnnouncerPlugin plugin)
/*    */   {
/* 46 */     this.plugin = plugin;
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/* 54 */     if (this.plugin.isAnnouncerEnabled()) {
/* 55 */       if (this.plugin.isRandom()) {
/* 56 */         this.lastAnnouncement = Math.abs(this.randomGenerator.nextInt() % this.plugin.numberOfAnnouncements());
/*    */       }
/* 58 */       else if (++this.lastAnnouncement >= this.plugin.numberOfAnnouncements()) {
/* 59 */         this.lastAnnouncement = 0;
/*    */       }
/*    */ 
/* 63 */       if (this.lastAnnouncement < this.plugin.numberOfAnnouncements())
/* 64 */         this.plugin.announce(this.lastAnnouncement + 1);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\casdorph.gavin.casdorphgavin\Downloads\ScheduledAnnouncer.jar
 * Qualified Name:     org.whiskcraft.ScheduledAnnouncer.AnnouncerThread
 * JD-Core Version:    0.6.0
 */