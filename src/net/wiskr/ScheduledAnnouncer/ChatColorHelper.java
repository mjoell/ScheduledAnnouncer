/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wiskr.ScheduledAnnouncer;

/**
 *
 * @author Whisk
 */
import org.bukkit.ChatColor;

public class ChatColorHelper
{
  public static String replaceColorCodes(String message)
  {
    for (ChatColor color : ChatColor.values()) {
      message = message.replaceAll(String.format("&%c", new Object[] { Character.valueOf(color.getChar()) }), color.toString());
    }

    return message;
  }
}
