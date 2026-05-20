from .memory_reader import MemoryReader


class DeadCellsTrainer:
    """Main trainer class with cheat functionalities for Dead Cells."""

    # Example pointer chains (offsets) — these are illustrative, not real.
    # In a real project, these would be found via reverse engineering.
    _HEALTH_PTR = 0x00A1B2C3
    _HEALTH_OFFSETS = [0x10, 0x28, 0x40]
    _GOLD_PTR = 0x00D4E5F6
    _GOLD_OFFSETS = [0x18, 0x30]
    _STATS_PTR = 0x00F0A1B2
    _STATS_OFFSETS = [0x20, 0x48, 0x08]

    def __init__(self):
        self.memory = MemoryReader()
        self.attached = False

    def attach(self) -> bool:
        """Attach to the game process."""
        self.attached = self.memory.attach()
        return self.attached

    def set_health(self, value: int) -> bool:
        """Set player health to a specific value."""
        if not self.attached:
            return False
        try:
            addr = self.memory.get_pointer_address(
                self.memory.base_address + self._HEALTH_PTR, self._HEALTH_OFFSETS
            )
            self.memory.write_int(addr, value)
            return True
        except Exception:
            return False

    def set_gold(self, value: int) -> bool:
        """Set player gold to a specific value."""
        if not self.attached:
            return False
        try:
            addr = self.memory.get_pointer_address(
                self.memory.base_address + self._GOLD_PTR, self._GOLD_OFFSETS
            )
            self.memory.write_int(addr, value)
            return True
        except Exception:
            return False

    def set_stats(self, brutality: int, tactics: int, survival: int) -> bool:
        """Set player stats (brutality, tactics, survival) — writes sequentially."""
        if not self.attached:
            return False
        try:
            base = self.memory.base_address + self._STATS_PTR
            addr = self.memory.get_pointer_address(base, self._STATS_OFFSETS)
            self.memory.write_int(addr, brutality)
            self.memory.write_int(addr + 0x04, tactics)
            self.memory.write_int(addr + 0x08, survival)
            return True
        except Exception:
            return False
