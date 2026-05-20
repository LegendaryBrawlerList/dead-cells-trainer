import unittest
from unittest.mock import MagicMock, patch
from src.trainer import DeadCellsTrainer
from src.memory_reader import MemoryReader


class TestDeadCellsTrainer(unittest.TestCase):
    """Unit tests for DeadCellsTrainer using mocked memory."""

    def setUp(self):
        self.trainer = DeadCellsTrainer()
        # Mock the memory reader to avoid real process attachment
        self.trainer.memory = MagicMock(spec=MemoryReader)
        self.trainer.attached = True

    def test_set_health_success(self):
        """Test that set_health returns True on success."""
        self.trainer.memory.get_pointer_address.return_value = 0x1234
        self.trainer.memory.write_int.return_value = None
        result = self.trainer.set_health(50)
        self.assertTrue(result)
        self.trainer.memory.write_int.assert_called_once_with(0x1234, 50)

    def test_set_health_failure(self):
        """Test that set_health returns False on exception."""
        self.trainer.memory.get_pointer_address.side_effect = Exception("Memory error")
        result = self.trainer.set_health(50)
        self.assertFalse(result)

    def test_set_gold_success(self):
        """Test that set_gold returns True on success."""
        self.trainer.memory.get_pointer_address.return_value = 0x5678
        result = self.trainer.set_gold(99999)
        self.assertTrue(result)
        self.trainer.memory.write_int.assert_called_once_with(0x5678, 99999)

    def test_set_stats_success(self):
        """Test that set_stats writes three values in sequence."""
        self.trainer.memory.get_pointer_address.return_value = 0x9ABC
        result = self.trainer.set_stats(10, 5, 8)
        self.assertTrue(result)
        calls = [
            unittest.mock.call(0x9ABC, 10),
            unittest.mock.call(0x9AC0, 5),
            unittest.mock.call(0x9AC4, 8),
        ]
        self.trainer.memory.write_int.assert_has_calls(calls)

    def test_attach_false_when_not_attached(self):
        """Test that methods return False when not attached."""
        self.trainer.attached = False
        self.assertFalse(self.trainer.set_health(100))
        self.assertFalse(self.trainer.set_gold(100))
        self.assertFalse(self.trainer.set_stats(1, 1, 1))


if __name__ == "__main__":
    unittest.main()
