package cz.trsak.onlinelist;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import net.milkbowl.vault.permission.Permission;

public class Main extends JavaPlugin {
    public static Permission perms = null;
    private static boolean Version;

    public Main() {
    }

    @Override
    public void onEnable() {
        getLogger().info("[OnlineList] Loading OnlineList v" + this.getDescription().getVersion() + ".");
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        setupPermissions();

        VersionChecker Version = new VersionChecker(getDescription().getVersion());
        this.Version = Version.Check();

        if (this.Version) {
            getLogger().info("[OnlineList] Is up to date!");
        }
        else {
            getLogger().info("[OnlineList] There's a new update for OnlineList!");
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("[OnlineList] Disabling OnlineList v" + this.getDescription().getVersion() + ".");
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("listreload")) {
            if (sender.hasPermission("onlinelist.reload")||sender.isOp()||sender.hasPermission("onlinelist.*")) {
                this.getConfig();
                this.reloadConfig();
                this.saveConfig();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("ConfigReload")));
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("PermissionsError")));
            }
        }
        else if(cmd.getName().equalsIgnoreCase("list")) {
            if (sender.hasPermission("onlinelist.use")||sender.isOp()||sender.hasPermission("onlinelist.*")) {
                ArrayList<String> groups = new ArrayList<>();
                Map<String, List<String>> playerData = new HashMap<>();
                groups = new ArrayList<>();
                int players = 0;

                for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                    String group = perms.getPrimaryGroup(p);

                    if(!groups.contains(group)) {
                        groups.add(group);
                    }

                    if (playerData.get(group) == null) {
                        playerData.put(group, new ArrayList<>());
                    }

                    playerData.get(group).add(p.getDisplayName());

                    players += 1;
                }

                List<String> sort = getConfig().getStringList("GroupSort");
                int i = 0;

                for (String Group : sort) {
                    if(groups.contains(Group)) {
                        Integer toMoveIndex = groups.indexOf(Group);
                        String toMove = groups.get(toMoveIndex);

                        groups.set(toMoveIndex, groups.get(i));
                        groups.set(i, toMove);
                        i += 1;
                    }
                }

                String finalText = "";

                String playersOnline = players + "";

                finalText += ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("PlayersOnline").replace("%players_online%", playersOnline)) + "\n";

                for (String group : groups) {
                    finalText += ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("GroupsPrefix"));

                    if (this.getConfig().getConfigurationSection("Aliases").getString(group) != null) {
                        finalText += this.getConfig().getConfigurationSection("Aliases").getString(group);
                    }
                    else {
                        finalText += group;
                    }
                    int size = playerData.get(group).size();

                    String inGroup = Integer.toString(size);

                    finalText += ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("GroupsSufix")) + "(" + inGroup + ")" + ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("AfterGroups"));

                    for (String user : playerData.get(group)) {
                        finalText += ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("PlayersPrefix")) + user + ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("PlayersSufix"));
                        if (--size != 0) {
                            finalText += ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("PlayersDivider"));
                        }
                    }

                    finalText += "\n";
                }

                sender.sendMessage(finalText);
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("PermissionsError")));
            }
        }

        if (!this.Version) {
            if (sender.hasPermission("onlinelist.version")||sender.hasPermission("onlinelist.*")) {
                sender.sendMessage(ChatColor.GOLD + "[OnlineList] " + ChatColor.YELLOW + "New version is available!");
            }
        }

        return false;
    }

}
