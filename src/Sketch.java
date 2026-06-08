import processing.core.PApplet;
import processing.core.PImage;


/**
 * 16-Bit, 2D platformer game titled "Gravity Rush"
 * Goal: Avoid obstacles for as long as possible while being able to manipulate gravity
 * @author Zaid El-Batnigi
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
    float scrollSpeed = 2.5f;
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
    PImage barrel;

    float barrelNumber;
    float barrelX;
    float negativeSpeed = -20f;
    boolean barrelFlipped;
    float barrelY;
    

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

        barrel = loadImage("data/Barrel1.png");

        barrelX = width * 0.6f;
        barrelNumber = random(0, 6);
        barrelFlipped = random(1) > 0.1f;
        barrelY = height * 0.64f;
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
        scrollSpeed += speedIncrease;
        bg();
        ground();
        jumping();
        mainCharacter();
        barrel();
        checkAllHitboxes();
    }
        

    private void title() {
        background(10, 10, 30);
        fill(0, 200, 255);
        textSize(45);
        textAlign(CENTER);
        text("GRAVITY RUSH", 250, 100);
        fill(200);
        textSize(24);
        text("Press SPACE to start", 250, 160);
    }

    private void gameOverScreen() {
        background(10, 10, 30);
        fill(255, 50, 50);
        textSize(45);
        textAlign(CENTER);
        text("GAME OVER", 250, 100);
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
        bg1X -= 0.5f;

        if (bg2X + width < 0) {
            bg2X = 0;
        }
        image(building2, bg2X, 0, width, height);
        image(building2, bg2X + width, 0, width, height);
        bg2X -= 1;

    
        if (bg3X + width < 0) {
            bg3X = 0;
        }
        image(building3, bg3X, 0, width, height);
        image(building3, bg3X + width, 0, width, height);
        bg3X -= 2.2f;
        }

    private void ground() {
        strokeWeight(0);
        stroke(179, 236, 255);
        stroke(0);
        fill(179, 236, 255);


        rectStart -= scrollSpeed;
        rectEnd -= scrollSpeed;

        float tileW = width * 0.15f;
        float tileH = height * 0.2f;

        if (rectStart <= -tileW) {
            rectStart += tileW;  // reset every time one tile width has passed
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
        boolean wasJumping = jumpState;
        if (!onGround) {
            jumpState = true;
        } else {
            jumpState = false;  
        }

        if (wasJumping != jumpState) {
            currentFrame = 0;
            frameTimer = 0;
        }

        frameTimer++;

        if (!jumpState) {
            if (frameTimer >= frameDelay) {
                currentFrame = (currentFrame + 1) % 6;
                frameTimer = 0;
            }
            if (gravityFlipped) {
                drawFlipped(runFrames[currentFrame], mcX - 25, mcY - 62);
            } else {
                image(runFrames[currentFrame], mcX - 25, mcY - 62, 50, 75);
            }
        } else {
            if (frameTimer >= frameDelayJump) {
                currentFrame = (currentFrame + 1) % 4;
                frameTimer = 0;
            }
            if(gravityFlipped) {
                drawFlipped(jumpFrames[currentFrame], mcX - 25, mcY - 62);
            } else {
                image(jumpFrames[currentFrame], mcX - 25, mcY - 62, 50, 75);
            }
        }
    }

    private void drawFlipped(PImage frame, float x, float y) {
        pushMatrix();
        translate(x, y + 75);
        scale(1, -1);            
        image(frame, 0, 0 - 50, 50, 75);
        popMatrix();
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
                resetGame();
                gameState = 1;
            }
            
        }
       if ((key == 'w' || key == 'W' || keyCode == UP) && onGround) {
            if (gravityFlipped) {
                velocity = -jump;
            } else {
                velocity = jump;
                
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
    

    private void barrel() {
    // rest of code
    if (barrelFlipped) {
        barrelY = groundPOS2;
    } else {
        barrelY = height * 0.64f;
    }

    if (barrelFlipped) {
        if (barrelNumber <= 4) {
            pushMatrix();
            translate(barrelX, barrelY + 40);
            scale(1, -1);
            image(barrel, 0, 0 + 15, 20, 40);
            popMatrix();

            pushMatrix();
            translate(barrelX + 20, barrelY + 40);
            scale(1, -1);
            image(barrel, 0, 0 + 15, 20, 40);
            popMatrix();
        } else {
            pushMatrix();
            translate(barrelX, barrelY + 40);
            scale(1, -1);
            image(barrel, 0, 0 + 15, 20, 40);
            popMatrix();
        }
        } else {
            if (barrelNumber <= 4) {
                image(barrel, barrelX, barrelY, 20, 40);
                image(barrel, barrelX + 20, barrelY, 20, 40);
            } else {
                image(barrel, barrelX, barrelY, 20, 40);
            }
        }

        barrelX -= scrollSpeed;

        if (barrelX < -40) {
            barrelX = width + random(width * 0.1f, width * 0.4f);
            barrelNumber = random(0, 4);
            barrelFlipped = random(1) > 0.3f;
        }
    }
   
   private boolean hitbox(float rectX, float rectY, float rectW, float rectH) { //takes obstacles traits as parameters and returns true or false for collision
        float radius = 5; //half characters width, how far circle hitbox is from centre
        float closestX = constrain(mcX, rectX, rectX + rectW); //finds closest points on rectangle to circle centre, right edge
        float closestY = constrain(mcY, rectY, rectY + rectH); //bottom edge
        float distX = mcX - closestX; //distance between centre circle and cloest point on rectangle
        float distY = mcY - closestY;
        return (distX * distX + distY * distY) <= (radius * radius); //if diatcne within radius is touching the rectangle return true
    }

    private void checkAllHitboxes() {
        if (barrelFlipped) {
            if (hitbox(barrelX, barrelY, 20, 20)) gameState = 2;  // shorter height
            if (barrelNumber <= 4) {
                if (hitbox(barrelX + 20, barrelY, 20, 20)) gameState = 2;
            }
        } else {
            if (hitbox(barrelX, barrelY, 20, 40)) gameState = 2;
            if (barrelNumber <= 4) {
                if (hitbox(barrelX + 20, barrelY, 20, 40)) gameState = 2;
            }
        }
    }

    private void resetGame() {
        mcX = width * 0.1f;
        mcY = height * 0.8f;
        velocity = 0;
        gravityFlipped = false;
        onGround = false;
        jumpState = false;
        currentFrame = 0;
        frameTimer = 0;
        scrollSpeed = 2;

        barrelX = width + random(width * 0.5f, width);
        barrelNumber = random(0, 10);

        bg1X = 0;
        bg2X = 800;
        bg3X = 300;

        rectStart = 0;
        rectEnd = width;

        //add more varibles for future objects too
    }

    /** Additional helper methods below */

}
