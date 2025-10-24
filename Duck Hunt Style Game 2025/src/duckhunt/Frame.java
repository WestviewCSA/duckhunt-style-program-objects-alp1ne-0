package duckhunt;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
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

public class Frame extends JPanel implements ActionListener, MouseListener, KeyListener {
	private double scale = 2.0;
	private int screenWidth = (int)(640.0 * scale);
	private int screenHeight = (int)(480.0 * scale) + 30; // +30 is to account for title bar
	private String title = "Obelisk Defense";

	private Image bgImage;
	private Image fgImage;
	private Image cloudImage;

	// we call them ducks but they are actually evil alien obelisks
	public int duckCount = 5;
	public Duck ducks[] = new Duck[duckCount];

	// controls if a round is currently going, i.e. not on score display screen
	public boolean active = false;
	// controls if the game has been started, i.e. the Frame() initializer has completed
	public boolean gameStarted = false;
	public int roundsPlayed = 0;

	// frame counter, used to know when to end the current round
	int frames = 0;

	int misses = 0;
	int totalShots = 0;
	int ducksShot = 0;
	int escapes = 0;
	int maxTimer = 15;

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
			escapes++;
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

		Image duckImage = getImage("../imgs/obelisk.png");
		for (int i = 0; i < duckCount; i++) {
			ducks[i] = new Duck(duckImage, this::duckEscapeCallback, scale, screenWidth, screenHeight);
		}
		this.bgImage = getImage("../imgs/bg.png");
		this.fgImage = getImage("../imgs/fg.png");
		this.cloudImage = getImage("../imgs/clouds.png");

		// sends an event every 16 milliseconds to repaint the screen, approximately 60fps
		Timer t = new Timer(16, this);
		t.start();
		this.gameStarted = true;
	}

	// repaint when the timer goes off
	public void actionPerformed(ActionEvent arg0) {
		update();
		repaint();
	}

	public void update() {
		if (!gameStarted) { return; }
		if (active) {
			frames++;
		}
		if (frames / 60 == maxTimer) {
			this.active = false;
		}
		for (Duck duck : ducks) {
			duck.update();
		}
	}

	public void paint(Graphics g) {
		if (!gameStarted) { return; }
		super.paintComponent(g);

		g.drawImage(bgImage, 0, 0, screenWidth, screenHeight, null);
		g.drawImage(cloudImage, 0, 0, screenWidth, screenHeight, null);

		for (Duck duck : ducks) {
			duck.paint(g);
		}

		g.drawImage(fgImage, 0, 0, screenWidth, screenHeight, null);

		Font font = new Font("Sans Serif", Font.PLAIN, (int)(12.0 * scale));
		g.setFont(font);
		// render stats
		if (roundsPlayed != 0) {
			g.drawString(Integer.toString(totalShots) + " shots: " + Integer.toString(misses) + " misses, "
					+ Integer.toString(totalShots - misses) + " hits; " + Integer.toString(ducksShot) + " obelisks", 10,
					(int)(12.0 * scale * 2.0));
			g.drawString("Accuracy: " + String.format("%.2f", (double) (totalShots - misses) / (double) totalShots), 10,
					(int)(12.0 * scale * 4.0));
			g.drawString("Avg. ducks per shot: " + String.format("%.2f", (double) ducksShot / (double) totalShots), 10,
					(int)(12.0 * scale * 6.0));
			g.drawString("Escaped obelisks: " + Integer.toString(escapes), 10, (int)(12.0 * scale * 8.0));
			g.drawString(Integer.toString(frames / 60) + "/" + Integer.toString(maxTimer),
					screenWidth - (int)(12.0 * scale * 4.0), (int)(12.0 * scale * 2.0));
			g.drawString(
					"SCORE: " + String.format("%.2f",
							((double) ducksShot / (double) totalShots) * ((double) ducksShot - (double) escapes * 5)),
					10, screenHeight - 30 - 24);
		}

		if (!active) {
			Font fontBig = new Font("Sans Serif", Font.PLAIN, (int)(52.0 * scale));
			String bigString;
			if (roundsPlayed == 0) {
				bigString = "Click to start";
			} else {
				bigString = String.format("%.2f",
						((double) ducksShot / (double) totalShots) * ((double) ducksShot - (double) escapes * 5));
			}
			g.setFont(fontBig);
			FontMetrics metrics = g.getFontMetrics(fontBig);
			int x = (screenWidth - metrics.stringWidth(bigString)) / 2;
			int y = ((screenHeight - 30 - metrics.getHeight()) / 2) + metrics.getAscent();
			g.drawString(bigString, x, y);
		}
	}

	public void startNewRound() {
		if (roundsPlayed != 0) {
			System.out.println(String.format("%.2f",
					((double) ducksShot / (double) totalShots) * ((double) ducksShot - (double) escapes * 5)));
		}
		roundsPlayed++;
		frames = 0;
		misses = 0;
		totalShots = 0;
		escapes = 0;
		ducksShot = 0;

		for (Duck duck : ducks) {
			duck.resetPosition();
		}

		active = true;
	}

	@Override
	public void mouseClicked(MouseEvent mouse) {
		if (!active) {
			startNewRound();
		}
	}

	@Override
	public void mousePressed(MouseEvent mouse) {
		if (!active) {
			return;
		}
		boolean duckHit = false;
		// the mouseEvent's x and y are off for some reason, this corrects for that
		Point mousePoint = new Point(mouse.getX() - 7, mouse.getY() - 30);
		for (Duck duck : ducks) {
			Rectangle duckRect = new Rectangle(duck.x, duck.y, duck.width, duck.height);
			if (duckRect.contains(mousePoint) && !duck.isFalling) {
				duck.onShoot();
				ducksShot++;
				duckHit = true;				
			}
		}
		if (!duckHit) {
			misses++;
		}
		totalShots++;
	}

	public void mouseReleased(MouseEvent mouse) {}
	public void mouseEntered(MouseEvent mouse) {}
	public void mouseExited(MouseEvent mouse) {}

	public void keyPressed(KeyEvent key) {}
	public void keyReleased(KeyEvent key) {}
	public void keyTyped(KeyEvent key) {}
}
