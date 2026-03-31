package bank.ui;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Read {
	private static Scanner scanner = new Scanner(System.in);

	public static double readDouble(String message) {
		while (true) {
			try {
				System.out.print(message);
				double value = scanner.nextDouble();
				scanner.nextLine();

				if (value >= 0) {
					return value;
				} else {
					System.out.println("❌ Error: Amount cannot be negative.");
				}

			} catch (InputMismatchException e) {
				// TODO: handle exception
				System.out.println("❌ Error: You must enter a valid number.");
				scanner.nextLine(); // CRITICAL: This delete the bad input out of the memory
			}
		}
	}

	public static int readInt(String message) {
		while (true) {
			try {
				System.out.print(message);
				int value = scanner.nextInt();
				scanner.nextLine();
				return value;
			} catch (InputMismatchException e) {
				// TODO: handle exception
				System.out.println("❌ Error: You have to write an integer number (e.g:5");
				scanner.nextLine(); // CLEAR
			}
		}
	}

	public static String readString(String message) {

		while (true) {
			System.out.println(message);
			String input = scanner.nextLine().trim();
			if (!input.isEmpty()) {
				return input;
			}
			System.out.println("❌ Error: You must enter a name.");
		}

	}

}
