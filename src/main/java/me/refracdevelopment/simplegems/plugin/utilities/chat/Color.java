package me.refracdevelopment.simplegems.plugin.utilities.chat;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * Author:  Zachary (Refrac) Baldwin
 * Created: 2021-10-8
 */
public class Color {

    public static String translate(Player player, String source) {
        source = Placeholders.setPlaceholders(player, source);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            return PlaceholderAPI.setPlaceholders(player, Color.translate(source));
        } else return Color.translate(source);
    }

    public static String translate(String source) {
        source = IridiumColorAPI.process(source);

        return ChatColor.translateAlternateColorCodes('&', source);
    }

    public static List<String> translate(List<String> source) {
        source = IridiumColorAPI.process(source);

        return Collections.singletonList(ChatColor.translateAlternateColorCodes('&', String.valueOf(source)));
    }

    public static void sendMessage(CommandSender sender, String source, boolean color, boolean placeholders) {
        if (source.equalsIgnoreCase("%empty%") || source.contains("%empty%")) return;
        if (placeholders) source = Placeholders.setPlaceholders(sender, source);

        if (sender instanceof Player) {
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                source = PlaceholderAPI.setPlaceholders((Player) sender, source);
            }
        }

        if (color) source = translate(source);

        sender.sendMessage(source);
    }
}