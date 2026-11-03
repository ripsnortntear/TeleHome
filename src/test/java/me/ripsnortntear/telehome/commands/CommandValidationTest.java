package me.ripsnortntear.telehome.commands;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class CommandValidationTest {

    private static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{1,20}$");

    @Test
    void testValidHomeNames() {
        assertTrue(VALID_NAME_PATTERN.matcher("home").matches());
        assertTrue(VALID_NAME_PATTERN.matcher("base_1").matches());
        assertTrue(VALID_NAME_PATTERN.matcher("my-farm").matches());
        assertTrue(VALID_NAME_PATTERN.matcher("NetherPortal").matches());
        assertTrue(VALID_NAME_PATTERN.matcher("12345").matches());
    }

    @Test
    void testInvalidHomeNames() {
        assertFalse(VALID_NAME_PATTERN.matcher("").matches());
        assertFalse(VALID_NAME_PATTERN.matcher("home!").matches());
        assertFalse(VALID_NAME_PATTERN.matcher("base with spaces").matches());
        assertFalse(VALID_NAME_PATTERN.matcher("home.test").matches());
        assertFalse(VALID_NAME_PATTERN.matcher("a_very_long_home_name_that_exceeds_twenty_chars").matches());
    }
}
