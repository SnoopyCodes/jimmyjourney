import java.io.*;
import java.awt.*;
import java.util.Scanner;
import javax.swing.*;
import java.awt.event.*;
import java.util.HashSet;
import java.awt.image.*;
//
public class jimmyjourney {
    //formality class, ignore
    static JFrame frame;
    public static void main(String[] args) throws IOException{
        //run stuff
        frame = new JFrame();
        RealFrame real = new RealFrame();
        frame.setSize(real.getSize());
        frame.add(real);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
class RealFrame extends JPanel implements ActionListener, MouseListener, KeyListener {
    final static int nLevels = 3;  //numlevels
    private Timer art;
    static int lvl = 0;
    static Level[] levels = new Level[nLevels];
    static Room room;  //what room we are in
    final int xsz = 1280, ysz = 720;
    public RealFrame() throws IOException{
        art = new Timer(10, this);
        for (int i = 0; i < nLevels; i++) {
            levels[i] = new Level();
        }
        load();
        setSize(xsz, ysz);
        addMouseListener(this);
		addKeyListener(this);
        setVisible(true);
        art.start();
        lvl = 0;
        room = levels[lvl].begin;
    }

    void load() throws IOException{
        int laval = 0;
        Scanner sc = new Scanner(new File("level.txt"));
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            Scanner bruh = new Scanner(line);

            if (!line.equals("")) {  //data
                Room uh = new Room();
                System.out.println(line);
                int r = bruh.nextInt(), c = bruh.nextInt();
                uh.r = r; uh.c = c;
                //TODO:read more data
                //PREREQ: figure out enemy
                System.out.println(laval);
                System.out.println(levels[laval]);
                levels[laval].rooms.add(uh);
                if (levels[laval].begin == null) {
                    levels[laval].begin = uh;
                }
            }   else {
                laval++;
            }
            bruh.close();
        }
        sc.close();
    }

    public void paintComponent(Graphics g) {
        g.drawString("LEVEL" + lvl, 100, 100);
        g.drawString("ROOM " + room.r + ", " + room.c, 200, 200);
    }
    
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
    public void keyPressed(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e){}
	
	public void mousePressed(MouseEvent e) {}
	
	public void mouseReleased(MouseEvent e){}
	public void mouseClicked(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}

}
class Level {
    Room begin;
    HashSet<Room> rooms = new HashSet<Room>();

    //we need colors, obstacles for each one
    //each room will look the same the only diff is 
    //obstacles and enemies
    //guys we are not making a minimap sorry
    //unless yale wants to try it
    //instead we are making it more "fun" for the player to 
    //map out where they are and where they want to go
    //we will need different side obstacles if we do 
    //obstacles to show where you can go
    //additionally we will not be trying collisions with the obstacles
    //just make it so that they cant go up or down 
    public Level() {}
}
class Player {
    int x, y;
    
}
class Room {
    Enemy[] enemies;
    //i assume we dont actually need that much here
    int r, c;
}
class Enemy {
    //do later
}