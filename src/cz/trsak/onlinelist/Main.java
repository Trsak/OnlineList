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

    public Main() {
    }

    @Override
    public void onEnable() {
        System.out.print("[OnlineList] Loading OnlineList v" + this.getDescription().getVersion() + ".");
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        setupPermissions();
    }

    @Override
    public void onDisable() {
        System.out.print("[OnlineList] Disabling OnlineList v" + this.getDescription().getVersion() + ".");
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("listreload")) {
            if (sender.hasPermission("onlinelist.use")) {
                this.getConfig();
                this.reloadConfig();
                this.saveConfig();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("ConfigReload")));
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("PermissionsError")));
            }
        }
        else if(cmd.getName().equalsIgnoreCase("list")) {
            if (sender.hasPermission("onlinelist.use")) {
                ArrayList<String> groups = new ArrayList<String>();
                Map<String, List<String>> playerData = new HashMap<>();
                groups = new ArrayList<String>();
                int players = 0;

                for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                    String group = perms.getPrimaryGroup(p);

                    if(!groups.contains(group)) {
                        groups.add(group);
                    }

                    if (playerData.get(group) == null) {
                        playerData.put(group, new ArrayList<String>());
                    }

                    playerData.get(group).add(p.getDisplayName());

                    players += 1;
                }

                String finalText = "";

                String playersOnline = players + "";

                finalText += ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("PlayersOnline").replace("%players_online%", playersOnline)) + "\n";

                for (String group : groups) {
                    finalText += ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("PlayersNameColor")) + group + ChatColor.WHITE + ": " + ChatColor.YELLOW;

                    int size = playerData.get(group).size();
                    for (String user : playerData.get(group)) {
                        finalText += user;
                        if (--size != 0) {
                            finalText += ", ";
                        }
                    }

                    finalText += "\n";
                }

                sender.sendMessage(finalText);
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("PermissionsError")));
            }
        }

        return false;
    }

}
