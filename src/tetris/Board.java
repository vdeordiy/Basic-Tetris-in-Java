package tetris;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.awt.Point;
import java.awt.event.KeyListener;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.Timer;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;
import java.awt.Font;
import java.awt.FontMetrics;

public class Board extends JPanel implements ActionListener{
    private List<Square> squares;
    private Commons.Shape currentShape;
    private int score;
    private Timer timer;
    private boolean inGame;
    private boolean paused;

    public Board(){
        // initialize variables
        squares = new ArrayList<Square>();
        score = 0;
        inGame = true;
        paused = false;

        initUI();

        // handle game logic, such as gravity and currentShape
        timer = new Timer(Commons.delayInterval, this);
        timer.setInitialDelay(Commons.initialDelay);
        timer.start();
    }

    private boolean validate(List<Square> newSquares){
        HashSet<Point> drawn_coordinates = new HashSet<Point>();

        for(Square square : newSquares){
            Point rawPoint = square.getRawPoint();
            Point normalizedPoint = square.getNormalizedPoint();

            // collision test
            if (drawn_coordinates.contains(normalizedPoint)){
                return false;
            }
            drawn_coordinates.add(normalizedPoint);

            // out of bounds test
            if (rawPoint.x < 0 || rawPoint.x >= Commons.columns){
                return false;
            }
            if (rawPoint.y >= Commons.rows){
                return false;
            }
        }

        // implement changes
        this.squares = newSquares;

        return true;
    }

    private List<Square> cloneSquares(List<Square> currentSquares){
        List<Square> clonedSquares = new ArrayList<Square>();

        for(Square currentSquare : currentSquares){
            Square clonedSquare = new Square(currentSquare.getLocation(), currentSquare.getRotation());

            clonedSquare.setColor(currentSquare.getColor());
            clonedSquare.setState(currentSquare.getState());

            clonedSquares.add(clonedSquare);
        }

        return clonedSquares;
    }

    private List<Square> getFallingSquares(List<Square> subjectSquares){
        return subjectSquares.stream()
        .filter(square -> square.getState() == Commons.State.FALLING)
        .collect(Collectors.toList());
    }

    private boolean translateFallingSquares(int dx, int dy){
        List<Square> newSquares = cloneSquares(squares);

        for (Square square : getFallingSquares(newSquares)){
            square.translate(dx, dy);
        }

        return validate(newSquares);
    }

    private boolean rotateFallingSquares(){
        List<Square> newSquares = cloneSquares(squares);

        for (Square square : getFallingSquares(newSquares)){
            square.rotate();
        }

        return validate(newSquares);
    }

    private void score(){
        // count each row and check if complete
        Map<Integer, List<Square>> rowCounter = new HashMap<Integer, List<Square>>();
        int rowCompletions = 0;
        
        for(Square square : squares){
            Point rawPoint = square.getRawPoint();
            
            int rowNumber = rawPoint.y;

            if(!rowCounter.containsKey(rowNumber)){
                rowCounter.put(rowNumber, new ArrayList<Square>());
            }

            // check if landed
            if(square.getState() != Commons.State.LANDED){
                continue;
            }

            rowCounter.get(rowNumber).add(square);

            List<Square> row = rowCounter.get(rowNumber);

            if(row.size() == Commons.columns){
                rowCompletions += 1;
            }
            
        }

        // update score and difficulty
        score += Commons.scores.get(rowCompletions);

        int newDelay = Math.max(timer.getDelay() - Commons.speedIncrement.get(rowCompletions), 1);
        timer.setDelay(newDelay);

        // remove full row and move down other squares accordingly
        for(Integer rowNumber : rowCounter.keySet()){
            List<Square> row = rowCounter.get(rowNumber);

            // check if full
            if(row.size() != Commons.columns){
                continue;
            }

            // delete rowss
            for(Square rowSquare : row){
                squares.remove(rowSquare);
            }

            // move above rows one down
            for(Square aboveSquare: squares){
                Point rawPoint = aboveSquare.getRawPoint();

                if(rawPoint.y < rowNumber && aboveSquare.getState() == Commons.State.LANDED){
                    aboveSquare.translate(0, 1);
                }

            }
        }
    }
    
    private void gravitate(){
        boolean validated = translateFallingSquares(0, 1);

        if(!validated){
            // add new shape to the game
            for(Square square : getFallingSquares(squares)){
                square.setState(Commons.State.LANDED);
            }

            this.currentShape = null;
        }

        score();
    }

