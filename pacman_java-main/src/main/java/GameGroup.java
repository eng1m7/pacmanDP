import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class GameGroup implements GameComponent {

    private List<GameComponent> components = new ArrayList<>();

    public void add(GameComponent component) {
        components.add(component);
    }

    public void remove(GameComponent component) {
        components.remove(component);
    }

    @Override
    public void draw(Graphics g) {
        for (GameComponent component : components) {
            component.draw(g);
        }
    }
}