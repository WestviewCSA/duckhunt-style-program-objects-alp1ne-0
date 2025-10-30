package duckhunt;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.net.URL;
import java.util.ArrayList;

public class Frame extends JPanel implements ActionListener, MouseListener, KeyListener {
	private double scale = 2.0;
	private int screenWidth = (int) (640.0 * scale);
	private int screenHeight = (int) (480.0 * scale) + 30; // +30 is to account for title bar
	private String title = "Obelisk Defense";

	private boolean mouseHeld = false;

	private Image bgImage;
	private Image fgImage;
	private Image cloudImage;

	// we call them ducks but they are actually evil alien obelisks
	public ArrayList<Duck> ducks = new ArrayList<Duck>();
	public ArrayList<Duck> bigDucks = new ArrayList<Duck>();

	// controls if a round is currently going, i.e. not on score display screen
	public boolean active = false;
	// controls if the game has been started, i.e. the Frame() initializer has
	// completed
	public boolean gameStarted = false;
	public int roundsPlayed = -1;

	// frame counter, used to know when to end the current round
	int frames = 0;

	int misses = 0;
	int totalShots = 0;
	int ducksShot = 0;
	int escapes = 0;
	int maxEscapes = 5;

	// only exists so Eclipse will stop yelling at me, this class will not actually ever be serialized
	private static final long serialVersionUID = 1L;

