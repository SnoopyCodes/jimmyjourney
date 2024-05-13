import java.io.*;
import java.awt.*;
import java.util.Scanner;
import javax.swing.*;
import java.awt.event.*;
import java.util.HashSet;
import java.awt.image.*;
import java.util.ArrayList;
import javax.sound.sampled.*;
import javax.imageio.*;

//
public class jimmyjourney {
    //formality class, please ignore
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
        frame.setUndecorated(true);
        frame.setVisible(true);
    }
}
class RealFrame extends JPanel implements ActionListener, KeyListener {
    final static int nLevels = 3;  //numlevels
    private Timer art;
    static int lvl;
    static Player p;
    static Level[] levels;
    static Room room;  //what room we are in
    static final int xsz = 1280, ysz = 720;
    public RealFrame() throws IOException{
        Sprite.init();
        Sound.play();
        art = new Timer(10, this);
        p = new Player();
        levels = new Level[nLevels];
        //define levels[0]
        levels[0] = new Level(Sprite.soulSoil, Sprite.netherrack);
        //define levels[1]
        levels[1] = new Level(Sprite.soulSoil, Sprite.netherrack);
        //define levels[2]
        levels[2] = new Level(Sprite.soulSoil, Sprite.netherrack);
        load();
        setSize(xsz, ysz);
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
			if (line.length() != 0 && line.charAt(0) == '/') { continue; }
            if (!line.equals("")) {  //data
                Room uh = new Room();
                int x = bruh.nextInt(), y = bruh.nextInt();
                uh.x = x; uh.y = y;
                levels[laval].rooms.add(uh);
                if (levels[laval].begin == null) {
                    levels[laval].begin = uh;
                }
                int jims = bruh.nextInt();
                for (int i = 0; i < jims; i++) {
                    String type = bruh.next();
                    int xc = bruh.nextInt(), yc = bruh.nextInt();
                    //me when the switch case
                    if (type.equals("melee")) {
                        uh.jimmies.add(new MeleeJimmy(xc, yc));
                    }   else if (type.equals("lightning")) {
                        uh.jimmies.add(new LightningJimmy(xc, yc));
                    }   else if (type.equals("bigmelee")) {
                        uh.jimmies.add(new BigMeleeJimmy(xc, yc));
                    }   else if (type.equals("tree")) {
                        uh.jimmies.add(new TreeJimmy(xc, yc));
                    }   else if (type.equals("laser")) {
                        uh.jimmies.add(new LaserJimmy(xc, yc));
                    }   else if (type.equals("magic")) {
                        uh.jimmies.add(new MagicJimmy(xc, yc));
                    }   else if (type.equals("boss")) {
                        uh.jimmies.add(new Boss(xc, yc, 35));
                    }
                }
				uh.woll = levels[lvl].woll;
            }   else {
                laval++;
            }
            bruh.close();
        }
        sc.close();

		for (Room r1 : RealFrame.levels[RealFrame.lvl].rooms) {
			for (Room r2 : RealFrame.levels[RealFrame.lvl].rooms) {
				if (r1.x == r2.x && r1.y == r2.y-1) {
					r2.dWoll = false;
				}
				if (r1.x == r2.x && r1.y == r2.y+1) {
					r2.uWoll = false;
				}
				if (r1.x == r2.x+1 && r1.y == r2.y) {
					r2.rWoll = false;
				}
				if (r1.x == r2.x-1 && r1.y == r2.y) {
					r2.lWoll = false;
				}
			}
		}

		Rectangle vertical1 = new Rectangle(0,0,64,720);
		Rectangle vertical2 = new Rectangle(1280-64,0,64,720);

		Rectangle verticalHole1 = new Rectangle(0,0,64,64*4);
		Rectangle verticalHole2 = new Rectangle(0,720-64*4,64,64*4);

		Rectangle verticalHole3 = new Rectangle(1280-64,0,64,64*4);
		Rectangle verticalHole4 = new Rectangle(1280-64,720-64*4,64,64*4);

		Rectangle horizontal1 = new Rectangle(0,0,1280,64);
		Rectangle horizontal2 = new Rectangle(0,720-64,1280,64);

		Rectangle horizontalHole1 = new Rectangle(0,0,64*9,64);
		Rectangle horizontalHole2 = new Rectangle(1280-64*9,0,64*9,64);

		Rectangle horizontalHole3 = new Rectangle(0,720-64,64*9,64);
		Rectangle horizontalHole4 = new Rectangle(1280-64*9,720-64,64*9,64);

