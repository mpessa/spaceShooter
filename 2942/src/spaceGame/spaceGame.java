package spaceGame;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import jig.Collision;
import jig.Entity;
import jig.ResourceManager;
import jig.Vector;

import org.newdawn.slick.Animation;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

/**
 * Graphics resources courtesy of qubodup:
 * http://opengameart.org/content/bomb-explosion-animation
 * 
 */
public class spaceGame extends BasicGame {
	private static final int START_UP = 1;
	private static final int PLAYING = 2;
	private static final int GAME_OVER = 3;
	private static final int TRANSITION = 4;
	private static final int WIN = 5;
	private static final int EXIT = 6;

	private final int ScreenWidth;
	private final int ScreenHeight;

	private static int gameState; 
	private int gameOverTimer;
	private ArrayList<Bang> explosions;
	private Background space;
	private spaceShip playerShip;
	private ArrayList<lifeShip> showLives;
	private lifeShip life1;
	private ArrayList<laser> pShots;
	private laser pShot;
	private ArrayList<simpleEnemy> REnemies1;
	private ArrayList<simpleEnemy> LEnemies1;
	private simpleEnemy en1;
	private shieldPowerUp shield;
	private ArrayList<shieldPowerUp> sh1;
	private ArrayList<shieldIcon> sIcons;
	private shieldIcon sIcon;
	private ArrayList<shipShield> pShields;
	private shipShield pShield;
	private ArrayList<boss1> bossQ;
	private boss1 boss;
	private ArrayList<enemyLaser> eShots;
	private enemyLaser eShot;
	private ArrayList<bigEnemy> LEnemies2;
	private ArrayList<bigEnemy> REnemies2;
	private bigEnemy en2;
	private ArrayList<shootingEnemy> LEnemies3;
	private ArrayList<shootingEnemy> REnemies3;
	private shootingEnemy en3;
	private ArrayList<kEnemy> kamikaze;
	private kEnemy en4;
	private ArrayList<rapidFire> rfs;
	private rapidFire RF;
	private ArrayList<threeWayPower> threeWay;
	private threeWayPower threeP;
	private ArrayList<threeWayL> pShotsL;
	private threeWayL pShotL;
	private ArrayList<threeWayR> pShotsR;
	private threeWayR pShotR;
	private int lives = 3;
	private Random random;
	public int score = 0;
	private int gameTimer = 0;
	private int level = 3;
	private int shipsCreated = 0;
	private int shipsDestroyed = 0;
	
	/**
	 * Create the BounceGame frame, saving the width and height for later use.
	 * 
	 * @param title
	 *            the window's title
	 * @param width
	 *            the window's width
	 * @param height
	 *            the window's height
	 */
	public spaceGame(String title, int width, int height) {
		super(title);
		ScreenHeight = height;
		ScreenWidth = width;

		Entity.setCoarseGrainedCollisionBoundary(Entity.AABB);
		showLives = new ArrayList<lifeShip>(3);
		pShots = new ArrayList<laser>(20);
		REnemies1 = new ArrayList<simpleEnemy>(10);
		LEnemies1 = new ArrayList<simpleEnemy>(10);
		explosions = new ArrayList<Bang>(10);
		sh1 = new ArrayList<shieldPowerUp>(5);
		sIcons = new ArrayList<shieldIcon>(3);
		pShields = new ArrayList<shipShield>(2);
		bossQ = new ArrayList<boss1>(1);
		eShots = new ArrayList<enemyLaser>(10);
		REnemies2 = new ArrayList<bigEnemy>(10);
		LEnemies2 = new ArrayList<bigEnemy>(10);
		REnemies3 = new ArrayList<shootingEnemy>(10);
		LEnemies3 = new ArrayList<shootingEnemy>(10);
		kamikaze = new ArrayList<kEnemy>(5);
		rfs = new ArrayList<rapidFire>(5);
		threeWay = new ArrayList<threeWayPower>(10);
		pShotsR = new ArrayList<threeWayR>(10);
		pShotsL = new ArrayList<threeWayL>(10);
	}

	/**
	 * Initialize the game after the container has been set up. This is one-time
	 * initialization, and a good place to do things like load sounds and
	 * graphics...
	 * 
	 */
	@Override
	public void init(GameContainer container) throws SlickException {
		
		ResourceManager.loadImage("resource/playerShip.png");
		ResourceManager.loadImage("resource/lifeShip.png");
		ResourceManager.loadImage("resource/space-1.jpg");
		ResourceManager.loadImage("resource/explosion.png");
		ResourceManager.loadImage("resource/laser.png");
		ResourceManager.loadImage("resource/enemy1.png");
		ResourceManager.loadImage("resource/shield1.png");
		ResourceManager.loadImage("resource/shieldIcon.png");
		ResourceManager.loadImage("resource/shieldIcon2.png");
		ResourceManager.loadImage("resource/enemy2.png");
		ResourceManager.loadImage("resource/boss1.png");
		ResourceManager.loadImage("resource/enemyLaser.png");
		ResourceManager.loadImage("resource/enemy3.png");
		ResourceManager.loadImage("resource/enemy.png");
		ResourceManager.loadImage("resource/enemy45.png");
		ResourceManager.loadImage("resource/enemy-45.png");
		ResourceManager.loadImage("resource/enemy90.png");
		ResourceManager.loadImage("resource/enemy-90.png");
		ResourceManager.loadImage("resource/Flameless.png");
		ResourceManager.loadImage("resource/level2.png");
		ResourceManager.loadImage("resource/medal1.png");
		ResourceManager.loadImage("resource/medal2.png");
		
		ResourceManager.loadSound("resource/laser5.wav");
		
		// the sound resource takes a particularly long time to load,
		// we preload it here to (1) reduce latency when we first play it
		// and (2) because loading it will load the audio libraries and
		// unless that is done now, we can't *disable* sound as we
		// attempt to do in the startUp() method.
		space = new Background(ScreenWidth / 2, ScreenHeight / 2);
		playerShip = new spaceShip(ScreenWidth / 2, 650, 0, 0);
		life1 = new lifeShip(30, 90);
		showLives.add(life1);
		life1 = new lifeShip(80, 90);
		showLives.add(life1);
		life1 = new lifeShip(130, 90);
		showLives.add(life1);
		sIcon = new shieldIcon(170, 685);
		sIcons.add(sIcon);
		sIcon = new shieldIcon(185, 685);
		sIcons.add(sIcon);
		startUp(container);
	}

	/**
	 * Put the game in 'demo' mode to lure new players!
	 */
	public void startUp(GameContainer container) {
		gameState = START_UP;
		container.setSoundOn(false);
	}

	/**
	 * Prepare for a new game (after one time initialization)
	 */
	public void newGame(GameContainer container) {
		gameState = PLAYING;
		container.setSoundOn(true);
	}
	
	/*
	 * Pause the game
	 
	public void pauseGame(){
		gameState = PAUSE;
	}

	*/
	/**
	 * Put the game in the GameOver state, which will last for a limited time.
	 */
	public void gameOver() {
		gameState = GAME_OVER;
		gameOverTimer = 4000;
	}
	
	public void gameWon(){
		gameState = WIN;
	}

