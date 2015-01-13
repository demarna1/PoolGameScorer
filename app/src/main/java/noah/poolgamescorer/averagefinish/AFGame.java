package noah.poolgamescorer.averagefinish;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import noah.poolgamescorer.main.Contact;

public class AFGame {

    private long id;
    private int round;
    private boolean sendTexts;
    private List<AFPlayer> playerList;

    public AFGame() {
        id = -1;
        round = 0;
        sendTexts = false;
        playerList = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public boolean getSendTexts() {
        return sendTexts;
    }

    public void setSendTexts(boolean sendTexts) {
        this.sendTexts = sendTexts;
    }

    public List<AFPlayer> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<AFPlayer> playerList) {
        this.playerList = playerList;
    }

    public AFPlayer getPlayer(int index) {
        return playerList.get(index);
    }

    public int getPlayerCount() {
        return playerList.size();
    }

    /**
     * Sort the player list from smallest to largest total - uses insertion
     * sort.
     */
    public void sortPlayerListByTotal() {
        Collections.sort(playerList, new Comparator<AFPlayer>() {
            @Override
            public int compare(AFPlayer lhs, AFPlayer rhs) {
                if (lhs.getTotal() < rhs.getTotal()) {
                    return -1;
                }
                else if (lhs.getTotal() > rhs.getTotal()) {
                    return 1;
                }
                return 0;
            }
        });
    }

    @Override
    public String toString() {
        return "id = " + id + ", round = " + round + ", sendTexts = " + sendTexts +
                ", playerCount = " + playerList.size();
    }
}
