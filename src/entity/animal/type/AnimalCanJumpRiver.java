package entity.animal.type;

import entity.tile.Tile;
import entity.animal.Animal;
import entity.Board;
import exception.GameWinException;
import exception.InvalidActionException;

/**
 * Created by lifengshuang on 9/25/16.
 */
public abstract class AnimalCanJumpRiver extends Animal {

    @Override
    public Tile act(Board.Direction direction, boolean simulate) throws InvalidActionException, GameWinException {
        Tile currentTile = Board.getInstance().getTileByAnimal(this);
        Tile nextTile = Board.getInstance().getTileByDirection(currentTile, direction);

        checkBoundary(nextTile);

        if (nextTile.getType() == Tile.TileType.RIVER) {
            while (nextTile.getType() == Tile.TileType.RIVER) {
                if (nextTile.getAnimal() != null && nextTile.getAnimal().getSide() != this.side) {
                    throw new InvalidActionException("敌方动物在水中,不能跳河!");
                }
                nextTile = Board.getInstance().getTileByDirection(nextTile, direction);
            }
        }

        move(currentTile, nextTile, simulate);
        if (!simulate) {
            checkWin(nextTile);
        }
        return nextTile;
    }


}
