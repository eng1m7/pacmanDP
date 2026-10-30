
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Random;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class PacMan extends JPanel implements ActionListener, KeyListener {

    private static PacMan instance;

    private int rowCount = 21;
    private int columnCount = 19;
    private int tileSize = 32;

    private int width = columnCount * tileSize;
    private int height = rowCount * tileSize;

    private Image wallImage;
    private Image powerFood;

    private Image redGhostImage;
    private Image pinkGhostImage;
    private Image blueGhostImage;
    private Image orangeGhostImage;

    private Image pacmanRight;
    private Image pacmanLeft;
    private Image pacmanUp;
    private Image pacmanDown;

    // Prototype objects
    private Ghost redGhostPrototype;
    private Ghost pinkGhostPrototype;
    private Ghost blueGhostPrototype;
    private Ghost orangeGhostPrototype;

    // Composite
    private GameGroup gameObjects = new GameGroup();

    // X = wall, O = skip, P = Pac-Man, space = food
    // Ghosts: b = blue, o = orange, p = pink, r = red
    private String[] tileMap = {
        "XXXXXXXXXXXXXXXXXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X                 X",
        "X XX X XXXXX X XX X",
        "X    X       X    X",
        "XXXX XXXX XXXX XXXX",
        "OOOX         X XOOO",
        "XXXX X XXrXX X XXXX",
        "O       bpo       O",
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
    HashSet<Ghost> ghosts;
    Block pacman;

    Timer gameLoop;

    char[] directions = {'U', 'D', 'L', 'R'};
    Random random = new Random();

    int score = 0;
    int lives = 3;
    boolean gameOver = false;

    public static PacMan getInstance() {
        if (instance == null) {
            instance = new PacMan();
        }
        return instance;
    }

    private PacMan() {
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);

        addKeyListener(this);
        setFocusable(true);

        // Load images
        wallImage = new ImageIcon(
            getClass().getResource("/wall.png")
        ).getImage();

        powerFood = new ImageIcon(
            getClass().getResource("/powerFood.png")
        ).getImage();

        redGhostImage = new ImageIcon(
            getClass().getResource("/redGhost.png")
        ).getImage();

        pinkGhostImage = new ImageIcon(
            getClass().getResource("/pinkGhost.png")
        ).getImage();

        blueGhostImage = new ImageIcon(
            getClass().getResource("/blueGhost.png")
        ).getImage();

        orangeGhostImage = new ImageIcon(
            getClass().getResource("/orangeGhost.png")
        ).getImage();

        pacmanRight = new ImageIcon(
            getClass().getResource("/pacmanRight.png")
        ).getImage();

        pacmanLeft = new ImageIcon(
            getClass().getResource("/pacmanLeft.png")
        ).getImage();

        pacmanUp = new ImageIcon(
            getClass().getResource("/pacmanUp.png")
        ).getImage();

        pacmanDown = new ImageIcon(
            getClass().getResource("/pacmanDown.png")
        ).getImage();

        // Create Prototype objects
        redGhostPrototype = new Ghost(
            0, 0, tileSize, tileSize, redGhostImage
        );

        pinkGhostPrototype = new Ghost(
            0, 0, tileSize, tileSize, pinkGhostImage
        );

        blueGhostPrototype = new Ghost(
            0, 0, tileSize, tileSize, blueGhostImage
        );

        orangeGhostPrototype = new Ghost(
            0, 0, tileSize, tileSize, orangeGhostImage
        );

        // Build the map using the prototypes
        loadMap();

        // Set walls for Pac-Man and all ghosts
        pacman.setWalls(walls);

        for (Ghost ghost : ghosts) {
            ghost.setWalls(walls);
        }

        // Find the initial path from each ghost to Pac-Man
        for (Ghost ghost : ghosts) {
            int ghostRow = ghost.y / tileSize;
            int ghostCol = ghost.x / tileSize;
            int pacmanRow = pacman.y / tileSize;
            int pacmanCol = pacman.x / tileSize;

            List<int[]> path = AStar.aStar(
                ghostRow, ghostCol, pacmanRow, pacmanCol
            );

            if (path != null && path.size() > 1) {
                int[] nextStep = path.get(1);

                int nextRow = nextStep[0];
                int nextCol = nextStep[1];

                if (nextRow > ghostRow) {
                    ghost.updateDirection('D');
                } else if (nextRow < ghostRow) {
                    ghost.updateDirection('U');
                } else if (nextCol > ghostCol) {
                    ghost.updateDirection('R');
                } else if (nextCol < ghostCol) {
                    ghost.updateDirection('L');
                }
            }
        }

        gameLoop = new Timer(50, this);
        gameLoop.start();
    }

    public void loadMap() {
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Ghost>();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {

                String row = tileMap[r];
                char tile = row.charAt(c);

                int x = c * tileSize;
                int y = r * tileSize;

                if (tile == 'X') {
                    walls.add(
                        new Block(
                            x, y, tileSize, tileSize, wallImage
                        )
                    );

                } else if (tile == 'O') {
                    foods.add(
                        new Block(x, y, tileSize, tileSize, null)
                    );

                } else if (tile == 'P') {
                    pacman = new Block(
                        x, y, tileSize, tileSize, pacmanRight
                    );

                } else if (tile == ' ') {
                    foods.add(
                        new Block(
                            x + 14, y + 14, 4, 4, powerFood
                        )
                    );

                } else if (tile == 'r') {

                    // Prototype: clone the red ghost
                    ghosts.add(
                        redGhostPrototype.clone(x, y)
                    );

                } else if (tile == 'p') {

                    // Prototype: clone the pink ghost
                    ghosts.add(
                        pinkGhostPrototype.clone(x, y)
                    );

                } else if (tile == 'b') {

                    // Prototype: clone the blue ghost
                    ghosts.add(
                        blueGhostPrototype.clone(x, y)
                    );

                } else if (tile == 'o') {

                    // Prototype: clone the orange ghost
                    ghosts.add(
                        orangeGhostPrototype.clone(x, y)
                    );
                }
            }
        }

        // Composite: Add all game objects to the group
        gameObjects = new GameGroup();

        gameObjects.add(pacman);

        for (Ghost ghost : ghosts) {
            gameObjects.add(ghost);
        }

        for (Block wall : walls) {
            gameObjects.add(wall);
        }

        for (Block food : foods) {
            gameObjects.add(food);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {

        // Composite: Draw all game objects through one group
        gameObjects.draw(g);

        // Score and lives
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.setColor(Color.WHITE);

        if (gameOver) {
            g.drawString(
                "Game Over " + score,
                tileSize / 2 + 2,
                tileSize / 2 + 2
            );
        } else {
            g.drawString(
                "x" + lives + " Score: " + score,
                tileSize / 2 + 2,
                tileSize / 2 + 2
            );
        }
    }

    void move() {
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        // Check collision between Pac-Man and walls
        for (Block wall : walls) {
            if (checkCollision(pacman, wall)
                    || pacman.x < 0
                    || pacman.x + pacman.width > width) {

                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
            }
        }

        for (Ghost ghost : ghosts) {

            // Check collision between Pac-Man and a ghost
            if (checkCollision(pacman, ghost)) {
                lives--;

                if (lives == 0) {
                    gameOver = true;

                } else {
                    pacman.reset();
                    pacman.velocityX = 0;
                    pacman.velocityY = 0;

                    for (Ghost ghost2 : ghosts) {
                        ghost2.reset();

                        int ghostRow = ghost2.y / tileSize;
                        int ghostCol = ghost2.x / tileSize;
                        int pacmanRow = pacman.y / tileSize;
                        int pacmanCol = pacman.x / tileSize;

                        List<int[]> path = AStar.aStar(
                            ghostRow,
                            ghostCol,
                            pacmanRow,
                            pacmanCol
                        );

                        if (path != null && path.size() > 1) {
                            int[] nextStep = path.get(1);

                            int nextRow = nextStep[0];
                            int nextCol = nextStep[1];

                            if (nextRow > ghostRow) {
                                ghost2.updateDirection('D');
                            } else if (nextRow < ghostRow) {
                                ghost2.updateDirection('U');
                            } else if (nextCol > ghostCol) {
                                ghost2.updateDirection('R');
                            } else if (nextCol < ghostCol) {
                                ghost2.updateDirection('L');
                            }
                        }
                    }
                }
            }

            if (ghost.y == tileSize * 9
                    && ghost.direction != 'U'
                    && ghost.direction != 'D') {

                ghost.updateDirection('U');
            }

            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;

            for (Block wall : walls) {
                if (checkCollision(ghost, wall)
                        || ghost.x < 0
                        || ghost.x + ghost.width > width) {

                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;

                    ghost.direction = directions[
                        random.nextInt(directions.length)
                    ];

                    ghost.updateVelocity();
                }
            }
        }

        // Check food collision
        Block eatenFood = null;

        for (Block food : foods) {
            if (checkCollision(pacman, food)) {
                eatenFood = food;
                break;
            }
        }

        if (eatenFood != null) {
            foods.remove(eatenFood);
            gameObjects.remove(eatenFood);
            score += 10;
        }
    }

    public boolean checkCollision(Block a, Block b) {
        return a.x < b.x + b.width
            && a.x + a.width > b.x
            && a.y < b.y + b.height
            && a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();

        if (gameOver) {
            gameLoop.stop();
        }

        if (foods.size() == 0) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            pacman.updateDirection('U');

        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            pacman.updateDirection('D');

        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            pacman.updateDirection('L');

        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
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
