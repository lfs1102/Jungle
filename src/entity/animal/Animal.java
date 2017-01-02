package entity.animal;

import entity.tile.Tile;
import entity.animal.instance.*;
import entity.Board;
import exception.GameWinException;
import exception.InvalidActionException;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Created by lifengshuang on 9/19/16.
 */
public abstract class Animal {

    protected Board.Side side;
    private int power;
    private ImageView view;
    private Image coloredImage;
    private Image greyImage;

    public static Animal newAnimal(int power, int index) {
        Board.Side side;
        if (index < 3) {
            side = Board.Side.LEFT;
        } else {
            side = Board.Side.RIGHT;
        }
        Animal animal;
        switch (power) {
            case 1:
                animal = new Mouse();
                break;
            case 2:
                animal = new Cat();
                break;
            case 3:
                animal = new Wolf();
                break;
            case 4:
                animal = new Dog();
                break;
            case 5:
                animal = new Leopard();
                break;
            case 6:
                animal = new Tiger();
                break;
            case 7:
                animal = new Lion();
                break;
            case 8:
                animal = new Elephant();
                break;
            default:
                animal = null;
                break;
        }
        if (animal != null) {
            animal.power = power;
            animal.side = side;
            return animal;
        }
        return null;
    }

    protected abstract String getName();

    public abstract Tile act(Board.Direction direction, boolean simulate) throws InvalidActionException, GameWinException;

    public Board.Side getSide() {
        return side;
    }

    private boolean canBeat(Animal enemyAnimal) throws InvalidActionException {
        if (enemyAnimal.isInEnemyTrap()) {
            return true;
        } else if (this instanceof Mouse && enemyAnimal instanceof Elephant) {
            return true;
        } else if (this instanceof Elephant && enemyAnimal instanceof Mouse) {
            return false;
        } else {
            return this.power >= enemyAnimal.power;
        }
    }

    protected void checkBoundary(Tile nextTile) throws InvalidActionException {
        if (nextTile == null) {
            throw new InvalidActionException("不能走出边界");
        } else if (nextTile.isInHomeOrTrap(this) > 0
                && (nextTile.getType() == Tile.TileType.HOME_LEFT || nextTile.getType() == Tile.TileType.HOME_RIGHT)) {
            throw new InvalidActionException("不能走进自己家");
        }
    }

    protected void checkWin(Tile nextTile) throws GameWinException {
        if (nextTile.isHome() && nextTile.isInHomeOrTrap(this) < 0) {
            throw new GameWinException(this.side, "攻占敌方兽穴!");
        }
        if (Board.getInstance().animalAllDead(this.side)) {
            throw new GameWinException(this.side, "消灭了敌方的所有动物!");
        }
        if (Board.getInstance().animalCannotMove(this.side)) {
            throw new GameWinException(this.side, "敌方所有动物都不能移动!");
        }
    }

    protected void move(Tile currentTile, Tile nextTile, boolean simulate) throws InvalidActionException {
        Animal nextAnimal = nextTile.getAnimal();
        if (nextAnimal == null) {
            if (!simulate) {
                Board.getInstance().animalMove(currentTile, nextTile);
            }
        } else {
            if (this.side == nextAnimal.side) {
                throw new InvalidActionException("不能和友方单位重叠");
            }
            if (this.canBeat(nextAnimal)) {
                if (!simulate) {
                    Board.getInstance().animalMove(currentTile, nextTile);
                }
            } else {
                throw new InvalidActionException(this.getName() + "打不过" + nextAnimal.getName());
            }
        }
    }

    private boolean isInEnemyTrap() throws InvalidActionException {
        Tile tile = Board.getInstance().getTileByAnimal(this);
        return tile.isInHomeOrTrap(this) < 0;
    }

    @Override
    public String toString() {
        if (side == Board.Side.LEFT) {
            return this.power + getName() + " ";
        } else {
            return " " + getName() + this.power;
        }
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Animal) && ((Animal) obj).power == this.power && ((Animal) obj).side == this.side;
    }

    public ImageView getView() {
        return view;
    }

    public void setView(ImageView view) {
        this.view = view;
    }

    public void setColoredImage(Image coloredImage) {
        this.coloredImage = coloredImage;
    }

    public void setGreyImage(Image greyImage) {
        this.greyImage = greyImage;
    }

    public void switchImage(Board.Side side) {
        if (this.side == side) {
            view.setImage(coloredImage);
        } else {
            view.setImage(greyImage);
        }
    }
}
