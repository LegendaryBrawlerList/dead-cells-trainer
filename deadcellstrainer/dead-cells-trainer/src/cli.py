import sys
from .trainer import DeadCellsTrainer


def main():
    """Command-line interface for the Dead Cells Trainer."""
    trainer = DeadCellsTrainer()
    print("Dead Cells Trainer CLI")
    if not trainer.attach():
        print("Error: Dead Cells process not found. Make sure the game is running.")
        sys.exit(1)
    print("Attached to Dead Cells successfully.")

    while True:
        print("\nOptions:")
        print("1. Set Health (max 100)")
        print("2. Set Gold (max 99999)")
        print("3. Set Stats (brutality, tactics, survival)")
        print("4. Exit")
        choice = input("Select option: ").strip()

        if choice == "1":
            try:
                val = int(input("Health value: "))
                if trainer.set_health(val):
                    print(f"Health set to {val}")
                else:
                    print("Failed to set health.")
            except ValueError:
                print("Invalid input.")

        elif choice == "2":
            try:
                val = int(input("Gold value: "))
                if trainer.set_gold(val):
                    print(f"Gold set to {val}")
                else:
                    print("Failed to set gold.")
            except ValueError:
                print("Invalid input.")

        elif choice == "3":
            try:
                b = int(input("Brutality: "))
                t = int(input("Tactics: "))
                s = int(input("Survival: "))
                if trainer.set_stats(b, t, s):
                    print(f"Stats set to B:{b} T:{t} S:{s}")
                else:
                    print("Failed to set stats.")
            except ValueError:
                print("Invalid input.")

        elif choice == "4":
            print("Exiting.")
            break

        else:
            print("Unknown option.")


if __name__ == "__main__":
    main()
