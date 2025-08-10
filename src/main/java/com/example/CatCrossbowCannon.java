package com.example;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class CatCrossbowCannon extends JavaPlugin {

    @Override
    public void onEnable() {
        // 注册事件监听器
        getServer().getPluginManager().registerEvents(new CatCannonListener(this), this);
        getLogger().info("猫咪连弩炮插件已启用！");
    }

    @Override
    public void onDisable() {
        getLogger().info("猫咪连弩炮插件已禁用！");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("catcannon")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "只有玩家才能使用此命令！");
                return true;
            }

            Player player = (Player) sender;
            if (!player.hasPermission("catcannon.use")) {
                player.sendMessage(ChatColor.RED + "你没有权限使用此命令！");
                return true;
            }

            // 给予玩家猫咪连弩炮
            ItemStack catCannon = createCatCannon();
            player.getInventory().addItem(catCannon);
            player.sendMessage(ChatColor.GREEN + "你获得了一个猫咪连弩炮！右键点击发射爆炸猫咪！");
            return true;
        }
        return false;
    }

    /**
     * 创建猫咪连弩炮物品
     * @return 猫咪连弩炮物品
     */
    public ItemStack createCatCannon() {
        ItemStack catCannon = new ItemStack(Material.CROSSBOW);
        ItemMeta meta = catCannon.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "猫咪连弩炮");
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.YELLOW + "右键点击发射爆炸猫咪！");
        lore.add(ChatColor.RED + "警告：猫咪会爆炸！");
        meta.setLore(lore);
        
        // 添加附魔效果使其看起来更特别
        meta.addEnchant(Enchantment.QUICK_CHARGE, 3, true);
        meta.addEnchant(Enchantment.DURABILITY, 3, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        
        // 设置自定义模型数据，用于识别这是猫咪连弩炮
        meta.setCustomModelData(9527);
        
        catCannon.setItemMeta(meta);
        return catCannon;
    }
}