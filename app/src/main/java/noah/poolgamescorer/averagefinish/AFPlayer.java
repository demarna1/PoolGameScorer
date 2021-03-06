package noah.poolgamescorer.averagefinish;

import java.util.ArrayList;
import java.util.List;

public class AFPlayer {
    private long id;
    private String name;
    private String number;
    private int total;
    private int last;
    private List<Integer> balls;

    public AFPlayer() {
        id = -1;
        name = "";
        number = "";
        total = 0;
        last = 0;
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

    /**
     * Shortens a name from the full name to the first name + last name initial.
     * Example: Noah DeMarco -> Noah D.
     */
    private String getShortName(String fullName) {
        String[] pieces = fullName.split(" ");
        StringBuilder s = new StringBuilder();
        s.append(pieces[0]);
        if (pieces.length > 1) {
            s.append(' ').append(pieces[1].charAt(0)).append('.');
        }
        return s.toString();
    }
}
