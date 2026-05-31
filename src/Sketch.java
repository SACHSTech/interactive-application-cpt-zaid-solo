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
    int mcX = 100;
    int mcY = 425;

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
        movement();
        mainCharacter();
    }

    private void ground() {
        strokeWeight(0);
        fill(179, 236, 255);
        rect(0, 450, 1000, 150);
        rect(0, 0, 1000, 150);
    }

    private void mainCharacter() {
        fill(0, 0, 0);
        circle(mcX, mcY, 50);
    }

    private void movement() {

    }

    /** Additional helper methods below */

}
