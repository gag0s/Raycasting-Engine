import java.awt.*;

// Handles the player, movement and the rays
public class Player {

    // FIELDS
    private final int size;
    private final double speed;
    private final double angleSpeed;
    private int x;
    private int y;
    private double angle;
    private double dx;
    private double dy;

    private double distance;
    private boolean horizontal;

    private boolean forward;
    private boolean turnLeft;
    private boolean turnRight;

    // CONSTRUCTOR
    public Player() {
        size = GamePanel.cellSize;

        x = freeTile(false) * size + size / 2;
        y = freeTile(true) * size + size / 2;

        speed = 0.07;
        angle = 0;
        angleSpeed = 0.1;

        dx = 0;
        dy = 0;

        distance = 0;
        horizontal = true;

        forward = false;
        turnLeft = false;
        turnRight = false;
    }

    // FUNCTIONS
    public void update() {

        // forward
        if (forward) {
            double dxs = dx * speed;
            double dys = dy * speed;
            // check if new position would be on a tile
            if (GamePanel.MAP[(int) ((y+dys)/size)][(int) ((x+dxs)/size)] != 1) {
                x += dxs;
                y += dys;
            }

            // preventing index error
            if (y < 80) {
                y = 80;
            }
            if (x < 80) {
                x = 80;
            }

            if (y > 720) {
                y = 720;
            }
            if (x > 720) {
                x = 720;
            }

        }

        // directions
        if (turnLeft) {
            angle += angleSpeed;
            if (angle > 2 * Math.PI) {
                angle -= 2 * Math.PI;
            }
        }
        if (turnRight) {
            angle -= angleSpeed;
            if (angle < 0) {
                angle += 2 * Math.PI;
            }
        }

    }

    // draws player and the rays
    public void draw(Graphics2D g, double ang, boolean threeD) {

        dx = Math.sin(ang) * size / 2;
        dy = Math.cos(ang) * size / 2;

        if (!threeD) {
            // circle
            g.setColor(new Color(255, 0, 0));
            g.fillOval(x - size / 4, y - size / 4, size / 2, size / 2);
        }

        // ---------- Calculating rays ----------

        // calculating hitpoint horizontal
        int yr = 0;
        int deltay = 0;
        double factor = 0;
        double newDx = 0;
        double distanceX = 0;
        double distanceY = 0;
        distance = 0;
        horizontal = true;

        g.setColor(new Color(255, 200, 0));

        if (Math.PI / 2 <= ang && ang < 1.5 * Math.PI) { // up
            yr = y / size;
            yr *= size;
            deltay = yr - y; // distance to above line
            factor = deltay / dy; // adjusting dy to distance
            newDx = dx * factor; // multiplying dx with same factor

            // check first point

            if (x + newDx <= GamePanel.WIDTH && x + newDx >= 0) { // indexOutOfBounds
                if (GamePanel.MAP[y / size - 1][(int) ((x + newDx) / size)] != 1 && // wall above point
                        GamePanel.MAP[y / size][(int) ((x + newDx) / size)] != 1) { // wall below point

                    double yTo1 = size / dy;
                    double adjX = yTo1 * dx;

                    // 9 more points
                    for (int i = 1; i < GamePanel.MAP.length; i++) {

                        // check every point, if it collided
                        if (x + newDx - adjX * i >= 0 && x + newDx - adjX * i <= GamePanel.WIDTH && (y + deltay - size * i) / size - 1 > 0) { // out of view
                            if (GamePanel.MAP[(y + deltay - size * i) / size - 1][(int) (x + newDx - adjX * i) / size] == 1 || // wall above
                                    GamePanel.MAP[(y + deltay - size * i) / size][(int) (x + newDx - adjX * i) / size] == 1) { // wall below
                                distanceX = newDx - adjX * i;
                                distanceY = deltay - size * i;
                                break;
                            }
                        } else {
                            distanceX = newDx - adjX * i;
                            distanceY = deltay - size * i;
                            break;
                        }

                    }
                }
            }

        } else { // down
            yr = (y + size) / size;
            yr *= size;
            deltay = yr - y; // distance to below line
            factor = deltay / dy; // adjusting dy to distance
            newDx = dx * factor; // multiplying dx with same factor

            // check first point
            if (x + newDx <= GamePanel.WIDTH && x + newDx >= 0) { // indexOutOfBounds
                if (GamePanel.MAP[y / size][(int) ((x + newDx) / size)] != 1 && // wall above point
                        GamePanel.MAP[y / size + 1][(int) ((x + newDx) / size)] != 1) { // wall below point

                    double yTo1 = size / dy;
                    double adjX = yTo1 * dx;

                    // 9 more points
                    for (int i = 1; i < GamePanel.MAP.length; i++) {

                        // check every point, if it collided
                        if (x + newDx + adjX * i >= 0 && x + newDx + adjX * i <= GamePanel.WIDTH && (y + deltay + size * i) / size - 1 > 0) { // out of view
                            if (GamePanel.MAP[(y + deltay + size * i) / size - 1][(int) (x + newDx + adjX * i) / size] == 1 || // wall above point
                                    GamePanel.MAP[(y + deltay + size * i) / size][(int) (x + newDx + adjX * i) / size] == 1) { // wall below point
                                distanceX = newDx + adjX * i;
                                distanceY = deltay + size * i;
                                break;
                            }
                        } else {
                            distanceX = newDx + adjX * i;
                            distanceY = deltay + size * i;
                            break;
                        }

                    }

                }
            }
        }

        // adjust disX & disY, if first point collided
        if (distanceX == 0 && distanceY == 0) {
            distanceX = newDx;
            distanceY = deltay;
        }
        // calculate distance
        distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);

