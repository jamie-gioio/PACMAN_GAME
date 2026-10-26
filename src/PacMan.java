import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;
import java.awt.geom.Path2D;
import java.awt.geom.Area;

public class PacMan extends JPanel implements ActionListener, KeyListener {

    private int rowAmount = 21;
    private int columnAmount = 19;
    private int tileSize = 32;
    private int boardWidth = columnAmount * tileSize;
    private int boardHeight = rowAmount * tileSize;

    private Image blueGhostDown;
    private Image blueGhostUp; 
    private Image blueGhostLeft; 
    private Image blueGhostRight;  

    private Image pinkGhostDown;
    private Image pinkGhostUp; 
    private Image pinkGhostLeft; 
    private Image pinkGhostRight; 

    private Image redGhostDown;
    private Image redGhostUp; 
    private Image redGhostLeft; 
    private Image redGhostRight; 

    private Image yellowGhostDown;
    private Image yellowGhostUp; 
    private Image yellowGhostLeft; 
    private Image yellowGhostRight; 

    private Image pacmanDown;
    private Image pacmanUp; 
    private Image pacmanLeft; 
    private Image pacmanRight; 
    
    private Image wallImage;

    /* Map Layout
       - 'X': Wall
       - ' ': Pathway with food placed in center of block
       - 'O': Nothing placed in block (empty)
       - 'P': Pac-Man
       - 'b': Blue ghost
       - 'r': Red ghost
       - 'p': Pink ghost
       - 'y': Yellow ghost
    */
    private String[] tileMap = {
        "XXXXXXXXXXXXXXXXXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X                 X",
        "X XX X XXXXX X XX X",
        "X    X       X    X",
        "XXXX XXXX XXXX XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXrXX X XXXX",
        "        bpy        ",
        "XXXX X XXXXX X XXXX",
        "OOOX X OOOOO X XOOO",
        "XXXX X XXXXX X XXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X  X     P     X  X",
        "XX X X XXXXX X X XX",
        "X    X   X   X    X",
        "X XXXXXX X XXXXXX X",
        "X                 X",
        "XXXXXXXXXXXXXXXXXXX" 
    };

    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;
    Area wallArea;
    Area outsideArea;

    Timer gameLoop;
    JButton startButton;
    Timer countdownTimer;
    int countDown = 0;


    char[] directions = {'U', 'D', 'L', 'R'};
    Random random = new Random();
    int score = 0;
    int lives = 3;
    boolean gameOver = false;
    char nextDirection = ' ';

    PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        setLayout(null);

        addKeyListener(this);
        setFocusable(true);

        // Load Images
        blueGhostDown = new ImageIcon(getClass().getResource("assets/blueGhostDown.png")).getImage();
        blueGhostUp = new ImageIcon(getClass().getResource("assets/blueGhostUp.png")).getImage();
        blueGhostLeft = new ImageIcon(getClass().getResource("assets/blueGhostLeft.png")).getImage();
        blueGhostRight = new ImageIcon(getClass().getResource("assets/blueGhostRight.png")).getImage();

        pinkGhostDown = new ImageIcon(getClass().getResource("assets/pinkGhostDown.png")).getImage();
        pinkGhostUp = new ImageIcon(getClass().getResource("assets/pinkGhostUp.png")).getImage();
        pinkGhostLeft = new ImageIcon(getClass().getResource("assets/pinkGhostLeft.png")).getImage();
        pinkGhostRight = new ImageIcon(getClass().getResource("assets/pinkGhostRight.png")).getImage();

        redGhostDown = new ImageIcon(getClass().getResource("assets/redGhostDown.png")).getImage();
        redGhostUp = new ImageIcon(getClass().getResource("assets/redGhostUp.png")).getImage();
        redGhostLeft = new ImageIcon(getClass().getResource("assets/redGhostLeft.png")).getImage();
        redGhostRight = new ImageIcon(getClass().getResource("assets/redGhostRight.png")).getImage();

