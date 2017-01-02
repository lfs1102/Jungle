package entity;

import exception.CannotUndoException;
import history.GameHistory;
import entity.animal.Animal;
import entity.tile.Tile;
import exception.FileWrongFormatException;
import exception.GameWinException;
import exception.InvalidActionException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by lifengshuang on 9/20/16.
 */
public class Board {

    private static Board board;

    private final Tile[][] tiles = new Tile[7][9];
    private final Animal[] animalLeft = new Animal[8];
    private final Animal[] animalRight = new Animal[8];
    private Side turn = Side.LEFT;

    private Animal selectedAnimal;

    public Animal getSelectedAnimal() {
        return selectedAnimal;
    }

    private Board(File tileFile, File animalFile) {
        try {
            loadTiles(tileFile);
            loadAnimals(animalFile);
        } catch (FileWrongFormatException | FileNotFoundException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    public static Board getInstance() {
        if (board == null) {
            board = new Board(new File("data" + File.separator + "tile.txt"), new File("data" + File.separator + "animal.txt"));
        }
        return board;
    }

    public void restart() {
        board = null;
        GameHistory.restart();
    }

    public void animalMove(Tile from, Tile to) {
        GameHistory.getInstance().addRecord(from, to);
        to.setAnimal(from.getAnimal());
        from.setAnimal(null);
    }

    public Animal getAnimal(Side turn, int power) {
        switch (turn) {
            case LEFT:
                return animalLeft[power - 1];
            case RIGHT:
                return animalRight[power - 1];
            default:
                return null;
        }
    }

    public void tileClicked(int row, int col) throws GameWinException {
        if (row == -1 || col == -1) {
            return;
        }
        Tile tile = tiles[row][col];
        Animal clickedAnimal = tile.getAnimal();
        if (selectedAnimal == null) {
            if (clickedAnimal != null && clickedAnimal.getSide().equals(turn)) {
                selectedAnimal = tile.getAnimal();
            }
        } else {
            if (clickedAnimal == null) {
                animalAct(getTileByAnimal(selectedAnimal), row, col);
            } else {
                if (clickedAnimal.getSide().equals(turn)) {
                    selectedAnimal = tile.getAnimal();
                } else {
                    animalAct(getTileByAnimal(selectedAnimal), row, col);
                }
            }
        }
    }

    private void animalAct(Tile tile, int row, int col) throws GameWinException {
        Direction direction = getDirection(tile, row, col);
        System.out.println("act " + direction);
        if (direction != null) {
            try {
                Tile result = tile.getAnimal().act(direction, true);
                if (result.getCol() == col && result.getRow() == row) {
                    tile.getAnimal().act(direction, false);
                    selectedAnimal = null;
                    nextTurn();
                }
            } catch (InvalidActionException e) {
                selectedAnimal = null;
            }
        } else {
            selectedAnimal = null;
        }
    }

    private Direction getDirection(Tile tile, int row, int col) {
        if (tile.getRow() == row) {
            if (tile.getCol() > col) {
                return Direction.LEFT;
            }
            if (tile.getCol() < col) {
                return Direction.RIGHT;
            }
        } else if (tile.getCol() == col) {
            if (tile.getRow() > row) {
                return Direction.UP;
            }
            if (tile.getRow() < row) {
                return Direction.DOWN;
            }
        }
        return null;
    }

    public Animal[] getAnimals() {
        Animal[] animals = new Animal[16];
        System.arraycopy(animalLeft, 0, animals, 0, 8);
        System.arraycopy(animalRight, 0, animals, 8, 8);
        return animals;
    }

    public Tile getTileByDirection(Tile tile, Direction direction) {
        try {
            switch (direction) {
                case UP:
                    return tiles[tile.getRow() - 1][tile.getCol()];
                case DOWN:
                    return tiles[tile.getRow() + 1][tile.getCol()];
                case LEFT:
                    return tiles[tile.getRow()][tile.getCol() - 1];
                case RIGHT:
                    return tiles[tile.getRow()][tile.getCol() + 1];
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
        return null;
    }

    public Tile getTileByAnimal(Animal animal) {
        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                if (animal.equals(tile.getAnimal())) {
                    return tile;
                }
            }
        }
        return null;
    }

    public boolean animalCannotMove(Side side) {
        Animal[] animals;
        if (side == Side.LEFT) {
            animals = animalRight;
        } else {
            animals = animalLeft;
        }
        for (Animal animal : animals) {
            for (Direction direction : Direction.values()) {
                try {
                    animal.act(direction, true);
                    return false;
                } catch (Exception ignored) {

                }
            }
        }
        return true;
    }

    public boolean animalAllDead(Side attackSide){
        Animal[] animals;
        if (attackSide == Side.RIGHT) {
            animals = animalLeft;
        } else {
            animals = animalRight;
        }
        for (Animal animal : animals) {
            if (getTileByAnimal(animal) != null) {
                return false;
            }
        }
        return true;
    }

    public void printBoard() {
        System.out.println();
        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                if (tile.getAnimal() != null) {
                    System.out.print(tile.getAnimal());
                } else {
                    System.out.print(tile);
                }
            }
            System.out.println();
        }
    }

