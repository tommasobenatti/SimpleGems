
package me.refracdevelopment.simplegems.leaderboard;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.refracdevelopment.simplegems.SimpleGems;
import me.refracdevelopment.simplegems.data.DataType;
import me.refracdevelopment.simplegems.data.ProfileData;
import me.refracdevelopment.simplegems.utilities.Methods;
import me.refracdevelopment.simplegems.utilities.chat.Color;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.SQLException;
import java.util.TreeMap;

@RequiredArgsConstructor
@Getter
@Setter
public class Leaderboard {

    private final SimpleGems plugin;
    private final TreeMap<String, TopGems> topGems;

    public void load() {
        this.topGems.clear();

        if (this.plugin.getDataType() == DataType.MYSQL) {

            // get top 10 gems from database in order of highest to lowest to the map
            this.plugin.getSqlManager().select("SELECT * FROM SimpleGems ORDER BY gems DESC LIMIT 10", resultSet -> {
                try {
                // order from highest to lowest
                    while (resultSet.next()) {
                        String name = resultSet.getString("name");
                        long gems = resultSet.getLong("gems");
                        this.topGems.putIfAbsent(name, new TopGems(name, gems));
                    }
                } catch (SQLException exception) {
                    Color.log(exception.getMessage());
                }
            });

            ValueComparator<String> vc = new ValueComparator<>(this.topGems);
            TreeMap<String, TopGems> sorted = new TreeMap<>(vc);
            sorted.putAll(this.topGems);
            this.topGems.clear();
            this.topGems.putAll(sorted);
        } else if (this.plugin.getDataType() == DataType.YAML) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                ProfileData profileData = plugin.getProfileManager().getProfile(player.getUniqueId()).getData();
                String name = player.getName();
                long gems = profileData.getGems().getAmount();
                if (!this.topGems.containsKey(name)) {
                    this.topGems.putIfAbsent(name, new TopGems(name, gems));
                }
            });

            for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
                String name = op.getName();
                long gems = Methods.getOfflineGems(op);
                if (!this.topGems.containsKey(name)) {
                    this.topGems.putIfAbsent(name, new TopGems(name, gems));
                }
            }

            ValueComparator<String> vc = new ValueComparator<>(this.topGems);
            TreeMap<String, TopGems> sorted = new TreeMap<>(vc);
            sorted.putAll(this.topGems);
            this.topGems.clear();
            this.topGems.putAll(sorted);
        }
    }
}