package noah.averagefinish;

/**
 * Wrapper class for a pool ball number.
 *
 * @author noah
 */
public class Ball {
	private final int number;

	public Ball(int number) {
		this.number = number;
	}

	@Override
	public String toString() {
		return number + "";
	}
}
