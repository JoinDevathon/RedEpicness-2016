package org.devathon.contest2016;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class DevathonPlugin extends JavaPlugin implements Listener{

    @Override
    public void onEnable() {
        System.out.println("loading plugin");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // put your disable code here
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(!e.getAction().equals(Action.RIGHT_CLICK_AIR)
                || !p.getInventory().getItemInMainHand().getType().equals(Material.BLAZE_ROD)) return;
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            Vector direction = p.getEyeLocation().getDirection().normalize().multiply(0.02);
            Location origin = p.getEyeLocation();
            Location display = origin.clone();
            for(int i = 0; i < 5000; i++){
                Block check = display.clone().add(direction).getBlock();
                if(check.getType().equals(Material.IRON_BLOCK)){
                    Vector oldBlock = display.getBlock().getLocation().toVector();
                    Vector newBlock = check.getLocation().toVector();
                    Vector normal = newBlock.subtract(oldBlock).normalize();
                    direction = normal.clone().multiply(direction.clone().dot(normal.clone())).multiply(-2).add(direction.clone());
                }
                else if(check.getType().isOccluding()) return;
                check = display.clone().add(direction).getBlock();
                p.getWorld().spigot().playEffect(display.add(direction), Effect.LAVADRIP, 0, 0, 255, 0, 0, 0, 0, 50);
            }
        });
    }

}

