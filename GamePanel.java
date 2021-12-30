import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

// Combines and draws the map, player and the rays
public class GamePanel extends JPanel implements Runnable, KeyListener {

    // FIELDS
    public static int[][] MAP = {{1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                                 {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                                 {1, 0, 1, 0, 0, 0, 1, 1, 0, 1},
                                 {1, 0, 1, 0, 0, 0, 0, 1, 0, 1},
                                 {1, 0, 0, 0, 1, 1, 0, 0, 0, 1},
                                 {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                                 {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                                 {1, 0, 1, 0, 0, 1, 0, 0, 1, 1},
                                 {1, 0, 1, 0, 0, 0, 0, 0, 0, 1},
                                 {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}};
    public static final int cellSize = 80;
    public static int WIDTH = MAP[0].length * cellSize;
    public static int HEIGHT = MAP.length * cellSize;

    private Thread thread;
    private boolean running;

    private BufferedImage image;
    private Graphics2D g;

    private Player p;
    private boolean threeD; // for switching views

    // CONSTRUCTOR
    public GamePanel() {
        super();
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocus();
    }

    // FUNCTIONS
    public void addNotify() {
        super.addNotify();
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
        addKeyListener(this);
    }

    public void run() {

        running = true;
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = (Graphics2D) image.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        long startTime;
        long waitTime;

        int maxFrameCount = 60;
        long targetTime = 1000 / maxFrameCount;

        p = new Player();
        threeD = true;

        // GAME LOOP
        while (running) {

            startTime = System.nanoTime();

            gameUpdate();
            gameRender();
            gameDraw();

            waitTime = targetTime - (System.nanoTime() - startTime) / 1000000;

            try{
                Thread.sleep(waitTime);
            } catch (Exception ignored) {

            }

        }

    }

    private void gameUpdate() {
        p.update();
    }

    private void gameRender() {

        // background
        g.setColor(new Color(0, 0, 0));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // sky
        if(threeD) {
            g.setColor(new Color(104, 24, 24));
            g.fillRect(0, 0, WIDTH, HEIGHT / 2);
        }

        // walls
        if(!threeD) {
            g.setColor(new Color(40, 40, 190));
            for (int i = 0; i < MAP.length; i++) {
                for (int j = 0; j < MAP[0].length; j++) {
                    if (MAP[i][j] != 0) {
                        g.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
                    }
                }
            }
        }

        // 64 rays to the left
        for (int i = 0; i < 64; i++) {
            double angle = p.getAngle() + Math.PI/4 - i*Math.PI/256;
            if(angle < 0) { angle += 2*Math.PI; }
            if(angle > 2*Math.PI) { angle -= 2*Math.PI; }
            p.draw(g, angle, threeD);

            if(threeD) { // 3D line
                int distance = (int) (p.getDistance() * Math.cos(Math.PI/4-i*Math.PI/256)); // new distance; fixing fish-eye-effect

                g.setColor(new Color(0, 0, 255 - distance/5));
                if (p.getHorizontal()) {
                    if(distance/5 > 150) { // out of range exception
                        g.setColor(new Color(0, 0, 0));
                    } else {
                        g.setColor(new Color(0, 0, 150 - distance / 5));
                    }
                }

                g.fillRect(i * GamePanel.WIDTH / 129, GamePanel.HEIGHT / 2 - (300 - distance / 2), GamePanel.WIDTH / 129 + 1, 600 - distance);
            }
        }

        // 64 rays to the right
        for (int i = 0; i < 64; i++) {
            double angle = p.getAngle() - i*Math.PI/256;
            if(angle < 0) { angle += 2*Math.PI; }
            if(angle > 2*Math.PI) { angle -= 2*Math.PI; }
            p.draw(g, angle, threeD);

            if(threeD) { // 3D line
                int distance = (int) (p.getDistance() * Math.cos(-i*Math.PI/256)); // new distance; fixing fish-eye-effect

                g.setColor(new Color(0, 0, 255 - distance/5));
                if (p.getHorizontal()) {
                    if(distance/5 > 150) { // out of range exception
                        g.setColor(new Color(0, 0, 0));
                    } else {
                        g.setColor(new Color(0, 0, 150 - distance / 5));
                    }
                }

                g.fillRect((i + 65) * GamePanel.WIDTH / 129, GamePanel.HEIGHT / 2 - (300 - distance / 2), GamePanel.WIDTH / 129 + 1, 600 - distance);
            }
        }

        // last true ray
        p.draw(g, p.getAngle(), threeD);

        if(threeD) { // 3D line
            int distance = (int) p.getDistance(); // new distance; fixing fish-eye-effect

            g.setColor(new Color(0, 0, 255 - distance/5));
            if (p.getHorizontal()) {
                if(distance/5 > 150) { // out of range exception
                    g.setColor(new Color(0, 0, 0));
                } else {
                    g.setColor(new Color(0, 0, 150 - distance / 5));
                }
            }
            g.fillRect(64 * GamePanel.WIDTH / 129, GamePanel.HEIGHT / 2 - (300 - distance / 2), GamePanel.WIDTH / 129 + 1, 600 - distance);
        }

        // Press "E" to switch view
        g.setColor(Color.white);
        g.drawString("Press \"E\" to switch view", 10, 20);
    }

    private void gameDraw() {
        Graphics g2 = this.getGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
    }

    @Override
    public void keyTyped(KeyEvent key) {

    }

    @Override
    public void keyPressed(KeyEvent key) {

        int keyCode = key.getKeyCode();

        // direction
        if (keyCode == KeyEvent.VK_W) {
            p.setForward(true);
        }
        if (keyCode == KeyEvent.VK_A) {
            p.setTurnLeft(true);
        }
        if (keyCode == KeyEvent.VK_D) {
            p.setTurnRight(true);
        }

    }

    @Override
    public void keyReleased(KeyEvent key) {

        int keyCode = key.getKeyCode();

        // directions
        if (keyCode == KeyEvent.VK_W) {
            p.setForward(false);
        }
        if (keyCode == KeyEvent.VK_A) {
            p.setTurnLeft(false);
        }
        if (keyCode == KeyEvent.VK_D) {
            p.setTurnRight(false);
        }

        // 3D on/off
        if (keyCode == KeyEvent.VK_E) {
            threeD ^= true;
        }

    }

}
