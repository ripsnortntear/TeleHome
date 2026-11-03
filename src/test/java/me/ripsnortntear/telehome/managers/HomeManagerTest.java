package me.ripsnortntear.telehome.managers;

import me.ripsnortntear.telehome.models.Home;
import me.ripsnortntear.telehome.testutils.TestHelper;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

public class HomeManagerTest {

    @TempDir
    Path tempDir;

    private HomeManager homeManager;

    @BeforeEach
    void setUp() {
        homeManager = new HomeManager(tempDir.toFile(), Logger.getLogger("HomeManagerTest"), 5);
        homeManager.loadHomes();
    }

    @Test
    void testSetAndGetHomeCaseInsensitive() {
        UUID playerUuid = UUID.randomUUID();
        World mockWorld = TestHelper.createMockWorld("world");

        Location loc = new Location(mockWorld, 100.0, 65.0, 200.0, 45.0f, 0.0f);

        boolean existed = homeManager.setHome(playerUuid, "MyCastle", loc);
        assertFalse(existed, "New home should return false for existed");
        assertEquals(1, homeManager.getHomeCount(playerUuid));

        // Exact match
        Home h1 = homeManager.getHome(playerUuid, "MyCastle");
        assertNotNull(h1);
        assertEquals("MyCastle", h1.getName());

        // Lowercase match
        Home h2 = homeManager.getHome(playerUuid, "mycastle");
        assertNotNull(h2);
        assertEquals("MyCastle", h2.getName());

        // Uppercase match
        Home h3 = homeManager.getHome(playerUuid, "MYCASTLE");
        assertNotNull(h3);
        assertEquals("MyCastle", h3.getName());

        // Updating location
        Location loc2 = new Location(mockWorld, 105.0, 65.0, 205.0, 45.0f, 0.0f);
        boolean existedOnUpdate = homeManager.setHome(playerUuid, "mycastle", loc2);
        assertTrue(existedOnUpdate, "Updating home should return true for existed");
        assertEquals(1, homeManager.getHomeCount(playerUuid));
    }

    @Test
    void testDeleteHome() {
        UUID playerUuid = UUID.randomUUID();
        World mockWorld = TestHelper.createMockWorld("world");
        Location loc = new Location(mockWorld, 0, 70, 0);

        homeManager.setHome(playerUuid, "HomeA", loc);
        homeManager.setHome(playerUuid, "HomeB", loc);
        assertEquals(2, homeManager.getHomeCount(playerUuid));

        boolean deleted = homeManager.deleteHome(playerUuid, "homea");
        assertTrue(deleted);
        assertEquals(1, homeManager.getHomeCount(playerUuid));
        assertNull(homeManager.getHome(playerUuid, "homea"));

        assertFalse(homeManager.deleteHome(playerUuid, "nonexistent"));
    }

    @Test
    void testPersistenceAcrossReload() {
        UUID playerUuid = UUID.randomUUID();
        World mockWorld = TestHelper.createMockWorld("world");
        Location loc = new Location(mockWorld, 50.5, 64.0, -100.2, 90.0f, 15.0f);

        homeManager.setHome(playerUuid, "SurvivalBase", loc);

        // Create new HomeManager pointing to same directory
        HomeManager newManager = new HomeManager(tempDir.toFile(), Logger.getLogger("HomeManagerTest"), 5);
        newManager.loadHomes();

        Home loaded = newManager.getHome(playerUuid, "survivalbase");
        assertNotNull(loaded);
        assertEquals("SurvivalBase", loaded.getName());
        assertEquals("world", loaded.getWorld());
        assertEquals(50.5, loaded.getX(), 0.001);
        assertEquals(64.0, loaded.getY(), 0.001);
        assertEquals(-100.2, loaded.getZ(), 0.001);
    }

    @Test
    void testMaxHomesPermissions() {
        // Default
        Player p1 = TestHelper.createMockPlayer(false, false, Collections.emptySet());
        assertEquals(5, homeManager.getMaxHomes(p1));

        // Bypass limit
        Player p2 = TestHelper.createMockPlayer(true, false, Collections.emptySet());
        assertEquals(Integer.MAX_VALUE, homeManager.getMaxHomes(p2));

        // Admin limit
        Player p3 = TestHelper.createMockPlayer(false, true, Collections.emptySet());
        assertEquals(Integer.MAX_VALUE, homeManager.getMaxHomes(p3));

        // Custom permission telehome.max.20
        Player p4Holder[] = new Player[1];
        Player p4 = TestHelper.createMockPlayer(false, false, Collections.emptySet());
        p4Holder[0] = p4;
        PermissionAttachmentInfo permInfo = new PermissionAttachmentInfo(p4, "telehome.max.20", null, true);
        p4 = TestHelper.createMockPlayer(false, false, Set.of(permInfo));
        assertEquals(20, homeManager.getMaxHomes(p4));
    }
}
