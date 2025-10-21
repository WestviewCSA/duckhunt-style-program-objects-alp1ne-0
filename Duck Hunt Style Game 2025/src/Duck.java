import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.net.URL;
import java.util.function.Consumer;
import java.util.function.Function;

// The Duck class represents a picture of a duck that can be drawn on the screen.
public class Duck {
    // Instance variables (data that belongs to each Duck object)
    private Image img;               // Stores the picture of the duck
    private AffineTransform tx;      // Used to move (translate) and resize (scale) the image

    public boolean active = true;

    // Variables to control the size (scale) of the duck image
    public double scaleX;           
    public double scaleY;           

    // Variables to control the location (x and y position) of the duck
    public double x;                
    public double y;        
    
    //variables for speed
    public int vx;
    public int vy;

    public int width;
    public int height;
    
    Consumer<Integer> escapeCallback;

    // Constructor: runs when you make a new Duck object
    public Duck(Consumer<Integer> escapeCallback) {
    	this.escapeCallback = escapeCallback;
        img = getImage("/imgs/duck.gif"); // Load the image file
        width = 250;
        height = 250;
        
        tx = AffineTransform.getTranslateInstance(0, 0); // Start with image at (0,0)
        
        // Default values
        scaleX = 1.0;
        scaleY = 1.0;
        x = 0;
        y = 0;

        init(x, y); // Set up the starting location and size
    }
    
    //2nd constructor to initialize location and scale!
    public Duck(Consumer<Integer> escapeCallback, int x, int y, int scaleX, int scaleY) {
    	this(escapeCallback);
    	this.x 		= x;
    	this.y 		= y;
    	this.scaleX = scaleX;
    	this.scaleY = scaleY;
    	init(x,y);
    }
    
    //2nd constructor to initialize location and scale!
    public Duck(Consumer<Integer> escapeCallback, int x, int y, int scaleX, int scaleY, int vx, int vy) {
    	this(escapeCallback, x, y, scaleX, scaleY);
    	this.vx 	= vx; 
    	this.vy 	= vy;
    }
    
    public void setVelocityVariables(int vx, int vy) {
    	this.vx = vx;
    	this.vy = vy;
    }
    
    // Changes the picture to a new image file
    public void changePicture(String imageFileName) {
        img = getImage("/imgs/"+imageFileName);
        init(x, y); // keep same location when changing image
    }

    public void reset() {
		x = -width;
		y = (Math.random() * (350 * 2 - height));    	
    }
    
    //update any variables for the object such as x, y, vx, vy
    public void update() {
		x += vx;
		y += vy;
		if (x >= 640 * 2 + width) {
			reset();
			if (active) {
				this.escapeCallback.accept(1);
			}
		}
    }
    
    // Draws the duck on the screen
    public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;   // Graphics2D lets us draw images
		g2.drawImage(img, tx, null);      // Actually draw the duck image
		update();
		init(x,y);
    }
    
    // Setup method: places the duck at (a, b) and scales it
    private void init(double a, double b) {
        tx.setToTranslation(a, b);        // Move the image to position (a, b)
        tx.scale(scaleX, scaleY);         // Resize the image using the scale variables
    }

    // Loads an image from the given file path
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

    // NEW: Method to set scale
    public void setScale(double sx, double sy) {
        scaleX = sx;
        scaleY = sy;
        init(x, y);  // Keep current location
    }

    // NEW: Method to set location
    public void setLocation(double newX, double newY) {
        x = newX;
        y = newY;
        init(x, y);  // Keep current scale
    }
}
