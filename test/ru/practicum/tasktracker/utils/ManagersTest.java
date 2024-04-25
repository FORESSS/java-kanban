package ru.practicum.tasktracker.utils;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ManagersTest {
    @Test
    public void getDefaultHistoryShouldReturnNotNullObject() {

        assertNotNull(Managers.getDefaultHistory());
    }

    @Test
    public void testLoadManagerFromFile() {

        assertNotNull(Managers.loadFromFile(new File("src\\resources\\test.csv")));
    }
}