import pymem
import pymem.process


class MemoryReader:
    """Handles reading and writing to Dead Cells process memory."""

    def __init__(self, process_name: str = "deadcells.exe"):
        self.process_name = process_name
        self.pm = None
        self.base_address = None

    def attach(self) -> bool:
        """Attach to the Dead Cells process."""
        try:
            self.pm = pymem.Pymem(self.process_name)
            self.base_address = pymem.process.module_from_name(
                self.pm.process_handle, self.process_name
            ).lpBaseOfDll
            return True
        except pymem.exception.ProcessNotFound:
            return False

    def read_int(self, address: int) -> int:
        """Read a 4-byte integer from memory."""
        if self.pm is None:
            raise RuntimeError("Not attached to process")
        return self.pm.read_int(address)

    def write_int(self, address: int, value: int) -> None:
        """Write a 4-byte integer to memory."""
        if self.pm is None:
            raise RuntimeError("Not attached to process")
        self.pm.write_int(address, value)

    def get_pointer_address(self, base: int, offsets: list) -> int:
        """Resolve a multi-level pointer chain."""
        addr = self.read_int(base)
        for offset in offsets[:-1]:
            addr = self.read_int(addr + offset)
        return addr + offsets[-1]
