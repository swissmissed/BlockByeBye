import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.JFrame;
import javax.swing.JPanel;

abstract class MyObject{
	double x, y;
	int width, height;
	boolean isRacket;
	boolean isYellow;
	abstract public void draw(Graphics g);
	public void update(double dt) {};
	public boolean resolve(MyObject o, Loc c) {return false;};
	public boolean isHit() {return false;}
	public boolean isFall() {return false;}
}

class MyWall extends MyObject{
	Color color;
	boolean isBlock;
	public int hit;
	static int yellow_num = 0;
	MyWall(int _x, int _y, int _w, int _h, boolean tf, boolean rw){
		int n = (int)(Math.random()*1000);

		if(n%2==0) {
			isYellow = true;
		}
		else isYellow = false;
		
		if(isYellow) color = new Color(204,204,0);
		else color = new Color(180,0,180);
		
		if(rw == true) color = Color.lightGray;
		isRacket = false;
		isBlock = tf;
		x = _x; y = _y; width = _w; height = _h;
	}
	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		g.fill3DRect((int)x, (int)y, width, height, true);
				
	}
	public boolean isIn(MySphere o) {
		double xmin = x - o.r;
		double xmax = x + width + o.r;
		double ymin = y - o.r;
		double ymax = y + height + o.r;
		
		if(o.x>xmin && o.x < xmax && o.y >ymin && o.y< ymax)
			return true;
		return false;
	}
	@Override
	public boolean isHit() {
		if(this.hit >= 1)
			return true;
		return false;
	}
}

class MyRacket extends MyObject {

	MyRacket(){
		isYellow = false;
		isRacket = true;
		width = 160;
		height = 20;
		x = 320; y = 700-40;
	}
	@Override
	public void draw(Graphics g) {
		g.setColor(new Color(139,69,19));
		g.fill3DRect((int)x, (int)y, width, height, true);
	}
	public boolean isIn(MySphere o) {
		double xmin = x - o.r;
		double xmax = x + width + o.r;
		double ymin = y - o.r;
		double ymax = y + height + o.r;
		
		if(o.x>xmin && o.x < xmax && o.y >ymin && o.y< ymax)
			return true;
		return false;
	}
}

class Loc {
	int x = 0;
	int y = 0;
}

class MySphere extends MyObject {
	double prevX, prevY;
	double vx = 0, vy = 0;
	double r;
	double angle, speed;
	Color color;

	MySphere(double _x, double _y, double _r){
		isYellow = false;
		isRacket = false;
		x = _x; y = _y; r = _r; 
		color = Color.white;
		angle = Math.random() * Math.PI / 3;
		speed = 300.0 + 100.0;
		
		prevX = _x;
		prevY = _y;
		vx = -Math.cos(angle) * speed;
		vy = -200;
	}
	MySphere(double _x, double _y, double _r, MySphere o, int sign){
		isYellow = false;
		isRacket = false;
		x = _x; y = _y; r = _r; 
		color = Color.white;
		speed = 300.0 + 100.0;
		
		prevX = _x;
		prevY = _y;
		vx = o.vx - sign * Math.cos(Math.PI / 6) * 100.0;
		vy = -sign*200;
	}
	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		g.fillOval((int)(x-r), (int)(y-r), (int)(2*r), (int)(2*r));
		
	}
	@Override
	public void update(double dt) {
		prevX = x;
		prevY = y;
		x = x + vx * dt;
		y = y + vy * dt;
	 }    
	@Override
	public boolean resolve(MyObject o, Loc c) {
		if(o instanceof MyWall) {
			MyWall w = (MyWall) o;
			if(w.isIn(this) == false)
				return false;
			if(w.isBlock == true)
				w.hit++;

			double xmin = w.x - r;
			double xmax = w.x + w.width + r;
			double ymin = w.y - r;
			double ymax = w.y + w.height + r;
			
			if(prevX<xmin) { vx = -vx;x = xmin;}
			if(prevX>xmax) { vx = -vx;x = xmax;}
			if(prevY<ymin) { vy = -vy;y = ymin;}
			if(prevY>ymax) { vy = -vy;y = ymax;}
			c.x = (int)x;
			c.y = (int)y;
			if(!w.isBlock)
				return true;
			return false;
		}
		if(o instanceof MyRacket) {
			MyRacket w = (MyRacket) o;
			if(w.isIn(this) == false)
				return false;
			double xmin = w.x - r;
			double xmax = w.x + w.width + r;
			double ymin = w.y - r;
			double ymax = w.y + w.height + r;
			
			if(prevX<xmin) { vx = -vx;x = xmin;}
			if(prevX>xmax) { vx = -vx;x = xmax;}
			if(prevY<ymin) { vy = -vy;y = ymin;}
			if(prevY>ymax) { vy = -vy;y = ymax;}
			return true;
		}
		return false;
	}
	@Override
	public boolean isFall() {
		if(this.y >= 740)
			return true;
		return false;
	}
}

