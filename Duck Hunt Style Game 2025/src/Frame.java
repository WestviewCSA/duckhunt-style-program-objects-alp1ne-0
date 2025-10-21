import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
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

	// frame size
	private int screenWidth = 640 * 2, screenHeight = 480 * 2 + 32;
	private String title = "Duck Hunt";

	private Image bgImage;
	private Image fgImage;

	public boolean active = false;
	public int gamesPlayed = 0;

	int misses = 0;
	int totalShots = 0;
	int duckCount = 0;
	int escapes = 0;
	int frames = 0;
	int maxTimer = 5;

	public void incrementEscapeCounter(Integer i) {
		escapes++;
	}

	/**
	 * Declare and instantiate (create) your objects here
	 */
	private Duck duck1 = new Duck(this::incrementEscapeCounter, 0, 0, 1, 1, 30, 0);
	private Duck duck2 = new Duck(this::incrementEscapeCounter, 0, 0, 1, 1, 30, 0);
	private Duck duck3 = new Duck(this::incrementEscapeCounter, 0, 0, 1, 1, 30, 0);

	public void paint(Graphics g) {
		if (active) {
			frames++;
		}
		super.paintComponent(g);
		Font font = new Font("Sans Serif", Font.PLAIN, 24);
		g.setFont(font);

		g.drawImage(bgImage, 0, 0, screenWidth, screenHeight - 32, null);
		if (frames / 60 == maxTimer) {
			duck1.active = false;
			duck2.active = false;
			duck3.active = false;
			this.active = false;
			gamesPlayed++;
		}
		duck1.paint(g);
		duck2.paint(g);
		duck3.paint(g);

		g.drawImage(fgImage, 0, 0, screenWidth, screenHeight - 32, null);

		if (gamesPlayed != 0) {
			g.drawString(
					Integer.toString(totalShots) + " shots: " + Integer.toString(misses) + " misses, "
							+ Integer.toString(totalShots - misses) + " hits; " + Integer.toString(duckCount) + " ducks",
					10, 34);
			g.drawString("Accuracy: " + String.format("%.2f", (double)(totalShots - misses) / (double) totalShots),
					10, 34 * 2);
			g.drawString("Avg. ducks per shot: " + String.format("%.2f", (double) duckCount / (double) totalShots),
					10, 34 * 3);
			g.drawString("Escaped ducks: " + Integer.toString(escapes),
					10, 34 * 4);
			g.drawString(Integer.toString(frames / 60) + "/" + Integer.toString(maxTimer),
					640 * 2 - 80, 34);
			g.drawString(
					"SCORE: " + String.format("%.2f",
							((double) duckCount / (double) totalShots) * ((double) duckCount - (double) escapes * 5)),
					10, 480 * 2 - 24);
		}
		
		if (!active) {			
			Font fontBig = new Font("Sans Serif", Font.PLAIN, 104);
			String bigString;
			if (gamesPlayed == 0) {
				bigString = "Click to start";
			} else {
				bigString = String.format("%.2f", ((double) duckCount / (double) totalShots) * ((double) duckCount - (double) escapes * 5));
			}
			g.setFont(fontBig);
			FontMetrics metrics = g.getFontMetrics(fontBig);
			int x = (640 * 2 - metrics.stringWidth(bigString)) / 2;
			int y = ((480 * 2 - metrics.getHeight()) / 2) + metrics.getAscent();
			g.drawString(bigString, x, y);
		}
	}

	@Override
	public void mouseClicked(MouseEvent mouse) {
		// play another round of the game on click
		if (!active) {
			if (gamesPlayed != 0) {
				System.out.println(String.format("%.2f", ((double) duckCount / (double) totalShots) * ((double) duckCount - (double) escapes * 5)));
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
			duckCount = 0;
		}
	}

	@Override
	public void mouseEntered(MouseEvent mouse) {
	}

	@Override
	public void mouseExited(MouseEvent mouse) {
	}

	@Override
	public void mousePressed(MouseEvent mouse) {
		if (!active) {
			return;
		}
		int mx = mouse.getX();
		int my = mouse.getY();
		boolean duckHit = false;
		if (mx > duck1.x && mx <= duck1.x + duck1.width && my > duck1.y && my <= duck1.y + duck1.height) {
			duck1.reset();
			duckHit = true;
			duckCount++;
		}
		if (mx > duck2.x && mx <= duck2.x + duck2.width && my > duck2.y && my <= duck2.y + duck2.height) {
			duck2.reset();
			duckHit = true;
			duckCount++;
		}
		if (mx > duck3.x && mx <= duck3.x + duck3.width && my > duck3.y && my <= duck3.y + duck3.height) {
			duck3.reset();
			duckHit = true;
			duckCount++;
		}
		if (!duckHit) {
			misses++;
		}
		totalShots++;
	}

	@Override
	public void mouseReleased(MouseEvent mouse) {
		// Runs when a mouse button is released.
		// Example: You could stop dragging the object or drop it in place.
	}

	/*
	 * This method runs automatically when a key is pressed down
	 */
	public void keyPressed(KeyEvent key) {
		System.out.println("from keyPressed method:" + key.getKeyCode());
	}

	/*
	 * This method runs when a keyboard key is released from a pressed state aka
	 * when you stopped pressing it
	 */
	public void keyReleased(KeyEvent key) {
	}

	/*
	 * Runs when a keyboard key is pressed then released
	 */
	public void keyTyped(KeyEvent key) {
	}

	/*
	 * The Timer animation calls this method below which calls for a repaint of the
	 * JFrame. Allows for our animation since any changes to states/variables will
	 * be reflected on the screen if those variables are being used for any drawing
	 * on the screen.
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		repaint();
	}

	/*
	 * Main method to create a Frame (the GUI that you see)
	 */
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
	}

}
