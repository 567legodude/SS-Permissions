package com.ssplugins.ssperm;

import com.ssplugins.ssperm.perm.Group;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

class AttMan {
	
	private Manager manager;
	
	private Map<String, PermissionAttachment> map = new HashMap<>();
	
	AttMan(Manager manager) {
		this.manager = manager;
	}
	
	void clean() {
		for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) remove(it.next());
	}
	
	void remove(String id) {
		PermissionAttachment att = map.get(id);
		if (att != null) att.remove();
		map.remove(id);
	}
	
	private PermissionAttachment get(String id) {
		return map.get(id);
	}
	
	private boolean value(String perm) {
		return !perm.startsWith("-");
	}
	
	private String fix(String perm) {
		if (perm.startsWith("-")) return perm.substring(1);
		return perm;
	}
	
	void setup(Player player) {
		PermissionAttachment att = player.addAttachment(SSPerm.get());
		map.put(player.getUniqueId().toString(), att);
		playerSet(player, manager.getPlayerManager().getPlayer(player).getGroup());
//		manager.getPlayerManager().getPlayer(player).getAllPermissions().forEach(s -> att.setPermission(fix(s), value(s)));
	}
	
	void groupUpdate(String name, String perm, boolean add) {
		Optional<Group> optional = manager.getGroupManager().getGroup(name);
		if (!optional.isPresent()) return;
		Group group = optional.get();
		group.getPlayers().forEach(s -> playerUpdate(s, perm, add));
	}
	
	void playerUpdate(String id, String perm, boolean add) {
		PermissionAttachment att = get(id);
		if (att == null) return;
		if (add) att.setPermission(fix(perm), value(perm));
		else att.unsetPermission(fix(perm));
	}
	
	void playerSet(Player player, Group group) {
		playerSet(player.getUniqueId().toString(), group);
	}
	
	void playerSet(String id, Group group) {
		PermissionAttachment att = get(id);
		if (att == null) return;
		att.getPermissions().forEach((s, set) -> att.unsetPermission(s));
		group.getAllPermissions().forEach(s -> att.setPermission(fix(s), value(s)));
	}
}
