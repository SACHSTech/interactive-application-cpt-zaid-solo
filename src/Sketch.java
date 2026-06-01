import processing.core.PApplet;
import processing.core.PImage;


/**
 * Template for programs with Processing graphics output.
 * @author Your Name
 */
public class Sketch extends PApplet {
    public static void main(String[] args) {
        PApplet.main("Sketch");
    }

    //variables for setup
    float mcX = 100;
    float mcY = 475;
    float groundPOS1 = 475;
    float groundPOS2 = 125;

    float rectStart = 0;
    float rectEnd = 1000;
    float scrollSpeed = 2;
    float speedIncrease = 0.001f;

    float velocity = 0;
    float gravity = 0.6f;
    float jump = -13;
    boolean onGround;
    boolean gravityFlipped = false;

    

    @Override
    public void settings() {
        size(1000, 600); 
    }

    PImage myImage;

    @Override
    public void setup() {
        myImage = loadImage("data/2.png");

    }

    @Override
    public void draw() {
        background(230, 247, 255);
        bg();
        ground();
        jumping();
        mainCharacter();
    }

    private void bg() {
        image(myImage, 0, 0, 1000, 600);
    }

    private void ground() {
        strokeWeight(0);
        stroke(179, 236, 255);
        stroke(0);
        fill(179, 236, 255);

        scrollSpeed += speedIncrease;

        rectStart -= scrollSpeed;
        rectEnd -= scrollSpeed;

        if (rectStart + 1000 < 0) { //once fully offscreen, telelports behind second rectangle waiting to begin moving
            rectStart = rectEnd + 1000;
        }
        if (rectEnd + 1000 < 0) {
            rectEnd = rectStart + 1000;
        }

        rect(rectStart, 500, 1000, 100);
        rect(rectEnd, 500, 1000, 100);
        
        rect(rectStart, 0, 1000, 100);
        rect(rectEnd, 0, 1000, 100);
        
    }

    private void mainCharacter() {
        fill(0, 0, 0);
        circle(mcX, mcY, 50);
    }

    private void jumping() {
        onGround = false;
        mcY += velocity;

        if (gravityFlipped) {
            velocity -= gravity;
            if (mcY <= groundPOS2) {
                mcY = groundPOS2;
                velocity = 0;
                onGround = true;
            }
        } else {
            velocity += gravity;
            if (mcY >= groundPOS1) {
                mcY = groundPOS1;
                velocity = 0;
                onGround = true;
            }
        }

    }

    public void keyPressed() {
       if ((key == 'w' || key == 'W' || keyCode == UP) && onGround) {
            if (gravityFlipped) {
                velocity = 13;
            } else {
                velocity = jump;
            }
            onGround = false;
        }
        if (key == ' ' && onGround) {
            gravityFlipped = !gravityFlipped;
            velocity = 0;
        }
    }

   public void keyReleased() {
    if (key == 'w' || key == 'W' || keyCode == UP) {
        if (!gravityFlipped && velocity < 0) {
            velocity *= 0.5f;
        }
        if (gravityFlipped && velocity > 0) {
            velocity *= 0.5f;
        }
    }
}


    /** Additional helper methods below */

}
