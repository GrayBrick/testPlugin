package MainPackage;

import net.kyori.adventure.text.*;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Test extends JavaPlugin implements Listener
{
    public static final File                    configFile = new File("plugins/config.yml");
    public static final String                  blockListKey = "odds";
    public static final Color                   c0 = Color.fromRGB(255, 255, 255);
    public static final Color                   c1 = Color.fromRGB(0, 255, 255);
    public static ItemStack                     customItem;
    public static FileConfiguration             config;
    public static HashMap<Material, Double>     probabilities = new HashMap<>();

    public void onEnable()
    {
        if (!configFile.exists())
        {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        readProbabilities();
        setCustomItem();
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
    }

    public void onDisable()
    {

    }

    @EventHandler
    public void blockBreack(BlockBreakEvent e)
    {
        Player p = e.getPlayer();

        for (Material material : probabilities.keySet())
        {
            if (material.equals(e.getBlock().getType()))
            {
                double probability = probabilities.get(material);

                if (Math.random() <= probability)
                    p.getInventory().addItem(customItem);
                return ;
            }
        }
    }

    public static void readProbabilities()
    {
        if (!config.isConfigurationSection(blockListKey))
            config.createSection(blockListKey);
        for (String key : config.getConfigurationSection(blockListKey).getKeys(false))
        {
            Material material = Material.getMaterial(key);
            probabilities.put(material, config.getConfigurationSection(blockListKey).getDouble(key));
        }
    }

    public static void setCustomItem()
    {
        ItemStack item = new ItemStack(Material.DIAMOND);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(gradientName("Алмаз"));
        item.setItemMeta(meta);
        customItem = item;
    }

    public static Component gradientName(String name)
    {
        int[] c_0 = new int[]{c0.getRed(), c0.getGreen(), c0.getBlue()};
        int[] c_1 = new int[]{c1.getRed(), c1.getGreen(), c1.getBlue()};

        int interval = name.length();

        TextComponent.Builder newName = Component.text();
        for (int i = 0; i < interval; i++)
        {
            double percent = (double) i / interval;

            newName.append(Component.text(name.split("")[i]).color(TextColor.color(
                    (int) (c_0[0] + ((c_1[0] - c_0[0]) * percent)),
                    (int) (c_0[1] + ((c_1[1] - c_0[1]) * percent)),
                    (int) (c_0[2] + ((c_1[2] - c_0[2]) * percent)))));
        }
        return newName.build();
    }
}