class Start {
	   Font font1=new Font("바탕체", Font.BOLD, 60);
	   Font font2=new Font("stencil", Font.BOLD, 80);
	   Font font3=new Font("바탕체",Font.LAYOUT_LEFT_TO_RIGHT, 25);
	   
	   String st1=new String("Java Programming");
	   String st2=new String("Homework #4&5");
	   String st3=new String("BLOCK BREAKER");
	   String st4=new String("PRESS SPACEBAR TO PLAY!");
	   void draw(Graphics g) {
		   g.setFont(font1);
		   g.setColor(Color.black);
		   g.drawString(st1, 130, 170);
		   g.drawString(st2, 190, 230);
		   g.setColor(Color.white);
		   g.drawString(st1, 127, 170);
		   g.drawString(st2, 187, 230);
		   g.setFont(font2);
		   g.setColor(Color.black);
		   g.drawString(st3, 40, 390);
		   g.setColor(Color.white);
		   g.drawString(st3, 37, 390);
		   g.setFont(font3);
		   g.setColor(Color.red);
		   g.drawString(st4, 250, 600);
	   }
}

class End {
	   int high_score=0;
	   int score=0;
	   Game g;
	   Font font1=new Font("stencil", Font.BOLD, 110);
	   Font font2=new Font("stencil", Font.BOLD, 40);
	   Font font3=new Font("바탕체",Font.LAYOUT_LEFT_TO_RIGHT, 40);
	   
	   String st1=new String("GAME OVER");
	   String st2=new String("HIGH SCORE: "+high_score);
	   String st3=new String("YOUR SCORE: "+score);
	   String st4=new String("PRESS SPACEBAR!");
	   
	   void draw(Graphics g)
	   {
	      g.setFont(font1);
	      
	      g.setColor(Color.BLACK);
	      g.drawString(st1, 70, 310);
	      g.setColor(Color.white);
	      g.drawString(st1, 67, 310);
	      
	      g.setFont(font2);
	      g.setColor(Color.BLACK);
	      g.drawString(st2, 240, 450);
	      g.setColor(Color.white);
	      g.drawString(st2, 237, 450);
	      
	      g.setColor(Color.BLACK);
	      g.drawString(st3, 240, 490);
	      g.setColor(Color.white);
	      g.drawString(st3, 237, 490);
	      g.setFont(font3);
	      g.setColor(Color.red);
		  g.drawString(st4, 250, 600);
	   }
	   void updateScore() {
			score=g.score;
			if(high_score<g.score)
				high_score=g.score;
			st2=new String("HIGH SCORE: "+high_score);
			st3=new String("YOUR SCORE: "+score);
	   }
}

class Stage {
	ArrayList<MyObject> objs;
	int box_num = 0;
	int sphere_num = 0;
	boolean leftKey, rightKey;

	Stage(int n){
		leftKey = false; rightKey = false;
		objs = new ArrayList<>();

		objs.add(new MyWall(0,0,800,16, false, true));
		objs.add(new MyWall(0,800 - 16,800,16, false, true));
		objs.add(new MyWall(0,0,16,800, false, true));
		objs.add(new MyWall(800 - 16,0,16,800, false, true));
		
		objs.add(new MySphere(397, 700-45, 5));
		sphere_num++;

		int w = (800 - 32) / (3*n);
		int h = (120) / n;
		
		for(int i=0;i<3*n;i++) {
			for(int j=0;j<3*n;j++) {
				objs.add(new MyWall(16 + w*j + 1, 17 + h*i + i, w, h, true, false));
			}
		}
		box_num += 3*3*n*n;
		

		objs.add(new MyRacket());
	}
	public void draw(Graphics g) {
		
		var it = objs.iterator();
		while(it.hasNext()) {
			var o = it.next();
			o.draw(g);
		}
	}
}

class Game extends JPanel implements KeyListener, Runnable{
	Start start = new Start();
	LinkedList <Stage> stages = new LinkedList<>();
	End end = new End();
	Loc c;
	int mode = 0;
	int stage_number = 0;
	int score = 0;
	Clip bounce = null, dead = null, background = null;
	Clip clear = null, punch = null;
	