	/**
	 * Render the game state.
	 */
	@Override
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		switch (gameState) {
		case PLAYING:
			space.render(g);
			if(playerShip.timeDead <= 0){
				playerShip.render(g);
			}
			for(int i = 0; i < lives; i++){
				life1 = showLives.get(i);
				life1.render(g);
			}
			for(int i = 0; i < pShots.size(); i++){
				pShot = pShots.get(i);
				pShot.render(g);
			}
			for(int i = 0; i < pShotsL.size(); i++){
				pShotL = pShotsL.get(i);
				pShotL.render(g);
			}
			for(int i = 0; i < pShotsR.size(); i++){
				pShotR = pShotsR.get(i);
				pShotR.render(g);
			}
			for(int i = 0; i < REnemies1.size(); i++){
				en1 = REnemies1.get(i);
				en1.render(g);
			}
			for(int i = 0; i < LEnemies1.size(); i++){
				en1 = LEnemies1.get(i);
				en1.render(g);
			}
			for(int i = 0; i < sh1.size(); i++){
				shield = sh1.get(i);
				shield.render(g);
			}
			for(int i = 0; i < playerShip.canShield; i++){
				sIcon = sIcons.get(i);
				sIcon.render(g);
			}
			for(int i = 0; i < pShields.size(); i++){
				pShield = pShields.get(i);
				pShield.render(g);
			}
			for(int i = 0; i < rfs.size(); i++){
				RF = rfs.get(i);
				RF.render(g);
			}
			for(int i = 0; i < threeWay.size(); i++){
				threeP = threeWay.get(i);
				threeP.render(g);
			}
			for(int i = 0; i < bossQ.size(); i++){
				boss = bossQ.get(i);
				boss.render(g);
			}
			for(int i = 0; i < eShots.size(); i ++){
				eShot = eShots.get(i);
				eShot.render(g);
			}
			for(int i = 0; i < REnemies2.size(); i++){
				en2 = REnemies2.get(i);
				en2.render(g);
			}
			for(int i = 0; i < LEnemies2.size(); i++){
				en2 = LEnemies2.get(i);
				en2.render(g);
			}
			for(int i = 0; i < REnemies3.size(); i++){
				en3 = REnemies3.get(i);
				en3.render(g);
			}
			for(int i = 0; i < LEnemies3.size(); i++){
				en3 = LEnemies3.get(i);
				en3.render(g);
			}
			for(int i = 0; i < kamikaze.size(); i++){
				en4 = kamikaze.get(i);
				en4.render(g);
			}
			for(Bang b : explosions)
				b.render(g);
			g.drawString("Score: " + score, 10, 30);
			g.drawString("Lives: ", 10, 50);
			g.drawString("Shield Available: ", 10, 675);
			break;
		case START_UP:
			space.render(g);
			playerShip.render(g);
			for(int i = 0; i < lives; i++){
				life1 = showLives.get(i);
				life1.render(g);
			}
			g.drawString("Score: ?", 10, 30);
			g.drawString("Lives: ", 10, 50);
			break;
		case GAME_OVER:
			space.render(g);
			for(Bang b : explosions)
				b.render(g);
			break;
		case TRANSITION:
			space.render(g);
			playerShip.render(g);
			if(shipsCreated == shipsDestroyed){
				g.drawImage(ResourceManager.getImage("resource/medal1.png"), ScreenWidth / 2 - 20, ScreenHeight / 3);
			}
			else if(shipsCreated - shipsDestroyed <= 2){
				g.drawImage(ResourceManager.getImage("resource/medal2.png"), ScreenWidth / 2 - 20, ScreenHeight / 3);
			}
			if(level == 2)
				g.drawImage(ResourceManager.getImage("resource/level2.png"), ScreenWidth / 3 + 40, ScreenHeight / 2);
			if(level == 3)
				g.drawImage(ResourceManager.getImage("resource/level3.png"), ScreenWidth / 3 + 40, ScreenHeight / 2);
			for (Bang b : explosions)
				b.render(g);
			break;
		case WIN:

		}
	}

	public boolean dropPowerUp(){
		random = new Random();
		float x = random.nextFloat();
		if(x <= 0.2)
			return true;
		else
			return false;
	}
	
	public int selectPowerUp(){
		random = new Random();
		float x = random.nextFloat();
		if(x > 0.7)
			return 2;
		else if(x < 0.7 && x > 0.3){
			return 1;
		}
		else
			return 0;
	}
	
	public void killShip(){
		playerShip.isAlive = false;
		lives -= 1;
		score -= 100;
		playerShip.timeDead = 1000;
		playerShip.setX(ScreenWidth / 2);
		playerShip.setY(650);
		playerShip.setVelocity(new Vector(0, 0));
		playerShip.removeImage(ResourceManager.getImage("resource/playerShip.png"));
	}
	
	public void killBoss(){
		for(int i = 0; i < bossQ.size(); i ++){
			boss = bossQ.get(i);
			boss.setVelocity(new Vector(0f, 0f));
			boss.canShoot = false;
			explosions.add(new Bang(boss.getX(), boss.getY() + 100));
			explosions.add(new Bang(boss.getX() - 100, boss.getY() + 100));
			explosions.add(new Bang(boss.getX() + 100, boss.getY() + 100));
			bossQ.remove(i);
		}
	}
	/**
	 * Update the game state based on user input and events that transpire in
	 * this frame.
	 */
	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {

		// in the GameOver state we just updated the explosions and wait until
		// the state switches... The ball is invisible and paralyzed.
		if (gameState == GAME_OVER) {
			gameOverTimer -= delta;
			if (gameOverTimer <= 0)
				startUp(container);
		} else {
			// get user input
			Input input = container.getInput();

			if (gameState == START_UP) {
				if (input.isKeyDown(Input.KEY_ENTER)){
					newGame(container);
				}
			} else if(gameState == TRANSITION){
				gameTimer = -50;
				if(playerShip.getY() > 0)
					playerShip.setVelocity(new Vector(0f, -0.3f));
				if(playerShip.getY() < 0){
					playerShip.setPosition(ScreenWidth / 2, 650);
					gameState = PLAYING;
				}
				playerShip.update(delta);
			} else if(gameState == WIN){
				if(input.isKeyDown(Input.KEY_SPACE)){
					startUp(container);
				}
				if(input.isKeyDown(Input.KEY_X)){
					gameState = EXIT;
				}
			} else if(gameState == EXIT){

				container.exit();
			} else {
				if(playerShip.isAlive){
					if(input.isKeyDown(Input.KEY_A)){
						playerShip.setVelocity(new Vector(-0.25f, 0f));
					}
					if(input.isKeyDown(Input.KEY_D)){
						playerShip.setVelocity(new Vector(0.25f, 0f));
					}
					if (input.isKeyDown(Input.KEY_SPACE) && playerShip.canShoot == true) {
						playerShip.shotDelay = 0;
						playerShip.canShoot = false;
						pShot = new laser(playerShip.getX(), playerShip.getY() - 20, 0f, -0.3f);
						pShots.add(pShot);
						ResourceManager.getSound("resource/laser5.wav").play();
						if(playerShip.threeWay){
							pShotR = new threeWayR(playerShip.getX() + 5, playerShip.getY() - 20, 0.1f, -0.3f);
							pShotsR.add(pShotR);
							pShotL = new threeWayL(playerShip.getX() - 5, playerShip.getY() - 20, -0.1f, -0.3f);
							pShotsL.add(pShotL);
						}
					}
					if (input.isKeyDown(Input.KEY_W)) {
						playerShip.setVelocity(new Vector(0f, -0.25f));
					}
					if (input.isKeyDown(Input.KEY_S)) {
						playerShip.setVelocity(new Vector(0f, 0.25f));
					}
					if(input.isKeyDown(Input.KEY_A) && input.isKeyDown(Input.KEY_S)){
						playerShip.setVelocity(new Vector(-0.25f, 0.25f));
					}
					if(input.isKeyDown(Input.KEY_A) && input.isKeyDown(Input.KEY_W)){
						playerShip.setVelocity(new Vector(-0.25f, -0.25f));
					}
					if(input.isKeyDown(Input.KEY_D) && input.isKeyDown(Input.KEY_S)){
						playerShip.setVelocity(new Vector(0.25f, 0.25f));
					}
					if(input.isKeyDown(Input.KEY_D) && input.isKeyDown(Input.KEY_W)){
						playerShip.setVelocity(new Vector(0.25f, -0.25f));
					}
					if (!input.isKeyDown(Input.KEY_W) && !input.isKeyDown(Input.KEY_S)
						&& !input.isKeyDown(Input.KEY_A) && !input.isKeyDown(Input.KEY_D)) {
						playerShip.setVelocity(new Vector(0f, 0));
					}
					if(input.isKeyDown(Input.KEY_RSHIFT) && playerShip.canShield > 0
						&& playerShip.isAlive){
						pShield = new shipShield(playerShip.getX(), playerShip.getY(), 0, 0);
						pShields.add(pShield);
						playerShip.shieldOn = true;
						playerShip.canShield -= 1;
					}
				}
				if(playerShip.getCoarseGrainedMaxX() > ScreenWidth){
					playerShip.setVelocity(new Vector(-0.05f, 0f));
				}
				if(playerShip.getCoarseGrainedMinX() < 0){
					playerShip.setVelocity(new Vector(0.05f, 0f));
				}
				if(playerShip.getCoarseGrainedMaxY() > ScreenHeight){
					playerShip.setVelocity(new Vector(0f, -0.5f));
				}
				if(playerShip.getCoarseGrainedMinY() <= ScreenHeight / 2){
				playerShip.setVelocity(new Vector(0f, 0.5f));
				}
			
				//Collision checks for next ~550 lines
				for(int i = 0; i < sh1.size(); i ++){
					shield = sh1.get(i);
					if(playerShip.collides(shield) != null){
						Collision collision = playerShip.collides(shield);
						Vector collVector = collision.getMinPenetration();
						if(collVector.getX() != 0){
							if(playerShip.canShield <= 1)
								playerShip.canShield += 1;
							sh1.remove(i);
						}
						if(collVector.getY() != 0){
							if(playerShip.canShield <= 1)
								playerShip.canShield += 1;
							sh1.remove(i);
						}
					}
				}
				for(int i = 0; i < rfs.size(); i++){
					RF = rfs.get(i);
					if(playerShip.collides(RF) != null){
						Collision collision = playerShip.collides(RF);
						Vector collVector = collision.getMinPenetration();
						if(collVector.getX() != 0){
							rfs.remove(i);
							playerShip.powerUpTimer = 0;
							playerShip.powerUp = true;
						}
						if(collVector.getY() != 0){
							rfs.remove(i);
							playerShip.powerUpTimer = 0;
							playerShip.powerUp = true;
						}
					}
				}
				for(int i = 0; i < threeWay.size(); i++){
					threeP = threeWay.get(i);
					if(playerShip.collides(threeP) != null){
						Collision collision = playerShip.collides(threeP);
						Vector collVector = collision.getMinPenetration();
						if(collVector.getX() != 0){
							threeWay.remove(i);
							playerShip.threeWayTimer = 0;
							playerShip.threeWay = true;
						}
						if(collVector.getY() != 0){
							threeWay.remove(i);
							playerShip.threeWayTimer = 0;
							playerShip.threeWay = true;
						}
					}
				}
				if(!playerShip.shieldOn && playerShip.isAlive){
					for(int i = 0; i < LEnemies1.size(); i++){
						en1 = LEnemies1.get(i);
						if(playerShip.collides(en1) != null){
							Collision collision = playerShip.collides(en1);
							Vector collVector = collision.getMinPenetration();
							if(collVector.getX() != 0){
								explosions.add(new Bang(en1.getX(), en1.getY()));
								explosions.add(new Bang(playerShip.getX(), playerShip.getY()));
								LEnemies1.remove(i);
								killShip();
							}
							if(collVector.getY() != 0){
								explosions.add(new Bang(en1.getX(), en1.getY()));
								explosions.add(new Bang(playerShip.getX(), playerShip.getY()));
								LEnemies1.remove(i);
								killShip();
							}
						}
						if(en1.getCoarseGrainedMinY() >= ScreenHeight){
							LEnemies1.remove(i);
						}
					}
					for(int i = 0; i < REnemies1.size(); i++){
						en1 = REnemies1.get(i);
						if(playerShip.collides(en1) != null){
							Collision collision = playerShip.collides(en1);
							Vector collVector = collision.getMinPenetration();
							if(collVector.getX() != 0){
								explosions.add(new Bang(en1.getX(), en1.getY()));
								explosions.add(new Bang(playerShip.getX(), playerShip.getY()));
								REnemies1.remove(i);
								killShip();
							}
							if(collVector.getY() != 0){
								explosions.add(new Bang(en1.getX(), en1.getY()));
								explosions.add(new Bang(playerShip.getX(), playerShip.getY()));
								REnemies1.remove(i);
								killShip();
							}
						}
						if(en1.getCoarseGrainedMinY() >= ScreenHeight){
							REnemies1.remove(i);
						}
					}
					for(int i = 0; i < LEnemies2.size(); i++){
						en2 = LEnemies2.get(i);
						if(playerShip.collides(en2) != null){
							Collision collision = playerShip.collides(en2);
							Vector collVector = collision.getMinPenetration();
							if(collVector.getX() != 0){
								explosions.add(new Bang(en2.getX(), en2.getY()));
								explosions.add(new Bang(playerShip.getX(), playerShip.getY()));
								LEnemies2.remove(i);
								killShip();
							}
							if(collVector.getY() != 0){
								explosions.add(new Bang(en2.getX(), en2.getY()));
								explosions.add(new Bang(playerShip.getX(), playerShip.getY()));
								LEnemies2.remove(i);
								killShip();
							}
						}
						if(en2.getCoarseGrainedMinY() >= ScreenHeight){
							LEnemies2.remove(i);
						}
					}
					for(int i = 0; i < REnemies2.size(); i++){
						en2 = REnemies2.get(i);
						if(playerShip.collides(en2) != null){
							Collision collision = playerShip.collides(en2);
							Vector collVector = collision.getMinPenetration();
							if(collVector.getX() != 0){
								explosions.add(new Bang(en2.getX(), en2.getY()));
								explosions.add(new Bang(playerShip.getX(), playerShip.getY()));
								REnemies2.remove(i);
								killShip();
							}
							if(collVector.getY() != 0){
								explosions.add(new Bang(en2.getX(), en2.getY()));
								explosions.add(new Bang(playerShip.getX(), playerShip.getY()));
								REnemies2.remove(i);
								killShip();
							}
						}
						if(en2.getCoarseGrainedMinY() >= ScreenHeight){
							REnemies2.remove(i);
						}
					}
					for(int i = 0; i < LEnemies3.size(); i++){
						en3 = LEnemies3.get(i);
						if(playerShip.collides(en3) != null){
							Collision collision = playerShip.collides(en3);
							Vector collVector = collision.getMinPenetration();
							if(collVector.getX() != 0){
								explosions.add(new Bang(en3.getX(), en3.getY()));
								explosions.add(new Bang(playerShip.getX(), playerShip.getY()));
								LEnemies3.remove(i);
								killShip();
							}
							if(collVector.getY() != 0){
								explosions.add(new Bang(en3.getX(), en3.getY()));
								explosions.add(new Bang(playerShip.getX(), playerShip.getY()));
								LEnemies3.remove(i);
								killShip();
							}
						}
						if(en3.getCoarseGrainedMinY() >= ScreenHeight){
							LEnemies3.remove(i);
						}
					}
					for(int i = 0; i < REnemies3.size(); i++){
						en3 = REnemies3.get(i);
						if(playerShip.collides(en3) != null){
							Collision collision = playerShip.collides(en3);
							Vector collVector = collision.getMinPenetration();
							if(collVector.getX() != 0){
								explosions.add(new Bang(en3.getX(), en3.getY()));
								explosions.add(new Bang(playerShip.getX(), playerShip.getY()));
								REnemies3.remove(i);
								killShip();
							}
							if(collVector.getY() != 0){
								explosions.add(new Bang(en3.getX(), en3.getY()));
								explosions.add(new Bang(playerShip.getX(), playerShip.getY()));
								REnemies3.remove(i);
								killShip();
							}
						}
						if(en3.getCoarseGrainedMinY() >= ScreenHeight){
							REnemies3.remove(i);
						}
					}
					for(int i = 0; i < kamikaze.size(); i++){
						en4 = kamikaze.get(i);
						if(playerShip.collides(en4) != null){
							Collision collision = playerShip.collides(en4);
							Vector collVector = collision.getMinPenetration();
							if(collVector.getX() != 0){
								explosions.add(new Bang(en4.getX(), en4.getY()));
								explosions.add(new Bang(playerShip.getX(), playerShip.getY()));
								kamikaze.remove(i);
								killShip();
							}
							if(collVector.getY() != 0){
								explosions.add(new Bang(en4.getX(), en4.getY()));
								explosions.add(new Bang(playerShip.getX(), playerShip.getY()));
								kamikaze.remove(i);
								killShip();
							}
						}
						if(en4.getCoarseGrainedMinY() >= ScreenHeight
								|| en4.getCoarseGrainedMaxX() >= ScreenWidth
								|| en4.getCoarseGrainedMinX() <= 0){
							kamikaze.remove(i);
						}
					}
					for(int i = 0; i < eShots.size(); i++){
						eShot = eShots.get(i);
						if(playerShip.collides(eShot) != null){
							Collision collision = playerShip.collides(eShot);
							Vector collVector = collision.getMinPenetration();
							if(collVector.getX() != 0){
								explosions.add(new Bang(playerShip.getX(), playerShip.getY()));
								eShots.remove(i);
								killShip();
							}
							if(collVector.getY() != 0){
								explosions.add(new Bang(playerShip.getX(), playerShip.getY()));
								eShots.remove(i);
								killShip();
							}
						}
					}
				}
				else{
					for(int j = 0; j < pShields.size(); j++){
						pShield = pShields.get(j);
						for(int i = 0; i < LEnemies1.size(); i++){
							en1 = LEnemies1.get(i);
							if(pShield.collides(en1) != null){
								Collision collision = pShield.collides(en1);
								Vector collVector = collision.getMinPenetration();
								if(collVector.getX() != 0){
									explosions.add(new Bang(en1.getX(), en1.getY()));
									LEnemies1.remove(i);
									shipsDestroyed += 1;
								}
								if(collVector.getY() != 0){
									explosions.add(new Bang(en1.getX(), en1.getY()));
									LEnemies1.remove(i);
									shipsDestroyed += 1;
								}
							}
						}
						for(int i = 0; i < REnemies1.size(); i++){
							en1 = REnemies1.get(i);
							if(pShield.collides(en1) != null){
								Collision collision = playerShip.collides(en1);
								Vector collVector = collision.getMinPenetration();
								if(collVector.getX() != 0){
									explosions.add(new Bang(en1.getX(), en1.getY()));
									REnemies1.remove(i);
									shipsDestroyed += 1;
								}
								if(collVector.getY() != 0){
									explosions.add(new Bang(en1.getX(), en1.getY()));
									REnemies1.remove(i);
									shipsDestroyed += 1;
								}
							}
						}
						for(int i = 0; i < LEnemies2.size(); i++){
							en2 = LEnemies2.get(i);
							if(pShield.collides(en2) != null){
								Collision collision = pShield.collides(en2);
								Vector collVector = collision.getMinPenetration();
								if(collVector.getX() != 0){
									explosions.add(new Bang(en2.getX(), en2.getY()));
									LEnemies2.remove(i);
									shipsDestroyed += 1;
								}
								if(collVector.getY() != 0){
									explosions.add(new Bang(en2.getX(), en2.getY()));
									LEnemies2.remove(i);
									shipsDestroyed += 1;
								}
							}
						}
						for(int i = 0; i < REnemies2.size(); i++){
							en2 = REnemies2.get(i);
							if(pShield.collides(en2) != null){
								Collision collision = playerShip.collides(en2);
								Vector collVector = collision.getMinPenetration();
								if(collVector.getX() != 0){
									explosions.add(new Bang(en2.getX(), en2.getY()));
									REnemies2.remove(i);
									shipsDestroyed += 1;
								}
								if(collVector.getY() != 0){
									explosions.add(new Bang(en2.getX(), en2.getY()));
									REnemies2.remove(i);
									shipsDestroyed += 1;
								}
							}
						}
						for(int i = 0; i < LEnemies3.size(); i++){
							en3 = LEnemies3.get(i);
							if(pShield.collides(en3) != null){
								Collision collision = pShield.collides(en3);
								Vector collVector = collision.getMinPenetration();
								if(collVector.getX() != 0){
									explosions.add(new Bang(en3.getX(), en3.getY()));
									LEnemies3.remove(i);
									shipsDestroyed += 1;
								}
								if(collVector.getY() != 0){
									explosions.add(new Bang(en3.getX(), en3.getY()));
									LEnemies3.remove(i);
									shipsDestroyed += 1;
								}
							}
						}
						for(int i = 0; i < REnemies3.size(); i++){
							en3 = REnemies3.get(i);
							if(pShield.collides(en3) != null){
								Collision collision = playerShip.collides(en3);
								Vector collVector = collision.getMinPenetration();
								if(collVector.getX() != 0){
									explosions.add(new Bang(en3.getX(), en3.getY()));
									REnemies3.remove(i);
									shipsDestroyed += 1;
								}
								if(collVector.getY() != 0){
									explosions.add(new Bang(en3.getX(), en3.getY()));
									REnemies3.remove(i);
									shipsDestroyed += 1;
								}
							}
						}
						for(int i = 0; i < kamikaze.size(); i++){
							en4 = kamikaze.get(i);
							if(pShield.collides(en4) != null){
								Collision collision = pShield.collides(en4);
								Vector collVector = collision.getMinPenetration();
								if(collVector.getX() != 0){
									explosions.add(new Bang(en4.getX(), en4.getY()));
									kamikaze.remove(i);
									shipsDestroyed += 1;
								}
								if(collVector.getY() != 0){
									explosions.add(new Bang(en4.getX(), en4.getY()));
									kamikaze.remove(i);
									shipsDestroyed += 1;
								}
							}
						}
						for(int i = 0; i < eShots.size(); i++){
							eShot = eShots.get(i);
							if(pShield.collides(eShot) != null){
								Collision collision = pShield.collides(eShot);
								Vector collVector = collision.getMinPenetration();
								if(collVector.getX() != 0){
									eShots.remove(i);
								}
								if(collVector.getY() != 0){
									eShots.remove(i);
								}
							}
						}
					}
				}
				for(int i = 0; i < pShots.size(); i++){
					pShot = pShots.get(i);
					for(int j = 0; j < REnemies1.size(); j++){
						en1 = REnemies1.get(j);
						if(pShot.collides(en1) != null){
							Collision collision = pShot.collides(en1);
							Vector collVector = collision.getMinPenetration();
							if(collVector.getX() != 0){
								if(dropPowerUp()){
									int power = selectPowerUp();
									if(power == 2){
										shield = new shieldPowerUp(en1.getX(), en1.getY(), 0f, 0.2f);
										sh1.add(shield);
									}
									else if(power == 1){
										threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
										threeWay.add(threeP);
									}
									else{
										RF = new rapidFire(en1.getX(), en1.getY(), 0f, 0.2f);
										rfs.add(RF);
									}
								}
								explosions.add(new Bang(en1.getX(), en1.getY()));
								REnemies1.remove(j);
								pShots.remove(i);
							}
							if(collVector.getY() != 0){
								if(dropPowerUp()){
									if(dropPowerUp()){
										int power = selectPowerUp();
										if(power == 2){
											shield = new shieldPowerUp(en1.getX(), en1.getY(), 0f, 0.2f);
											sh1.add(shield);
										}
										else if(power == 1){
											threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
											threeWay.add(threeP);
										}
										else{
											RF = new rapidFire(en1.getX(), en1.getY(), 0f, 0.2f);
											rfs.add(RF);
										}
									}
							}
								explosions.add(new Bang(en1.getX(), en1.getY()));
								REnemies1.remove(j);
								pShots.remove(i);
							}
							shipsDestroyed += 1;
							score += 20;
						}
					}
					for(int j = 0; j < LEnemies1.size(); j++){
						en1 = LEnemies1.get(j);
						if(pShot.collides(en1) != null){
							Collision collision = pShot.collides(en1);
							Vector collVector = collision.getMinPenetration();
							if(collVector.getX() != 0){
								if(dropPowerUp()){
									if(dropPowerUp()){
										int power = selectPowerUp();
										if(power == 2){
											shield = new shieldPowerUp(en1.getX(), en1.getY(), 0f, 0.2f);
											sh1.add(shield);
										}
										else if(power == 1){
											threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
											threeWay.add(threeP);
										}
										else{
											RF = new rapidFire(en1.getX(), en1.getY(), 0f, 0.2f);
											rfs.add(RF);
										}
									}
								}
								explosions.add(new Bang(en1.getX(), en1.getY()));
								LEnemies1.remove(j);
								pShots.remove(i);
							}
							if(collVector.getY() != 0){
								if(dropPowerUp()){
									if(dropPowerUp()){
										int power = selectPowerUp();
										if(power == 2){
											shield = new shieldPowerUp(en1.getX(), en1.getY(), 0f, 0.2f);
											sh1.add(shield);
										}
										else if(power == 1){
											threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
											threeWay.add(threeP);
										}
										else{
											RF = new rapidFire(en1.getX(), en1.getY(), 0f, 0.2f);
											rfs.add(RF);
										}
									}
								}
								explosions.add(new Bang(en1.getX(), en1.getY()));
								LEnemies1.remove(j);
								pShots.remove(i);
							}
							shipsDestroyed += 1;
							score += 20;
						}
					}
					for(int j = 0; j < LEnemies2.size(); j++){
						en2 = LEnemies2.get(j);
						if(pShot.collides(en2) != null){
							Collision collision = pShot.collides(en2);
							Vector collVector = collision.getMinPenetration();
							if(collVector.getX() != 0){
								if(dropPowerUp()){
										int power = selectPowerUp();
										if(power == 2){
											shield = new shieldPowerUp(en2.getX(), en2.getY(), 0f, 0.2f);
											sh1.add(shield);
										}
										else if(power == 1){
											threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
											threeWay.add(threeP);
										}
										else{
											RF = new rapidFire(en2.getX(), en2.getY(), 0f, 0.2f);
											rfs.add(RF);
										}
									}
								explosions.add(new Bang(en2.getX(), en2.getY()));
								LEnemies2.remove(j);
								pShots.remove(i);
							}
							if(collVector.getY() != 0){
								if(dropPowerUp()){
									int power = selectPowerUp();
									if(power == 2){
										shield = new shieldPowerUp(en2.getX(), en2.getY(), 0f, 0.2f);
										sh1.add(shield);
									}
									else if(power == 1){
										threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
										threeWay.add(threeP);
									}
									else{
										RF = new rapidFire(en2.getX(), en2.getY(), 0f, 0.2f);
										rfs.add(RF);
									}
								}
								explosions.add(new Bang(en2.getX(), en2.getY()));
								LEnemies2.remove(j);
								pShots.remove(i);
							}
							shipsDestroyed += 1;
							score += 10;
						}
					}for(int j = 0; j < REnemies2.size(); j++){
						en2 = REnemies2.get(j);
						if(pShot.collides(en2) != null){
							Collision collision = pShot.collides(en2);
							Vector collVector = collision.getMinPenetration();
							if(collVector.getX() != 0){
								if(dropPowerUp()){
									int power = selectPowerUp();
									if(power == 2){
										shield = new shieldPowerUp(en2.getX(), en2.getY(), 0f, 0.2f);
										sh1.add(shield);
									}
									else if(power == 1){
										threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
										threeWay.add(threeP);
									}
									else{
										RF = new rapidFire(en2.getX(), en2.getY(), 0f, 0.2f);
										rfs.add(RF);
									}
								}
								explosions.add(new Bang(en2.getX(), en2.getY()));
								REnemies2.remove(j);
								pShots.remove(i);
							}
							if(collVector.getY() != 0){
								if(dropPowerUp()){
									int power = selectPowerUp();
									if(power == 2){
										shield = new shieldPowerUp(en2.getX(), en2.getY(), 0f, 0.2f);
										sh1.add(shield);
									}
									else if(power == 1){
										threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
										threeWay.add(threeP);
									}
									else{
										RF = new rapidFire(en2.getX(), en2.getY(), 0f, 0.2f);
										rfs.add(RF);
									}
								}
								explosions.add(new Bang(en2.getX(), en2.getY()));
								REnemies2.remove(j);
								pShots.remove(i);
							}
							shipsDestroyed += 1;
							score += 10;
						}
					}
					for(int j = 0; j < LEnemies3.size(); j++){
						en3 = LEnemies3.get(j);
						if(pShot.collides(en3) != null){
							Collision collision = pShot.collides(en3);
							Vector collVector = collision.getMinPenetration();
							if(collVector.getX() != 0){
								if(dropPowerUp()){
									int power = selectPowerUp();
									if(power == 2){
										shield = new shieldPowerUp(en3.getX(), en3.getY(), 0f, 0.2f);
										sh1.add(shield);
									}
									else if(power == 1){
										threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
										threeWay.add(threeP);
									}
									else{
										RF = new rapidFire(en3.getX(), en3.getY(), 0f, 0.2f);
										rfs.add(RF);
									}
								}
								explosions.add(new Bang(en3.getX(), en3.getY()));
								LEnemies3.remove(j);
								pShots.remove(i);
							}
							if(collVector.getY() != 0){
								if(dropPowerUp()){
									int power = selectPowerUp();
									if(power == 2){
										shield = new shieldPowerUp(en3.getX(), en3.getY(), 0f, 0.2f);
										sh1.add(shield);
									}
									else if(power == 1){
										threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
										threeWay.add(threeP);
									}
									else{
										RF = new rapidFire(en3.getX(), en3.getY(), 0f, 0.2f);
										rfs.add(RF);
									}
								}
								explosions.add(new Bang(en3.getX(), en3.getY()));
								LEnemies3.remove(j);
								pShots.remove(i);
							}
							shipsDestroyed += 1;
							score += 50;
						}
					}for(int j = 0; j < REnemies3.size(); j++){
						en3 = REnemies3.get(j);
						if(pShot.collides(en3) != null){
							Collision collision = pShot.collides(en3);
							Vector collVector = collision.getMinPenetration();
							if(collVector.getX() != 0){
								if(dropPowerUp()){
									int power = selectPowerUp();
									if(power == 2){
										shield = new shieldPowerUp(en3.getX(), en3.getY(), 0f, 0.2f);
										sh1.add(shield);
									}
									else if(power == 1){
										threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
										threeWay.add(threeP);
									}
									else{
										RF = new rapidFire(en3.getX(), en3.getY(), 0f, 0.2f);
										rfs.add(RF);
									}
								}
								explosions.add(new Bang(en3.getX(), en3.getY()));
								REnemies3.remove(j);
								pShots.remove(i);
							}
							if(collVector.getY() != 0){
								if(dropPowerUp()){
									int power = selectPowerUp();
									if(power == 2){
										shield = new shieldPowerUp(en3.getX(), en3.getY(), 0f, 0.2f);
										sh1.add(shield);
									}
									else if(power == 1){
										threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
										threeWay.add(threeP);
									}
									else{
										RF = new rapidFire(en3.getX(), en3.getY(), 0f, 0.2f);
										rfs.add(RF);
									}
								}
								explosions.add(new Bang(en3.getX(), en3.getY()));
								REnemies3.remove(j);
								pShots.remove(i);
							}
							shipsDestroyed += 1;
							score += 50;
						}
					}
					for(int j = 0; j < kamikaze.size(); j++){
						en4 = kamikaze.get(j);
						if(pShot.collides(en4) != null){
							Collision collision = pShot.collides(en4);
							Vector collVector = collision.getMinPenetration();
							if(collVector.getX() != 0){
								if(dropPowerUp()){
									int power = selectPowerUp();
									if(power == 2){
										shield = new shieldPowerUp(en4.getX(), en4.getY(), 0f, 0.2f);
										sh1.add(shield);
									}
									else if(power == 1){
										threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
										threeWay.add(threeP);
									}
									else{
										RF = new rapidFire(en4.getX(), en4.getY(), 0f, 0.2f);
										rfs.add(RF);
									}
								}
								explosions.add(new Bang(en4.getX(), en4.getY()));
								kamikaze.remove(j);
								pShots.remove(i);
							}
							if(collVector.getY() != 0){
								if(dropPowerUp()){
									int power = selectPowerUp();
									if(power == 2){
										shield = new shieldPowerUp(en4.getX(), en4.getY(), 0f, 0.2f);
										sh1.add(shield);
									}
									else if(power == 1){
										threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
										threeWay.add(threeP);
									}
									else{
										RF = new rapidFire(en4.getX(), en4.getY(), 0f, 0.2f);
										rfs.add(RF);
									}
								}
								explosions.add(new Bang(en4.getX(), en4.getY()));
								kamikaze.remove(j);
								pShots.remove(i);
							}
							shipsDestroyed += 1;
							score += 100;
						}
					}
					for(int j = 0; j < bossQ.size(); j++){
						boss = bossQ.get(j);
						if(pShot.collides(boss) != null){
							Collision collision = pShot.collides(boss);
							Vector collVector = collision.getMinPenetration();
							if(collVector.getX() != 0){
								pShots.remove(i);
								boss.hits += 1;
								if(boss.hits >= 15)
									killBoss();
							}
							if(collVector.getY() != 0){
								pShots.remove(i);
								boss.hits += 1;
								if(boss.hits >= 15)
									killBoss();
							}
						}
					}
				}
				for(int i = 0; i < pShotsR.size(); i++){
					pShotR = pShotsR.get(i);
					for(int j = 0; j < REnemies1.size(); j++){
						en1 = REnemies1.get(j);
						if(pShotR.collides(en1) != null){
							Collision collision = pShotR.collides(en1);
							Vector collVector = collision.getMinPenetration();
							if(collVector.getX() != 0){
								if(dropPowerUp()){
									int power = selectPowerUp();
									if(power == 2){
										shield = new shieldPowerUp(en1.getX(), en1.getY(), 0f, 0.2f);
										sh1.add(shield);
									}
									else if(power == 1){
										threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
										threeWay.add(threeP);
									}
									else{
										RF = new rapidFire(en1.getX(), en1.getY(), 0f, 0.2f);
										rfs.add(RF);
									}
								}
								explosions.add(new Bang(en1.getX(), en1.getY()));
								REnemies1.remove(j);
								pShotsR.remove(i);
							}
							if(collVector.getY() != 0){
								if(dropPowerUp()){
									if(dropPowerUp()){
										int power = selectPowerUp();
										if(power == 2){
											shield = new shieldPowerUp(en1.getX(), en1.getY(), 0f, 0.2f);
											sh1.add(shield);
										}
										else if(power == 1){
											threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
											threeWay.add(threeP);
										}
										else{
											RF = new rapidFire(en1.getX(), en1.getY(), 0f, 0.2f);
											rfs.add(RF);
										}
									}
							}
								explosions.add(new Bang(en1.getX(), en1.getY()));
								REnemies1.remove(j);
								pShotsR.remove(i);
							}
							shipsDestroyed += 1;
							score += 20;
						}
					}
					for(int j = 0; j < LEnemies1.size(); j++){
						en1 = LEnemies1.get(j);
						if(pShotR.collides(en1) != null){
							Collision collision = pShotR.collides(en1);
							Vector collVector = collision.getMinPenetration();
							if(collVector.getX() != 0){
								if(dropPowerUp()){
									if(dropPowerUp()){
										int power = selectPowerUp();
										if(power == 2){
											shield = new shieldPowerUp(en1.getX(), en1.getY(), 0f, 0.2f);
											sh1.add(shield);
										}
										else if(power == 1){
											threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
											threeWay.add(threeP);
										}
										else{
											RF = new rapidFire(en1.getX(), en1.getY(), 0f, 0.2f);
											rfs.add(RF);
										}
									}
								}
								explosions.add(new Bang(en1.getX(), en1.getY()));
								LEnemies1.remove(j);
								pShotsR.remove(i);
							}
							if(collVector.getY() != 0){
								if(dropPowerUp()){
									if(dropPowerUp()){
										int power = selectPowerUp();
										if(power == 2){
											shield = new shieldPowerUp(en1.getX(), en1.getY(), 0f, 0.2f);
											sh1.add(shield);
										}
										else if(power == 1){
											threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
											threeWay.add(threeP);
										}
										else{
											RF = new rapidFire(en1.getX(), en1.getY(), 0f, 0.2f);
											rfs.add(RF);
										}
									}
								}
								explosions.add(new Bang(en1.getX(), en1.getY()));
								LEnemies1.remove(j);
								pShotsR.remove(i);
							}
							shipsDestroyed += 1;
							score += 20;
						}
					}
					for(int j = 0; j < LEnemies2.size(); j++){
						en2 = LEnemies2.get(j);
						if(pShotR.collides(en2) != null){
							Collision collision = pShotR.collides(en2);
							Vector collVector = collision.getMinPenetration();
							if(collVector.getX() != 0){
								if(dropPowerUp()){
										int power = selectPowerUp();
										if(power == 2){
											shield = new shieldPowerUp(en2.getX(), en2.getY(), 0f, 0.2f);
											sh1.add(shield);
										}
										else if(power == 1){
											threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
											threeWay.add(threeP);
										}
										else{
											RF = new rapidFire(en2.getX(), en2.getY(), 0f, 0.2f);
											rfs.add(RF);
										}
									}
								explosions.add(new Bang(en2.getX(), en2.getY()));
								LEnemies2.remove(j);
								pShotsR.remove(i);
							}
							if(collVector.getY() != 0){
								if(dropPowerUp()){
									int power = selectPowerUp();
									if(power == 2){
										shield = new shieldPowerUp(en2.getX(), en2.getY(), 0f, 0.2f);
										sh1.add(shield);
									}
									else if(power == 1){
										threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
										threeWay.add(threeP);
									}
									else{
										RF = new rapidFire(en2.getX(), en2.getY(), 0f, 0.2f);
										rfs.add(RF);
									}
								}
								explosions.add(new Bang(en2.getX(), en2.getY()));
								LEnemies2.remove(j);
								pShotsR.remove(i);
							}
							shipsDestroyed += 1;
							score += 10;
						}
					}for(int j = 0; j < REnemies2.size(); j++){
						en2 = REnemies2.get(j);
						if(pShotR.collides(en2) != null){
							Collision collision = pShotR.collides(en2);
							Vector collVector = collision.getMinPenetration();
							if(collVector.getX() != 0){
								if(dropPowerUp()){
									int power = selectPowerUp();
									if(power == 2){
										shield = new shieldPowerUp(en2.getX(), en2.getY(), 0f, 0.2f);
										sh1.add(shield);
									}
									else if(power == 1){
										threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
										threeWay.add(threeP);
									}
									else{
										RF = new rapidFire(en2.getX(), en2.getY(), 0f, 0.2f);
										rfs.add(RF);
									}
								}
								explosions.add(new Bang(en2.getX(), en2.getY()));
								REnemies2.remove(j);
								pShotsR.remove(i);
							}
							if(collVector.getY() != 0){
								if(dropPowerUp()){
									int power = selectPowerUp();
									if(power == 2){
										shield = new shieldPowerUp(en2.getX(), en2.getY(), 0f, 0.2f);
										sh1.add(shield);
									}
									else if(power == 1){
										threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
										threeWay.add(threeP);
									}
									else{
										RF = new rapidFire(en2.getX(), en2.getY(), 0f, 0.2f);
										rfs.add(RF);
									}
								}
								explosions.add(new Bang(en2.getX(), en2.getY()));
								REnemies2.remove(j);
								pShotsR.remove(i);
							}
							shipsDestroyed += 1;
							score += 10;
						}
					}
					for(int j = 0; j < LEnemies3.size(); j++){
						en3 = LEnemies3.get(j);
						if(pShotR.collides(en3) != null){
							Collision collision = pShotR.collides(en3);
							Vector collVector = collision.getMinPenetration();
							if(collVector.getX() != 0){
								if(dropPowerUp()){
									int power = selectPowerUp();
									if(power == 2){
										shield = new shieldPowerUp(en3.getX(), en3.getY(), 0f, 0.2f);
										sh1.add(shield);
									}
									else if(power == 1){
										threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
										threeWay.add(threeP);
									}
									else{
										RF = new rapidFire(en3.getX(), en3.getY(), 0f, 0.2f);
										rfs.add(RF);
									}
								}
								explosions.add(new Bang(en3.getX(), en3.getY()));
								LEnemies3.remove(j);
								pShotsR.remove(i);
							}
							if(collVector.getY() != 0){
								if(dropPowerUp()){
									int power = selectPowerUp();
									if(power == 2){
										shield = new shieldPowerUp(en3.getX(), en3.getY(), 0f, 0.2f);
										sh1.add(shield);
									}
									else if(power == 1){
										threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
										threeWay.add(threeP);
									}
									else{
										RF = new rapidFire(en3.getX(), en3.getY(), 0f, 0.2f);
										rfs.add(RF);
									}
								}
								explosions.add(new Bang(en3.getX(), en3.getY()));
								LEnemies3.remove(j);
								pShotsR.remove(i);
							}
							shipsDestroyed += 1;
							score += 50;
						}
					}for(int j = 0; j < REnemies3.size(); j++){
						en3 = REnemies3.get(j);
						if(pShotR.collides(en3) != null){
							Collision collision = pShotR.collides(en3);
							Vector collVector = collision.getMinPenetration();
							if(collVector.getX() != 0){
								if(dropPowerUp()){
									int power = selectPowerUp();
									if(power == 2){
										shield = new shieldPowerUp(en3.getX(), en3.getY(), 0f, 0.2f);
										sh1.add(shield);
									}
									else if(power == 1){
										threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
										threeWay.add(threeP);
									}
									else{
										RF = new rapidFire(en3.getX(), en3.getY(), 0f, 0.2f);
										rfs.add(RF);
									}
								}
								explosions.add(new Bang(en3.getX(), en3.getY()));
								REnemies3.remove(j);
								pShotsR.remove(i);
							}
							if(collVector.getY() != 0){
								if(dropPowerUp()){
									int power = selectPowerUp();
									if(power == 2){
										shield = new shieldPowerUp(en3.getX(), en3.getY(), 0f, 0.2f);
										sh1.add(shield);
									}
									else if(power == 1){
										threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
										threeWay.add(threeP);
									}
									else{
										RF = new rapidFire(en3.getX(), en3.getY(), 0f, 0.2f);
										rfs.add(RF);
									}
								}
								explosions.add(new Bang(en3.getX(), en3.getY()));
								REnemies3.remove(j);
								pShotsR.remove(i);
							}
							shipsDestroyed += 1;
							score += 50;
						}
					}
					for(int j = 0; j < kamikaze.size(); j++){
						en4 = kamikaze.get(j);
						if(pShotR.collides(en4) != null){
							Collision collision = pShotR.collides(en4);
							Vector collVector = collision.getMinPenetration();
							if(collVector.getX() != 0){
								if(dropPowerUp()){
									int power = selectPowerUp();
									if(power == 2){
										shield = new shieldPowerUp(en4.getX(), en4.getY(), 0f, 0.2f);
										sh1.add(shield);
									}
									else if(power == 1){
										threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
										threeWay.add(threeP);
									}
									else{
										RF = new rapidFire(en4.getX(), en4.getY(), 0f, 0.2f);
										rfs.add(RF);
									}
								}
								explosions.add(new Bang(en4.getX(), en4.getY()));
								kamikaze.remove(j);
								pShotsR.remove(i);
							}
							if(collVector.getY() != 0){
								if(dropPowerUp()){
									int power = selectPowerUp();
									if(power == 2){
										shield = new shieldPowerUp(en4.getX(), en4.getY(), 0f, 0.2f);
										sh1.add(shield);
									}
									else if(power == 1){
										threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
										threeWay.add(threeP);
									}
									else{
										RF = new rapidFire(en4.getX(), en4.getY(), 0f, 0.2f);
										rfs.add(RF);
									}
								}
								explosions.add(new Bang(en4.getX(), en4.getY()));
								kamikaze.remove(j);
								pShotsR.remove(i);
							}
							shipsDestroyed += 1;
							score += 100;
						}
					}
					for(int j = 0; j < bossQ.size(); j++){
						boss = bossQ.get(j);
						if(pShotR.collides(boss) != null){
							Collision collision = pShotR.collides(boss);
							Vector collVector = collision.getMinPenetration();
							if(collVector.getX() != 0){
								pShotsR.remove(i);
								boss.hits += 1;
								if(boss.hits >= 15)
									killBoss();
							}
							if(collVector.getY() != 0){
								pShotsR.remove(i);
								boss.hits += 1;
								if(boss.hits >= 15)
									killBoss();
							}
						}
					}
				}
			for(int i = 0; i < pShotsL.size(); i++){
				pShotL = pShotsL.get(i);
				for(int j = 0; j < REnemies1.size(); j++){
					en1 = REnemies1.get(j);
					if(pShotL.collides(en1) != null){
						Collision collision = pShotL.collides(en1);
						Vector collVector = collision.getMinPenetration();
						if(collVector.getX() != 0){
							if(dropPowerUp()){
								int power = selectPowerUp();
								if(power == 2){
									shield = new shieldPowerUp(en1.getX(), en1.getY(), 0f, 0.2f);
									sh1.add(shield);
								}
								else if(power == 1){
									threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
									threeWay.add(threeP);
								}
								else{
									RF = new rapidFire(en1.getX(), en1.getY(), 0f, 0.2f);
									rfs.add(RF);
								}
							}
							explosions.add(new Bang(en1.getX(), en1.getY()));
							REnemies1.remove(j);
							pShotsL.remove(i);
						}
						if(collVector.getY() != 0){
							if(dropPowerUp()){
								if(dropPowerUp()){
									int power = selectPowerUp();
									if(power == 2){
										shield = new shieldPowerUp(en1.getX(), en1.getY(), 0f, 0.2f);
										sh1.add(shield);
									}
									else if(power == 1){
										threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
										threeWay.add(threeP);
									}
									else{
										RF = new rapidFire(en1.getX(), en1.getY(), 0f, 0.2f);
										rfs.add(RF);
									}
								}
						}
							explosions.add(new Bang(en1.getX(), en1.getY()));
							REnemies1.remove(j);
							pShotsL.remove(i);
						}
						shipsDestroyed += 1;
						score += 20;
					}
				}
				for(int j = 0; j < LEnemies1.size(); j++){
					en1 = LEnemies1.get(j);
					if(pShotL.collides(en1) != null){
						Collision collision = pShotL.collides(en1);
						Vector collVector = collision.getMinPenetration();
						if(collVector.getX() != 0){
							if(dropPowerUp()){
								if(dropPowerUp()){
									int power = selectPowerUp();
									if(power == 2){
										shield = new shieldPowerUp(en1.getX(), en1.getY(), 0f, 0.2f);
										sh1.add(shield);
									}
									else if(power == 1){
										threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
										threeWay.add(threeP);
									}
									else{
										RF = new rapidFire(en1.getX(), en1.getY(), 0f, 0.2f);
										rfs.add(RF);
									}
								}
							}
							explosions.add(new Bang(en1.getX(), en1.getY()));
							LEnemies1.remove(j);
							pShotsL.remove(i);
						}
						if(collVector.getY() != 0){
							if(dropPowerUp()){
								if(dropPowerUp()){
									int power = selectPowerUp();
									if(power == 2){
										shield = new shieldPowerUp(en1.getX(), en1.getY(), 0f, 0.2f);
										sh1.add(shield);
									}
									else if(power == 1){
										threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
										threeWay.add(threeP);
									}
									else{
										RF = new rapidFire(en1.getX(), en1.getY(), 0f, 0.2f);
										rfs.add(RF);
									}
								}
							}
							explosions.add(new Bang(en1.getX(), en1.getY()));
							LEnemies1.remove(j);
							pShotsL.remove(i);
						}
						shipsDestroyed += 1;
						score += 20;
					}
				}
				for(int j = 0; j < LEnemies2.size(); j++){
					en2 = LEnemies2.get(j);
					if(pShotL.collides(en2) != null){
						Collision collision = pShotL.collides(en2);
						Vector collVector = collision.getMinPenetration();
						if(collVector.getX() != 0){
							if(dropPowerUp()){
									int power = selectPowerUp();
									if(power == 2){
										shield = new shieldPowerUp(en2.getX(), en2.getY(), 0f, 0.2f);
										sh1.add(shield);
									}
									else if(power == 1){
										threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
										threeWay.add(threeP);
									}
									else{
										RF = new rapidFire(en2.getX(), en2.getY(), 0f, 0.2f);
										rfs.add(RF);
									}
								}
							explosions.add(new Bang(en2.getX(), en2.getY()));
							LEnemies2.remove(j);
							pShotsL.remove(i);
						}
						if(collVector.getY() != 0){
							if(dropPowerUp()){
								int power = selectPowerUp();
								if(power == 2){
									shield = new shieldPowerUp(en2.getX(), en2.getY(), 0f, 0.2f);
									sh1.add(shield);
								}
								else if(power == 1){
									threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
									threeWay.add(threeP);
								}
								else{
									RF = new rapidFire(en2.getX(), en2.getY(), 0f, 0.2f);
									rfs.add(RF);
								}
							}
							explosions.add(new Bang(en2.getX(), en2.getY()));
							LEnemies2.remove(j);
							pShotsL.remove(i);
						}
						shipsDestroyed += 1;
						score += 10;
					}
				}for(int j = 0; j < REnemies2.size(); j++){
					en2 = REnemies2.get(j);
					if(pShotL.collides(en2) != null){
						Collision collision = pShotL.collides(en2);
						Vector collVector = collision.getMinPenetration();
						if(collVector.getX() != 0){
							if(dropPowerUp()){
								int power = selectPowerUp();
								if(power == 2){
									shield = new shieldPowerUp(en2.getX(), en2.getY(), 0f, 0.2f);
									sh1.add(shield);
								}
								else if(power == 1){
									threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
									threeWay.add(threeP);
								}
								else{
									RF = new rapidFire(en2.getX(), en2.getY(), 0f, 0.2f);
									rfs.add(RF);
								}
							}
							explosions.add(new Bang(en2.getX(), en2.getY()));
							REnemies2.remove(j);
							pShotsL.remove(i);
						}
						if(collVector.getY() != 0){
							if(dropPowerUp()){
								int power = selectPowerUp();
								if(power == 2){
									shield = new shieldPowerUp(en2.getX(), en2.getY(), 0f, 0.2f);
									sh1.add(shield);
								}
								else if(power == 1){
									threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
									threeWay.add(threeP);
								}
								else{
									RF = new rapidFire(en2.getX(), en2.getY(), 0f, 0.2f);
									rfs.add(RF);
								}
							}
							explosions.add(new Bang(en2.getX(), en2.getY()));
							REnemies2.remove(j);
							pShotsL.remove(i);
						}
						shipsDestroyed += 1;
						score += 10;
					}
				}
				for(int j = 0; j < LEnemies3.size(); j++){
					en3 = LEnemies3.get(j);
					if(pShotL.collides(en3) != null){
						Collision collision = pShotL.collides(en3);
						Vector collVector = collision.getMinPenetration();
						if(collVector.getX() != 0){
							if(dropPowerUp()){
								int power = selectPowerUp();
								if(power == 2){
									shield = new shieldPowerUp(en3.getX(), en3.getY(), 0f, 0.2f);
									sh1.add(shield);
								}
								else if(power == 1){
									threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
									threeWay.add(threeP);
								}
								else{
									RF = new rapidFire(en3.getX(), en3.getY(), 0f, 0.2f);
									rfs.add(RF);
								}
							}
							explosions.add(new Bang(en3.getX(), en3.getY()));
							LEnemies3.remove(j);
							pShotsL.remove(i);
						}
						if(collVector.getY() != 0){
							if(dropPowerUp()){
								int power = selectPowerUp();
								if(power == 2){
									shield = new shieldPowerUp(en3.getX(), en3.getY(), 0f, 0.2f);
									sh1.add(shield);
								}
								else if(power == 1){
									threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
									threeWay.add(threeP);
								}
								else{
									RF = new rapidFire(en3.getX(), en3.getY(), 0f, 0.2f);
									rfs.add(RF);
								}
							}
							explosions.add(new Bang(en3.getX(), en3.getY()));
							LEnemies3.remove(j);
							pShotsL.remove(i);
						}
						shipsDestroyed += 1;
						score += 50;
					}
				}for(int j = 0; j < REnemies3.size(); j++){
					en3 = REnemies3.get(j);
					if(pShotL.collides(en3) != null){
						Collision collision = pShotL.collides(en3);
						Vector collVector = collision.getMinPenetration();
						if(collVector.getX() != 0){
							if(dropPowerUp()){
								int power = selectPowerUp();
								if(power == 2){
									shield = new shieldPowerUp(en3.getX(), en3.getY(), 0f, 0.2f);
									sh1.add(shield);
								}
								else if(power == 1){
									threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
									threeWay.add(threeP);
								}
								else{
									RF = new rapidFire(en3.getX(), en3.getY(), 0f, 0.2f);
									rfs.add(RF);
								}
							}
							explosions.add(new Bang(en3.getX(), en3.getY()));
							REnemies3.remove(j);
							pShotsL.remove(i);
						}
						if(collVector.getY() != 0){
							if(dropPowerUp()){
								int power = selectPowerUp();
								if(power == 2){
									shield = new shieldPowerUp(en3.getX(), en3.getY(), 0f, 0.2f);
									sh1.add(shield);
								}
								else if(power == 1){
									threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
									threeWay.add(threeP);
								}
								else{
									RF = new rapidFire(en3.getX(), en3.getY(), 0f, 0.2f);
									rfs.add(RF);
								}
							}
							explosions.add(new Bang(en3.getX(), en3.getY()));
							REnemies3.remove(j);
							pShotsL.remove(i);
						}
						shipsDestroyed += 1;
						score += 50;
					}
				}
				for(int j = 0; j < kamikaze.size(); j++){
					en4 = kamikaze.get(j);
					if(pShotL.collides(en4) != null){
						Collision collision = pShotL.collides(en4);
						Vector collVector = collision.getMinPenetration();
						if(collVector.getX() != 0){
							if(dropPowerUp()){
								int power = selectPowerUp();
								if(power == 2){
									shield = new shieldPowerUp(en4.getX(), en4.getY(), 0f, 0.2f);
									sh1.add(shield);
								}
								else if(power == 1){
									threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
									threeWay.add(threeP);
								}
								else{
									RF = new rapidFire(en4.getX(), en4.getY(), 0f, 0.2f);
									rfs.add(RF);
								}
							}
							explosions.add(new Bang(en4.getX(), en4.getY()));
							kamikaze.remove(j);
							pShotsL.remove(i);
						}
						if(collVector.getY() != 0){
							if(dropPowerUp()){
								int power = selectPowerUp();
								if(power == 2){
									shield = new shieldPowerUp(en4.getX(), en4.getY(), 0f, 0.2f);
									sh1.add(shield);
								}
								else if(power == 1){
									threeP = new threeWayPower(en1.getX(), en1.getY(), 0f, 0.2f);
									threeWay.add(threeP);
								}
								else{
									RF = new rapidFire(en4.getX(), en4.getY(), 0f, 0.2f);
									rfs.add(RF);
								}
							}
							explosions.add(new Bang(en4.getX(), en4.getY()));
							kamikaze.remove(j);
							pShotsL.remove(i);
						}
						shipsDestroyed += 1;
						score += 100;
					}
				}
				for(int j = 0; j < bossQ.size(); j++){
					boss = bossQ.get(j);
					if(pShotL.collides(boss) != null){
						Collision collision = pShotL.collides(boss);
						Vector collVector = collision.getMinPenetration();
						if(collVector.getX() != 0){
							pShotsL.remove(i);
							boss.hits += 1;
							if(boss.hits >= 15)
								killBoss();
						}
						if(collVector.getY() != 0){
							pShotsL.remove(i);
							boss.hits += 1;
							if(boss.hits >= 15)
								killBoss();
						}
					}
				}
			}
			
			//Remove shots off screen
			for(int i = 0; i < pShots.size(); i++){
				pShot = pShots.get(i);
				if(pShot.getCoarseGrainedMaxY() <= 0)
					pShots.remove(i);
			}
			for(int i = 0; i < eShots.size(); i++){
				eShot = eShots.get(i);
				if(eShot.getCoarseGrainedMinY() >= ScreenHeight)
					eShots.remove(i);
			}
			
			//Level setups
			if(level == 1){
				if(shipsCreated < 1)
					System.out.println("Level 1 Start");
				//Add enemies
				if((gameTimer % 75 == 0 && gameTimer <= 225) || (gameTimer % 75 == 0 && (gameTimer >= 525 && gameTimer <= 750))){
					en1 = new simpleEnemy(4 * ScreenWidth / 5, 0, -0.05f, 0.1f);
					REnemies1.add(en1);
					shipsCreated += 1;
				}
			
				//Add enemies
				if((gameTimer % 75 == 0 && (gameTimer >= 225 && gameTimer <= 450)) || (gameTimer % 75 == 0 && (gameTimer >= 825 && gameTimer <= 1050))){
					en1 = new simpleEnemy(ScreenWidth / 5, 0, 0.05f, 0.1f);
					LEnemies1.add(en1);
					shipsCreated += 1;
				}
			
				//Add more enemies
				if((gameTimer % 50 == 0 && (gameTimer >= 950 && gameTimer <= 1100))){
					en2 = new bigEnemy(2 * ScreenWidth / 3, 0, -0.05f, 0.2f);
					REnemies2.add(en2);
					shipsCreated += 1;
				}
			
				//Add more enemies
				if((gameTimer % 50 == 0 && (gameTimer >= 1200 && gameTimer <= 1350))){
					en2 = new bigEnemy(ScreenWidth / 3, 0, 0.05f, 0.2f);
					LEnemies2.add(en2);
					shipsCreated += 1;
				}
				if(gameTimer >= 1600 && LEnemies2.size() == 0 && REnemies2.size() == 0
						&& LEnemies1.size() == 0 && REnemies1.size() == 0){
					level = 2;
					for(int i = 0; i < pShots.size(); i++){
						pShots.remove(i);
					}
					gameTimer = -50;
					gameState = TRANSITION;
				}
			}
			if(level == 2){
				if(gameTimer < 0){
					shipsDestroyed = 0;
					shipsCreated = 0;
					System.out.println("Level 2 Start");
				}
				//Add shooting enemies
				if((gameTimer % 75 == 0 && (gameTimer >= 225 && gameTimer <= 450))){
					en3 = new shootingEnemy(ScreenWidth / 5, 0, 0.1f, 0.15f);
					LEnemies3.add(en3);
					shipsCreated += 1;
				}
				//Add shooting enemies
				if(gameTimer % 75 == 0 && gameTimer < 225){
					en3 = new shootingEnemy(4 * ScreenWidth / 5, 0, -0.1f, 0.15f);
					REnemies3.add(en3);
					shipsCreated += 1;
				}
			
				//Add kamikaze enemies
				if(gameTimer % 100 == 0 && gameTimer < 700 && gameTimer >= 0){
					if(gameTimer % 200 == 0)
						en4 = new kEnemy(2 * ScreenWidth / 3, 0, new Vector(0f, 0.01f));
					else
						en4 = new kEnemy(ScreenWidth / 3, 0, new Vector(0f, 0.01f));
					kamikaze.add(en4);
					shipsCreated += 1;
				}
				
				//Add boss to screen
				if(gameTimer == 800){
					boss = new boss1(ScreenWidth / 2, 50, 0.1f, 0);
					bossQ.add(boss);
				}
				if(gameTimer >= 1600 && bossQ.size() == 0){
					level = 3;
					for(int i = 0; i < pShots.size(); i++){
						pShots.remove(i);
					}
					gameState = TRANSITION;
				}
			}
			
			if(level == 3){
				if(gameTimer < 0){
					shipsCreated = 0;
					shipsDestroyed = 0;
				}
				//Add easy enemies
				if(gameTimer % 50 == 0 && gameTimer < 200){
					en1 = new simpleEnemy(ScreenWidth / 4, 0, 0.1f, 0.15f);
					LEnemies1.add(en1);
				}
				if(gameTimer % 75 == 0 && gameTimer < 225){
					en1 = new simpleEnemy(3 * ScreenWidth / 4, 0, -0.1f, 0.15f);
					REnemies1.add(en1);
				}
				
				//Add shooting enemies
				if(gameTimer % 50 == 0 && gameTimer >=100 && gameTimer <= 200){
					en3 = new shootingEnemy(2 * ScreenWidth / 3, 0, -0.15f, 0.2f);
					REnemies3.add(en3);
				}
				if(gameTimer % 50 == 0 && gameTimer >= 200 && gameTimer <= 300){
					en3 = new shootingEnemy(ScreenWidth / 3, 0, 0.15f, 0.2f);
					LEnemies3.add(en3);
				}
				
				//Add big slow enemies
				if(gameTimer % 50 == 0 && gameTimer >= 200 && gameTimer <= 400){
					en2 = new bigEnemy(ScreenWidth / 4, 0, 0.05f, 0.1f);
					LEnemies2.add(en2);
				}
				if(gameTimer % 75 == 0 && gameTimer >= 225 && gameTimer <= 300){
					en2 = new bigEnemy(3 * ScreenWidth / 4, 0, -0.05f, 0.1f);
					REnemies2.add(en2);
				}
			}
			
			//Add shots from boss
			for(int i = 0; i < bossQ.size(); i++){
				boss = bossQ.get(i);
				if(boss.canShoot){
					boss.shotDelay = 0;
					boss.canShoot = false;
					eShot = new enemyLaser(boss.getX() - 53, boss.getY() + 85, new Vector(0f, 0.3f));
					eShots.add(eShot);
					eShot = new enemyLaser(boss.getX() + 53, boss.getY() + 85, new Vector(0f, 0.3f));
					eShots.add(eShot);
				}
			}
			
			//Update boss
			for(int i = 0; i < bossQ.size(); i++){
				boss = bossQ.get(i);
				if(boss.getCoarseGrainedMaxX() > ScreenWidth){
					boss.setVelocity(new Vector(-0.1f, 0f));
				}
				if(boss.getCoarseGrainedMinX() < 0){
					boss.setVelocity(new Vector(0.1f, 0f));
				}
				boss.update(delta);
			}
			//Update where enemy lasers are
			for(int i = 0; i < eShots.size(); i++){
				eShot = eShots.get(i);
				eShot.update(delta);
			}
			//Update the player movement
			playerShip.update(delta);
			
			//Update where player lasers are
			for(int i = 0; i < pShots.size(); i++){
				pShot = pShots.get(i);
				pShot.update(delta);
			}
			for(int i = 0; i < pShotsL.size(); i++){
				pShotL = pShotsL.get(i);
				pShotL.update(delta);
			}
			for(int i = 0; i < pShotsR.size(); i++){
				pShotR = pShotsR.get(i);
				pShotR.update(delta);
			}
			
			//Update where the shield powerup is on the screen
			for(int i = 0; i < sh1.size(); i++){
				shield = sh1.get(i);
				shield.update(delta);
			}
			
			//Update rapid fire powerup
			for(int i = 0; i < rfs.size(); i++){
				RF = rfs.get(i);
				RF.update(delta);
			}
			
			//Update three way powerup
			for(int i = 0; i < threeWay.size(); i++){
				threeP = threeWay.get(i);
				threeP.update(delta);
			}
			
			//Ship shield - make it move with the ship and turn it off
			for(int i = 0; i < pShields.size(); i++){
				pShield = pShields.get(i);
				pShield.setVelocity(playerShip.getVelocity());
				pShield.update(delta);
				if(pShield.timeOn >= 2000){
					pShields.remove(i);
					playerShip.shieldOn = false;
				}
			}
			
			//Update enemy ship movements
			for(int i = 0; i < REnemies1.size(); i++){
				en1 = REnemies1.get(i);
				if(en1.moveTimer >= 2500 && en1.canChangeV == true){
					en1.canChangeV = false;
					en1.setVelocity(en1.velocity.add(new Vector(-0.15f, 0.1f)));
				}
				en1.update(delta);
			}
			for(int i = 0; i < LEnemies1.size(); i++){
				en1 = LEnemies1.get(i);
				if(en1.moveTimer >= 2500 && en1.canChangeV == true){
					en1.canChangeV = false;
					en1.setVelocity(en1.velocity.add(new Vector(0.15f, 0.1f)));
				}
				en1.update(delta);
			}
			for(int i = 0; i < REnemies2.size(); i++){
				en2 = REnemies2.get(i);
				en2.update(delta);
			}
			for(int i = 0; i < LEnemies2.size(); i++){
				en2 = LEnemies2.get(i);
				en2.update(delta);
			}
			for(int i = 0; i < REnemies3.size(); i++){
				en3 = REnemies3.get(i);
				if(en3.canShoot){
					en3.shotDelay = 0;
					en3.canShoot = false;
					eShot = new enemyLaser(en3.getX(), en3.getY(), en3.shotVector(en3.getX(),
							en3.getY(), playerShip.getX(), playerShip.getY()));
					eShots.add(eShot);
				}
				en3.update(delta);
			}
			for(int i = 0; i < LEnemies3.size(); i++){
				en3 = LEnemies3.get(i);
				if(en3.canShoot){
					en3.shotDelay = 0;
					en3.canShoot = false;
					eShot = new enemyLaser(en3.getX(), en3.getY(), en3.shotVector(en3.getX(),
							en3.getY(), playerShip.getX(), playerShip.getY()));
					eShots.add(eShot);
				}
				en3.update(delta);
			}
			for(int i = 0; i < kamikaze.size(); i++){
				en4 = kamikaze.get(i);
				if(en4.chaseTimer > 150 && playerShip.getY() - en4.getY() > -10){
					en4.chaseTimer = 0;
					en4.setVelocity(en4.chase(en4.getX(), en4.getY(), playerShip.getX(),
							playerShip.getY(), en4.flag));
				}
				en4.update(delta);
			}
			//Increment game timer 
			gameTimer += 1;
			System.out.println(gameTimer);
			if(playerShip.powerUp)
				playerShip.powerUpTimer += 1;
			if(playerShip.threeWay)
				playerShip.threeWayTimer += 1;
		}

		// check if there are any finished explosions, if so remove them
		for (Iterator<Bang> i = explosions.iterator(); i.hasNext();) {
			if (!i.next().isActive()) {
				i.remove();
			}
		}

		if (gameState == PLAYING && lives == 0) {
			gameOver();
		}
	}
}

	public static void main(String[] args) throws Exception{
		AppGameContainer app;
		//Class.forName("org.sqlite.JDBC");
		//db_conn = DriverManager.getConnection("jdbc:sqlite:Scores.db");
		//stmt = db_conn.createStatement();
		try {
			app = new AppGameContainer(new spaceGame("2942", 800, 700));
			app.setDisplayMode(800, 700, false);
			app.setVSync(true);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}

	}


	/**
	 * The Ball class is an Entity that has a velocity (since it's moving). When
	 * the Ball bounces off a surface, it temporarily displays a image with
	 * cracks for a nice visual effect.
	 * 
	 */
	
	class spaceShip extends Entity {

		private Vector velocity;
		public int shotDelay;
		public boolean canShoot;
		public int canShield;
		public boolean shieldOn;
		public boolean isAlive;
		public int timeDead;
		public int powerUpTimer;
		public boolean powerUp;
		public int threeWayTimer;
		public boolean threeWay;

		public spaceShip(final float x, final float y, final float vx, final float vy) {
			super(x, y);
			addImageWithBoundingBox(ResourceManager
					.getImage("resource/playerShip.png"));
			velocity = new Vector(vx, vy);
			shotDelay = 0;
			canShoot = true;
			canShield = 0;
			isAlive = true;
			timeDead = -1;
			shieldOn = false;
			powerUp = false;
			powerUpTimer = 0;
			threeWay = false;
			threeWayTimer = 0;
		}

		public void setVelocity(final Vector v) {
			velocity = v;
		}

		public Vector getVelocity() {
			return velocity;
		}

		public void update(final int delta) {
			translate(velocity.scale(delta));
			shotDelay += delta;
			if(playerShip.threeWay)
				if(playerShip.threeWayTimer > 200)
					playerShip.threeWay = false;
			if(shotDelay >= 500)
				canShoot = true;
			else if(shotDelay >= 200 && playerShip.powerUp)
				canShoot = true;
			if(powerUpTimer >= 100)
				powerUp = false;
			if(timeDead > 0 && !playerShip.isAlive)
				timeDead -= delta;
			else
				playerShip.isAlive = true;
				playerShip.addImageWithBoundingBox(ResourceManager.getImage("resource/playerShip.png"));
		}
	}
	
	class simpleEnemy extends Entity{
		private Vector velocity;
		public int moveTimer;
		private boolean canChangeV;
		public simpleEnemy(final float x, final float y, final float vx, final float vy){
			super(x,y);
			addImageWithBoundingBox(ResourceManager.getImage("resource/enemy1.png"));
			velocity = new Vector(vx, vy);
			moveTimer = 1;
			canChangeV = true;
		}
		
		public Vector getVelocity(){
			return velocity;
		}
		
		public void setVelocity(final Vector v){
			velocity = v;
		}
		public void update(final int delta) {
			translate(velocity.scale(delta));
			moveTimer += delta;
		}
	}

	/**
	 * A class representing a transient explosion. The game should monitor
	 * explosions to determine when they are no longer active and remove/hide
	 * them at that point.
	 */
	class Bang extends Entity {
		private Animation explosion;

		public Bang(final float x, final float y) {
			super(x, y);
			explosion = new Animation(ResourceManager.getSpriteSheet(
					"resource/explosion.png", 64, 64), 0, 0, 22, 0, true, 50,
					true);
			addAnimation(explosion);
			explosion.setLooping(false);
			//ResourceManager.getSound("resource/explosion.wav").play();
		}

		public boolean isActive() {
			return !explosion.isStopped();
		}
	}

	class Background extends Entity{
		public Background(final float x, final float y){
			super(x,y);
			addImage(ResourceManager.getImage("resource/space-1.jpg"));
		}
	}
	
	class lifeShip extends Entity{
		public lifeShip(final float x, final float y){
			super(x,y);
			addImage(ResourceManager.getImage("resource/lifeShip.png"));
		}
	}
	
	class laser extends Entity{
		private Vector speed;
		public laser(final float x, final float y, final float vx, final float vy){
			super(x,y);
			addImageWithBoundingBox(ResourceManager.getImage("resource/laser.png"));
			speed = new Vector(vx, vy);
		}
		
		public void update(final int delta) {
			translate(speed.scale(delta));
		}
	}
	
	class enemyLaser extends Entity{
		private Vector speed;
		public enemyLaser(final float x, final float y, final Vector v){
			super(x,y);
			addImageWithBoundingBox(ResourceManager.getImage("resource/enemyLaser.png"));
			speed = v;
		}
		
		public void update(final int delta){
			translate(speed.scale(delta));
		}
	}
	
	class shieldPowerUp extends Entity{
		private Vector speed;
		public shieldPowerUp(final float x, final float y, final float vx, final float vy){
			super(x,y);
			addImageWithBoundingBox(ResourceManager.getImage("resource/shieldIcon.png"));
			speed = new Vector(vx, vy);
		}
		
		public void update(final int delta){
			translate(speed.scale(delta));
		}
	}
	
	class rapidFire extends Entity{
		private Vector speed;
		public rapidFire(final float x, final float y, final float vx, final float vy){
			super(x,y);
			addImageWithBoundingBox(ResourceManager.getImage("resource/Flameless.png"));
			speed = new Vector(vx, vy);
		}
		
		public void update(final int delta){
			translate(speed.scale(delta));
		}
	}
	
	class threeWayPower extends Entity{
		private Vector speed;
		public threeWayPower(final float x, final float y, final float vx, final float vy){
			super(x,y);
			addImageWithBoundingBox(ResourceManager.getImage("resource/icon_110.png"));
			speed = new Vector(vx, vy);
		}
		
		public void update(final int delta){
			translate(speed.scale(delta));
		}
	}
	class threeWayL extends Entity{
		private Vector speed;
		public threeWayL(final float x, final float y, final float vx, final float vy){
			super(x,y);
			addImageWithBoundingBox(ResourceManager.getImage("resource/laserL.png"));
			speed = new Vector(vx, vy);
		}
		
		public void update(final int delta){
			translate(speed.scale(delta));
		}
	}
	
	class threeWayR extends Entity{
		private Vector speed;
		public threeWayR(final float x, final float y, final float vx, final float vy){
			super(x,y);
			addImageWithBoundingBox(ResourceManager.getImage("resource/laserR.png"));
			speed = new Vector(vx, vy);
		}
		
		public void update(final int delta){
			translate(speed.scale(delta));
		}
	}
	
	class shieldIcon extends Entity{
		public shieldIcon(final float x, final float y){
			super(x,y);
			addImage(ResourceManager.getImage("resource/shieldIcon2.png"));
		}
	}
	
	class shipShield extends Entity{
		private Vector speed;
		public int timeOn;
		public shipShield(final float x, final float y, final float vx, final float vy){
			super(x,y);
			//shipShield.setCoarseGrainedCollisionBoundary(Entity.CIRCLE);
			addImageWithBoundingBox(ResourceManager.getImage("resource/shield1.png"));
			speed = new Vector(vx, vy);
			timeOn = 0;
		}
		
		public void setVelocity(final Vector v) {
			speed = v;
		}
		
		public void update(final int delta){
			translate(speed.scale(delta));
			timeOn += delta;
		}
	}
	
	class boss1 extends Entity{
		private Vector speed;
		public int shotDelay;
		public boolean canShoot;
		public int hits;
		public boss1(final float x, final float y, final float vx, final float vy){
			super(x,y);
			addImageWithBoundingBox(ResourceManager.getImage("resource/boss1.png"));
			speed = new Vector(vx, vy);
			shotDelay = 0;
			canShoot = true;
			hits = 0;
		}
		
		public void setVelocity(final Vector v){
			speed = v;
		}
		
		public void update(final int delta){
			translate(speed.scale(delta));
			shotDelay += delta;
			if(shotDelay >= 500)
				canShoot = true;				
		}
	}

	class bigEnemy extends Entity{
		private Vector speed;
		public bigEnemy(final float x, final float y, final float vx, final float vy){
			super(x,y);
			addImageWithBoundingBox(ResourceManager.getImage("resource/enemy2.png"));
			speed = new Vector(vx, vy);
		}
		public void setVelocity(final Vector v){
			speed = v;
		}
		public Vector getVelocity(){
			return speed;
		}
		public void update(final int delta){
			translate(speed.scale(delta));
		}
	}
	
	class shootingEnemy extends Entity{
		private Vector speed;
		public boolean canShoot;
		public int shotDelay;
		public shootingEnemy(final float x, final float y, final float vx, final float vy){
			super(x,y);
			addImageWithBoundingBox(ResourceManager.getImage("resource/enemy3.png"));
			speed = new Vector(vx, vy);
			canShoot = true;
			shotDelay = 0;
		}
		public void setVelocity(final Vector v){
			speed = v;
		}
		public Vector getVelocity(){
			return speed;
		}
		public Vector shotVector(final float x1, final float y1, final float x2, final float y2){
			Vector shotV;
			double distance;
			float newX, newY;
			distance = Math.sqrt(Math.pow((x2 - x1),2) + Math.pow((y2 - y1),2));
			newX = x2 - x1;
			newY = y2 - y1;
			newX /= (2 * distance);
			newY /= (2 * distance);
			shotV = new Vector(newX, newY);
			return shotV;
		}
		public void update(final int delta){
			translate(speed.scale(delta));
			shotDelay += 1;
			if(shotDelay >= 150)
				canShoot = true;
		}
	}
	
	class kEnemy extends Entity{
		private Vector speed;
		public int flag;
		private String images[] = new String[5];	
		private int chaseTimer;
		
		public kEnemy(final float x, final float y, final Vector v){
			super(x,y);
			addImageWithBoundingBox(ResourceManager.getImage("resource/enemy.png"));
			speed = v;
			flag = 0;
			chaseTimer = 0;
			images[0] = "resource/enemy.png";
			images[1] = "resource/enemy45.png";
			images[2] = "resource/enemy90.png";
			images[3] = "resource/enemy-45.png";
			images[4] = "resource/enemy-90.png";
		}
		public Vector chase(final float x1, final float y1, final float x2, final float y2, int flag){
			Vector chaseVector;
			double distance;
			float newX, newY;
			distance = Math.sqrt(Math.pow((x2 - x1),2) + Math.pow((y2 - y1),2));
			newX = x2 - x1;
			newY = y2 - y1;
			newX /= (4 * distance);
			newY /= (4 * distance);
			//Mostly to the left, little y diff
			if(x2 - x1 >= 150 && Math.abs(y2 - y1) < 50){
				en4.removeImage(ResourceManager.getImage(images[en4.flag]));
				en4.flag = 4;
				en4.addImageWithBoundingBox(ResourceManager.getImage(images[en4.flag]));
			}
			//Mostly to right, little y diff
			else if(x2 - x1 <= -150 && Math.abs(y2 - y1) < 50){
				en4.removeImage(ResourceManager.getImage(images[en4.flag]));
				en4.flag = 2;
				en4.addImageWithBoundingBox(ResourceManager.getImage(images[en4.flag]));
			}
			//Angled to left and below
			else if(x2 - x1 >= 200 && Math.abs(y2 - y1) > 100){
				en4.removeImage(ResourceManager.getImage(images[en4.flag]));
				en4.flag = 3;
				en4.addImageWithBoundingBox(ResourceManager.getImage(images[en4.flag]));
			}
			//Angled to right and below
			else if(x2 - x1 <= -200 && Math.abs(y2 - y1) > 100){
				en4.removeImage(ResourceManager.getImage(images[en4.flag]));
				en4.flag = 1;
				en4.addImageWithBoundingBox(ResourceManager.getImage(images[en4.flag]));
			}
			//Close to straight down
			else{
				en4.removeImage(ResourceManager.getImage(images[en4.flag]));
				en4.flag = 0;
				en4.addImageWithBoundingBox(ResourceManager.getImage(images[en4.flag]));
			}
			chaseVector = new Vector(newX, newY);
			return chaseVector;
		}
		public void setVelocity(Vector v){
			speed = v;
		}
		public void update(final int delta){
			translate(speed.scale(delta));
			chaseTimer += delta;
		}
	}
}