        // calculating hitpoint vertical
        int xr = 0;
        int deltax = 0;
        double factor2 = 0;
        double newDy = 0;
        double distanceXX = 0;
        double distanceYY = 0;

        g.setColor(new Color(100, 255, 0));

        if (Math.PI <= ang) { // left
            xr = x / size;
            xr *= size;
            deltax = xr - x; // distance to left line
            factor2 = deltax / dx; // adjusting dy to distance
            newDy = dy * factor2; // multiplying dy with same factor

            // check first point
            if (y + newDy <= GamePanel.HEIGHT && y + newDy >= 0) { // indexOutOfBounds
                if (GamePanel.MAP[(int) ((y + newDy) / size)][x / size - 1] != 1 && // wall left from point
                        GamePanel.MAP[(int) ((y + newDy) / size)][x / size] != 1) { // wall right from point

                    double xTo1 = size / dx;
                    double adjY = xTo1 * dy;

                    // 9 more points
                    for (int i = 1; i < GamePanel.MAP.length; i++) {

                        // check every point, if it collided
                        if (y + newDy - adjY * i >= 0 && y + newDy - adjY * i <= GamePanel.HEIGHT && (x + deltax - size * i) / size - 1 > 0) { // out of view
                            if (GamePanel.MAP[(int) (y + newDy - adjY * i) / size][(x + deltax - size * i) / size - 1] == 1 || // wall above
                                    GamePanel.MAP[(int) (y + newDy - adjY * i) / size][(x + deltax - size * i) / size] == 1) { // wall below
                                distanceXX = deltax - size * i;
                                distanceYY = newDy - adjY * i;
                                break;
                            }
                        } else {
                            distanceXX = deltax - size * i;
                            distanceYY = newDy - adjY * i;
                            break;
                        }

                    }

                }
            }
        } else { // right
            xr = (x + size) / size;
            xr *= size;
            deltax = xr - x; // distance to left line
            factor2 = deltax / dx; // adjusting dy to distance
            newDy = dy * factor2; // multiplying dy with same factor

            // check first point
            if (y + newDy <= GamePanel.HEIGHT && y + newDy >= 0) { // indexOutOfBounds
                if (GamePanel.MAP[(int) ((y + newDy) / size)][x / size] != 1 && // wall left from point
                        GamePanel.MAP[(int) ((y + newDy) / size)][x / size + 1] != 1) { // wall right from point

                    double xTo1 = size / dx;
                    double adjY = xTo1 * dy;

                    // 9 more points
                    for (int i = 1; i < GamePanel.MAP[0].length; i++) {

                        // check every point, if it collided
                        if (y + newDy + adjY * i >= 0 && y + newDy + adjY * i <= GamePanel.HEIGHT && (x + deltax + size * i) / size - 1 > 0) { // out of view
                            if (GamePanel.MAP[(int) (y + newDy + adjY * i) / size][(x + deltax + size * i) / size - 1] == 1 || // wall above point
                                    GamePanel.MAP[(int) (y + newDy + adjY * i) / size][(x + deltax + size * i) / size] == 1) { // wall below point
                                distanceXX = deltax + size * i;
                                distanceYY = newDy + adjY * i;
                                break;
                            }
                        } else {
                            distanceXX = deltax + size * i;
                            distanceYY = newDy + adjY * i;
                            break;
                        }

                    }
                }
            }
        }

        // adjust disXX & disYY, if first point collided
        if (distanceXX == 0 && distanceYY == 0) {
            distanceXX = deltax;
            distanceYY = newDy;
        }

        // calculate final distance
        g.setColor(new Color(255, 255, 255));
        if (Math.sqrt(distanceXX * distanceXX + distanceYY * distanceYY) < distance) {
            distance = Math.sqrt(distanceXX * distanceXX + distanceYY * distanceYY);
            horizontal = false;

            // draw center-collision-point
            if(!threeD) {
                g.drawLine(x, y,(int) (x + distanceXX), (int) (y + distanceYY));
            }
        } else {
            if(!threeD) {
                g.drawLine(x, y,(int) (x + distanceX), (int) (y + distanceY));
            }
        }

    }

    private int freeTile(boolean woh) {
        for (int i = 0; i < GamePanel.MAP.length; i++) {
            for (int j = 0; j < GamePanel.MAP[0].length; j++) {
                if (GamePanel.MAP[i][j] == 0) {
                    if (woh) {
                        return i;
                    } else {
                        return j;
                    }
                }
            }
        }
        return -1;
    }

    public double getAngle() {
        return angle;
    }

    public double getDistance() {
        return distance;
    }

    public boolean getHorizontal() {
        return horizontal;
    }

    public void setForward(boolean b) {
        this.forward = b;
    }

    public void setTurnLeft(boolean b) {
        this.turnLeft = b;
    }

    public void setTurnRight(boolean b) {
        this.turnRight = b;
    }

}
