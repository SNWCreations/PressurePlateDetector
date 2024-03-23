package snw.ppd.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.AnaloguePowerable;
import org.bukkit.block.data.Powerable;

public final class BukkitUtils {
    private BukkitUtils() {
    }

    public static boolean isPressurePlate(Material material) {
        return material.name().endsWith("_PRESSURE_PLATE");
    }

    public static boolean activated(Location location) {
        return activated(location.getBlock());
    }

    public static boolean activated(Block block) {
        final Material type = block.getType();
        if (isPressurePlate(type)) {
            boolean powered;
            if (type.name().endsWith("_WEIGHTED_PRESSURE_PLATE")) {
                powered = ((AnaloguePowerable) block.getBlockData()).getPower() > 0;
            } else {
                powered = ((Powerable) block.getBlockData()).isPowered();
            }
            return powered;
        }
        return false;
    }

    public static boolean blockXYZEquals(Location a, Location b) {
        return a.getBlockX() == b.getBlockX() &&
                a.getBlockY() == b.getBlockY() &&
                a.getBlockZ() == b.getBlockZ();
    }
}