        yellowGhostDown = new ImageIcon(getClass().getResource("assets/yellowGhostDown.png")).getImage();
        yellowGhostUp = new ImageIcon(getClass().getResource("assets/yellowGhostUp.png")).getImage();
        yellowGhostLeft = new ImageIcon(getClass().getResource("assets/yellowGhostLeft.png")).getImage();
        yellowGhostRight = new ImageIcon(getClass().getResource("assets/yellowGhostRight.png")).getImage();

        pacmanDown = new ImageIcon(getClass().getResource("assets/pacmanDown.png")).getImage();
        pacmanUp = new ImageIcon(getClass().getResource("assets/pacmanUp.png")).getImage();
        pacmanLeft = new ImageIcon(getClass().getResource("assets/pacmanLeft.png")).getImage();
        pacmanRight = new ImageIcon(getClass().getResource("assets/pacmanRight.png")).getImage();

        wallImage = new ImageIcon(getClass().getResource("assets/wall.png")).getImage();

        loadmap();

        // Gives every ghost a random starting direction
        for (Block ghost : ghosts) {
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection, walls);
        }

        gameLoop = new Timer(50, this);

        // Placement of start button: centered over tile columns 8-12 on row 12
        int row = 12;
        int firstCol = 8;
        int lastCol = 12;

        ImageIcon startBtn = new ImageIcon(getClass().getResource("assets/startBtn.png"));

        // Scale the button to span the columns while keeping its aspect ratio
        int btnW = (lastCol - firstCol + 1) * tileSize;
        int btnH = btnW * startBtn.getIconHeight() / startBtn.getIconWidth();

        int btnX = (firstCol - 1) * tileSize;
        int btnY = (row - 1) * tileSize + (tileSize - btnH) / 2;


        Image scaled = startBtn.getImage().getScaledInstance(btnW, btnH, Image.SCALE_SMOOTH);

        // Make the button look like a plain image: no border, no fill or focus ring 
        startButton = new JButton(new ImageIcon(scaled));
        startButton.setBorderPainted(false);
        startButton.setContentAreaFilled(false);
        startButton.setFocusPainted(false);
        startButton.setOpaque(false);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startButton.setBounds(btnX, btnY, btnW, btnH);
        startButton.setFocusable(false);
        startButton.addActionListener(e -> startCountDown());
        add(startButton);
    } 
    
    /**
     * Called when the start button is clicked. Removes the button, shows a 
     * 3-2-1 countdown (one number per second) and starts 
     * when it reaches zero. Also gives the panel keyboard focus
    */
    void startCountDown() {
        remove(startButton);
        revalidate();
        countDown = 3;
        repaint();
        requestFocusInWindow();

        countdownTimer = new Timer(1000, e -> {
            countDown--;
            if (countDown <= 0) {
                countDown = 0;
                countdownTimer.stop();
                gameLoop.start();
            }
            repaint();

        });
        countdownTimer.start();
    }


    public void loadmap() {
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Block>();

        for(int r = 0; r < rowAmount; r++) {
            for(int c = 0; c < columnAmount; c++) {
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);

                int x = c*tileSize;
                int y = r*tileSize;

                if (tileMapChar == 'X') {
                    Block wall = new Block(wallImage, x, y, tileSize, tileSize);
                    wall.roundTL = isPath(r - 1, c) && isPath(r, c - 1);
                    wall.roundTR = isPath(r - 1, c) && isPath(r, c + 1);
                    wall.roundBL = isPath(r + 1, c) && isPath(r, c - 1);
                    wall.roundBR = isPath(r + 1, c) && isPath(r, c + 1);
                    walls.add(wall);
                }
                else if (tileMapChar == 'b') {
                    Block ghost = new Block(blueGhostDown, x, y, tileSize - 2, tileSize - 2);
                    ghost.ghostColor = 'b';
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'p') {
                    Block ghost = new Block(pinkGhostDown, x, y, tileSize - 2, tileSize - 2);
                    ghost.ghostColor = 'p';
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'r') {
                    Block ghost = new Block(redGhostDown, x, y, tileSize - 2, tileSize - 2);
                    ghost.ghostColor = 'r';
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'y') {
                    Block ghost = new Block(yellowGhostDown, x, y, tileSize - 2, tileSize - 2);
                    ghost.ghostColor = 'y';
                    ghosts.add(ghost);
                }
                else if (tileMapChar == ' ') {
                    Block food = new Block(null, x + 14, y + 14, 4, 4);
                    foods.add(food);
                }
                else if (tileMapChar == 'P') {
                    pacman = new Block(pacmanRight, x, y, tileSize - 2, tileSize - 2);
                }
            }
        }

        // Merge all walls into one shape so the outline is drawn once
        wallArea = new Area();
        for (Block wall : walls) {
            wallArea.add(new Area(wallShape(wall)));
        }

        // Everything on the board that is not a wall (used for the glow clip)
        outsideArea = new Area(new Rectangle(0, 0, boardWidth, boardHeight));
        outsideArea.subtract(wallArea);
    }

    /**
     * Tells whether a tile is walkable (not a wall)
     * 
     * @param r tile row
     * @param c tile column
     * @return {@code true} if the tile is inside the board and is not an 
     *         {@code 'X'}; {@code false} for walls and out of bounds tiles 
     */
    boolean isPath(int r, int c) {
        if (r < 0 || r >= rowAmount || c < 0 || c >= columnAmount) {
            return false;
        }
        return tileMap[r].charAt(c) != 'X';
    }
    
    /**
     * Builds the outline of a single wall tie as a square whose corners are
     * rounded according to the block's {@code roundTL}, {@code roundTR},
     * {@code roundBL} and {@code roundBR} flags. Rounded corners use quadratic
     * curves with a radius of one fifth of a tile.
     * 
     * @param w the wall block
     * @return the wall's outline shape
     */
    Shape wallShape(Block w) {
        double x = w.x, y = w.y, s = w.width;
        double rad = tileSize / 5.0;

        // Radius of each corner: rad if rounded, 0 if square 
        double tl = w.roundTL ? rad : 0;
        double tr = w.roundTR ? rad : 0;
        double br = w.roundBR ? rad : 0;
        double bl = w.roundBL ? rad : 0;

        // Trace clockwise from the top edge
        Path2D.Double p = new Path2D.Double();
        p.moveTo(x + tl, y);
        p.lineTo(x + s - tr, y);
        p.quadTo(x + s, y, x + s, y + tr);          
        p.lineTo(x + s, y + s - br);
        p.quadTo(x + s, y + s, x + s - br, y + s);  
        p.lineTo(x + bl, y + s);
        p.quadTo(x, y + s, x, y + s - bl);         
        p.lineTo(x, y + tl);
        p.quadTo(x, y, x + tl, y);                
        p.closePath();
        return p;

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.BLACK);
        g2.fill(wallArea);

        Shape oldClip = g2.getClip();
        g2.clip(outsideArea);
        for (int i = 4; i >= 1; i--) {
            g2.setColor(new Color(0, 120, 255, 30));
            g2.setStroke(new BasicStroke(2f + i * 3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(wallArea);
        }

        g2.setClip(oldClip);

        g2.setColor(new Color(30, 90, 255));
        g2.setStroke(new BasicStroke(1f));
        g2.draw(wallArea);
        g2.setStroke(new BasicStroke(0.5f));

        for (Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        g.setColor(Color.YELLOW); 
        for (Block food : foods) {
            g.fillOval(food.x, food.y, food.width, food.height);
                  
        }

        g.setFont(new Font("Arial", Font.PLAIN, 18));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf(score), tileSize/2, tileSize/2);
        }
        else {
            g.drawString("x" + String.valueOf(lives) + " Score: " + String.valueOf(score), tileSize/2, tileSize/2);
        }

        if (countDown > 0) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 96));
            String text = String.valueOf(countDown);
            FontMetrics fm = g.getFontMetrics();
            int tx = (boardWidth - fm.stringWidth(text)) / 2;
            int ty = (boardHeight + fm.getAscent() - fm.getDescent() / 2);
            g.drawString(text, tx, ty);
        }
    
        
        
    }

    public void move() {

        if (nextDirection != ' ' && pacman.x % tileSize == 0 && pacman.y % tileSize == 0) {
            pacman.updateDirection(nextDirection, walls);
            updatePacmanImage();
        }

        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        // Undo the move if it ran into a wall
        for (Block wall : walls) {
            if (Block.collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        } 

        for (Block ghost : ghosts) {
            // Caught by a ghost
            if (Block.collision(ghost, pacman)) {       
                lives -= 1;
                if (lives == 0) {
                    gameOver = true;
                    return;
                }
                resetPositions();
            }    

            if (ghost.y == tileSize*9 && ghost.direction != 'U' && ghost.direction != 'D') {
                ghost.updateDirection('U', walls);
            }
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;

            // On a wall (or right edge) hit, step back and choose a new random direction
            for (Block wall : walls) {
                if (Block.collision(ghost, wall) || ghost.x + ghost.width >= boardWidth) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection, walls);
                }
            }

            updateGhostImage(ghost);
        }

        // Eat food (removal happens after the loop to avoid modifyoing the set while iterating)
        Block foodEaten = null;
        for (Block food : foods) {
            if (Block.collision(pacman, food)) {
                foodEaten = food;
                score += 10;
            }
        }
        foods.remove(foodEaten);

        // Level cleared: rebuild the map and put everyone back at their start
        if (foods.isEmpty()) {
            loadmap();
            resetPositions();
        }
    }


    /**
     * Returns Pac-Man and all the ghosts to their starting tiles. PacMan's 
     * velocity and queued directions are cleared, and each ghost is given a 
     * fresh random direction 
     */
    public void resetPositions() {
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;
        nextDirection = ' ';

        for(Block ghost : ghosts) {
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection, walls);
        }
    }

    @Override 
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
        }
    }

    @Override 
    public void keyTyped(KeyEvent e) {}

    @Override 
    public void keyPressed(KeyEvent e) {}

    @Override 
    public void keyReleased(KeyEvent e) {

        if (!gameLoop.isRunning() && !gameOver) {
            return;
        }

        if (gameOver) {
            loadmap();
            resetPositions();
            lives = 3;
            score = 0;
            gameOver = false;
            nextDirection = ' ';
            gameLoop.start();
        }

        if(e.getKeyCode() == KeyEvent.VK_UP) {
            nextDirection = 'U';
        } else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
            nextDirection = 'D';
        } else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
            nextDirection = 'L';
        } else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
            nextDirection = 'R';
        }

        if (pacman.direction == 'U') {
            pacman.image = pacmanUp;
        } else if (pacman.direction == 'D') {
            pacman.image = pacmanDown;
        } else if (pacman.direction == 'L') {
            pacman.image = pacmanLeft;
        } else if (pacman.direction == 'R') {
            pacman.image = pacmanRight;
        }
    }


    /**
     * Sets PacMan's image to the one that matched his current 
     * {@code direction}
     */
    void updatePacmanImage() {
        if (pacman.direction == 'U') {
            pacman.image = pacmanUp;
        } else if (pacman.direction == 'D') {
            pacman.image = pacmanDown;
        } else if (pacman.direction == 'L') {
            pacman.image = pacmanLeft;
        } else if (pacman.direction == 'R') {
            pacman.image = pacmanRight;
        }
    }


    /**
     * Set's a ghost's image from its colour ({@code ghostColor}: 'b', 'p',
     * 'r', 'y') and current {@code direction}
     * 
     * @param ghost the ghost to update
     */
    void updateGhostImage(Block ghost) {
        char d = ghost.direction;

        switch (ghost.ghostColor) {
            case 'b':
                ghost.image = d == 'U'? blueGhostUp : d == 'D' ? blueGhostDown : d == 'L' ? blueGhostLeft : blueGhostRight;
                break;
            case 'p':
                ghost.image = d == 'U'? pinkGhostUp : d == 'D' ? pinkGhostDown : d == 'L' ? pinkGhostLeft : pinkGhostRight;
                break;
            case 'r':
                ghost.image = d == 'U'? redGhostUp : d == 'D' ? redGhostDown : d == 'L' ? redGhostLeft : redGhostRight;
                break;
            case 'y':
                ghost.image = d == 'U'? yellowGhostUp : d == 'D' ? yellowGhostDown : d == 'L' ? yellowGhostLeft : yellowGhostRight;
                break;
        }
    }

}
