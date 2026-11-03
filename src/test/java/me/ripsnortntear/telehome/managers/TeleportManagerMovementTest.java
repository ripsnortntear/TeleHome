package me.ripsnortntear.telehome.managers;

import me.ripsnortntear.telehome.testutils.TestHelper;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TeleportManagerMovementTest {

    @Test
    void testHasMovedCalculation() {
        World worldA = TestHelper.createMockWorld("world_overworld");
        World worldB = TestHelper.createMockWorld("world_nether");

        Location start = new Location(worldA, 100.0, 64.0, 100.0);
        Location slightMove = new Location(worldA, 100.2, 64.0, 100.1); // distSq = 0.04 + 0.01 = 0.05 <= 0.25
        Location largeMove = new Location(worldA, 101.0, 64.0, 100.0);  // distSq = 1.0 > 0.25
        Location diffWorld = new Location(worldB, 100.0, 64.0, 100.0);

        assertFalse(TeleportManager.hasMoved(start, slightMove), "Minor twitching should not trigger move cancellation");
        assertTrue(TeleportManager.hasMoved(start, largeMove), "Walking a block away should trigger cancellation");
        assertTrue(TeleportManager.hasMoved(start, diffWorld), "Changing world should trigger cancellation");
    }
}