		for (Room r : RealFrame.levels[RealFrame.lvl].rooms) {
			if (r.dWoll) {
				r.walls.add(horizontal1);
			}
			else {
				r.walls.add(horizontalHole1);
				r.walls.add(horizontalHole2);
			}

			if (r.uWoll) {
				r.walls.add(horizontal2);
			}
			else {
				r.walls.add(horizontalHole3);
				r.walls.add(horizontalHole4);
			}

			if (r.lWoll) {
				r.walls.add(vertical1);
			}
			else {
				r.walls.add(verticalHole1);
				r.walls.add(verticalHole2);
			}

			if (r.rWoll) {
				r.walls.add(vertical2);
			}
			else {
				r.walls.add(verticalHole3);
				r.walls.add(verticalHole4);
			}
		}
		
    }

    public void paintComponent(Graphics g) {
		for (int i = 0; i <= 1280 - 64; i += 128) {
			for (int j = 0; j <= 720 - 64; j += 128) {
				g.drawImage(levels[lvl].bg, i, j, null);
			}
		}
        g.drawString("LEVEL" + lvl, 100, 100);
        g.drawString("ROOM " + room.x + ", " + room.y, 200, 200);
        for (int i=0; i<Projectile.projectiles.size(); i++) {
			Projectile ps = Projectile.projectiles.get(i);
			ps.update();
			ps.render(g);
			if (Math.hypot(ps.pos[0]-600,ps.pos[1]-300) > 1000 || ps.life < -100) {
				Projectile.projectiles.remove(i);
				i--;
			}
			if ((ps.type.equals("magic") || ps.type.equals("laser")
            || ps.type.equals("sword"))&& p.rect.contains(new Point((int)ps.pos[0],(int)ps.pos[1]))) {
				Projectile.projectiles.remove(i);
				i--;
				p.health--;
				p.hurtTimer = 10;
			}
		}
        p.render(g);
        room.render(g);
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
	
}
class Level {
    Room begin;
    HashSet<Room> rooms = new HashSet<Room>();
    Image bg;
	Image woll;
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
	public Level(Image img, Image w) {
		bg = img;
		woll = w;
	}
}
class Player {
    int x = 200, y = 200;
    final int speed = 5;
    final int width = 20, height = 60;
	int attackTimer;
	int shootTimer;
	int hurtTimer;
	boolean attacking = false;
	Rectangle rect = new Rectangle(x,y,20,60);
	Rectangle swordRect = new Rectangle(x,y,40,20);
	boolean swap = true;
    boolean direction = true;
	HealthBar bar = new HealthBar(100,500,200,5,100,1);
    double health = 20;
    private final int W = 0, A = 1, S = 2, D = 3;
    boolean[] wasd = new boolean[4];
    
