package org.devathon.contest2016;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Dispenser;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class DevathonPlugin extends JavaPlugin implements Listener {

    public static DevathonPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // put your disable code here
    }

    public static DevathonPlugin getInstance() {
        return instance;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(!e.getAction().toString().contains("RIGHT") || e.getItem() == null) return;
        switch (e.getItem().getType()){
            case BLAZE_ROD:{
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
                            direction = normal.clone().multiply(direction.clone().dot(normal.clone()))
                                    .multiply(-2).add(direction.clone());
                        }
                        else if(check.getType().isOccluding()) return;
                        p.getWorld().spigot().playEffect(display.add(direction), Effect.COLOURED_DUST, 0, 0, 0, 0, 0, 0, 0, 50);
                    }
                });
                break;
            }
            case STICK: {
                if(e.getClickedBlock() == null || !e.getClickedBlock().getType().equals(Material.DISPENSER)) return;
                Block block = e.getClickedBlock();
                e.setCancelled(true);
                if(!LaserSource.ALL_SOURCES.containsKey(block.getLocation())){
                    Dispenser displ = new Dispenser(block.getType(), block.getData());
                    BlockFace facing1 = displ.getFacing();
                    Vector direction = new Vector(facing1.getModX(), facing1.getModY(), facing1.getModZ());
                    Location origin = block.getLocation().add(0.5, 0.5, 0.5)
                            .add(direction.clone().normalize().multiply(0.51));
                    LaserSource.ALL_SOURCES.put(block.getLocation(),
                            new LaserSource(new LaserBeam(1000, 0.15, origin, direction, false)));
                }
                LaserSource.ALL_SOURCES.get(block.getLocation()).toggleFiring();
            }
        }

    }

    @EventHandler
    public void onBreak(BlockBreakEvent e){
        if(LaserSource.ALL_SOURCES.containsKey(e.getBlock().getLocation())){
            LaserSource.ALL_SOURCES.remove(e.getBlock().getLocation()).stopFire();
        }
    }

    /*@EventHandler
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
    }*/

}

