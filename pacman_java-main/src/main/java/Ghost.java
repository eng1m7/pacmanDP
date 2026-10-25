import java.awt.Image;

public class Ghost extends Block {

    public Ghost(int x, int y, int width, int height, Image image) {
        super(x, y, width, height, image);
    }

    // Prototype: create a copy of this ghost
    public Ghost clone(int x, int y) {
        Ghost copy = new Ghost(
            x,
            y,
            this.width,
            this.height,
            this.image
        );
        
        copy.startX = x;
        copy.startY = y;

        copy.direction = this.direction;
        copy.velocityX = this.velocityX;
        copy.velocityY = this.velocityY;

        return copy;
    }
}
