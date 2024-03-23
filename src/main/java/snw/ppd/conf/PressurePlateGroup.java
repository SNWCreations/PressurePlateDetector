package snw.ppd.conf;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import snw.ppd.PressurePlateDetector;
import snw.ppd.util.BukkitUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static snw.ppd.util.BukkitUtils.blockXYZEquals;

public final class PressurePlateGroup implements ConfigurationSerializable {
    private static final List<PressurePlateGroup> instances = new ArrayList<>();
    @Getter
    private final int id;
    private final List<Location> locations;
    private boolean finish;

    public static PressurePlateGroup fromLoc(Location location) {
        return instances.stream()
                .filter(it -> it.contains(location))
                .findFirst().orElse(null);
    }

    public PressurePlateGroup(int id) {
        this.id = id;
        this.locations = new ArrayList<>();
        instances.add(this);
    }

    public PressurePlateGroup(List<Location> locations) {
        this(instances.size() + 1, locations);
    }

    public PressurePlateGroup(int id, List<Location> locations) {
        this.id = id;
        this.locations = new ArrayList<>(locations);
        instances.add(this);
    }

    public static PressurePlateGroup fromBlock(Block block) {
        return fromLoc(block.getLocation());
    }

    public static void resetAllState() {
        for (PressurePlateGroup group : instances) {
            group.finish = false;
        }
    }

    public boolean contains(Location location) {
        return locations.stream().anyMatch(it -> blockXYZEquals(location, it));
    }

    @Override
    public Map<String, Object> serialize() {
        return ImmutableMap.<String, Object>builder()
                .put("id", id)
                .put("loc", locations)
                .build();
    }

    public static PressurePlateGroup deserialize(Map<String, Object> map) {
        //noinspection unchecked
        return new PressurePlateGroup(
                (int) map.get("id"),
                (List<Location>) map.get("loc")
        );
    }

    public boolean allActivated() {
        return locations.stream().allMatch(BukkitUtils::activated);
    }

    public void save() {
        PressurePlateDetector.instance.getConfig().set("ppg_" + id, this);
    }

    public boolean finished() {
        return finish;
    }

    public void finish() {
        finish = true;
    }
}
