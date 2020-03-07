package tech.hootlab.client;

import java.util.LinkedList;
import java.util.List;
import tech.hootlab.core.Player;

public class ClientModel {

    List<ModelListener> modelListeners = new LinkedList<>();

    String userID;
    List<Player> playerList;
    Player currentPlayer;
    Player dealer;

    public void addModelListener() {

    }

    public void removeModelListener() {

    }
}