    public void input(KeyEvent e) {  //stuff happened
        if (e.getKeyCode() == (KeyEvent.VK_W)) {
            wasd[W] = true;
        }   else if (e.getKeyCode() == (KeyEvent.VK_A)) {
            wasd[A] = true;
            direction = false;
        }   else if (e.getKeyCode() == (KeyEvent.VK_S)) {
            wasd[S] = true;
        }   else if (e.getKeyCode() == (KeyEvent.VK_D)) {
            direction = true;
            wasd[D] = true;
        }   else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (attackTimer == 0) {
                attacking = true;
                attackTimer++;
            }
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
        bar.percent = (int)(health*5);
        //pos
        if (health <= 0) {
			System.exit(0);
			System.out.println("thou hast perished");
		}
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

        if (x + dx > RealFrame.xsz) {
            boolean happen = false;
            for (Room v : RealFrame.levels[RealFrame.lvl].rooms) {
                if (v.x == RealFrame.room.x + 1 && v.y == RealFrame.room.y) {
                    RealFrame.room = v;
                    x = 0;
                    happen = true;
                    break;
                }
            }
            if (!happen) { dx = 0; }
        }
        if (x + dx < 0) {
            boolean happen = false;
            for (Room v : RealFrame.levels[RealFrame.lvl].rooms) {
                if (v.x == RealFrame.room.x - 1 && v.y == RealFrame.room.y) {
                    RealFrame.room = v;
                    x = RealFrame.xsz;
                    happen = true;
                    break;
                }
            }
            if (!happen) {
                dx = 0;
            }
        }
        if (y + dy > RealFrame.ysz) {
            boolean happen = false;
            for (Room v : RealFrame.levels[RealFrame.lvl].rooms) {
                if (v.y == RealFrame.room.y + 1 && v.x == RealFrame.room.x) {
                    RealFrame.room = v;
                    y = 0;
                    happen = true;
                    break;
                }
            }
            if (!happen) { dy = 0; }
        }
        if (y + dy < 0) {
            boolean happen = false;
            for (Room v : RealFrame.levels[RealFrame.lvl].rooms) {
                if (v.y == RealFrame.room.y - 1 && v.x == RealFrame.room.x) {
                    RealFrame.room = v;
                    y = RealFrame.ysz;
                    happen = true;
                    break;
                }
            }
            if (!happen) {
                dy = 0;
            }
        }
        x += dx;
		rect.setLocation(x-width/2,y-height/2);
		for (Rectangle r : RealFrame.room.walls) {
			if (rect.intersects(r)) {
				x -= dx;
				rect.setLocation(x-width/2,y-height/2);
			}
		}

		y += dy;
		rect.setLocation(x-width/2,y-height/2);
		for (Rectangle r : RealFrame.room.walls) {
			if (rect.intersects(r)) {
				y -= dy;
				rect.setLocation(x-width/2,y-height/2);
			}
		}


        if (attackTimer > 0) {
			attackTimer++;
			if (attackTimer > 20) {
				attacking = false;
			}
			if (attackTimer > 30) {
				attackTimer = 0;
			}
		}

		if (direction) {
			swordRect.setLocation(x+20,y+20);
		}
		else {
			swordRect.setLocation(x-40,y+20);
		}

    }
    public void render(Graphics g) {
        g.setColor(Color.BLUE);
        //g.fillRect(x-width/2, y-height/2, width, height);//draw sword
		g.fillRect(rect.x,rect.y,rect.width,rect.height);
		//i died doing this
        Graphics2D g2 = (Graphics2D)(g);
		g2.setColor(new Color(150,100,50));
		g2.setStroke(new BasicStroke(5));
        int handx;
        if (direction) {
			handx = x+10;
		}
		else {
			handx = x-10;
		}
		if (direction && !attacking) {
			g2.drawLine(handx, y-5, handx+30, y-15);
		}
		if (direction && attacking) {
			g2.drawLine(handx, y-5, handx+30, y);
		}
		if (!direction && !attacking) {
			g2.drawLine(handx, y-5, handx-30, y-15);
		}
		if (!direction && attacking) {
			g2.drawLine(handx, y-5, handx-30, y);
		}
        bar.render(g);
    }
}
class Room {
    ArrayList<Jimmy> jimmies = new ArrayList<Jimmy>();
	ArrayList<Rectangle> walls = new ArrayList<Rectangle>();
	String txt = "";
	int sx, sy;
	Image woll;
	boolean lWoll = true, rWoll = true, uWoll = true, dWoll = true;
    void render(Graphics g) {
        for (int j = 0; j < jimmies.size(); j++) {
            jimmies.get(j).update(RealFrame.p);
            jimmies.get(j).checkHurt(RealFrame.p);
			if (jimmies.get(j).health <= 0) { jimmies.remove(j); j--; continue; }
            jimmies.get(j).render(g);
            jimmies.get(j).bump(jimmies);
        }
		if (txt != "") { g.drawString(txt, sx, sy); }

		//System.out.println(lWoll + " " + rWoll + " " + uWoll + " " + dWoll);
		//leftWoll
		/* 
		if (lWoll) {
			for (int y = 0; y + 64 <= 720; y+=64) {
				g.drawImage(woll, 0, y, null);
			}
		}	else {
			for (int y = 0; y + 64 <= 360 - 64; y += 64) {
				g.drawImage(woll, 0, y, null);
			}
			for (int y = 360 + 64; y + 64 <= 720; y += 64) {
				g.drawImage(woll, 0, y, null);
			}
		}
		if (uWoll) {
			for (int y = 0; y + 64 <= 1280; y+=64) {
				g.drawImage(woll, y, 720-64, null);
			}
		}	else {
			for (int y = 0; y + 64 <= 640 - 64; y += 64) {
				g.drawImage(woll, y, 720-64, null);
			}
			for (int y = 640 + 64; y + 64 <= 1280; y += 64) {
				g.drawImage(woll, y, 720-64, null);
			}
		}
		if (rWoll) {
			for (int y = 0; y + 64 <= 720; y+=64) {
				g.drawImage(woll, 1280-64, y, null);
			}
		}	else {
			for (int y = 0; y + 64 <= 360 - 64; y += 64) {
				g.drawImage(woll, 1280-64, y, null);
			}
			for (int y = 360 + 64; y + 64 <= 720; y += 64) {
				g.drawImage(woll, 1280-64, y, null);
			}
		}
		if (dWoll) {
			for (int y = 0; y + 64 <= 1280; y+=64) {
				g.drawImage(woll, y, 0, null);
			}
		}	else {
			for (int y = 0; y + 64 <= 640; y += 64) {
				g.drawImage(woll, y, 0, null);
			}
			for (int y = 640 + 64; y + 64 <= 1280; y += 64) {
				g.drawImage(woll, y, 0, null);
			}
		}*/
		for (Rectangle r : walls) {
			//g.fillRect(r.x,r.y,r.width,r.height);
			renderWall(g, r, woll);
		}
    }
    int x, y;

	public void renderWall(Graphics g, Rectangle r, Image image) {
		int ver = r.height/image.getHeight(null);
		int hor = r.width/image.getWidth(null);
		for (int i=0; i<hor; i++) {
			for (int j=0; j<ver; j++) {
				g.drawImage(image, 
				i*image.getHeight(null)+r.x, 
				j*image.getWidth(null)+r.y, null);
			}
		}
	}
}
class HealthBar
{
	int x, y, length, height, percent, border;
	public HealthBar(int a, int b, int l, int h, int p, int i) {
		x = a;
		y = b;
		length = l;
		height = h;
		percent = p;
		border = i;
	}
	public void render(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(x-border,y-border,length+2*border,height+2*border);
		g.setColor(Color.GRAY);
		g.fillRect(x,y,length,height);
		g.setColor(Color.RED);
		g.fillRect(x,y,length*percent/100,height);
	}
}
class Jimmy
{
	int health, maxHealth;
	double x, y;
	int cooldown;
	double angle;
	int hurtTimer;
	double knockback;
	
	Image image, normalImage, hurtImage;
	Rectangle rect;
	HealthBar bar;
	
