import processing.core.PApplet;

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
    float mcY = 425;

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

    @Override
    public void setup() {

    }

    @Override
    public void draw() {
        background(230, 247, 255);
        ground();
        jumping();
        mainCharacter();
    }

    private void ground() {
        strokeWeight(0);
        stroke(179, 236, 255);
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

        rect(rectStart, 450, 1000, 150);
        rect(rectEnd, 450, 1000, 150);
        
        rect(rectStart, 0, 1000, 150);
        rect(rectEnd, 0, 1000, 150);
        
    }

    private void mainCharacter() {
        fill(0, 0, 0);
        circle(mcX, mcY, 50);
    }

    private void jumping() {
        mcY += velocity;

        if (gravityFlipped) {
            velocity -= gravity;
            if (mcY <= 175) {
                mcY = 175;
                velocity = 0;
                onGround = true;
            }
        } else {
            velocity += gravity;
            if (mcY >= 425) {
                mcY = 425;
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
        if (key == ' ') {
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
