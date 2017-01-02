package entity.animal.type;

import entity.tile.Tile;
import entity.animal.Animal;
import entity.Board;
import exception.GameWinException;
import exception.InvalidActionException;

/**
 * Created by lifengshuang on 9/25/16.
 */
public abstract class AnimalWithoutSkill extends Animal {

    @Override
    public Tile act(Board.Direction direction, boolean simulate) throws InvalidActionException, GameWinException {
        Tile currentTile = Board.getInstance().getTileByAnimal(this);
        Tile nextTile = Board.getInstance().getTileByDirection(currentTile, direction);

        checkBoundary(nextTile);
        checkRiver(nextTile);
        move(currentTile, nextTile, simulate);
        if (!simulate) {
            checkWin(nextTile);
        }
        return nextTile;
    }

    private void checkRiver(Tile nextTile) throws InvalidActionException {
        if (nextTile.getType() == Tile.TileType.RIVER) {
            throw new InvalidActionException(this.getName() + "不能下水");
        }
    }

}