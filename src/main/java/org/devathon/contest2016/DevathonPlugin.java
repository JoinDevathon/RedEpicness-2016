package org.devathon.contest2016;

import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Dispenser;
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
        if(!e.getAction().toString().contains("RIGHT")
                || !p.getInventory().getItemInMainHand().getType().equals(Material.BLAZE_ROD)) return;
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            Vector direction = p.getEyeLocation().getDirection().normalize().multiply(0.05);
            Location origin = p.getEyeLocation();
            Location display = origin.clone();
            for(int i = 0; i < 2500; i++){
                Block check = display.clone().add(direction).getBlock();
                if(check.getType().equals(Material.IRON_BLOCK)){
                    Vector oldBlock = display.getBlock().getLocation().toVector();
                    Vector newBlock = check.getLocation().toVector();
                    Vector normal = newBlock.subtract(oldBlock).normalize();
                    direction = normal.clone().multiply(direction.clone().dot(normal.clone())).multiply(-2).add(direction.clone());
                }
                else if(check.getType().isOccluding()) return;
                p.getWorld().spigot().playEffect(display.add(direction), Effect.COLOURED_DUST, 0, 0, 0, 0, 0, 0, 0, 50);
            }
        });
    }

    @EventHandler
    public void block(BlockPlaceEvent e){
        if(!e.getBlockPlaced().getType().equals(Material.DISPENSER)) return;
        Dispenser disp = new Dispenser(e.getBlockPlaced().getType(), e.getBlockPlaced().getData());
        BlockFace facing = disp.getFacing();

        AtomicDouble verifiedDistance = new AtomicDouble(-1);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            Vector direction = new Vector(facing.getModX(), facing.getModY(), facing.getModZ()).multiply(0.15);
            Location origin = e.getBlockPlaced().getLocation().add(0.5, 0.5, 0.5)
                    .add(direction.clone().normalize().multiply(0.51));
            Location display = origin.clone();
            double distance = 0;
            for(int i = 0; i < 1000; i++){
                distance = i*0.15;
                Block check = display.add(direction).getBlock();
                if(check.getType().equals(Material.DROPPER)){
                    Dispenser displ = new Dispenser(check.getType(), check.getData());
                    BlockFace facing1 = displ.getFacing();
                    direction = new Vector(facing1.getModX(), facing1.getModY(), facing1.getModZ()).multiply(0.15);
                    display = check.getLocation().add(0.5, 0.5, 0.5).add(direction.clone().normalize().multiply(0.51));
                }
                else if(check.getType().isOccluding()) break;
                if(check.getWorld().getNearbyEntities(display, 0.1, 0.1, 0.1).size() > 0) break;
                e.getBlockPlaced().getWorld().spigot().playEffect(display, Effect.COLOURED_DUST, 0, 0, 0, 0, 0, 0, 0, 50);
            }
            if(verifiedDistance.get() == -1){
                verifiedDistance.set(distance);
            } else if(verifiedDistance.get() != distance){
                e.getBlockPlaced().getWorld().playSound(display, Sound.BLOCK_NOTE_PLING, 1, 1);
            }
            Bukkit.broadcastMessage("Distance: "+distance);
        }, 2, 2);
    }

}