	//no way an actual use for polymorphism
	public Jimmy() {}
	public void update(Player p) {
	}
	public void render(Graphics g) {
		g.drawImage(image,(int)x,(int)y,null);
		bar.percent = (int)((double)health/maxHealth*100);
		bar.render(g);
	}
	public void checkHurt(Player p) {
		boolean hurt = false;
		
		if (hurtTimer > 0) { //damage ticking: after the jimmy is hurt it cant be hurt again for 20 frames to prevent weird stuff
			hurtTimer--;
			if (hurtTimer <= 0) {
				image = normalImage;
			}
		}
		
		if (hurtTimer == 0 && p.attacking && rect.intersects(p.swordRect)) {
			if (p.direction) {
				x += 30*knockback;
			}
			else {
				x -= 30*knockback;
			}
			hurt = true;
		}
		
		if (hurt) {
			health--;
			hurtTimer = 20; //20 frames is also the length of the melee attack animation
			image = hurtImage;
			Sound.play();
		}
	}
	public void bump(ArrayList<Jimmy> jimmies) { //this stops the jimmies from going into each other and becoming indistinct
		for (Jimmy i : jimmies) {
			if (rect.intersects(i.rect) && x != i.x && y != i.y) { //the second and third conditions are to make it not check for itself
				double angle = Math.atan2(rect.getCenterX()-i.rect.getCenterX(),rect.getCenterY()-i.rect.getCenterY());
				x += Math.sin(angle)*knockback;
				y += Math.cos(angle)*knockback;
				i.x -= Math.sin(angle)*i.knockback;
				i.y -= Math.cos(angle)*i.knockback;
			}
		}
	}
	
}


class MeleeJimmy extends Jimmy
{
	public MeleeJimmy(double a, double b) {
		x = a;
		y = b;
		health = maxHealth = 4;
		knockback = 1;
		normalImage = Sprite.jimmyNormal;
		hurtImage = Sprite.jimmyHurt;
		image = normalImage;
		rect = new Rectangle((int)x,(int)y,45,65); //45 and 65 are the dimensions of the image
		bar = new HealthBar((int)x,(int)y-10,45,3,100,1);
	}
	
	public void update(Player p) {
		double dist = Math.hypot(p.rect.getCenterX()-rect.getCenterX(),p.rect.getCenterY()-rect.getCenterY());
		if (dist > 25 && dist < 500) {
			angle = Math.atan2(p.rect.getCenterX()-rect.getCenterX(),p.rect.getCenterY()-rect.getCenterY());
			x += 2*Math.sin(angle);
			y += 2*Math.cos(angle);
		}
		
			rect.setLocation((int)x,(int)y);
			bar.x = (int)x;
			bar.y = (int)y-10;
		
		if (rect.intersects(p.rect)) {
			cooldown++;
			if (cooldown >= 50) { //after being in contact with the player for 50 frames the melee jimmy will attack
				p.hurtTimer = 10;
				p.health--;
				cooldown = 0;
			}
		}
		else {
			cooldown = 0;
		}
	}
}


class BigMeleeJimmy extends MeleeJimmy
{
	public BigMeleeJimmy(double a, double b) {
		super(a,b);
		health = maxHealth = 15;
		knockback = 0.5;
		normalImage = Sprite.jimmyBig;
		hurtImage = Sprite.jimmyBigHurt;
		image = normalImage; //82 x 121
		rect = new Rectangle((int)x,(int)y,82,121);
		bar = new HealthBar((int)x,(int)y-10,82,4,100,1);
	}
	
	public void update(Player p) {
		double dist = Math.hypot(p.rect.getCenterX()-rect.getCenterX(),p.rect.getCenterY()-rect.getCenterY());
		if (dist > 40 && dist < 800) {
			angle = Math.atan2(p.rect.getCenterX()-rect.getCenterX(),p.rect.getCenterY()-rect.getCenterY());
			x += 3*Math.sin(angle);
			y += 3*Math.cos(angle);
		}
		
			rect.setLocation((int)x,(int)y);
			bar.x = (int)x;
			bar.y = (int)y-10;
		
		if (rect.intersects(p.rect)) {
			cooldown++;
			if (cooldown >= 30) {
				p.hurtTimer = 10;
				p.health -= 2;
				cooldown = 0;
			}
		}
		else {
			cooldown = 0;
		}
	}
}


class LightningJimmy extends Jimmy
{
	ArrayList<Point> lightningPoints = new ArrayList<Point>();
	
	public LightningJimmy(double a, double b) {
		x = a;
		y = b;
		health = maxHealth= 4;
		knockback = 1;
		normalImage = Sprite.jimmyGreen;
		hurtImage = Sprite.jimmyHurt;
		image = normalImage;
		rect = new Rectangle((int)x,(int)y,45,65);
		bar = new HealthBar((int)x,(int)y-10,45,3,100,1);
	}
	
