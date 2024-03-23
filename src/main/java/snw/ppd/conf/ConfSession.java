package snw.ppd.conf;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

import static snw.ppd.util.BukkitUtils.blockXYZEquals;

public final class ConfSession {
    private static final BiMap<UUID, ConfSession> cache = HashBiMap.create();
    private final List<Location> locations = new ArrayList<>();

    public void add(Location location) {
        if (!contains(location)) { // check block pos
            locations.add(location);
        }
    }

    public void remove(Location location) {
        locations.removeIf(it -> blockXYZEquals(it, location));
    }

    public boolean contains(Location location) {
        return locations.stream().anyMatch(it -> blockXYZEquals(it, location));
    }

    public int containedLocations() {
        return locations.size();
    }

    public void saveAndDestroy() {
        new PressurePlateGroup(locations).save();
        destroyWithoutSave();
    }

    public void destroyWithoutSave() {
        cache.inverse().remove(this);
    }

    public static boolean has(Player player) {
        return cache.containsKey(player.getUniqueId());
    }

    public static ConfSession getOrCreate(Player player) {
        return cache.computeIfAbsent(player.getUniqueId(), unused -> new ConfSession());
    }

    public static void destroyAll() {
        cache.clear();
    }
}
