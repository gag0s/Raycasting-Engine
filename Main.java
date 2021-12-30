import javax.swing.JFrame;

// Creates the JFrame
public class Main {

    public static void main(String[] args){
        JFrame frame = new JFrame("Raycasting Engine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new GamePanel());
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
