package snw.ppd;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import snw.ppd.conf.ConfSession;
import snw.ppd.conf.PressurePlateGroup;

@Command(name = "ppd")
@Permission("op")
public final class RootCommand {

    @Execute(name = "conf")
    void conf(@Context Player sender) {
        if (ConfSession.has(sender)) {
            final ConfSession session = ConfSession.getOrCreate(sender);
            final int locs = session.containedLocations();
            if (locs > 0) {
                session.saveAndDestroy();
                sender.sendMessage(ChatColor.GREEN + "已保存。");
            } else {
                sender.sendMessage(ChatColor.RED + "你至少定义一个点。");
            }
        } else {
            ConfSession.getOrCreate(sender);
            sender.sendMessage(ChatColor.GREEN + "Go!");
        }
    }

    @Execute(name = "track")
    void track(@Context CommandSender sender) {
        final PressurePlateDetector instance = PressurePlateDetector.instance;
        instance.tracking = !instance.tracking;
        if (instance.tracking) {
            sender.sendMessage(ChatColor.GREEN + "现在开始追踪");
        } else {
            sender.sendMessage(ChatColor.RED + "现在停止追踪");
        }
        PressurePlateGroup.resetAllState();
    }
}
