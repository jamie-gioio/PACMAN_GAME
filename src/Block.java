import java.awt.*;
import java.util.HashSet;

public class Block {
    int x;
    int y;
    int width;
    int height;
    Image image;

    int startX;
    int startY;
    char direction = 'U';
    char ghostColor = ' ';
    boolean roundTL, roundTR, roundBL, roundBR;

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

    void updateDirection(char direction, HashSet<Block> walls) {
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
            this.velocityY = -Contants.TILE_SIZE/4;
        } 
        
        else if(this.direction == 'D') {
            this.velocityX = 0;
            this.velocityY = Contants.TILE_SIZE/4;
        } 
        
        else if(this.direction == 'L') {
            this.velocityX = -Contants.TILE_SIZE/4;
            this.velocityY = 0;
        } 
        
        else if(this.direction == 'R') {
            this.velocityX = Contants.TILE_SIZE/4;
            velocityY = 0;
        }
    }

    void reset() {
        this.x = this.startX;
        this.y = this.startY;
    }

    static boolean collision(Block a, Block b) {
        return  a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }
} 
    

