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
    float speedIncrease = 0.001f;

    float velocity = 0;
    float gravity = 0.6f;
    float jump = -8.5f;
    boolean onGround;
    boolean gravityFlipped = false;
    
    float droneX;
    float droneY;
    float droneV = 4;

    float bg1X = 0;
    float bg2X = 800;
    float bg3X = 300;

    int gameState = 0;

    int currentFrame = 0;
    int frameTimer = 0;
    int frameDelay = 6;
    int frameDelayJump = 4;
    PImage[] runFrames;
    PImage[] jumpFrames;

    boolean jumpState = false;

    

    @Override
    public void settings() {
        size(500, 250, P2D); 
    }

    PImage building1;
    PImage building2;
    PImage building3;
    PImage floorTile;
    PImage ceilingTile;
    PImage spriteRun;
    PImage spriteJump;
    

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

        boolean spikeOnBottom;

        building1 = loadImage("data/3.png");
        building2 = loadImage("data/4.png");
        building3 = loadImage("data/5.png");

        floorTile = loadImage("data/IndustrialTile_81.png");
        ceilingTile = loadImage("data/IndustrialTile_78.png");

        spriteRun = loadImage("data/Cyborg_run.png");
       
        spriteJump = loadImage("data/Cyborg_jump.png");

        runFrames = new PImage[6];
        int frameW = spriteRun.width / 6;
        int frameH = spriteRun.height;
        for(int i = 0; i < 6; i++) {
            runFrames[i] = spriteRun.get(i * frameW, 0, frameW, frameH);
        }

        jumpFrames = new PImage[4];
        int frameJW = spriteJump.width / 4;
        int frameJH = spriteJump.height;
        for(int i = 0; i < 4; i++) {
            jumpFrames[i] = spriteJump.get(i * frameJW, 0, frameJW, frameJH);
        }

    }


    @Override
    public void draw() {
        background(194, 194, 214);
        if (gameState == 0) title();
        else if (gameState == 1) {
            methodDecomp();
        } else if (gameState == 2) {
            gameOverScreen(); }
    }

    private void methodDecomp() {
        bg();
        ground();
        jumping();
        mainCharacter();
        drone(droneX, 0);
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

    private void bg() {
        if (bg1X + width < 0) {
            bg1X = 0;  // reset back to start once fully offscreen
        }
        image(building1, bg1X, 0, width, height);
        image(building1, bg1X + width, 0, width, height);  // always draw second copy behind
        bg1X -= 1;

        if (bg2X + width < 0) {
            bg2X = 0;
        }
        image(building2, bg2X, 0, width, height);
        image(building2, bg2X + width, 0, width, height);
        bg2X -= 2;

    
        if (bg3X + width < 0) {
            bg3X = 0;
        }
        image(building3, bg3X, 0, width, height);
        image(building3, bg3X + width, 0, width, height);
        bg3X -= 3;
        }

    private void ground() {
        strokeWeight(0);
        stroke(179, 236, 255);
        stroke(0);
        fill(179, 236, 255);

        scrollSpeed += speedIncrease;

        rectStart -= scrollSpeed;
        rectEnd -= scrollSpeed;

        float tileW = width * 0.15f;
        float tileH = height * 0.2f;

       rectStart -= scrollSpeed;
        if (rectStart <= -tileW) {
            rectStart = 0;  // reset every time one tile width has passed
        }

        for (float x = rectStart; x < width; x += tileW) {
            image(floorTile, x, height * 0.8f, tileW, tileH);
            image(ceilingTile, x, height * 0.9f, tileW, tileH);
            pushMatrix();
            translate(x + tileW, height * 0.1f + tileH);
            rotate(PI);
            image(floorTile, 0, height * 0.1f, tileW, tileH);
            popMatrix();
            image(ceilingTile, x, height * 0.0098f, tileW, tileH);
        }
        
    }


    private void mainCharacter() {
        if (!onGround) jumpState = true;
        else jumpState = false;  

        frameTimer++;

        if (!jumpState) {
            if (frameTimer >= frameDelay) {
                currentFrame = (currentFrame + 1) % 6;
                frameTimer = 0;
            }
            image(runFrames[currentFrame], mcX - 25, mcY - 62, 50, 75);
        } else {
            if (frameTimer >= frameDelayJump) {
                currentFrame = (currentFrame + 1) % 4;
                frameTimer = 0;
            }
            image(jumpFrames[currentFrame], mcX - 25, mcY - 62, 50, 75);
        }
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
                jumpState = true;
                currentFrame = 0;  
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
            droneY = groundPOS2;      
            droneV *= -1;
        }

        droneX -= droneSpeed;
    }

    /** Additional helper methods below */

}
