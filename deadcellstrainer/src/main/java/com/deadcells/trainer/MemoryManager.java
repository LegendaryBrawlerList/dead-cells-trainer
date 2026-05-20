package com.deadcells.trainer;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;

/**
 * Manages low-level memory operations for the Dead Cells process.
 * Uses JNA to interface with Windows kernel32 APIs for reading/writing process memory.
 */
public class MemoryManager {

    private final Kernel32 kernel32;
    private WinNT.HANDLE processHandle;
    private int processId;

    /**
     * Initialize the memory manager and open a handle to the Dead Cells process.
     *
     * @param targetProcessName the name of the target process (e.g., "deadcells.exe")
     * @throws IllegalStateException if the process cannot be found or opened
     */
    public MemoryManager(String targetProcessName) {
        this.kernel32 = Kernel32.INSTANCE;
        this.processId = findProcessId(targetProcessName);
        if (processId == -1) {
            throw new IllegalStateException("Dead Cells process not found: " + targetProcessName);
        }
        this.processHandle = kernel32.OpenProcess(
                WinNT.PROCESS_VM_READ | WinNT.PROCESS_VM_WRITE | WinNT.PROCESS_VM_OPERATION,
                false,
                processId
        );
        if (processHandle == null || !processHandle.isValid()) {
            throw new IllegalStateException("Failed to open process handle. Error: " + Native.getLastError());
        }
    }

    /**
     * Simple process ID lookup via enumerating processes (simplified — real impl would use Toolhelp32Snapshot).
     */
    private int findProcessId(String processName) {
        // Placeholder: in a real trainer, iterate processes using CreateToolhelp32Snapshot.
        // For this example, we assume a known PID or mock.
        return 1234; // Replace with actual PID detection
    }

    /**
     * Read a 4-byte integer from the target process at the given address.
     *
     * @param address the memory address to read from
     * @return the integer value read, or -1 on failure
     */
    public int readInt(long address) {
        IntByReference bytesRead = new IntByReference();
        int[] buffer = new int[1];
        boolean success = kernel32.ReadProcessMemory(
                processHandle,
                new Pointer(address),
                buffer,
                4,
                bytesRead
        );
        if (success && bytesRead.getValue() == 4) {
            return buffer[0];
        }
        return -1;
    }

    /**
     * Write a 4-byte integer to the target process at the given address.
     *
     * @param address the memory address to write to
     * @param value   the integer value to write
     * @return true if write succeeded
     */
    public boolean writeInt(long address, int value) {
        IntByReference bytesWritten = new IntByReference();
        int[] buffer = new int[]{value};
        return kernel32.WriteProcessMemory(
                processHandle,
                new Pointer(address),
                buffer,
                4,
                bytesWritten
        ) && bytesWritten.getValue() == 4;
    }

    /**
     * Close the process handle when done.
     */
    public void close() {
        if (processHandle != null && processHandle.isValid()) {
            kernel32.CloseHandle(processHandle);
        }
    }
}
