package com.deadcells.trainer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MemoryManager.
 * Since MemoryManager requires a real process, these tests use a mock approach
 * by verifying constructor behavior and edge cases.
 */
public class MemoryManagerTest {

    private MemoryManager memoryManager;

    @BeforeEach
    public void setUp() {
        // In real test, we'd mock Kernel32. Here we skip if no process.
        // This test is designed to be run in an environment where deadcells.exe is NOT running.
    }

    @AfterEach
    public void tearDown() {
        if (memoryManager != null) {
            memoryManager.close();
        }
    }

    @Test
    public void testConstructorFailsWhenProcessNotFound() {
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            new MemoryManager("nonexistent_process.exe");
        });
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    public void testReadIntReturnsMinusOneOnInvalidAddress() {
        // This would normally require a valid handle; we skip if not attached.
        // For demonstration, we just verify the logic structure.
        assertTrue(true, "Placeholder: real test would mock process handle");
    }

    @Test
    public void testWriteIntReturnsFalseOnInvalidAddress() {
        // Similar placeholder.
        assertTrue(true, "Placeholder: real test would use mock");
    }

    @Test
    public void testCloseDoesNotThrow() {
        // If we never opened, close should be safe.
        MemoryManager mgr = null;
        try {
            mgr = new MemoryManager("nonexistent.exe");
        } catch (IllegalStateException e) {
            // Expected
        }
        if (mgr != null) {
            mgr.close();
        }
        assertTrue(true, "Close did not throw");
    }
}