	public void update(Player p) {
		double dist = Math.hypot(p.rect.getCenterX()-rect.getCenterX(),p.rect.getCenterY()-rect.getCenterY());
		
		if (dist > 300 && cooldown < 100) {
			angle = Math.atan2(p.rect.getCenterX()-rect.getCenterX(),p.rect.getCenterY()-rect.getCenterY());
			x += Math.sin(angle);
			y += Math.cos(angle);
		}
			rect.setLocation((int)x,(int)y);
			bar.x = (int)x;
			bar.y = (int)y-10;
		
		if (dist < 400) {
			cooldown++;
			if (cooldown == 100) {
				angle = Math.atan2(p.rect.getCenterX()-rect.getCenterX(),p.rect.getCenterY()-rect.getCenterY());
			}
			if (cooldown > 100) {
				//code for lightning attack
				lightningPoints = new ArrayList<Point>();
				int tX = (int) rect.getCenterX();
				int tY = (int) rect.getCenterY();
				for (int i=0; i<10; i++) {
					lightningPoints.add(new Point(tX,tY));
					tX += (int)(40*Math.sin(angle));
					tY += (int)(40*Math.cos(angle));
				}
				for (int i=0; i<lightningPoints.size(); i++) {
					lightningPoints.get(i).translate((int)(Math.random()*30-30),(int)(Math.random()*30-30));
					if (p.rect.contains(lightningPoints.get(i))) {
						p.hurtTimer = 10;
						p.health -= 0.03;
					}
				}
			}
			if (cooldown >= 200) {
				cooldown = 0;
			}
		}
		else {
			cooldown = 0;
		}
		if (cooldown == 0) {
			lightningPoints = new ArrayList<Point>();
		}
	}
	
	public void render(Graphics g) {
		//render lightning
		for (int i=0; i<lightningPoints.size()-1; i++) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(0,255,0));
			g2.setStroke(new BasicStroke(2));
			Point p1 = lightningPoints.get(i);
			Point p2 = lightningPoints.get(i+1);
			g2.drawLine((int)p1.getX(),(int)p1.getY(),(int)p2.getX(),(int)p2.getY());
		}
		g.drawImage(image,(int)x,(int)y,null);
		bar.percent = (int)((double)health/4*100);
		bar.render(g);
	}
}


class LaserJimmy extends Jimmy
{
	public LaserJimmy(double a, double b) {
		x = a;
		y = b;
		health = maxHealth = 4;
		knockback = 1;
		normalImage = Sprite.jimmyBlue;
		hurtImage = Sprite.jimmyHurt;
		image = normalImage;
		rect = new Rectangle((int)x,(int)y,45,65);
		bar = new HealthBar((int)x,(int)y-10,45,3,100,1);
	}
	
	public void update(Player p) {
		double dist = Math.hypot(p.rect.getCenterX()-rect.getCenterX(),p.rect.getCenterY()-rect.getCenterY());
		
		if (dist > 300) {
			angle = Math.atan2(p.rect.getCenterX()-rect.getCenterX(),p.rect.getCenterY()-rect.getCenterY());
			x += Math.sin(angle);
			y += Math.cos(angle);
		}
		rect.setLocation((int)x,(int)y);
		bar.x = (int)x;
		bar.y = (int)y-10;
		
		if (dist < 800) {
			cooldown++;
			if (cooldown > 100) {
				shoot(p);
				cooldown = 0;
			}
		}
		else {
			cooldown = 0;
		}
	}
	
	public void shoot(Player p) {
		angle = Math.atan2(p.rect.getCenterX()-rect.getCenterX(),p.rect.getCenterY()-rect.getCenterY());
		Projectile laser = new Projectile(
			new double[]{rect.getCenterX(),rect.getCenterY()},
			new double[]{Math.sin(angle)*20,Math.cos(angle)*20},
			new double[]{0,0},
			"laser"
		);
		Projectile.projectiles.add(laser);
	}
}

class MagicJimmy extends LaserJimmy //MagicJimmy is basically the laser jimmy but shoots a different projectile
{
	public MagicJimmy(double a, double b) {
		super(a,b);
		normalImage = Sprite.jimmyWhite;
		image = normalImage;
	}
	
	public void shoot(Player p) {
		angle = Math.atan2(p.rect.getCenterX()-rect.getCenterX(),p.rect.getCenterY()-rect.getCenterY());
		Projectile magic = new Projectile(
			new double[]{rect.getCenterX(),rect.getCenterY()},
			new double[]{Math.sin(angle)*5,Math.cos(angle)*5},
			new double[]{0,0},
			"magic"
		);
		Projectile.projectiles.add(magic);
	}
}

class TreeJimmy extends Jimmy
{
	public TreeJimmy(double a, double b) {
		cooldown = 500;
		x = a;
		y = b;
		health = maxHealth = 25;
		knockback = 0;
		normalImage = Sprite.jimmyTree;
		hurtImage = Sprite.jimmyTreeHurt;
		image = normalImage;
		rect = new Rectangle((int)x,(int)y,150,150);
		bar = new HealthBar((int)x,(int)y-10,300,5,100,1);
	}
	public void update(Player p) { //still needs player parameter to override even though it is unused
		cooldown++;
		if (cooldown >= 600 + (int) (Math.random() * 500)) {
			// RealFrame.room.jimmies.add(new LightningJimmy(Math.random()*1000+100,Math.random()*500+50));
			for (int i = 0; i < 2; i++) {
				RealFrame.room.jimmies.add(new Boss(Math.random()*1000+100,Math.random()*500+50, 4));
			}
			cooldown = 0;
		}
		rect.setLocation((int)(x+75),(int)(y+150));
	}
}

