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
    float mcX;
    float mcY;
    float groundPOS1;
    float groundPOS2;

    float rectStart;
    float rectEnd = width;
    float scrollSpeed = 2;
    float speedIncrease = 0.002f;

    float velocity = 0;
    float gravity = 0.6f;
    float jump = -8.5f;
    boolean onGround;
    boolean gravityFlipped = false;
    
    float droneX;
    float droneY;
    float droneV = 4;

    // float bg1X = 0;
    // float bg2X = 800;
    // float bg3X = 300;

    int gameState = 0;

    float[] obsX = {}
    

    

    @Override
    public void settings() {
        size(500, 250); 
    }

    // PImage building1;
    // PImage building2;
    // PImage building3;
    // PImage floorTile;
    // PImage ceilingTile;

    @Override
    public void setup() {
        mcX = width * 0.1f; //20% from left side
        mcY = height * 0.8f; //sit on bottom

        groundPOS1 = height * 0.75f; // floor point
        groundPOS2 = height * 0.25f; // ceiling point (flipped gravity)

        rectStart  = 0;
        rectEnd    = width;

        droneX = random(width * 0.6f, width * 2f);
        droneY = random(height * 0.5f, height * 0.9f);

        

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
        }
        else if (gameState == 2) {
            gameOverScreen();
        }
    }
        

    private void title() {
        background(10, 10, 30);
        fill(0, 200, 255);
        textSize(45);
        textAlign(CENTER);
        text("MOMENTUM RUSH", 250, 100);
        fill(200);
        textSize(24);
        text("Press SPACE to start", 250, 160);
    }

    private void gameOverScreen() {
        background(10, 10, 30);
        fill(255, 50, 50);
        textSize(45);
        textAlign(CENTER);
        text("GAME OVER", 250, 160);
        fill(200);
        textSize(24);
        text("Press SPACE to restart", 250, 160);
    }

    // private void bg() {
    //     image(building1, bg1X, 0, width, height);
    //     if (bg1X <= 0) {
    //         image(building1, bg1X + width, 0, width, height);
    //     }
    //     bg1X += speedIncrease;
    //
    //     image(building2, bg2X, 0, width * 0.875f, height);
    //     if (bg2X <= 0) {
    //         image(building2, bg2X + width * 1.125f, 0, width * 0.875f, height);
    //     }
    //     bg2X += speedIncrease;
    //
    //     image(building3, bg3X, 0, width * 0.75f, height);
    //     if (bg3X <= width * 0.1875f) {
    //         image(building3, bg3X + width, 0, width * 1.0625f, height);
    //     }
    //     bg3X += speedIncrease;
    //
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

        float tileW = width;
        float tileH = height *0.2f;

        if (rectStart + tileW < 0) { //once fully offscreen, telelports behind second rectangle waiting to begin moving
            rectStart = rectEnd + tileW;
        }
        if (rectEnd + tileW < 0) {
            rectEnd = rectStart + tileW;
        }

        rect(rectStart, height * 0.8f, tileW, tileH);
        rect(rectEnd, height * 0.8f, tileW, tileH);
        
        rect(rectStart, 0, tileW, tileH);
        rect(rectEnd, 0, tileW, tileH);
        
    }

    private void mainCharacter() {
        fill(0, 0, 0);
        circle(mcX, mcY, width * 0.05f);
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
                velocity = -jump;
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
    float dW = width * 0.06f;
    float dH = height * 0.09f;

    rect(x + droneX, y + droneY, dW, dH);

    float droneSpeed = scrollSpeed * 0.6f;
    droneY += droneV;

    if (droneY + dH >= groundPOS1) {
        droneY = groundPOS1 - dH;  // clamp to floor
        droneV *= -1;
    } else if (droneY <= groundPOS2) {
        droneY = groundPOS2;       // clamp to ceiling
        droneV *= -1;
    }

    droneX -= droneSpeed;
}

private void hitbox() {
    if (mcX)
}




    /** Additional helper methods below */

}
