package snw.ppd;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;
import snw.ppd.conf.ConfSession;
import snw.ppd.conf.PressurePlateGroup;

import static snw.ppd.util.BukkitUtils.activated;
import static snw.ppd.util.BukkitUtils.isPressurePlate;

public final class PressurePlateDetector extends JavaPlugin implements Listener {
    public static PressurePlateDetector instance;
    boolean tracking;
    private LiteCommands<CommandSender> commands;

    @Override
    public void onLoad() {
        instance = this;
        ConfigurationSerialization.registerClass(PressurePlateGroup.class);
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveConfig(); // causes the objects to be loaded

        commands = LiteBukkitFactory.builder()
                .commands(
                        new RootCommand()
                )
                .build(true);

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        instance = null;
        saveConfig();
        if (commands != null) {
            commands.unregister();
        }
        tracking = false;
        PressurePlateGroup.resetAllState();
        ConfSession.destroyAll();
    }



    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        // begin physical
        if (event.getAction() == Action.PHYSICAL) {
            if (tracking) {
                final Block block = event.getClickedBlock();
                if (block == null) {
                    return;
                }
                getServer().getScheduler().runTaskLater(this, () -> {
                    if (activated(block)) {
                        final PressurePlateGroup group = PressurePlateGroup.fromBlock(block);
                        if (group.allActivated()) {
                            if (!group.finished()) {
                                group.finish();
                                final int id = group.getId();
                                Bukkit.broadcastMessage(
                                        ChatColor.GREEN + "" + ChatColor.BOLD +
                                                id + " 号任务点已完成"
                                );
                            }
                        }
                    }
                }, 1L);
            }
        }
        // end physical

        // begin configure
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getHand() == EquipmentSlot.HAND) {
            if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                final Block clickedBlock = event.getClickedBlock();
                assert clickedBlock != null;
                if (isPressurePlate(clickedBlock.getType())) {
                    if (ConfSession.has(player)) {
                        final ConfSession s = ConfSession.getOrCreate(player);
                        final Location location = clickedBlock.getLocation();
                        if (!s.contains(location)) {
                            s.add(location);
                            player.sendMessage(ChatColor.GREEN + "添加了 " + location + " 的记录");
                        } else {
                            s.remove(location);
                            player.sendMessage(ChatColor.RED + "移除了 " + location + " 的记录");
                        }

                    }
                }
            }
        }
        // end configure
    }
}