class Boss extends Jimmy
{
	int currentAttack;
	int previousAttack;
	ArrayList<Point> lightningPoints = new ArrayList<Point>();
	ArrayList<Point> swords = new ArrayList<Point>();
	Point[] swordPoints = new Point[5];
	int wings;
	
	public Boss(double a, double b, int h) {
		x = a;
		y = b;
		health = maxHealth = h;
		knockback = 0;
		normalImage = Sprite.boss;
		hurtImage = Sprite.bossHurt;
		image = normalImage;
		rect = new Rectangle((int)x,(int)y,45,65);
		bar = new HealthBar((int)x,(int)y-50,100,4,100,1);
	}
	
	public void teleport() {
		x = Math.random()*1000+100;
		y = Math.random()*450+50;
		rect.setLocation((int)x,(int)y);
	}
	
	public void update(Player p) {
		if (cooldown == -150) { //cooldown
			currentAttack = (int)(Math.random()*4);
			if (currentAttack == previousAttack) {
				currentAttack = (int)(Math.random()*4); //smaller chance of using the same attack twice
			}
			previousAttack = currentAttack;
			
			teleport();
			if (currentAttack == 0) {
				cooldown = 200;
			}
			if (currentAttack == 1) {
				cooldown = 100;
				angle = Math.atan2(p.rect.getCenterX()-rect.getCenterX(),p.rect.getCenterY()-rect.getCenterY());
			}
			if (currentAttack == 2) {
				cooldown = 200;
				int tX = (int) rect.getCenterX();
				int tY = (int) rect.getCenterY();
				swordPoints = new Point[]{
					new Point(tX-50,tY-50),new Point(tX-25,tY-60),new Point(tX,tY-65),new Point(tX+25,tY-60),new Point(tX+50,tY-50)
				};
			}
			if (currentAttack == 3) {
				cooldown = 400;
			}
		}
		if (cooldown == 0) {
			currentAttack = -1; //resting
			lightningPoints = new ArrayList<Point>();
			swords = new ArrayList<Point>();
		}
		if (currentAttack == 0 && cooldown % 5 == 0) {
			angle = Math.atan2(p.rect.getCenterX()-rect.getCenterX(),p.rect.getCenterY()-rect.getCenterY()) + Math.random() - 0.5;
			Projectile magic = new Projectile(
			new double[]{rect.getCenterX(),rect.getCenterY()},
			new double[]{Math.sin(angle)*5,Math.cos(angle)*5},
			new double[]{0,0},
			"magic"
			);
			Projectile.projectiles.add(magic);
		}
		if (currentAttack == 1) {
			lightningPoints = new ArrayList<Point>();
			int tX = (int) rect.getCenterX();
			int tY = (int) rect.getCenterY();
			for (int i=0; i<30; i++) {
				lightningPoints.add(new Point(tX,tY));
				tX += (int)(40*Math.sin(angle));
				tY += (int)(40*Math.cos(angle));
			}
			for (int i=0; i<lightningPoints.size(); i++) {
				lightningPoints.get(i).translate((int)(Math.random()*30-30),(int)(Math.random()*30-30));
				if (p.rect.contains(lightningPoints.get(i))) {
					p.hurtTimer = 10;
					p.health -= 0.03;
				}
			}
		}
		if (currentAttack == 2) {
			if (cooldown % 25 == 0 && cooldown > 75) {
				swords.add(swordPoints[(200-cooldown)/25]);
			}
			if (cooldown <= 75 && cooldown % 15 == 0) {
				Point sword = swords.get(0);
				angle = Math.atan2(p.rect.getCenterX()-sword.getX(),p.rect.getCenterY()-sword.getY());
				Projectile newSword = new Projectile(
					new double[]{sword.getX(),sword.getY()},
					new double[]{Math.sin(angle)*25,Math.cos(angle)*25},
					new double[]{0,0},
					"sword"
				);
				Projectile.projectiles.add(newSword);
				swords.remove(0);
			}
		}
		if (currentAttack == 3) {
			x += Math.sin(angle)*15;
			y += Math.cos(angle)*15;
			if (x < -150 || x > 1350 || y < -150 || y > 750) {
				teleport();
				angle = Math.atan2(p.rect.getCenterX()-rect.getCenterX(),p.rect.getCenterY()-rect.getCenterY());
			}
			if (rect.intersects(p.rect)) {
				p.health -= 0.1;
				p.hurtTimer = 10;
			}
		}
		
		cooldown--;
		bar.x = (int)x-27;
		bar.y = (int)y-20;
		rect.setLocation((int)x,(int)y);
		
		wings++;
		if (wings > 60) {
			wings = 0;
		}
	}
	