	public static Image getImage(String path) {
		Image tempImage = null;
		try {
			URL imageURL = Frame.class.getResource(path);
			tempImage = Toolkit.getDefaultToolkit().getImage(imageURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tempImage;
	}

	// used as a callback for the ducks
	public void duckEscapeCallback(Integer i) {
		if (active) {
			if (i > 1) {
				escapes = 2147483647;
			} else {
				escapes += i;
			}
		}
	}

	public static void main(String[] arg) {
		new Frame();
	}

	public Frame() {
		JFrame f = new JFrame(title);
		f.setSize(new Dimension(screenWidth, screenHeight));

		f.add(this);
		f.addMouseListener(this);
		f.addKeyListener(this);

		f.setResizable(false);
		f.setLayout(new GridLayout(1, 2));
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);

		this.startNewRound();
		this.active = false;

		this.bgImage = getImage("../imgs/bg.png");
		this.fgImage = getImage("../imgs/fg.png");
		this.cloudImage = getImage("../imgs/clouds.png");

		// sends an event every 16 milliseconds to repaint the screen, approximately
		// 60fps
		Timer t = new Timer(16, this);
		t.start();
		this.gameStarted = true;
	}

	// repaint when the timer goes off
	public void actionPerformed(ActionEvent arg0) {
		repaint();
		update();
	}

	public void update() {
		if (!gameStarted) {
			return;
		}
		if (active) {
			frames++;
		}
		if (frames % 300 == 0 && active) {
			addDuck();
		}
		if (frames % 300 == 0 && active) {
			addBigDuck();
		}
		if (mouseHeld && active) {
			shoot();
		}
		if (escapes >= maxEscapes) {
			this.active = false;
		}
		for (Duck duck : ducks) {
			duck.update();
		}
		for (Duck bigDuck : bigDucks) {
			bigDuck.update();
		}
		bigDucks.removeIf(bigDuck -> bigDuck.removeable);
	}

	public void addDuck() {
		Image duckImage = getImage("../imgs/obelisk.png");
		Image fireImage = getImage("../imgs/fire.gif");
		Duck newDuck = new Duck(duckImage, fireImage, this::duckEscapeCallback, scale, screenWidth, screenHeight);
		newDuck.resetPosition();
		ducks.add(newDuck);
	}

	public void addBigDuck() {		
		Image theBigDuckImage = getImage("../imgs/bigObelisk.png");
		Image fireImage = getImage("../imgs/fire.gif");
		Duck theBigDuck = new Duck(theBigDuckImage, fireImage, this::duckEscapeCallback, scale, screenWidth, screenHeight);
		theBigDuck.startingVx = (int)(1.0 * scale);
		theBigDuck.startingHp = 200;
		theBigDuck.width = (int)(450.0 * scale);
		theBigDuck.height = (int)(132.0 * scale);
		theBigDuck.resetPosition();
		theBigDuck.maxFlaming = 300;
		bigDucks.add(theBigDuck);
	}

	public double calculateScore() {
		return ((Math.pow((double) ducksShot, 2.0) / (double) totalShots));
	}

	public void paint(Graphics g) {
		if (!gameStarted) {
			return;
		}
		super.paintComponent(g);

		g.drawImage(bgImage, 0, 0, screenWidth, screenHeight, null);

		for (Duck bigDuck : bigDucks) {
			bigDuck.paint(g);
		}

		g.drawImage(cloudImage, 0, 0, screenWidth, screenHeight, null);

		for (Duck duck : ducks) {
			duck.paint(g);
		}

		g.drawImage(fgImage, 0, 0, screenWidth, screenHeight, null);

		Font font = new Font("Sans Serif", Font.PLAIN, (int) (12.0 * scale));
		g.setFont(font);
		// render stats
		if (roundsPlayed != 0) {
			g.drawString(Integer.toString(totalShots) + " shots: " + Integer.toString(misses) + " misses, "
					+ Integer.toString(totalShots - misses) + " hits; " + Integer.toString(ducksShot) + " damage dealt", 10,
					(int) (12.0 * scale * 2.0));
			g.drawString("Accuracy: " + String.format("%.2f", (double) (totalShots - misses) / (double) totalShots), 10,
					(int) (12.0 * scale * 4.0));
			g.drawString("Avg. damage per shot: " + String.format("%.2f", (double) ducksShot / (double) totalShots), 10,
					(int) (12.0 * scale * 6.0));
			g.drawString("Escaped obelisks: " + Integer.toString(escapes) + "/" + Integer.toString(maxEscapes), 10, (int) (12.0 * scale * 8.0));
			g.drawString(Integer.toString(frames / 60), screenWidth - (int) (12.0 * scale * 4.0), (int) (12.0 * scale * 2.0));
			g.drawString(
					"SCORE: " + String.format("%.2f", calculateScore()),
					10, screenHeight - 30 - 24);
		}

		if (!active) {
			Font fontBig = new Font("Sans Serif", Font.PLAIN, (int) (52.0 * scale));
			String bigString;
			if (roundsPlayed == 0) {
				bigString = "Click to start";
			} else {
				bigString = String.format("%.2f", calculateScore());
			}
			g.setFont(fontBig);
			FontMetrics metrics = g.getFontMetrics(fontBig);
			int x = (screenWidth - metrics.stringWidth(bigString)) / 2;
			int y = ((screenHeight - 30 - metrics.getHeight()) / 2) + metrics.getAscent();
			g.drawString(bigString, x, y);
		}
	}

	public void startNewRound() {
		if (roundsPlayed > 0) {
			System.out.print(String.format("%.2f ", calculateScore()));
			System.out.println(frames / 60);
		}
		roundsPlayed++;
		frames = 0;
		misses = 0;
		totalShots = 0;
		escapes = 0;
		ducksShot = 0;

		ducks.removeAll(ducks);
		this.addDuck();
		this.addDuck();
		this.addDuck();
		bigDucks.removeAll(bigDucks);

		active = true;
	}

	public void shoot() {
		boolean duckHit = false;
		// the mouseEvent's x and y are off for some reason, this corrects for that
		Point mousePoint = new Point(MouseInfo.getPointerInfo().getLocation().x - 7, MouseInfo.getPointerInfo().getLocation().y - 30);
		Rectangle mouseZone = new Rectangle(mousePoint.x - 5, mousePoint.y - 5, 10, 10);
		for (Duck duck : ducks) {
			Rectangle duckRect = new Rectangle(duck.x, duck.y, duck.width, duck.height);
			if (duckRect.intersects(mouseZone) && !duck.isFalling) {
				duck.hp--;
				duck.jitter += 5;
				ducksShot++;
				duckHit = true;
			}
		}
		for (Duck bigDuck : bigDucks) {
			Rectangle bigDuckRect = new Rectangle(bigDuck.x, bigDuck.y, bigDuck.width, bigDuck.height);
			if (bigDuckRect.intersects(mouseZone) && !bigDuck.isFalling) {
				bigDuck.hp--;
				bigDuck.jitter += 2;
				ducksShot++;
				duckHit = true;
			}
		}
		if (!duckHit) {
			misses++;
		}
		totalShots++;
	}

	@Override
	public void mousePressed(MouseEvent mouse) {
		if (!active) {
			startNewRound();
		}
		mouseHeld = true;
	}

	public void mouseReleased(MouseEvent mouse) {
		mouseHeld = false;		
	}
	public void mouseClicked(MouseEvent mouse) {}
	public void mouseEntered(MouseEvent mouse) {}
	public void mouseExited(MouseEvent mouse) {}

	public void keyPressed(KeyEvent key) {}
	public void keyReleased(KeyEvent key) {}
	public void keyTyped(KeyEvent key) {}
}
