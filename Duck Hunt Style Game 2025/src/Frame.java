import java.awt.Color;
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
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Frame extends JPanel implements ActionListener, MouseListener, KeyListener {
	// frame scale
	private int fscale = 1;
	private int screenWidth = 640 * fscale;
	// the +30 is to account for the title bar
	private int screenHeight = 480 * fscale + 30;
	// TODO: think of a good title for the game! also figure out what the game's
	// theme is supposed to be
	private String title = "Duck Hunt";

	private Image bgImage;
	private Image fgImage;
	private Image cloudImage;

	public boolean active = false;
	public int gamesPlayed = 0;

	int misses = 0;
	int totalShots = 0;
	int ducksShot = 0;
	int escapes = 0;
	int frames = 0;
	int maxTimer = 30;

	public void incrementEscapeCounter(Integer i) {
		escapes++;
	}

	/**
	 * Declare and instantiate (create) your objects here
	 */
	private Duck duck1 = new Duck(this::incrementEscapeCounter, fscale, screenWidth);
	private Duck duck2 = new Duck(this::incrementEscapeCounter, fscale, screenWidth);
	private Duck duck3 = new Duck(this::incrementEscapeCounter, fscale, screenWidth);

	public int duckCount = 3;

	public Duck ducks[] = new Duck[duckCount];

	public static void main(String[] arg) {
		new Frame();
	}

	private Image getImage(String path) {
		Image tempImage = null;
		try {
			URL imageURL = Duck.class.getResource(path);
			tempImage = Toolkit.getDefaultToolkit().getImage(imageURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tempImage;
	}

	public Frame() {
		JFrame f = new JFrame(title);
		f.setSize(new Dimension(screenWidth, screenHeight));
		f.setBackground(Color.blue);
		f.add(this);
		f.setResizable(false);
		f.setLayout(new GridLayout(1, 2));
		f.addMouseListener(this);
		f.addKeyListener(this);
		Timer t = new Timer(16, this);
		t.start();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		this.bgImage = getImage("imgs/bg.png");
		this.fgImage = getImage("imgs/fg.png");
		this.cloudImage = getImage("imgs/cloud.png");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		repaint();
	}

	public void paint(Graphics g) {
		if (active) {
			frames++;
		}
		super.paintComponent(g);
		Font font = new Font("Sans Serif", Font.PLAIN, 12 * fscale);
		g.setFont(font);

		g.drawImage(bgImage, 0, 0, screenWidth, screenHeight, null);
		if (frames / 60 == maxTimer) {
			duck1.active = false;
			duck2.active = false;
			duck3.active = false;
			this.active = false;
		}
		g.drawImage(cloudImage, 0, 0, screenWidth, screenHeight, null);

		duck1.paint(g);
		duck2.paint(g);
		duck3.paint(g);

		g.drawImage(fgImage, 0, 0, screenWidth, screenHeight, null);

		// render stats
		if (gamesPlayed != 0) {
			g.drawString(Integer.toString(totalShots) + " shots: " + Integer.toString(misses) + " misses, "
					+ Integer.toString(totalShots - misses) + " hits; " + Integer.toString(ducksShot) + " ducks", 10,
					12 * fscale * 2);
			g.drawString("Accuracy: " + String.format("%.2f", (double) (totalShots - misses) / (double) totalShots), 10,
					12 * fscale * 4);
			g.drawString("Avg. ducks per shot: " + String.format("%.2f", (double) ducksShot / (double) totalShots), 10,
					12 * fscale * 6);
			g.drawString("Escaped ducks: " + Integer.toString(escapes), 10, 12 * fscale * 8);
			g.drawString(Integer.toString(frames / 60) + "/" + Integer.toString(maxTimer),
					screenWidth - (12 * fscale * 4), 12 * fscale * 2);
			g.drawString(
					"SCORE: " + String.format("%.2f",
							((double) ducksShot / (double) totalShots) * ((double) ducksShot - (double) escapes * 5)),
					10, screenHeight - 30 - 24);
		}

		if (!active) {
			Font fontBig = new Font("Sans Serif", Font.PLAIN, 52 * fscale);
			String bigString;
			if (gamesPlayed == 0) {
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

	@Override
	public void mouseClicked(MouseEvent mouse) {
		// play another round of the game on click
		if (!active) {
			if (gamesPlayed != 0) {
				System.out.println(String.format("%.2f",
						((double) ducksShot / (double) totalShots) * ((double) ducksShot - (double) escapes * 5)));
			}
			gamesPlayed++;
			active = true;
			duck1.active = true;
			duck1.setLocation(0, 0);
			duck2.active = true;
			duck2.setLocation(0, 0);
			duck3.active = true;
			duck3.setLocation(0, 0);
			frames = 0;
			misses = 0;
			totalShots = 0;
			escapes = 0;
			ducksShot = 0;
		}
	}

	@Override
	public void mousePressed(MouseEvent mouse) {
		if (!active) {
			return;
		}
		boolean duckHit = false;
		Rectangle duck1Rect = new Rectangle((int) duck1.x, (int) duck1.y, duck1.width, duck1.height);
		Rectangle duck2Rect = new Rectangle((int) duck2.x, (int) duck2.y, duck2.width, duck2.height);
		Rectangle duck3Rect = new Rectangle((int) duck3.x, (int) duck3.y, duck3.width, duck3.height);
		// the mouseEvent's x and y are off for some reason, this corrects for that
		Point mousePoint = new Point(mouse.getX() - 7, mouse.getY() - 30);

		if (duck1Rect.contains(mousePoint)) {
			duck1.reset();
			ducksShot++;
			duckHit = true;
		}
		if (duck2Rect.contains(mousePoint)) {
			duck2.reset();
			ducksShot++;
			duckHit = true;
		}
		if (duck3Rect.contains(mousePoint)) {
			duck3.reset();
			ducksShot++;
			duckHit = true;
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