	public void render(Graphics g) {
		//render lightning
		for (int i=0; i<lightningPoints.size()-1; i++) {
			Graphics2D g2 = (Graphics2D) g;
			g.setColor(new Color(255,255,255));
			g2.setStroke(new BasicStroke(2));
			Point p1 = lightningPoints.get(i);
			Point p2 = lightningPoints.get(i+1);
			g2.drawLine((int)p1.getX(),(int)p1.getY(),(int)p2.getX(),(int)p2.getY());
		}
		
		//render wings
		g.setColor(new Color(255,255,255));
		int tX = (int)rect.getCenterX();
		int tY = (int)rect.getCenterY()-10;
		if (wings > 30) {
			g.fillPolygon(new int[]{tX,tX-70,tX-140},new int[]{tY,tY-10,tY-100},3);
			g.fillPolygon(new int[]{tX,tX+70,tX+140},new int[]{tY,tY-10,tY-100},3);
		
			g.fillPolygon(new int[]{tX,tX-75,tX-150},new int[]{tY,tY+30,tY+10},3);
			g.fillPolygon(new int[]{tX,tX+75,tX+150},new int[]{tY,tY+30,tY+10},3);
		}
		else {
			g.fillPolygon(new int[]{tX,tX-80,tX-160},new int[]{tY,tY+40,tY+25},3);
			g.fillPolygon(new int[]{tX,tX+80,tX+160},new int[]{tY,tY+40,tY+25},3);
		
			g.fillPolygon(new int[]{tX,tX-50,tX-140},new int[]{tY,tY+70,tY+100},3);
			g.fillPolygon(new int[]{tX,tX+50,tX+140},new int[]{tY,tY+70,tY+100},3);
		}
		
		for (Point i : swords) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke(3));
			g.drawLine((int)i.getX(),(int)i.getY(),(int)i.getX(),(int)i.getY()-55);
			g.drawLine((int)i.getX()-10,(int)i.getY()-15,(int)i.getX()+10,(int)i.getY()-15);
			g2.setStroke(new BasicStroke(5));
			g.drawLine((int)i.getX(),(int)i.getY()-15,(int)i.getX(),(int)i.getY()-50);
		}
		g.drawImage(image,(int)x,(int)y,null);
		bar.percent = (int)((double)health/maxHealth*100);
		bar.render(g);
	}
}

class Demon extends Jimmy{
	int currentAttack;
	int previousAttack;
	ArrayList<Point> spikePoints = new ArrayList<Point>();
	ArrayList<Point> shockPoints = new ArrayList<Point>();
	int wings;
	
	public Demon(double a, double b, int h) {
		x = a;
		y = b;
		health = maxHealth = h;
		knockback = 0;
		normalImage = Sprite.boss;
		hurtImage = Sprite.bossHurt;
		image = normalImage;
		rect = new Rectangle((int)x,(int)y,45,65);
		bar = new HealthBar((int)x,(int)y-50,100,4,100,1);
	}
	
	public void teleport() {
		x = Math.random()*1000+100;
		y = Math.random()*500+64;
		rect.setLocation((int)x,(int)y);
	}
	
	public void update(Player p) {
		//0 is shockwave attack
		//1 is spikes attack
		//
		if (cooldown == -150) { //cooldown
			currentAttack = (int)(Math.random()*3);
			if (currentAttack == previousAttack) {
				currentAttack = (int)(Math.random()*3); //smaller chance of using the same attack twice
			}
			previousAttack = currentAttack;
			
			teleport();
			//shockwave
			if (currentAttack == 0) {
				cooldown = 200;
			}
			//spikes
			if (currentAttack == 1) {
				x = RealFrame.xsz / 2;
				y = RealFrame.ysz / 2;
				rect.setLocation((int) x, (int) y);
				cooldown = 300;
				angle = Math.atan2(p.rect.getCenterX()-rect.getCenterX(),p.rect.getCenterY()-rect.getCenterY());
			}
		}
		if (cooldown == 0) {
			currentAttack = -1; //resting
			spikePoints = new ArrayList<Point>();
			shockPoints = new ArrayList<Point>();
		}
		if (currentAttack == 0 && cooldown % 50 == 0) {  //
			teleport();
			angle = Math.atan2(p.rect.getCenterX()-rect.getCenterX(),p.rect.getCenterY()-rect.getCenterY()) + Math.random() - 0.5;
			Projectile magic = new Projectile(
			new double[]{rect.getCenterX(),rect.getCenterY()},
			new double[]{Math.sin(angle)*5,Math.cos(angle)*5},
			new double[]{0,0},
			"shockwave"
			);
			Projectile.projectiles.add(magic);
		}
		if (currentAttack == 1 && cooldown % 10 == 0) {
			if (cooldown % 50 == 0) {

			}
		}
		if (currentAttack == 2) {  //rush
			x += Math.sin(angle)*15;
			y += Math.cos(angle)*15;
			if (x < -150 || x > 1350 || y < -150 || y > 750) {
				//do not teleport
				teleport();
				angle = Math.atan2(p.rect.getCenterX()-rect.getCenterX(),p.rect.getCenterY()-rect.getCenterY());
			}
			if (rect.intersects(p.rect)) {
				p.health -= 0.1;
				p.hurtTimer = 10;
			}
		}
		
		cooldown--;
		bar.x = (int)x-27;
		bar.y = (int)y-20;
		rect.setLocation((int)x,(int)y);
		
		wings++;
		if (wings > 60) {
			wings = 0;
		}
	}
	
