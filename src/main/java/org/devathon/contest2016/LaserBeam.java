
package org.devathon.contest2016;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.material.Dispenser;
import org.bukkit.util.Vector;

import java.util.Collection;

/**
 * Created by Red_Epicness on 11/6/2016 at 11:41 AM.
 */
public class LaserBeam {

    private final int length;
    private final double density;
    private final Location origin;
    private final Vector direction;
    private boolean invisible;
    private double damage = 0.0;

    public LaserBeam(int length, double density, Location origin, Vector direction, boolean invisible){
        this.length = length;
        this.density = density;
        this.origin = origin;
        this.direction = direction;
        this.invisible = invisible;
    }

    public Location getOrigin() {
        return origin.clone();
    }

    public double fire(){
        Vector direction = this.direction.clone().normalize().multiply(density);
        Location display = origin.clone();
        double distance = 0;

        for(int i = 0; i < length; i++){
            distance = i*density;
            Block check = display.add(direction).getBlock();

            if(check.getType().equals(Material.DROPPER)){
                BlockFace tmpFacing = new Dispenser(check.getType(), check.getData()).getFacing();

                direction = new Vector(tmpFacing.getModX(), tmpFacing.getModY(), tmpFacing.getModZ()).multiply(density);
                display = check.getLocation().add(0.5, 0.5, 0.5).add(direction.clone().normalize().multiply(0.51));
            }
            else if(check.getType().isOccluding())
                break;

            Collection<Entity> nearby = check.getWorld().getNearbyEntities(display, 0.1, 0.1, 0.1);
            if(nearby.size() > 0){
                Bukkit.getScheduler().runTask(DevathonPlugin.getInstance(), () -> {
                    if(damage > 0) nearby.stream().filter(e -> e instanceof LivingEntity)
                            .forEach(e -> ((LivingEntity) e).damage(damage));
                });
                break;
            }

            if(!invisible) origin.getWorld().spigot().playEffect(display, Effect.COLOURED_DUST, 0, 0, 0, 0, 0, 0, 0, 50);
        }

        return distance;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public double getDamage() {
        return damage;
    }

    public boolean isInvisible() {
        return invisible;
    }

    public void toggleInvisible() {
        this.invisible = !this.invisible;
    }
}
