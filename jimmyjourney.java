import java.io.*;
import java.awt.*;
import java.util.Scanner;
import javax.swing.*;
import java.awt.event.*;
import java.util.HashSet;
import java.awt.image.*;
//
public class jimmyjourney {
    //formality class, please
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
    static int lvl;
    static Player p;
    static Level[] levels;
    static Room room;  //what room we are in
    final int xsz = 1280, ysz = 720;
    public RealFrame() throws IOException{
        art = new Timer(10, this);
        p = new Player();
        levels = new Level[nLevels];
        //define levels[0]
        levels[0] = new Level(new Color(25, 15, 15));
        //define levels[1]
        levels[1] = new Level(new Color(50, 200, 50));
        //define levels[2]
        levels[2] = new Level(new Color(200, 200, 200));
        load();
        setSize(xsz, ysz);
        addMouseListener(this);
		addKeyListener(this);
        setFocusable(true);
        setVisible(true);
        lvl = 0;
        room = levels[lvl].begin;
        art.start();
    }

    void load() throws IOException{
        int laval = 0;
        Scanner sc = new Scanner(new File("level.txt"));
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            Scanner bruh = new Scanner(line);

            if (!line.equals("")) {  //data
                Room uh = new Room();
                int r = bruh.nextInt(), c = bruh.nextInt();
                uh.r = r; uh.c = c;
                //TODO:read more data
                //PREREQ: figure out enemy data
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
        g.setColor(levels[lvl].bg);
        g.fillRect(0, 0, xsz, ysz);
        g.setColor(Color.WHITE);
        g.drawString("LEVEL" + lvl, 100, 100);
        g.drawString("ROOM " + room.r + ", " + room.c, 200, 200);
        p.render(g);
    }
    
    public void actionPerformed(ActionEvent e) {
        p.update();
        repaint();
    }
    public void keyPressed(KeyEvent e) {
        p.input(e);
    }
    public void keyReleased(KeyEvent e) {
        p.unput(e);
    }
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
    Color bg;
    //we need colors for each one
    //each room will look the same the only diff is 
    //obstacles and enemies
    //guys we are not making a minimap sorry
    //unless yale wants to try it
    //instead we are making it more "fun" for the player to 
    //map out where they are and where they need to go
    //we will need different side obstacles if we do 
    //obstacles to show where you can go
    //additionally we will not be trying collisions with the obstacles
    //just make it so that they cant go up or down 
    public Level(Color c) {
        bg = c;
    }
}
class Player {
    int x, y;
    final int speed = 5;
    final int width = 20, height = 60;
    boolean swap = true;
    int health = 1000;
    private final int W = 0, A = 1, S = 2, D = 3;
    boolean[] wasd = new boolean[4];
    

    public void input(KeyEvent e) {  //stuff happened
        if (e.getKeyCode() == (KeyEvent.VK_W)) {
            wasd[W] = true;
        }   else if (e.getKeyCode() == (KeyEvent.VK_A)) {
            wasd[A] = true;
        }   else if (e.getKeyCode() == (KeyEvent.VK_S)) {
            wasd[S] = true;
        }   else if (e.getKeyCode() == (KeyEvent.VK_D)) {
            wasd[D] = true;
        }
        //add swinging
    }
    public void unput(KeyEvent e) {  //stuff unhappened
        if (e.getKeyCode() == (KeyEvent.VK_W)) {
            wasd[W] = false;
        }   else if (e.getKeyCode() == (KeyEvent.VK_A)) {
            wasd[A] = false;
        }   else if (e.getKeyCode() == (KeyEvent.VK_S)) {
            wasd[S] = false;
        }   else if (e.getKeyCode() == (KeyEvent.VK_D)) {
            wasd[D] = false;
        }
    }
    public void update() {
        //pos
        int dy = 0, dx = 0;
        if (wasd[W]) { dy -= speed; }
        if (wasd[A]) { dx -= speed; }
        if (wasd[S]) { dy += speed; }
        if (wasd[D]) { dx += speed; }
        if (dy != 0 && dx != 0) {
            if (swap) { dx = dx * 3/5; dy = dy * 4/5; }
            else { dx = dx * 4/5; dy = dy * 3/5; }
            swap = !swap;
        }
        x += dx;
        y += dy;

    }
    public void render(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(x, y, width, height);
    }
}
class Room {
    Enemy[] enemies;
    //i assume we dont actually need that much here
    int r, c;
}
class Enemy {
    //do later
}