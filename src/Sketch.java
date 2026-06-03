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
    float speedIncrease = 0.002f;

    float velocity = 0;
    float gravity = 0.6f;
    float jump = -13;
    boolean onGround;
    boolean gravityFlipped = false;
    
    float droneX = random(300, 1000);
    float droneY = random(125, 475);
    float droneV = (float)(4);

    // float bg1X = 0;
    // float bg2X = 800;
    // float bg3X = 300;

    int gameState = 0;
    

    

    @Override
    public void settings() {
        size(1000, 600); 
    }

    // PImage building1;
    // PImage building2;
    // PImage building3;
    // PImage floorTile;
    // PImage ceilingTile;

    @Override
    public void setup() {
        // building1 = loadImage("data/3.png");
        // building2 = loadImage("data/4.png");
        // building3 = loadImage("data/5.png");

        //floorTile = loadImage("IndustrialTile_81.png");

    }


    @Override
    public void draw() {
        background(194, 194, 214);
        if (gameState == 0) title();
        else if (gameState == 1) {
        //bg();
            ground();
            jumping();
            mainCharacter();
            drone(droneX, 0);
            droneX -= scrollSpeed;
        }
        else if (gameState == 2) {
            gameOverScreen();
        }
    }
        

    private void title() {
        background(10, 10, 30);
        fill(0, 200, 255);
        textSize(64);
        textAlign(CENTER);
        text("MOMENTUM RUSH", 500, 220);
        fill(200);
        textSize(24);
        text("Press SPACE to start", 500, 340);
    }

    private void gameOverScreen() {
        
    }

    // private void bg() {
    //     image(building1, bg1X, 0, 800, 600);
    //     if(bg1X <= 0) {
    //         image(building1, bg1X + 800, 0, 800, 600);
    //     }
    //     bg1X += speedIncrease;

    //     image(building2, bg2X, 0, 700, 600);
    //     if(bg2X <= 0) {
    //         image(building2, bg2X + 900, 0, 700, 600);
    //     }
    //     bg2X += speedIncrease;

    //     image(building3, bg3X, 0, 600, 600);
    //     if(bg3X <= 150) {
    //         image(building3, bg3X + 800, 0, 850, 600);
    //     }
    //     bg3X += speedIncrease;

    //     bg1X -= 1;
    //     bg2X -= 2;
    //     bg3X -= 3;

        
    // }

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
    if (key == ' ') {
        if (gameState == 0) {
            gameState = 1;
        } else if (gameState == 2 && key == ' ') {
            gameState = 1;
        }
        
    }
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



    

    private void drone(float x, float y) {
        fill(0);
        rect(x + droneX, y + droneY, 75, 50);
        droneY += droneV;
        if (droneY >= 475 || droneY < 125) {
            droneV *= -1;
        }
    }




    /** Additional helper methods below */

}
