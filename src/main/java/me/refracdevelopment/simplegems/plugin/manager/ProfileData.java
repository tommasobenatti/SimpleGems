/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 RefracDevelopment
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package me.refracdevelopment.simplegems.plugin.manager;

import lombok.Getter;
import me.refracdevelopment.simplegems.plugin.SimpleGems;
import me.refracdevelopment.simplegems.plugin.manager.database.DataType;
import me.refracdevelopment.simplegems.plugin.utilities.chat.Color;
import me.refracdevelopment.simplegems.plugin.utilities.files.Files;

import java.sql.SQLException;
import java.util.UUID;

/**
 * Author:  Zachary (Refrac) Baldwin
 * Created: 2021-10-8
 */
@Getter
public class ProfileData {

    private final SimpleGems plugin = SimpleGems.getInstance();
    private final String name;
    private final UUID uuid;

    private final Stat gems = new Stat();

    public ProfileData(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    /**
     * The #load method allows you to
     * load a specified player's data
     */
    public void load() {
        if (plugin.getDataType() == DataType.MYSQL) {
            plugin.getSqlManager().select("SELECT * FROM simplegems WHERE uuid=?", resultSet -> {
                try {
                    if (resultSet.next()) {
                        this.gems.setStat(resultSet.getDouble("gems"));
                    } else {
                        this.save();
                    }
                } catch (SQLException e) {
                    Color.log(e.getMessage());
                }
            }, uuid.toString());
        } else if (plugin.getDataType() == DataType.YAML) {
            this.gems.setStat(Files.getData().getDouble("data." + this.uuid.toString() + ".gems"));
        }
    }

    /**
     * The #save method allows you to
     * save a specified player's data
     */
    public void save() {
        if (plugin.getDataType() == DataType.MYSQL) {
            SimpleGems.getInstance().getSqlManager().execute("INSERT INTO simplegems (name,uuid,gems) VALUES (?,?,?) ON DUPLICATE KEY UPDATE name=?,gems=?",

                    // INSERT
                    name,
                    uuid.toString(),
                    gems.getStat(),
                    // UPDATE
                    name,
                    gems.getStat()

            );
        } else if (plugin.getDataType() == DataType.YAML) {
            Files.getData().set("data." + this.uuid.toString() + ".name", this.name);
            Files.getData().set("data." + this.uuid.toString() + ".gems", this.gems.getStat());
            Files.saveData();
        }
    }
}