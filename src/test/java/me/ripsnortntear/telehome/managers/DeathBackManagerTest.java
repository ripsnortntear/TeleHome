package me.ripsnortntear.telehome.managers;

import me.ripsnortntear.telehome.models.SavedLocation;
import me.ripsnortntear.telehome.testutils.TestHelper;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

public class DeathBackManagerTest {

    @TempDir
    Path tempDir;

    private DeathBackManager deathBackManager;

    @BeforeEach
    void setUp() {
        deathBackManager = new DeathBackManager(tempDir.toFile(), Logger.getLogger("DeathBackManagerTest"));
        deathBackManager.loadData();
    }

    @Test
    void testDeathAndBackLocations() {
        UUID playerUuid = UUID.randomUUID();
        World mockWorld = TestHelper.createMockWorld("world_the_end");

        Location deathLoc = new Location(mockWorld, 100, 50, -200, 0, 0);
        deathBackManager.setDeathLocation(playerUuid, deathLoc);
        assertTrue(deathBackManager.isDirty());

        SavedLocation savedDeath = deathBackManager.getDeathLocation(playerUuid);
        assertNotNull(savedDeath);
        assertEquals("world_the_end", savedDeath.getWorld());
        assertEquals(100.0, savedDeath.getX(), 0.001);

        Location backLoc = new Location(mockWorld, 0, 60, 0, 180, 0);
        deathBackManager.setLastLocation(playerUuid, backLoc);

        SavedLocation savedBack = deathBackManager.getLastLocation(playerUuid);
        assertNotNull(savedBack);
        assertEquals(0.0, savedBack.getX(), 0.001);

        // Save to file and reload
        deathBackManager.saveData();
        assertFalse(deathBackManager.isDirty());

        DeathBackManager newManager = new DeathBackManager(tempDir.toFile(), Logger.getLogger("DeathBackManagerTest"));
        newManager.loadData();

        SavedLocation reloadedDeath = newManager.getDeathLocation(playerUuid);
        assertNotNull(reloadedDeath);
        assertEquals("world_the_end", reloadedDeath.getWorld());

        SavedLocation reloadedBack = newManager.getLastLocation(playerUuid);
        assertNotNull(reloadedBack);
        assertEquals(0.0, reloadedBack.getX(), 0.001);

        // Clearing
        newManager.clearDeathLocation(playerUuid);
        assertNull(newManager.getDeathLocation(playerUuid));

        newManager.clearLastLocation(playerUuid);
        assertNull(newManager.getLastLocation(playerUuid));
    }
}
