/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wiskr.ScheduledAnnouncer;

/**
 *
 * @author Whisk
 */
import java.util.Random;

class AnnouncerThread extends Thread
{
  private final Random randomGenerator = new Random();
  private final AnnouncerPlugin plugin;
  private int lastAnnouncement = 0;

  public AnnouncerThread(AnnouncerPlugin plugin)
  {
    this.plugin = plugin;
  }

    @Override
  public void run()
  {
    if (this.plugin.isAnnouncerEnabled()) {
      if (this.plugin.isRandom()) {
        this.lastAnnouncement = Math.abs(this.randomGenerator.nextInt() % this.plugin.numberOfAnnouncements());
      }
      else if (++this.lastAnnouncement >= this.plugin.numberOfAnnouncements()) {
        this.lastAnnouncement = 0;
      }

      if (this.lastAnnouncement < this.plugin.numberOfAnnouncements())
        this.plugin.announce(this.lastAnnouncement + 1);
    }
  }
}