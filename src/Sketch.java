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

    //variables for main character position
    float mcX;
    float mcY;
    float groundPOS1;
    float groundPOS2;

    //variables for ground movement
    float rectStart;
    float rectEnd = width;
    float scrollSpeed = 2.5f;
    float speedIncrease = 0.001f;

    //variables for jumping
    float velocity = 0;
    float gravity = 0.6f;
    float jump = -8.5f;
    boolean onGround;
    boolean gravityFlipped = false;

    //variables for background positions
    float bg1X = 0;
    float bg2X = 800;
    float bg3X = 300;

    //variable for game over
    int gameState = 0;

    //variables for character movement animations
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

    //image setup for all assets
    PImage building1;
    PImage building2;
    PImage building3;
    PImage floorTile;
    PImage ceilingTile;
    PImage spriteRun;
    PImage spriteJump;
    PImage barrel;

    //variables for barrel obstacle
    float barrelNumber;
    float barrelX;
    float negativeSpeed = -20f;
    boolean barrelFlipped;
    float barrelY;
    

    @Override
    public void setup() {
        mcX = width * 0.1f; //10% from left side
        mcY = height * 0.8f; //sit on bottom

        groundPOS1 = height * 0.75f; // floor point
        groundPOS2 = height * 0.25f; // ceiling point (flipped gravity)

        rectStart  = 0;
        rectEnd    = width;


        building1 = loadImage("data/3.png");
        building2 = loadImage("data/4.png");
        building3 = loadImage("data/5.png");

        floorTile = loadImage("data/IndustrialTile_81.png");
        ceilingTile = loadImage("data/IndustrialTile_78.png");

        spriteRun = loadImage("data/Cyborg_run.png");
       
        spriteJump = loadImage("data/Cyborg_jump.png");

        runFrames = new PImage[6]; //create array for 6 for all 6 frames
        int frameW = spriteRun.width / 6; //slices spritesheet into 6 equal images
        int frameH = spriteRun.height; //measures the height of each sprite
        for(int i = 0; i < 6; i++) { //draws each image in the array from 0-5
            runFrames[i] = spriteRun.get(i * frameW, 0, frameW, frameH);
        }

        jumpFrames = new PImage[4];
        int frameJW = spriteJump.width / 4; //splits into 4 images instead of 6
        int frameJH = spriteJump.height;
        for(int i = 0; i < 4; i++) {
            jumpFrames[i] = spriteJump.get(i * frameJW, 0, frameJW, frameJH);
        }

        barrel = loadImage("data/Barrel1.png");

        barrelX = width * 0.6f;
        barrelNumber = random(0, 6); //random number between 0 and 6.
        barrelFlipped = random(1) > 0.1f; //odds for flipped barrel to appear, generates random between 0 and 1, checks if it is larger than 0.1.
        barrelY = height * 0.64f; 
    }


    @Override
    public void draw() {
        background(194, 194, 214);
        if (gameState == 0) {
            title(); //title if gamestate is 0
        } else if (gameState == 1) {
            methodDecomp(); //all methods to keep draw as simple as possible
        } else if (gameState == 2) {
            gameOverScreen(); } //game over if gamestate is 2
    }

    private void methodDecomp() {
        scrollSpeed += speedIncrease; //allows for global same speed increase for all assets
        bg();
        ground();
        jumping();
        mainCharacter();
        barrel();
        checkAllHitboxes();
        instructions();
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

    private void instructions() {
        fill(255, 255, 255);
        textSize(12);
        text("Press W or UP to Jump", 390, 20); 

        textSize(12);
        text("Press SPACE to Flip Gravity", 400, 35); 
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

        rectStart -= scrollSpeed; //makes sure start of ground moves at scroll speed
        rectEnd -= scrollSpeed; //same but with start of second rectangle

        float tileW = width * 0.15f;
        float tileH = height * 0.2f;

        if (rectStart <= -tileW) { //if rect moves offscreen, shift by one tile width
            rectStart += tileW;  // reset every time one tile width has passed
        }

        for (float x = rectStart; x < width; x += tileW) { //starting at rectStart, the loop places tiles every tileW pixels until it reaches right side of the screen
            image(floorTile, x, height * 0.8f, tileW, tileH);
            image(ceilingTile, x, height * 0.9f, tileW, tileH);
            pushMatrix(); //flips ceiling tiles
            translate(x + tileW, height * 0.1f + tileH);
            rotate(PI);
            image(floorTile, 0, height * 0.1f, tileW, tileH);
            popMatrix();
            image(ceilingTile, x, height * 0.0098f, tileW, tileH);
        }
    }


    private void mainCharacter() {
        boolean wasJumping = jumpState;
        if (!onGround) { //if not on ground, character is jumping
            jumpState = true;
        } else {
            jumpState = false;  
        }

        if (wasJumping != jumpState) { //checks if switch from running to jumping
            currentFrame = 0; //reset animation if switching from run/jump
            frameTimer = 0;
        }

        frameTimer++; //increase timer by one every frame

        if (!jumpState) {
            if (frameTimer >= frameDelay) { //checks if enough time has passed to move to next frame
                currentFrame = (currentFrame + 1) % 6; //moves to the next frame, % keeps frame number between 0 and 5
                frameTimer = 0;
            }
            if (gravityFlipped) {
                drawFlipped(runFrames[currentFrame], mcX - 25, mcY - 62); //draw the sprite but flipped
            } else {
                image(runFrames[currentFrame], mcX - 25, mcY - 62, 50, 75);
            }
        } else {
            if (frameTimer >= frameDelayJump) { //same thing but for jumping
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

    private void drawFlipped(PImage frame, float x, float y) { //draws upside down sprites
        pushMatrix();
        translate(x, y + 75);
        scale(1, -1);            
        image(frame, 0, 0 - 50, 50, 75);
        popMatrix();
    }

    private void jumping() {
        onGround = false; //player in air when key is pressed for jumping
        mcY += velocity; //speed of jump, moves player vert

        if (gravityFlipped) {
            velocity -= gravity; //applies reverse gravity, moves character on ceiling
            if (mcY <= groundPOS2) { //if character on ceiling (landed)
                mcY = groundPOS2; //snaps player back to ceiling and stops clipping
                velocity = 0; //stops vertical movement when landing
                onGround = true; //sets character on ground
            }
        } else { //if normal gravity 
            velocity += gravity; //same thing but for normal gravity
            if (mcY >= groundPOS1) {
                mcY = groundPOS1;
                velocity = 0;
                onGround = true;
            }
        }

    }


    public void keyPressed() {
        if (key == ' ') { 
            if (gameState == 0) { //if space pressed on title screen start game
                gameState = 1;
            } else if (gameState == 2 && key == ' ') { //if key pressed during game over
                resetGame(); //reset all variables
                gameState = 1; //start game again
            }
            
        }
       if ((key == 'w' || key == 'W' || keyCode == UP) && onGround) {
            if (gravityFlipped) {
                velocity = -jump; //if gravity is upside down, character jumps negative
            } else {
                velocity = jump; //if gravity is normal, character jumps positive
                
                currentFrame = 0; //resets animation frame
            }
            onGround = false; //character not on ground is jump is pressed
        }
        if (key == ' ' && onGround) { //flips gravity of character
            gravityFlipped = !gravityFlipped;
            velocity = 0;
        }
    }

   public void keyReleased() {
        if (key == 'w' || key == 'W' || keyCode == UP) {
            if (!gravityFlipped && velocity < 0) { //ends jump early when gravity is normal (< 0 means player moving downwards)
                velocity *= 0.5f;
            }
            if (gravityFlipped && velocity > 0) { //same but for flipped gravity (> 0 means player is going upwards)
                velocity *= 0.5f;
            }
        }
    }
    

    private void barrel() {
    // rest of code
    if (barrelFlipped) { //sets barrel to ceiling ground pos if flipped
        barrelY = groundPOS2;
    } else {
        barrelY = height * 0.64f; //sets barrel to normal ground
    }
    if (barrelFlipped) {
        if (barrelNumber <= 4) { //chances for upside down double barrel to appear
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
        } else { //upside down single barrel
            pushMatrix();
            translate(barrelX, barrelY + 40);
            scale(1, -1);
            image(barrel, 0, 0 + 15, 20, 40);
            popMatrix();
        }
        } else { //normal double barrel
            if (barrelNumber <= 4) {
                image(barrel, barrelX, barrelY, 20, 40);
                image(barrel, barrelX + 20, barrelY, 20, 40);
            } else { //normal single barrel
                image(barrel, barrelX, barrelY, 20, 40);
            }
        }

        barrelX -= scrollSpeed; //moves barrel with ground

        if (barrelX < -40) { //resets barrel when offscreen
            barrelX = width + random(width * 0.1f, width * 0.4f); //random X away from right edge
            barrelNumber = random(0, 4); 
            barrelFlipped = random(1) > 0.3f;
        }
    }
   
   private boolean hitbox(float rectX, float rectY, float rectW, float rectH) { //takes obstacles traits as parameters and returns true or false for collision
        float radius = 5; //half characters width, how far circle hitbox is from centre
        float closestX = constrain(mcX, rectX, rectX + rectW); //finds closest points on rectangle to circle centre, right edge, limits values to sprite width
        float closestY = constrain(mcY, rectY, rectY + rectH); //bottom edge hitbox
        float distX = mcX - closestX; //distance between centre circle and closest point on rectangle
        float distY = mcY - closestY;
        return (distX * distX + distY * distY) <= (radius * radius); //if distance within radius is touching the rectangle return true
        //squared distance between circle center and rectangle, squared radius of circle
        //If the squared distance is less than or equal to the squared radius, the circle is touching or inside the rectangle, so the method returns true, otherwise it returns false
    }

    private void checkAllHitboxes() {
        if (barrelFlipped) { 
            if (hitbox(barrelX, barrelY, 20, 20)) { //calls hitbox, creates a rectangle and returns true or false
                gameState = 2; //if true, game ends
            }  // shorter height
            if (barrelNumber <= 4) { //hitbox for double barrel
                if (hitbox(barrelX + 20, barrelY, 20, 20)) {
                    gameState = 2;
                }
            }
        } else {
            if (hitbox(barrelX, barrelY, 20, 40)) { //creates barrel hitbox for normal gravity
                gameState = 2;
            }
            if (barrelNumber <= 4) {
                if (hitbox(barrelX + 20, barrelY, 20, 40)) {
                    gameState = 2;
                }
            }
        }
    }

    private void resetGame() { //resets all variables when game resets
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
