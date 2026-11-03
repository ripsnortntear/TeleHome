package me.ripsnortntear.telehome.testutils;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.Set;

public class TestHelper {

    public static World createMockWorld(String name) {
        return (World) Proxy.newProxyInstance(
                World.class.getClassLoader(),
                new Class<?>[]{World.class},
                (proxy, method, args) -> {
                    if ("getName".equals(method.getName())) {
                        return name;
                    }
                    if ("equals".equals(method.getName())) {
                        if (args != null && args.length > 0 && args[0] != null) {
                            if (args[0] instanceof World other) {
                                return name.equals(other.getName());
                            }
                        }
                        return false;
                    }
                    if ("hashCode".equals(method.getName())) {
                        return name.hashCode();
                    }
                    if ("toString".equals(method.getName())) {
                        return "MockWorld[" + name + "]";
                    }
                    return null;
                }
        );
    }

    public static Player createMockPlayer(boolean bypass, boolean admin, Set<PermissionAttachmentInfo> perms) {
        return (Player) Proxy.newProxyInstance(
                Player.class.getClassLoader(),
                new Class<?>[]{Player.class},
                (proxy, method, args) -> {
                    String methodName = method.getName();
                    if ("hasPermission".equals(methodName) && args != null && args.length > 0) {
                        String perm = (String) args[0];
                        if ("telehome.bypass.limit".equals(perm)) return bypass;
                        if ("telehome.admin".equals(perm)) return admin;
                        return false;
                    }
                    if ("getEffectivePermissions".equals(methodName)) {
                        return perms != null ? perms : Collections.emptySet();
                    }
                    return null;
                }
        );
    }
}
