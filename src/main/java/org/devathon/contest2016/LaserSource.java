package org.devathon.contest2016;

import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Red_Epicness on 11/6/2016 at 11:57 AM.
 */
public class LaserSource {

    public static HashMap<Location, LaserSource> ALL_SOURCES = new HashMap<>();

    private boolean fire;
    private LaserBeam laserBeam;
    private int taskID = -1;

    public LaserSource(LaserBeam laserBeam){
        this.laserBeam = laserBeam;
        fire = false;
    }

    public boolean isFiring() {
        return fire;
    }

    public void toggleFiring() {
        if(fire) stopFire();
        else fire();
    }

    public void stopFire() {
        fire = false;
    }

    public void fire(){
        if(fire) return;
        fire = true;
        AtomicDouble distanceVerified = new AtomicDouble(-1);
        AtomicInteger counter = new AtomicInteger(0);
        boolean alwayInvis = laserBeam.isInvisible();
        taskID = Bukkit.getScheduler().scheduleAsyncRepeatingTask(DevathonPlugin.getInstance(), () -> {
            double distance = laserBeam.fire();
            if(!alwayInvis){
                if(counter.get() == 0){
                    if(laserBeam.isInvisible()) laserBeam.toggleInvisible();
                }
                else {
                    if(!laserBeam.isInvisible()) laserBeam.toggleInvisible();
                }
            }
            if(distanceVerified.get() == -1){
                distanceVerified.set(distance);
            }
            else if(distance != distanceVerified.get()){
                laserBeam.getOrigin().getWorld().playSound(laserBeam.getOrigin(), Sound.BLOCK_NOTE_PLING, 1, 1);
            }
            if(counter.incrementAndGet() > 5){
                counter.set(0);
            }
            if(!fire){
                if(laserBeam.isInvisible() != alwayInvis) laserBeam.toggleInvisible();
                Bukkit.getScheduler().cancelTask(taskID);
            }
        }, 1, 1);
    }

    public LaserBeam getLaserBeam() {
        return laserBeam;
    }
}
