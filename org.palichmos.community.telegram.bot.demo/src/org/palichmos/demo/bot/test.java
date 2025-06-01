package org.palichmos.demo.bot;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class test {

	public static void main(String[] args) {

		int i = 10;
		while (i != 0) {
			rand2DigitNo();
			i--;
		}
	}

	private static void rand2DigitNo() {
		Set<Integer> numbers = new HashSet<>();
		Random random = new Random();

		// Generate 4 unique 3-digit numbers
		while (numbers.size() < 4) {
			int num = random.nextInt(100); // Generates numbers between 100 and 999
			numbers.add(num);
		}

		int sum = 0;
		System.out.println("\nRandom 3-digit numbers:");
		for (int num : numbers) {
			System.out.println(num);
			sum += num;
		}

		System.out.println("Sum = " + sum);

	}

}
