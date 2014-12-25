package noah.averagefinish;

public class AFModel {

    private Player[] playerList;
    private boolean sendTexts;
    private int round;

    /**
     * Initializes a new Average Finish game with the given number of players.
     *
     * @param numPlayers
     * 			 the number of players in the game
     * @param sendTexts
     * 			 whether or not texts should be sent to the players
     */
    public AFModel(int numPlayers, boolean sendTexts) {
        this.sendTexts = sendTexts;
        round = 0;
        playerList = new Player[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            playerList[i] = new Player();
        }
    }

    /**
     * Gets the list of players.
     *
     * @return the list of players
     */
    public Player[] getPlayerList() {
        return playerList;
    }

    /**
     * Gets the player at the given index.
     *
     * @param index
     * 			 the index
     * @return the player at the given index
     */
    public Player getPlayer(int index) {
        return playerList[index];
    }

    /**
     * Gets the number of players in the game.
     *
     * @return the number of players in the game
     */
    public int getPlayerCount() {
        return playerList.length;
    }

    /**
     * Increment the round and reset player balls.
     */
    public void newRound() {
        round++;
        for (Player player : playerList) {
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
        for (int i = 0; i < playerList.length; i++) {
            Player p = playerList[i];
            if (sendTexts) {
                String shortName = getShortName(playerContacts[i].getName());
                p.setName(shortName);
            }
            else {
                p.setName(playerContacts[i].getName());
            }
            p.setNumber(playerContacts[i].getNumber());
        }
    }

    /**
     * Sort the player list from smallest to largest total - uses insertion
     * sort.
     */
    public void sortPlayerListByTotal() {
        for (int i = 1; i < playerList.length; i++) {
            Player toSort = playerList[i];
            int j = i;
            while (j > 0 && playerList[j-1].getTotal() > toSort.getTotal()) {
                playerList[j] = playerList[j-1];
                j--;
            }
            playerList[j] = toSort;
        }
    }

    /**
     * Sort the list in reverse order of most recent finishing position.
     */
    //	public void sortPlayerListByLast() {
    //		for (int i = 1; i < playerList.length; i++) {
    //			Player toSort = playerList[i];
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
        if (pieces.length > 0) {
            s.append(' ').append(pieces[1].charAt(0)).append('.');
        }
        return s.toString();
    }
}
