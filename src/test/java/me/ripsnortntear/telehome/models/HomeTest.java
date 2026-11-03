package me.ripsnortntear.telehome.models;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class HomeTest {

    @Test
    void testHomePreservesOriginalCasing() {
        Home home = new Home("MyBase_1", "world", 10.0, 70.0, -20.0, 180.0f, 0.0f, 1000L);
        assertEquals("MyBase_1", home.getName());
        assertEquals("world", home.getWorld());
        assertEquals(10.0, home.getX(), 0.001);
        assertEquals(70.0, home.getY(), 0.001);
        assertEquals(-20.0, home.getZ(), 0.001);
        assertEquals(1000L, home.getCreatedAt());

        Map<String, Object> map = home.serialize();
        assertEquals("MyBase_1", map.get("name"));
        assertEquals(1000L, map.get("createdAt"));

        Home fromMap = Home.fromMap("fallback", map);
        assertNotNull(fromMap);
        assertEquals("MyBase_1", fromMap.getName());
        assertEquals(1000L, fromMap.getCreatedAt());
    }
}
