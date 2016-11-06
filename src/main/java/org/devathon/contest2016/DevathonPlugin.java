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
                        p.getWorld().spigot().playEffect(display.add(direction), Effect.LAVADRIP, 0, 0, 0, 0, 0, 0, 0, 50);
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
                            new LaserSource(new LaserBeam(1000, 0.25, origin, direction, false)));
                }
                if(!p.isSneaking()){
                    LaserSource.ALL_SOURCES.get(block.getLocation()).toggleFiring();
                    p.sendMessage("Laser firing: "+LaserSource.ALL_SOURCES.get(block.getLocation()).isFiring());
                }
                else {
                    LaserSource.ALL_SOURCES.get(block.getLocation()).getLaserBeam().toggleInvisible();
                    p.sendMessage("Laser invisibility: "+LaserSource.ALL_SOURCES.get(block.getLocation()).getLaserBeam().isInvisible());
                }
                break;
            }
            case BLAZE_POWDER: {
                if(e.getClickedBlock() == null || !e.getClickedBlock().getType().equals(Material.DISPENSER)) return;
                Block block = e.getClickedBlock();
                if(!LaserSource.ALL_SOURCES.containsKey(block.getLocation())) return;
                e.setCancelled(true);
                LaserBeam beam = LaserSource.ALL_SOURCES.get(block.getLocation()).getLaserBeam();
                if(beam.getDamage() <= 0){
                    beam.setDamage(0.5);
                }
                else {
                    beam.setDamage(0.0);
                }
                p.sendMessage("Damage : "+LaserSource.ALL_SOURCES.get(block.getLocation()).getLaserBeam().getDamage());
            }
        }

    }

    @EventHandler
    public void onBreak(BlockBreakEvent e){
        if(LaserSource.ALL_SOURCES.containsKey(e.getBlock().getLocation())){
            LaserSource.ALL_SOURCES.remove(e.getBlock().getLocation()).stopFire();
        }
    }

}