    @Override
    public void actionPerformed(ActionEvent e){
        // deal with falling
        gravitate();

        // deal with adding new falling shapes
        if (currentShape == null){
            // choose random shape
            Random r = new Random();

            int randomIndex = r.nextInt(Commons.shapes.length);
            Commons.Shape chosenShape = Commons.Shape.values()[randomIndex];
            Color chosenColor = Commons.colors[randomIndex];

            this.currentShape = chosenShape;
            // display it on the screen
            Point spawnPoint = new Point(
                (int) Commons.columns / 2,
                2
            );

            Point[] shapePoints = Commons.shapes[chosenShape.ordinal()];

            for(Point rotationPoint : shapePoints){
                // attributes
                Square shapeSquare = new Square(spawnPoint, rotationPoint);
                shapeSquare.setColor(chosenColor);
                shapeSquare.setState(Commons.State.FALLING);

                squares.add(shapeSquare);
            }

            // check losing condition
            if(!validate(squares)){
                timer.stop();
                inGame = false;
            }
        }
        repaint();
    }
    

    private void initUI(){
        // setFocusable in order to make addKeyListener work
        setFocusable(true);
        setBackground(Color.decode("#161616"));
        setPreferredSize(new Dimension(Commons.columns * Commons.squareSize, Commons.rows * Commons.squareSize));
        
        addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();

                switch(key){
                    case KeyEvent.VK_A:
                    case KeyEvent.VK_LEFT:
                        translateFallingSquares(-1, 0);
                        break;
                    case KeyEvent.VK_D:
                    case KeyEvent.VK_RIGHT:
                        translateFallingSquares(1, 0);
                        break;
                    case KeyEvent.VK_W:
                    case KeyEvent.VK_UP:
                        rotateFallingSquares();
                        break;
                    case KeyEvent.VK_SPACE:
                    case KeyEvent.VK_S:
                    case KeyEvent.VK_DOWN:
                        gravitate();
                        break;
                    case KeyEvent.VK_P:
                        paused = !paused;
                        if(paused){
                            timer.stop();
                        }else{
                            timer.start();
                        }
                        break;
                }

                repaint();
            }

            public void keyReleased(KeyEvent e) {
            }
        });

    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        drawBoard(g);
    }

    private void drawSquare(Graphics g, Square square){
        Point normalizedPoint = square.getNormalizedPoint();

        g.setColor(square.getColor());

        g.fillRect(
            (int) normalizedPoint.getX(),
            (int) normalizedPoint.getY(),
            Commons.squareSize - 1, // -1 for creating borders
            Commons.squareSize - 1
        );
    }

    private void drawScore(Graphics g){
        Font font = new Font("serif", Font.BOLD, Commons.scoreSize);

        g.setColor(Commons.scoreColor);
        g.setFont(font);

        g.drawString(String.format("%d", score), 10, 15);
    }

    private void drawBoard(Graphics g){
        // draw squares first
        HashSet<Point> drawn_coordinates = new HashSet<Point>();
        for (Square square : squares){
            Point normalizedPoint = square.getNormalizedPoint();

            drawn_coordinates.add(normalizedPoint);

            drawSquare(g, square);
        }

        // draw background where there are no squares
        for (int x = 0; x < Commons.columns; x++){
            for (int y = 0; y < Commons.rows; y++){
                Square backgroundSquare = new Square(
                    new Point(x, y),
                    new Point(0, 0)
                );
                backgroundSquare.setColor(Color.BLACK);

                Point normalizedPoint = backgroundSquare.getNormalizedPoint();

                // check if free space
                if (drawn_coordinates.contains(normalizedPoint)){
                    continue;
                }
                
                drawSquare(g, backgroundSquare);
            }
        }
    
        // draw score
        drawScore(g);

        // draw losing condition
        if(!inGame){
            Font font = new Font("serif", Font.BOLD, Commons.loseMessageSize);

            g.setColor(Commons.loseMessageColor);
            g.setFont(font);

            // to keep it centered
            FontMetrics fontmetrics = g.getFontMetrics();

            String message = Commons.loseMessage;

            g.drawString(
                message,
                (int) (Commons.columns*Commons.squareSize)/2 - fontmetrics.stringWidth(message)/2,
                (int) (Commons.rows*Commons.squareSize)/2
            );
        }
    }
}
