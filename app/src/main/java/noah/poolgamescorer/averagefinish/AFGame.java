package noah.poolgamescorer.averagefinish;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import noah.poolgamescorer.main.Contact;

public class AFGame {

    private long id = -1;
    private int round;
    private boolean sendTexts;
    private List<AFPlayer> playerList;

    /**
     * Initializes a new Average Finish game with the given number of players.
     *
     * @param numPlayers
     * 			 the number of players in the game
     * @param sendTexts
     * 			 whether or not texts should be sent to the players
     */
    public AFGame(int numPlayers, boolean sendTexts) {
        this.sendTexts = sendTexts;
        round = 0;
        playerList = new ArrayList<AFPlayer>();
        for (int i = 0; i < numPlayers; i++) {
            playerList.add(new AFPlayer());
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the list of players.
     *
     * @return the list of players
     */
    public List<AFPlayer> getPlayerList() {
        return playerList;
    }

    /**
     * Gets the player at the given index.
     *
     * @param index
     * 			 the index
     * @return the player at the given index
     */
    public AFPlayer getPlayer(int index) {
        return playerList.get(index);
    }

    /**
     * Gets the number of players in the game.
     *
     * @return the number of players in the game
     */
    public int getPlayerCount() {
        return playerList.size();
    }

    /**
     * Increment the round and reset player balls.
     */
    public void newRound() {
        round++;
        for (AFPlayer player : playerList) {
            player.clearBalls();
        }
    }

    /**
     * Gets the round.
     *
     * @return the round
     */
    public int getRound() {
        return round;
    }

    /**
     * Gets whether the app should send texts.
     *
     * @return whether the app should send texts
     */
    public boolean getSendTexts() {
        return sendTexts;
    }

    /**
     * Sets the player information based on the given list of contacts.
     * <p>
     * Note: playerContacts.length should be equal to number of players.
     * </p>
     *
     * @param playerContacts
     *			 the contact info to set
     */
    public void setNamesAndNumbers(Contact[] playerContacts) {
        for (int i = 0; i < playerList.size(); i++) {
            AFPlayer p = playerList.get(i);
            String shortName = getShortName(playerContacts[i].getName());
            p.setName(shortName);
            p.setNumber(playerContacts[i].getNumber());
        }
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

    /**
     * Sort the list in reverse order of most recent finishing position.
     */
    //	public void sortPlayerListByLast() {
    //		for (int i = 1; i < playerList.length; i++) {
    //			AFPlayer toSort = playerList[i];
    //			int j = i;
    //			while (j > 0 && playerList[j-1].getLast() < toSort.getLast()) {
    //				playerList[j] = playerList[j-1];
    //				j--;
    //			}
    //			playerList[j] = toSort;
    //		}
    //	}

    /**
     * Shortens a name from the full name to the first name + last name initial.
     * <p>
     * Example: Noah DeMarco -> Noah D.
     * </p>
     *
     * @param fullName
     * 			 the name to shorten
     * @return the shortened name
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
