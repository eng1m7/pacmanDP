
import java.awt.Image;

public class Ghost extends Block {

    public Ghost(int x, int y, int width, int height, Image image) {
        super(x, y, width, height, image);
    }

    // Prototype: create a copy of this ghost
    public Ghost clone() {
        Ghost copy = new Ghost(
            this.x,
            this.y,
            this.width,
            this.height,
            this.image
        );

        copy.direction = this.direction;
        copy.velocityX = this.velocityX;
        copy.velocityY = this.velocityY;

        return copy;
    }

    // Create a copy at a new position
    public Ghost cloneAt(int x, int y) {
        Ghost copy = this.clone();

        copy.x = x;
        copy.y = y;
        copy.startX = x;
        copy.startY = y;

        return copy;
    }
}
