import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class PacMan extends JPanel implements ActionListener, KeyListener {

    class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;

        int startX;
        int startY;
        char direction = 'U';

        int velocityX = 0;
        int velocityY = 0;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;

            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;

            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity() {
            if(this.direction == 'U') {
                this.velocityX = 0;
                this.velocityY = -tileSize/4;
            } 
            
            else if(this.direction == 'D') {
                this.velocityX = 0;
                this.velocityY = tileSize/4;
            } 
            
            else if(this.direction == 'L') {
                this.velocityX = -tileSize/4;
                this.velocityY = 0;
            } 
            
            else if(this.direction == 'R') {
                this.velocityX = tileSize/4;
                velocityY = 0;
            }
        }

        void reset() {
            this.x = this.startX;
            this.y = this.startY;
        }
    }

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
    private Image powerFood;

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
        "OOOX X       X XOOO",
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

    Timer gameLoop;
    char[] directions = {'U', 'D', 'L', 'R'};
    Random random = new Random();
    int score = 0;
    int lives = 3;
    boolean gameOver = false;

    PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);

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
        powerFood = new ImageIcon(getClass().getResource("assets/powerFood.png")).getImage();

        loadmap();

        for (Block ghost : ghosts) {
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }

        gameLoop = new Timer(50, this);
        gameLoop.start();
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
                    walls.add(wall);
                }
                else if (tileMapChar == 'b') {
                    Block ghost = new Block(blueGhostDown, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'p') {
                    Block ghost = new Block(pinkGhostDown, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'r') {
                    Block ghost = new Block(redGhostDown, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'y') {
                    Block ghost = new Block(yellowGhostDown, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == ' ') {
                    Block food = new Block(null, x + 14, y + 14, 4, 4);
                    foods.add(food);
                }
                else if (tileMapChar == 'P') {
                    pacman = new Block(pacmanRight, x, y, tileSize, tileSize);
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        for (Block wall : walls) {
            g.setColor(Color.BLUE);
            g.fillRect(wall.x, wall.y, wall.width, wall.height);
        }

        for (Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        for (Block food : foods) {
            g.fillOval(food.x, food.y, food.width, food.height);
            g.setColor(Color.YELLOW);       
        }

        g.setFont(new Font("Arial", Font.PLAIN, 18));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf(score), tileSize/2, tileSize/2);
        }
        else {
            g.drawString("x" + String.valueOf(lives) + " Score: " + String.valueOf(score), tileSize/2, tileSize/2);
        }
        
        
    }

    public void move() {
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        } 

        for (Block ghost : ghosts) {
            if (collision(ghost, pacman)) {
                lives -= 1;
                if (lives == 0) {
                    gameOver = true;
                    return;
                }
                resetPositions();
            }    

            if (ghost.y == tileSize*9 && ghost.direction != 'U' && ghost.direction != 'D') {
                ghost.updateDirection('U');
            }
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;

            for (Block wall : walls) {
                if (collision(ghost, wall) || ghost.x + ghost.width >= boardWidth) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }
            }
        }

        Block foodEaten = null;
        for (Block food : foods) {
            if (collision(pacman, food)) {
                foodEaten = food;
                score += 10;
            }
        }
        foods.remove(foodEaten);

        if (foods.isEmpty()) {
            loadmap();
            resetPositions();
        }
    }

    public boolean collision(Block a, Block b) {
        return  a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }
    
    public void resetPositions() {
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;

        for(Block ghost : ghosts) {
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
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
        // System.out.println("KeyEvent " + e.getKeyCode());

        if (gameOver) {
            loadmap();
            resetPositions();
            lives = 3;
            score = 0;
            gameOver = false;
            gameLoop.start();
        }

        if(e.getKeyCode() == KeyEvent.VK_UP) {
            pacman.updateDirection('U');
        } else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
            pacman.updateDirection('D');
        } else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
            pacman.updateDirection('L');
        } else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pacman.updateDirection('R');
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

}
