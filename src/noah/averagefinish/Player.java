package noah.averagefinish;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single player in a game of AF.
 */
public class Player {
	private int total;
	private int last;
	private String name;
	private String number;
	private List<Ball> balls;

	public Player() {
		total = 0;
		last = 0;
		name = "";
		number = "";
		balls = new ArrayList<Ball>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public void addToTotal(int score) {
		total += score;
		last = score;
	}

	public int getTotal() {
		return total;
	}

	public int getLast() {
		return last;
	}

	public double getAF(int round) {
		if (round <= 0) {
			return 0;
		}
		return ((double)total) / round;
	}

	public void addBall(Ball ball) {
		balls.add(ball);
	}

	public void clearBalls() {
		balls.clear();
	}

	public String ballListToString() {
		StringBuilder s = new StringBuilder();
		s.append("{");
		for (int i = 0; i < balls.size(); i++) {
			s.append(i == 0 ? "" : ", ");
			s.append(balls.get(i));
		}
		s.append("}");
		return s.toString();
	}
}