	public void render(Graphics g) {
		//render projectiles

		
		
		
		g.drawImage(image,(int)x,(int)y,null);
		bar.percent = (int)((double)health/maxHealth*100);
		bar.render(g);
	}
}
class Sprite
{
	static Image jimmyNormal, jimmyHurt, jimmyBig, jimmyBigHurt, jimmyGreen,
	 jimmyBlue, jimmyWhite, jimmyTree, jimmyTreeHurt, boss, bossHurt, heart, soulSoil,
	 netherrack, demon;

	public static void init() {
		try {
			System.out.println("hello");
			jimmyNormal = ImageIO.read(new File("jimmy.png"));
			System.out.println("hello again");
			jimmyHurt = ImageIO.read(new File("jimmy-hurt.png"));
			jimmyBig = ImageIO.read(new File("jimmy-big.png"));
			jimmyBigHurt = ImageIO.read(new File("jimmy-big-hurt.png"));
			jimmyGreen = ImageIO.read(new File("jimmy-green.png"));
			jimmyBlue = ImageIO.read(new File("jimmy-blue.png"));
			jimmyWhite = ImageIO.read(new File("jimmy-white.png"));
			jimmyTree = ImageIO.read(new File("tree.png"));
			jimmyTreeHurt = ImageIO.read(new File("tree-hurt.png"));
			boss = ImageIO.read(new File("boss.png"));
			bossHurt = ImageIO.read(new File("boss-hurt.png"));
			heart = ImageIO.read(new File("heart.png"));
			soulSoil = ImageIO.read(new File("soul_soil.png"));
			netherrack = ImageIO.read(new File("netherrack.png"));

			demon = ImageIO.read(new File("heart.png"));
			System.out.println("working");
			
			
		}
		catch (Exception e) {
			System.out.println("sprite error");
		}
	}
}
class Projectile
{
	static ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
	
	double[] pos;
	double[] vel;
	double[] acc;
	String type;
	int life;
	
	public Projectile(double[] p, double[] v, double[] a, String s) {
		pos = p;
		vel = v;
		acc = a;
		type = s;
		
		if (type.equals("laser") || type.equals("magic") || type.equals("sword")) {
			life = 99999;
		}
	}
	
	public void update() {
		life--;
		if (life > 0) {
			pos[0] += vel[0];
			pos[1] += vel[1];
		}
	}
	public void render(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		if (type.equals("laser")) {
			g.setColor(new Color(100,150,255));
			g.setStroke(new BasicStroke(3));
			g.drawLine((int)pos[0],(int)pos[1],(int)(pos[0]-vel[0]*2),(int)(pos[1]-vel[1]*2));
		}
		if (type.equals("magic")) {
			g.setColor(new Color(255,255,255));
			g.fillOval((int)pos[0]-4,(int)pos[1]-4,8,8);
			g.setStroke(new BasicStroke(1));
			//sparkles
			int[] tPos = new int[]{(int)pos[0],(int)pos[1]};
			for (int i=0; i<10; i++) {
				int tx = (int)(tPos[0]+Math.random()*20-10);
				int ty = (int)(tPos[1]+Math.random()*20-10);
				g.drawLine(tx-3,ty,tx+3,ty);
				g.drawLine(tx,ty-3,tx,ty+3);
				tPos[0] -= vel[0];
				tPos[1] -= vel[1];
			}
		}
		if (type.equals("sword")) {
			int tX = (int) pos[0];
			int tY = (int) pos[1];
			g.setColor(new Color(255,255,255));
			g.setStroke(new BasicStroke(3));
			g.drawLine(tX,tY,(int)(tX+vel[0]*2.4),(int)(tY+vel[1]*2.4));
			
			int[] tPos = new int[]{(int)(tX+vel[0]),(int)(tY+vel[1])};
			double newAngle = Math.atan2(vel[0],vel[1])+Math.toRadians(90);
			
			g.drawLine((int)(tPos[0]+Math.sin(newAngle)*10),(int)(tPos[1]+Math.cos(newAngle)*10),(int)(tPos[0]-Math.sin(newAngle)*10),(int)(tPos[1]-Math.cos(newAngle)*10));
			
			g.setStroke(new BasicStroke(5));
			g.drawLine((int)(tX+vel[0]),(int)(tY+vel[1]),(int)(tX+vel[0]*2),(int)(tY+vel[1]*2));
		}
		if (type.equals("shock")) {

		}
		if (type.equals("spike")) {
			
		}
	}
}

class Sound
{
	static File owFile;
	static AudioInputStream owStream;
	static Clip ow;
	
	static void play() {
		try {
			owFile = new File("ow.wav");
			owStream = AudioSystem.getAudioInputStream(owFile);
			ow = AudioSystem.getClip();
			ow.open(owStream);
			ow.start();
		}
		catch (Exception e) {System.out.println("help");};
	}
}