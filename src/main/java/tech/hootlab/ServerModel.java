package tech.hootlab;

import java.util.LinkedList;
import java.util.List;
import tech.hootlab.core.Player;
import tech.hootlab.core.Round;

public class ServerModel {
    public final int ROUND_STAKE = 20;

    List<Player> playerList = new LinkedList<>();
    Round round = new Round();
    Player dealer;

    public List<Player> getPlayerList() {
        return playerList;
    }

    public Round getRound() {
        return round;
    }

    public void startNextRound() {
        round.reset(new LinkedList<>(playerList), dealer, ROUND_STAKE);
    }

    // Players added and removed from the game should be added on next round.
    public void addPlayer(Player player) {
        playerList.add(player);

        // If it's the first player, they're the dealer!
        if (playerList.size() == 1) {
            setDealer(player);
        }

        // We've got enough players to play
        if (playerList.size() == 2) {
            startNextRound();
        }
    }

    public void removePlayer(String ID) {
        for (Player player : playerList) {
            if (player.getID().equals(ID)) {
                removePlayer(player);
            }
        }
    }

    public void removePlayer(Player player) {
        playerList.remove(player);

        if (dealer.equals(player) && playerList.size() > 0) {
            setDealer(playerList.get(0));
        }

        if (round != null) {
            round.removePlayer(player); // Handled separately for scoring and logic reasons
        }
    }

    public List<Player> removeBrokePlayers() {
        List<Player> eliminatedPlayers = new LinkedList<>();
        for (Player player : playerList) {
            if (player.getTokens() == 0) {
                eliminatedPlayers.add(player);
                removePlayer(player);
            }
        }
        return eliminatedPlayers;
    }

    public Player getDealer() {
        return dealer;
    }

    public void setDealer(Player player) {
        dealer = player;
    }
}
