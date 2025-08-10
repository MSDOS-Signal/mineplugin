package com.example;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Cat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class CatCannonListener implements Listener {

    private final CatCrossbowCannon plugin;

    public CatCannonListener(CatCrossbowCannon plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        // 检查是否是右键点击且手持物品是猫咪连弩炮
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) 
                && item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            
            // 通过自定义模型数据识别猫咪连弩炮
            if (meta.hasCustomModelData() && meta.getCustomModelData() == 9527) {
                event.setCancelled(true); // 取消原版弩的射击行为
                
                // 发射爆炸猫咪
                launchExplosiveCat(player);
                
                // 播放发射音效
                player.playSound(player.getLocation(), Sound.ITEM_CROSSBOW_SHOOT, 1.0f, 0.5f);
                player.playSound(player.getLocation(), Sound.ENTITY_CAT_AMBIENT, 1.0f, 1.5f);
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        // 检查是否是爆炸猫咪
        if (event.getEntity().hasMetadata("explosive_cat")) {
            // 获取猫咪实体
            Cat cat = (Cat) event.getEntity().getMetadata("explosive_cat").get(0).value();
            
            // 在猫咪位置创建爆炸
            Location explosionLocation = cat.getLocation();
            cat.getWorld().createExplosion(explosionLocation, 2.0f, false, true);
            
            // 移除猫咪
            cat.remove();
        }
    }

    /**
     * 发射爆炸猫咪
     * @param player 发射猫咪的玩家
     */
    private void launchExplosiveCat(Player player) {
        // 在玩家位置生成猫咪
        Location spawnLocation = player.getEyeLocation();
        Cat cat = (Cat) player.getWorld().spawnEntity(spawnLocation, EntityType.CAT);
        
        // 设置猫咪属性
        cat.setCustomName(ChatColor.RED + "爆炸猫咪");
        cat.setCustomNameVisible(true);
        cat.setInvulnerable(true); // 使猫咪无敌，防止被提前杀死
        
        // 设置猫咪的飞行方向（与玩家视线方向一致）
        Vector direction = player.getLocation().getDirection().multiply(2.0);
        cat.setVelocity(direction);
        
        // 给猫咪添加元数据，标记为爆炸猫咪
        cat.setMetadata("explosive_cat", new FixedMetadataValue(plugin, cat));
        
        // 设置猫咪在5秒后爆炸（如果没有撞到任何东西）
        new BukkitRunnable() {
            @Override
            public void run() {
                if (cat.isValid()) { // 检查猫咪是否仍然存在
                    // 创建爆炸
                    cat.getWorld().createExplosion(cat.getLocation(), 2.0f, false, true);
                    // 移除猫咪
                    cat.remove();
                }
            }
        }.runTaskLater(plugin, 100L); // 100 ticks = 5 seconds
    }
}