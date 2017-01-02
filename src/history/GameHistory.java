package history;

import entity.tile.Tile;
import entity.animal.Animal;
import exception.CannotRedoException;
import exception.CannotUndoException;

/**
 * Created by lifengshuang on 9/26/16.
 */
public class GameHistory {

    private static GameHistory gameHistory;

    private Node currentNode;

    private GameHistory() {
        this.currentNode = new Node(null);
    }

    public static GameHistory getInstance() {
        if (gameHistory == null) {
            gameHistory = new GameHistory();
        }
        return gameHistory;
    }

    public static void restart() {
        gameHistory = null;
    }

    public void addRecord(Tile from, Tile to) {
        HistoryRecord record = new HistoryRecord(from, to, from.getAnimal(), to.getAnimal());
        Node newNode = new Node(record, currentNode);
        currentNode.next = newNode;
        currentNode = newNode;
    }

    public void undo() throws CannotUndoException {
        if (currentNode == null || currentNode.historyRecord == null) {
            throw new CannotUndoException();
        }
        HistoryRecord record = currentNode.historyRecord;
        record.tileFrom.setAnimal(record.animalFrom);
        record.tileTo.setAnimal(record.animalTo);
        currentNode = currentNode.prev;
    }

    public void redo() throws CannotRedoException {
        if (currentNode == null || currentNode.next == null) {
            throw new CannotRedoException();
        }
        currentNode = currentNode.next;
        HistoryRecord record = currentNode.historyRecord;
        record.tileFrom.setAnimal(null);
        record.tileTo.setAnimal(record.animalFrom);
    }

    private class HistoryRecord {
        final Tile tileFrom;
        final Tile tileTo;
        final Animal animalFrom;
        final Animal animalTo;

        HistoryRecord(Tile tileFrom, Tile tileTo, Animal animalFrom, Animal animalTo) {
            this.tileFrom = tileFrom;
            this.tileTo = tileTo;
            this.animalFrom = animalFrom;
            this.animalTo = animalTo;
        }
    }

    private class Node {
        final HistoryRecord historyRecord;
        Node prev;
        Node next;

        Node(HistoryRecord historyRecord) {
            this(historyRecord, null);
        }

        Node(HistoryRecord historyRecord, Node prev) {
            this.historyRecord = historyRecord;
            this.prev = prev;
        }

    }


}