    private void loadTiles(File file) throws FileNotFoundException, FileWrongFormatException {
        Scanner scanner = new Scanner(file);
        for (int i = 0; i < tiles.length; i++) {
            String line = scanner.nextLine();
            if (line.length() == tiles[0].length) {
                for (int j = 0; j < tiles[0].length; j++) {
                    int number = line.charAt(j) - '0';
                    if (number >= 0 && number < Tile.TileType.SIZE) {
                        tiles[i][j] = new Tile(i, j, Tile.TileType.values()[number]);
                    } else {
                        throw new FileWrongFormatException(file.getName() + ": Number " + number + " is not valid");
                    }
                }
            } else {
                throw new FileWrongFormatException(file.getName() + ": The width of the board is wrong");
            }
        }
    }

    private void loadAnimals(File file) throws FileNotFoundException, FileWrongFormatException {
        Scanner scanner = new Scanner(file);
        for (int i = 0; i < tiles.length; i++) {
            String line = scanner.nextLine();
            if (line.length() == tiles[0].length) {
                for (int j = 0; j < tiles[0].length; j++) {
                    int power = line.charAt(j) - '0';
                    Animal animal = Animal.newAnimal(power, j);
                    tiles[i][j].setAnimal(animal);
                    if (power != 0) {
                        int index = power - 1;
                        if (j < 3) {
                            animalLeft[index] = animal;
                        }
                        if (j > 5) {
                            animalRight[index] = animal;
                        }
                    }
                }
            } else {
                throw new FileWrongFormatException("The format of " + file.getName() + " is wrong.");
            }
        }

    }

    public void undo() {
        try {
            GameHistory.getInstance().undo();
            nextTurn();
        } catch (CannotUndoException e) {
            System.out.println("Cannot undo anymore");;
        }
    }

    private void nextTurn() {
        if (turn == Side.LEFT) {
            turn = Side.RIGHT;
        } else {
            turn = Side.LEFT;
        }
        refreshAnimalImages();
    }

    public void refreshAnimalImages() {
        for (Animal animal : animalLeft) {
            animal.switchImage(this.turn);
        }
        for (Animal animal : animalRight) {
            animal.switchImage(this.turn);
        }
    }

    public enum Side {
        LEFT, RIGHT
    }

    public enum Direction {
        UP, DOWN, LEFT, RIGHT;

        public static Direction parseDirection(String direction) {
            switch (direction) {
                case "w":
                    return UP;
                case "s":
                    return DOWN;
                case "a":
                    return LEFT;
                case "d":
                    return RIGHT;
            }
            return null;
        }
    }
}
