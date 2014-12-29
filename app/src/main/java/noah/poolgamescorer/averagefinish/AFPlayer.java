package noah.poolgamescorer.averagefinish;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single player in a game of AF.
 */
public class AFPlayer {
    private long id;
    private int total;
    private int last;
    private String name;
    private String number;
    private List<Integer> balls;

    public AFPlayer() {
        id = -1;
        total = 0;
        last = 0;
        name = "";
        number = "";
        balls = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public void setTotal(int total) {
        this.total = total;
    }

    public int getLast() {
        return last;
    }

    public void setLast(int last) {
        this.last = last;
    }

    public double getAF(int round) {
        if (round <= 0) {
            return 0;
        }
        return ((double)total) / round;
    }

    public void addBall(Integer ball) {
        balls.add(ball);
    }

    public void clearBalls() {
        balls.clear();
    }

    public String getNiceStringFromBallList() {
        StringBuilder s = new StringBuilder();
        s.append("{");
        for (int i = 0; i < balls.size(); i++) {
            s.append(i == 0 ? "" : ", ");
            s.append(balls.get(i));
        }
        s.append("}");
        return s.toString();
    }

    public String getDBStringFromBallList() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < balls.size(); i++) {
            s.append(i == 0 ? "" : ",");
            s.append(balls.get(i));
        }
        return s.toString();
    }

    public void setBallListFromDBString(String string) {
        balls = new ArrayList<>();
        if (string == null || string.equals("")) {
            return;
        }
        String[] sballs = string.split(",");
        for (String sball : sballs) {
            Integer ball = Integer.parseInt(sball);
            balls.add(ball);
        }
    }
}
