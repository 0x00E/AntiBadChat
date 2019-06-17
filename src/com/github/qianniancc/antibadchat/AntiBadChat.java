package com.github.qianniancc.antibadchat;

import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AntiBadChat extends JavaPlugin implements Listener {
	public boolean isOn = true;

	public void onEnable() {
		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
		File file = new File(getDataFolder(), "config.yml");
		if (!file.exists()) {
			saveDefaultConfig();
		}
		reloadConfig();
		getServer().getPluginManager().registerEvents(this, this);
		getLogger().info("§a§lAntiBadChat成功加载");
	}

	public void onDisable() {
		getLogger().info("§a§lAntiBadChat成功卸载");
	}

	public void reload() {
		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
		File file = new File(getDataFolder(), "config.yml");
		if (!file.exists()) {
			saveDefaultConfig();
		}
		reloadConfig();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) {
		if (lable.equalsIgnoreCase("abc")) {
			if ((sender.isOp()) || (sender.hasPermission("abc.cmd")) || (sender.hasPermission("abc.*"))) {
				if (args.length == 0) {
					sender.sendMessage("§e输入/abc help查看插件帮助");

					return true;
				}
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("reload")) {
						reload();
						sender.sendMessage("§a重载完成！");
						return true;
					}
					if (args[0].equalsIgnoreCase("help")) {
						sender.sendMessage("§a/abc help 插件帮助");
						sender.sendMessage("§a/abc reload  重载插件配置");
						sender.sendMessage("§a/abc on  开启插件");
						sender.sendMessage("§a/abc off  关闭插件");
						sender.sendMessage("§a/abc about 关于插件");
						return true;
					}
					if (args[0].equalsIgnoreCase("about")) {
						sender.sendMessage("§a-----------------------------");
						sender.sendMessage("§e§l本插件由BBS浅念开发");
						sender.sendMessage("§a-----------------------------");
						return true;
					}
					if (args[0].equalsIgnoreCase("on")) {
						this.isOn = true;
						sender.sendMessage("§a§l你开启了插件");
						return true;
					}
					if (args[0].equalsIgnoreCase("off")) {
						this.isOn = false;
						sender.sendMessage("§c§l你关闭了插件");
						return true;
					}
				}
				sender.sendMessage("§e输入/abc help查看插件帮助");
				return true;
			}
			sender.sendMessage("§c你没有权限使用它");
			return true;
		}
		return false;
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		boolean pd = false;
		if ((!player.hasPermission("abc.noabc")) && (!player.isOp()) && (!player.hasPermission("abc.*"))
				&& (this.isOn)) {
			List<String> list = getConfig().getStringList("Regular");
			String msg = event.getMessage().toLowerCase();
			for (int x = 0; x < list.toArray().length; x++) {
				String regexp = (String) list.toArray()[x];
				regexp = regexp.toLowerCase();
				if (msg.matches(regexp)) {
					pd = true;
					break;
				}
				list = getConfig().getStringList("AntiStart");
				for (x = 0; x < list.toArray().length; x++) {
					regexp = (String) list.toArray()[x];
					regexp = regexp.toLowerCase();
					if (msg.startsWith(regexp)) {
						pd = true;
						break;
					}
				}
				list = getConfig().getStringList("AntiEnd");
				for (x = 0; x < list.toArray().length; x++) {
					regexp = (String) list.toArray()[x];
					regexp = regexp.toLowerCase();
					if (msg.endsWith(regexp)) {
						pd = true;
						break;
					}
				}
				list = getConfig().getStringList("AntiHave");
				for (x = 0; x < list.toArray().length; x++) {
					regexp = (String) list.toArray()[x];
					regexp = regexp.toLowerCase();
					if (msg.contains(regexp)) {
						pd = true;
						break;
					}
				}
			}
			if (pd) {
				if (getConfig().getBoolean("Console")) {
					getLogger().info("§c§l玩家 " + player.getName() + "说了脏话，原话:" + event.getMessage());
				}
				if (getConfig().getBoolean("Warn")) {
					String warnmsg = getConfig().getString("WarnMsg");
					String s1 = warnmsg.replace("%chat%", event.getMessage());
					String s2 = ChatColor.translateAlternateColorCodes('&', s1);
					player.sendMessage(s2);
				}
				if (getConfig().getBoolean("Cancel")) {
					event.setCancelled(true);
				} else {
					String re = getConfig().getString("Replace");
					String name = event.getPlayer().getName();
					String s1 = re.replace("%player%", name);
					event.setMessage(s1);
				}
				if (getConfig().getStringList("Command") != null) {
					list = getConfig().getStringList("Command");
					for (int x = 0; x < list.toArray().length; x++) {
						String Command = (String) list.toArray()[x];
						String name = event.getPlayer().getName();
						String s1 = Command.replace("%player%", name);
						Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s1);
					}
				}
			}
		}
	}
}
