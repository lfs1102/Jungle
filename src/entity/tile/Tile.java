package entity.tile;

import entity.Board;
import entity.animal.Animal;

/**
 * Created by lifengshuang on 9/20/16.
 */
public class Tile {

    private int row;
    private int col;
    private final TileType type;
    private Animal animal;
    public Tile(int row, int col, TileType type) {
        this.row = row;
        this.col = col;
        this.type = type;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public TileType getType() {
        return type;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public boolean isHome() {
        return this.type == TileType.HOME_RIGHT || this.type == TileType.HOME_LEFT;
    }

    public int isInHomeOrTrap(Animal animal) {
        int tileSide;
        switch (this.type) {
            case TRAP_LEFT:
            case HOME_LEFT:
                tileSide = -1;
                break;
            case TRAP_RIGHT:
            case HOME_RIGHT:
                tileSide = 1;
                break;
            default:
                tileSide = 0;
        }
        int animalSide = animal.getSide() == Board.Side.LEFT ? -1 : 1;
        return animalSide * tileSide;
    }

    @Override
    public String toString() {
        return " " + this.type.getPrintWord() + " ";
    }

    public enum TileType {
        LAND("　"), RIVER("水"),
        TRAP_LEFT("陷"), HOME_LEFT("家"),
        TRAP_RIGHT("陷"), HOME_RIGHT("家");

        public static final int SIZE = TileType.values().length;

        private final String printWord;

        TileType(String printWord) {
            this.printWord = printWord;
        }

        public String getPrintWord() {
            return printWord;
        }
    }
}
