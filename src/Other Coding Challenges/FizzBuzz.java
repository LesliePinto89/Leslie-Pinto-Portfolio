public class FizzBuzz {

	public static void main(String[] args) {

		String fizz = "fizz";
		String buzz = "buzz";
		String fizzBuzz = "fizz buzz";

		for (int i = 0; i < 100; i++) {
			int noRemainder3 = i % 3;
			int noRemainder5 = i % 5;

			if (i == 0) {
				continue;
			}
			if (noRemainder3 == 0 && noRemainder5 == 0) {
				System.out.println(fizzBuzz);
			}

			else if (noRemainder3 == 0) {
				System.out.println(fizz);
			}

			else if (noRemainder5 == 0) {
				System.out.println(buzz);
			}
			
			else {System.out.println(i);}
		}
	}
}
