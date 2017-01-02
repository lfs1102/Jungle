package exception;

import entity.Board;

/**
 * Created by lifengshuang on 9/26/16.
 */
public class GameWinException extends Exception {

    private final Board.Side side;
    private final String message;

    public GameWinException(Board.Side side, String message) {
        super();
        this.side = side;
        this.message = message;
    }

    public Board.Side getSide() {
        return side;
    }

    public String getMessage() {
        return message;
    }
}
