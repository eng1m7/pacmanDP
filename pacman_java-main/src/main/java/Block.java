import java.awt.Image;
import java.util.HashSet;

public class Block {

    int x;
    int y;
    int width;
    int height;

    int startX;
    int startY;

    Image image;

    char direction = 'U';

    int velocityX = 0;
    int velocityY = 0;

    private HashSet<Block> walls;

    public Block(int x, int y, int width, int height, Image image) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.startX = x;
        this.startY = y;

        this.image = image;
    }

    public void setWalls(HashSet<Block> walls) {
        this.walls = walls;
    }

    public void updateDirection(char direction) {

        char prevDirection = this.direction;

        this.direction = direction;

        updateVelocity();

        this.x += this.velocityX;
        this.y += this.velocityY;

        if (walls != null) {

            for (Block wall : walls) {

                if (checkCollision(this, wall)) {

                    this.x -= this.velocityX;
                    this.y -= this.velocityY;

                    this.direction = prevDirection;

                    updateVelocity();

                    break;
                }
            }
        }
    }

    public void updateVelocity() {

        if (direction == 'U') {

            velocityX = 0;
            velocityY = -4;

        } else if (direction == 'D') {

            velocityX = 0;
            velocityY = 4;

        } else if (direction == 'L') {

            velocityX = -4;
            velocityY = 0;

        } else if (direction == 'R') {

            velocityX = 4;
            velocityY = 0;
        }
    }

    public void reset() {

        this.x = this.startX;
        this.y = this.startY;
    }

    private boolean checkCollision(Block a, Block b) {

        return a.x < b.x + b.width
                && a.x + a.width > b.x
                && a.y < b.y + b.height
                && a.y + a.height > b.y;
    }
}