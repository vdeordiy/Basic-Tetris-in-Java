package tetris;

import java.awt.Point;
import java.awt.Color;
import java.util.Map;

public interface Commons {
    public final int columns = 11;
    public final int rows = 20;

    public final int squareSize = 25;

    public final int initialDelay = 1000;
    public final int delayInterval = 1000;

    public enum State {
        FALLING,
        LANDED,
    }

    public final Color[] colors = {
        Color.decode("#0441ae"), // blue
        Color.decode("#72cb3b"), // green
        Color.decode("#ffd504"), // yellow
        Color.decode("#ff971d"), // orange
        Color.decode("#ff3215"), // red
        Color.decode("#f23553"), // pink
        Color.decode("#63145b"), // purple
    };
    
    enum Shape {
        I_Shape,
        J_Shape,
        L_Shape,
        O_Shape,
        Z_Shape,
        T_Shape,
        S_Shape,
    }

    public final Point[][] shapes = {
        { new Point(0, 2), new Point(0, 1), new Point(0, 0), new Point(0, -1) },
        { new Point(0, 1), new Point(0, 0), new Point(0, -1), new Point(-1, -1) },
        { new Point(0, 1), new Point(0, 0), new Point(0, -1), new Point(1, -1) },
        { new Point(0, 0), new Point(1, 0), new Point(0, 1), new Point(1, 1)},
        { new Point(-1, 1), new Point(0, 1), new Point(0, 0), new Point(1, 0)},
        { new Point(-1, 0), new Point(0, 0), new Point(1, 0), new Point(0, 1)},
        { new Point(-1, 0), new Point(0, 0), new Point(0, 1), new Point(1, 1)}
    };

    public final Map<Integer, Integer> scores = Map.of(
        0, 0,
        1, 100,
        2, 300,
        3, 500,
        4, 800
    );

    public final Map<Integer, Integer> speedIncrement = Map.of(
        0, 0,
        1, 50,
        2, 100,
        3, 150,
        4, 200
    );

    public final Color scoreColor = Color.WHITE;
    public final int scoreSize = 12;

    public final Color loseMessageColor = Color.decode("#8B0000"); // dark red
    public final int loseMessageSize = 25;
    public final String loseMessage = "You LOSE ;-;";
}

