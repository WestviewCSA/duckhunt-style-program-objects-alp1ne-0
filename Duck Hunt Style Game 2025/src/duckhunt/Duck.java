package duckhunt;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.util.function.Consumer;

public class Duck {
	// image and bounding box dimensions
	public Image img;
	public int width;
	public int height;
	public double scale;

	public int startingHp = 1;
	public int hp = 1;

	public int jitter = 0;

	public int x;
	public int y;

	public AffineTransform transform;

	public int startingVx;

	// velocity x and y
	public int vx;
	public int vy;

	// acceleration x and y
	public int ax;
	public int ay;

	public boolean isFalling = false;

	// used to tell if the duck has left the screen
	public int screenWidth;
	public int screenHeight;

	// called whenever a duck escapes to the right of the screen
	Consumer<Integer> escapeCallback;

	public Duck(Image img, Consumer<Integer> escapeCallback, double scale, int screenWidth, int screenHeight) {
		this.img = img;
		this.width = (int) (150.0 * scale);
		this.height = (int) (44.0 * scale);
		this.scale = scale;

		this.x = 0;
		this.y = 0;

		this.transform = AffineTransform.getTranslateInstance(x, y);
		this.transform.scale(scale, scale);

		this.startingVx = (int) (15.0 * scale);

		this.vx = this.startingVx;
		this.vy = 0;

		this.ax = 0;
		this.ay = 0;

		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;

		this.escapeCallback = escapeCallback;
	}

	public void onDeath() {
		this.ay = (int)(3.0 * scale);
		this.isFalling = true;
	}

	public void resetPosition() {
		this.x = -width;
		this.y = (int) (Math.random() * (320 * scale - height));

		this.vx = startingVx;
		this.vy = 0;

		this.ax = 0;
		this.ay = 0;

		this.isFalling = false;
		this.hp = startingHp;
		this.jitter = 0;
	}

	// update any variables for the object such as x, y, vx, vy
	public void update() {
		this.x += vx;
		this.y += vy;
		this.vx += ax;
		this.vy += ay;
		if (x >= screenWidth && !isFalling) {
			resetPosition();
			this.escapeCallback.accept(startingHp);
		}
		if (hp <= 0 && !isFalling) {
			onDeath();
		}
		if (y >= screenHeight) {
			resetPosition();
		}
	}

	// Draws the duck on the screen
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g; // Graphics2D lets us draw images
		this.transform.setToTranslation(this.x, this.y);
		if (jitter > 0) {
			jitter--;
			this.transform.translate((Math.random() * 2.0 - 1.0) * (jitter / 10) * this.scale, (Math.random() * 2.0 - 1.0) * (jitter / 10) * this.scale);
		}
		this.transform.scale(this.scale, this.scale);
		g2.drawImage(this.img, transform, null); // Actually draw the duck image
	}
}
