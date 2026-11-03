package me.ripsnortntear.telehome.models;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SavedLocationTest {

    @Test
    void testSerializationRoundTrip() {
        SavedLocation original = new SavedLocation("world_nether", 123.45, 64.0, -987.65, 90.0f, -45.0f);
        Map<String, Object> serialized = original.serialize();

        assertNotNull(serialized);
        assertEquals("world_nether", serialized.get("world"));
        assertEquals(123.45, (Double) serialized.get("x"), 0.001);

        SavedLocation deserialized = SavedLocation.deserialize(serialized);
        assertNotNull(deserialized);
        assertEquals(original.getWorld(), deserialized.getWorld());
        assertEquals(original.getX(), deserialized.getX(), 0.001);
        assertEquals(original.getY(), deserialized.getY(), 0.001);
        assertEquals(original.getZ(), deserialized.getZ(), 0.001);
        assertEquals(original.getYaw(), deserialized.getYaw(), 0.001);
        assertEquals(original.getPitch(), deserialized.getPitch(), 0.001);
    }

    @Test
    void testNullWorldDefaultsToWorld() {
        SavedLocation loc = new SavedLocation(null, 0, 0, 0, 0, 0);
        assertEquals("world", loc.getWorld());
    }
}