	Game(){
		end.g = this;
		try {
			bounce = AudioSystem.getClip();
			dead = AudioSystem.getClip();
			background = AudioSystem.getClip();
			clear = AudioSystem.getClip();
			punch = AudioSystem.getClip();
			URL url1 = getClass().getClassLoader().getResource("bounce.wav");
			URL url2 = getClass().getClassLoader().getResource("dead.wav");
			URL url3 = getClass().getClassLoader().getResource("background.wav");
			URL url4 = getClass().getClassLoader().getResource("clear.wav");
			URL url5 = getClass().getClassLoader().getResource("punch.wav");
			AudioInputStream stream1 = AudioSystem.getAudioInputStream(url1);
			AudioInputStream stream2 = AudioSystem.getAudioInputStream(url2);
			AudioInputStream stream3 = AudioSystem.getAudioInputStream(url3);
			AudioInputStream stream4 = AudioSystem.getAudioInputStream(url4);
			AudioInputStream stream5 = AudioSystem.getAudioInputStream(url5);
			bounce.open(stream1);
			dead.open(stream2);
			background.open(stream3);
			clear.open(stream4);
			punch.open(stream5);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		background.loop(background.LOOP_CONTINUOUSLY);
		
		Thread t = new Thread(this);
		t.start();
		
		c = new Loc();
		
		this.addKeyListener(this);
		this.setFocusable(true);
		this.requestFocus();
		
	}
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		GradientPaint gra = new GradientPaint(400,0,Color.DARK_GRAY, 400, 800, Color.gray);
		g2.setPaint(gra);
		g2.fillRect(0, 0, 816, 800);
		
		if(mode == 0) {
			start.draw(g);
		}
		if(mode == 1) {
			stages.getLast().draw(g);

		}
		if(mode == 2) {
			end.updateScore();
			end.draw(g);
		}

	}
	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyPressed(KeyEvent e) {
		if(mode == 0)
			if(e.getKeyCode() == KeyEvent.VK_SPACE) {
				int n = stages.size() + 1;
				stages.add(new Stage(n));
				mode = 1;
				background.stop();
			}
		if(mode == 1) {
			if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
				stages.getLast().rightKey = true;
				stages.getLast().leftKey = false;	
			}
			if(e.getKeyCode() == KeyEvent.VK_LEFT) {
				stages.getLast().leftKey = true;
				stages.getLast().rightKey = false;
			}
		}
		if(mode == 2)
			if(e.getKeyCode() == KeyEvent.VK_SPACE) {
				mode = 0;
				background.loop(background.LOOP_CONTINUOUSLY);
			}
		repaint();
	}
	@Override
	public void keyReleased(KeyEvent e) {
		if(mode == 1) {
			if(stages.size()>0)
				stages.getLast().rightKey = false; stages.getLast().leftKey = false;
		}
	}
	@Override
	public void run() {
		while(true) {
			//1. update
			if(mode == 1) {
				var it = stages.getLast().objs.iterator();
				while(it.hasNext()) {
					var o = it.next();
					o.update(0.016);
				}
				
				//라켓 조정
				if(stages.getLast().leftKey == true) {
					for(MyObject o : stages.getLast().objs)
						if(o.isRacket == true) {
							if(o.x <= 16)
								o.x = 16;
							else
								o.x -= 10;
						}
				}
				if(stages.getLast().rightKey == true) {
					for(MyObject o : stages.getLast().objs)
						if(o.isRacket == true) {
							if(o.x >= 800 - 160 - 16)
								o.x = 800-160 - 16;
							else
								o.x += 10;
							
						}
				}
				//2. resolve
				
				MySphere ball = null;
				int x = 0, y = 0;
				int w = 0, h = 0;
				var it1 = stages.getLast().objs.iterator();
				while(it1.hasNext()) {
					var o1 = it1.next();
					var it2 = stages.getLast().objs.iterator();
					while(it2.hasNext()) {
						var o2 = it2.next();
						if(o1 == o2) continue;
						if(o1.resolve(o2, c)) {
							bounce.start();
							bounce.setFramePosition(0);
						}
						if(o2 instanceof MySphere) {
							ball = (MySphere) o2;
						}
					}
					if(o1.isHit()) {
						it1.remove();
						stages.getLast().box_num--;
						score+=100;
						punch.start();
						punch.setFramePosition(0);
						if(o1.isYellow == true) {
							x = (int)c.x; y = (int)c.y;
							stages.getLast().objs.add(new MySphere(x, y, 5, ball, 1));
							stages.getLast().objs.add(new MySphere(x, y, 5, ball, -1));
							stages.getLast().sphere_num += 2;
							break;
						}
					}
					if(o1 instanceof MySphere) {
						ball = (MySphere)o1;
						if(ball.isFall()) {
							it1.remove();
							stages.getLast().sphere_num--;
							dead.start();
							dead.setFramePosition(0);
						}
					}
				}
				if(stages.getLast().box_num <= 0) {
					int n = stages.size() + 1;
					stages.add(new Stage(n));
					clear.start();
					clear.setFramePosition(0);
				}
				if(stages.getLast().sphere_num == 0 && stages.getLast().box_num != 0) {
					mode++;
					stages.removeAll(stages);
				}
			}
			
			end.updateScore();
			//3. render
			repaint();

			try {
				Thread.sleep(16);
			} catch (InterruptedException e) {
				return;
			}
		}
		
	}
}

public class BlockByeBye extends JFrame {

	BlockByeBye() {
		setSize(816,800);
		setTitle("Moving Balls");
		add(new Game());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	public static void main(String[] args) {

		BlockByeBye f = new BlockByeBye();


	}

}
