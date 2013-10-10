package spaceGame;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
	private ArrayList<simpleEnemy> Enemies1;
	private simpleEnemy en1;
	private shieldPowerUp shield;
	private ArrayList<shieldPowerUp> sh1;
	private ArrayList<shieldIcon> sIcons;
	private shieldIcon sIcon;
	private ArrayList<shipShield> pShields;
	private shipShield pShield;
	private boss1 boss;
	private ArrayList<enemyLaser> eShots;
	private enemyLaser eShot;
	private ArrayList<rapidFire> rfs;
	private rapidFire RF;
	private ArrayList<threeWayPower> threeWay;
	private threeWayPower threeP;
	private ArrayList<extraLife> extras;
	private extraLife extra;
	private int lives = 3;
	private Random random;
	public int score = 0;
	private int gameTimer = 0;
	private int level = 1;
	private int shipsCreated = 0;
	private int shipsDestroyed = 0;
	//private long timeStart = 0, timeStart1 = 0, timeStart2 = 0;
	//private long timeEnd = 0, timeEnd1 = 0, timeEnd2 = 0;
	public static Connection db_conn;
	public static ResultSet rs;
	public static Statement stmt;
	
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
		Enemies1 = new ArrayList<simpleEnemy>(10);
		explosions = new ArrayList<Bang>(10);
		sh1 = new ArrayList<shieldPowerUp>(5);
		sIcons = new ArrayList<shieldIcon>(3);
		pShields = new ArrayList<shipShield>(2);
		eShots = new ArrayList<enemyLaser>(10);
		rfs = new ArrayList<rapidFire>(5);
		threeWay = new ArrayList<threeWayPower>(10);
		extras = new ArrayList<extraLife>(10);
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
		ResourceManager.loadSound("resource/explosion.wav");
		
		// the sound resource takes a particularly long time to load,
		// we preload it here to (1) reduce latency when we first play it
		// and (2) because loading it will load the audio libraries and
		// unless that is done now, we can't *disable* sound as we
		// attempt to do in the startUp() method.
		space = new Background(ScreenWidth / 2, ScreenHeight / 2);
		playerShip = new spaceShip(ScreenWidth / 2, 650, 0, 0);
		boss = new boss1(ScreenWidth / 2, 100, 0f, 0f);
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
			//timeStart = System.nanoTime();
			space.render(g);
			//timeStart2 = System.nanoTime();
			if(playerShip.timeDead <= 0){
				playerShip.render(g);
			}
			//timeEnd2 = System.nanoTime();
			for(int i = 0; i < lives; i++){
				life1 = showLives.get(i);
				life1.render(g);
			}
			for(int i = 0; i < pShots.size(); i++){
				pShot = pShots.get(i);
				pShot.render(g);
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
			for(int i = 0; i < extras.size(); i++){
				extra = extras.get(i);
				extra.render(g);
			}
			for(int i = 0; i < rfs.size(); i++){
				RF = rfs.get(i);
				RF.render(g);
			}
			for(int i = 0; i < threeWay.size(); i++){
				threeP = threeWay.get(i);
				threeP.render(g);
			}
			if(boss.isAlive){
				boss.render(g);
			}
			for(int i = 0; i < eShots.size(); i ++){
				eShot = eShots.get(i);
				eShot.render(g);
			}
			for(int i = 0; i < Enemies1.size(); i++){
				en1 = Enemies1.get(i);
				en1.render(g);
			}
			for(Bang b : explosions)
				b.render(g);
			g.drawString("Score: " + score, 10, 30);
			g.drawString("Lives: ", 10, 50);
			g.drawString("Shield Available: ", 10, 675);
			//timeEnd = System.nanoTime();
			//System.out.println("renderTime = " + (timeEnd - timeStart));
			//System.out.println("playerShip render = " + (timeEnd2 - timeStart2));
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
		if(x >= 0.75)
			return 2;
		else if(x < 0.75 && x >= 0.5){
			return 1;
		}
		else if(x >= 0.25 && x < 0.5)
			return 3;
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
			boss.setVelocity(new Vector(0f, 0f));
			boss.canShoot = false;
			boss.isAlive = false;
			explosions.add(new Bang(boss.getX(), boss.getY() + 100));
			explosions.add(new Bang(boss.getX() - 100, boss.getY() + 100));
			explosions.add(new Bang(boss.getX() + 100, boss.getY() + 100));
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
				for(int i = 0; i < pShots.size(); i++){
					pShots.remove(i);
				}
				for(int i = 0; i < eShots.size(); i ++){
					eShots.remove(i);
				}
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
						pShot = new laser(playerShip.getX(), playerShip.getY() - 20, 0f, -0.3f, 0);
						pShots.add(pShot);
						ResourceManager.getSound("resource/laser5.wav").play();
						if(playerShip.threeWay){
							pShot = new laser(playerShip.getX() + 5, playerShip.getY() - 20, 0.1f, -0.3f, 1);
							pShots.add(pShot);
							pShot = new laser(playerShip.getX() - 5, playerShip.getY() - 20, -0.1f, -0.3f, 2);
							pShots.add(pShot);
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
			
				//timeStart1 = System.nanoTime();
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
				for(int i = 0; i < extras.size(); i++){
					extra = extras.get(i);
					if(playerShip.collides(extra) != null){
						Collision collision = playerShip.collides(extra);
						Vector collVector = collision.getMinPenetration();
						if(collVector.getX() != 0){
							extras.remove(i);
							if(lives < 3)
								lives += 1;
						}
						if(collVector.getY() != 0){
							extras.remove(i);
							if(lives < 3)
								lives += 1;
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
					for(int i = 0; i < Enemies1.size(); i++){
						en1 = Enemies1.get(i);
						if(playerShip.collides(en1) != null){
							Collision collision = playerShip.collides(en1);
							Vector collVector = collision.getMinPenetration();
							if(collVector.getX() != 0){
								explosions.add(new Bang(en1.getX(), en1.getY()));
								explosions.add(new Bang(playerShip.getX(), playerShip.getY()));
								Enemies1.remove(i);
								killShip();
							}
							if(collVector.getY() != 0){
								explosions.add(new Bang(en1.getX(), en1.getY()));
								explosions.add(new Bang(playerShip.getX(), playerShip.getY()));
								Enemies1.remove(i);
								killShip();
							}
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
						for(int i = 0; i < Enemies1.size(); i++){
							en1 = Enemies1.get(i);
							if(pShield.collides(en1) != null){
								Collision collision = pShield.collides(en1);
								Vector collVector = collision.getMinPenetration();
								if(collVector.getX() != 0){
									explosions.add(new Bang(en1.getX(), en1.getY()));
									Enemies1.remove(i);
									shipsDestroyed += 1;
								}
								if(collVector.getY() != 0){
									explosions.add(new Bang(en1.getX(), en1.getY()));
									Enemies1.remove(i);
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
					for(int j = 0; j < Enemies1.size(); j++){
						en1 = Enemies1.get(j);
						if(pShot.collides(en1) != null){
							Collision collision = pShot.collides(en1);
							Vector collVector = collision.getMinPenetration();
							if(collVector.getX() != 0){
								en1.hits += 1;
								System.out.println("Collision");
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
									else if(power == 3){
										extra = new extraLife(en1.getX(), en1.getY(), 0f, 0.2f);
										extras.add(extra);
									}
									else{
										RF = new rapidFire(en1.getX(), en1.getY(), 0f, 0.2f);
										rfs.add(RF);
									}
								}
								pShots.remove(i);
								if((en1.type == 1 || en1.type == 4) && en1.hits >= 1){
									System.out.println("Type 1 destroyed");
									explosions.add(new Bang(en1.getX(), en1.getY()));
									Enemies1.remove(j);
									shipsDestroyed += 1;
									score += 20;
								}
								if(en1.type == 0 && en1.hits >= 2){
									System.out.println("Type 2 destroyed");
									explosions.add(new Bang(en1.getX(), en1.getY()));
									Enemies1.remove(j);
									shipsDestroyed += 1;
									score += 10;
								}
								if(en1.type == 2 && en1.hits >= 1){
									explosions.add(new Bang(en1.getX(), en1.getY()));
									Enemies1.remove(j);
									shipsDestroyed += 1;
									score += 50;
								}
								if(en1.type == 5 && en1.hits >= 1){
									explosions.add(new Bang(en1.getX(), en1.getY()));
									Enemies1.remove(j);
									shipsDestroyed += 1;
									score += 100;
								}
							}
							else if(collVector.getY() != 0){
								System.out.println("Collision");
								en1.hits += 1;
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
										else if(power == 3){
											extra = new extraLife(en1.getX(), en1.getY(), 0f, 0.2f);
											extras.add(extra);
										}
										else{
											RF = new rapidFire(en1.getX(), en1.getY(), 0f, 0.2f);
											rfs.add(RF);
										}
									}
								}
								pShots.remove(i);
								if((en1.type == 1 || en1.type == 4) && en1.hits >= 1){
									System.out.println("Type 1 destroyed");
									explosions.add(new Bang(en1.getX(), en1.getY()));
									Enemies1.remove(j);
									shipsDestroyed += 1;
									score += 20;
								}
								if(en1.type == 0 && en1.hits >= 2){
									System.out.println("Type 2 destroyed");
									explosions.add(new Bang(en1.getX(), en1.getY()));
									Enemies1.remove(j);
									shipsDestroyed += 1;
									score += 10;
								}
								if(en1.type == 2 && en1.hits >= 1){
									explosions.add(new Bang(en1.getX(), en1.getY()));
									Enemies1.remove(j);
									shipsDestroyed += 1;
									score += 50;
								}
								if(en1.type == 5 && en1.hits >= 1){
									explosions.add(new Bang(en1.getX(), en1.getY()));
									Enemies1.remove(j);
									shipsDestroyed += 1;
									score += 100;
								}
							}
						}
					}
					if(pShot.collides(boss) != null && boss.isAlive){
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

			//timeEnd1 = System.nanoTime();
			//System.out.println("Collision checks: " + (timeEnd1 - timeStart1));
			
			//Level setups
			if(level == 1){
				if(shipsCreated < 1)
					System.out.println("Level 1 Start");
				//Add enemies
				if((gameTimer % 75 == 0 && gameTimer <= 225) || (gameTimer % 75 == 0 && (gameTimer >= 525 && gameTimer <= 750))){
					en1 = new simpleEnemy(4 * ScreenWidth / 5, 0, -0.05f, 0.1f, 4);
					Enemies1.add(en1);
					System.out.println("Left Created");
					shipsCreated += 1;
				}
			
				//Add enemies
				if((gameTimer % 75 == 0 && (gameTimer > 225 && gameTimer <= 450)) || (gameTimer % 75 == 0 && (gameTimer >= 825 && gameTimer <= 1050))){
					en1 = new simpleEnemy(ScreenWidth / 5, 0, 0.05f, 0.1f, 1);
					Enemies1.add(en1);
					System.out.println("Right Created");
					shipsCreated += 1;
				}
			
				//Add more enemies
				if((gameTimer % 50 == 0 && (gameTimer >= 950 && gameTimer <= 1100))){
					en1 = new simpleEnemy(2 * ScreenWidth / 3, 0, -0.05f, 0.2f, 0);
					Enemies1.add(en1);
					shipsCreated += 1;
				}
			
				//Add more enemies
				if((gameTimer % 50 == 0 && (gameTimer >= 1200 && gameTimer <= 1350))){
					en1 = new simpleEnemy(ScreenWidth / 3, 0, 0.05f, 0.2f, 0);
					Enemies1.add(en1);
					shipsCreated += 1;
				}
				if(gameTimer >= 1600 && Enemies1.size() == 0){
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
					en1 = new simpleEnemy(ScreenWidth / 5, 0, 0.1f, 0.15f, 2);
					Enemies1.add(en1);
					shipsCreated += 1;
				}
				//Add shooting enemies
				if(gameTimer % 75 == 0 && gameTimer < 225){
					en1 = new simpleEnemy(4 * ScreenWidth / 5, 0, -0.1f, 0.15f, 2);
					Enemies1.add(en1);
					shipsCreated += 1;
				}
			
				//Add kamikaze enemies
				if(gameTimer % 100 == 0 && gameTimer < 700 && gameTimer >= 0){
					if(gameTimer % 200 == 0)
						en1 = new simpleEnemy(2 * ScreenWidth / 3, 0, 0f, 0.01f, 5);
					else
						en1 = new simpleEnemy(ScreenWidth / 3, 0, 0f, 0.01f, 5);
					Enemies1.add(en1);
					shipsCreated += 1;
				}
				
				//Add boss to screen
				if(gameTimer == 800){
					boss.setVelocity(new Vector(0.1f, 0f));
					boss.isAlive = true;
					boss.canShoot = true;
				}
				if(gameTimer >= 1200 && boss.isAlive == false){
					level = 3;
					for(int i = 0; i < pShots.size(); i++){
						pShots.remove(i);
					}
					for(int i = 0; i < eShots.size(); i ++){
						eShots.remove(i);
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
					en1 = new simpleEnemy(ScreenWidth / 4, 0, 0.1f, 0.15f, 1);
					Enemies1.add(en1);
				}
				if(gameTimer % 75 == 0 && gameTimer < 225){
					en1 = new simpleEnemy(3 * ScreenWidth / 4, 0, -0.1f, 0.15f, 4);
					Enemies1.add(en1);
				}
				
				//Add shooting enemies
				if(gameTimer % 50 == 0 && gameTimer >=100 && gameTimer <= 200){
					en1 = new simpleEnemy(2 * ScreenWidth / 3, 0, -0.15f, 0.2f, 2);
					Enemies1.add(en1);
				}
				if(gameTimer % 50 == 0 && gameTimer >= 200 && gameTimer <= 300){
					en1 = new simpleEnemy(ScreenWidth / 3, 0, 0.15f, 0.2f, 2);
					Enemies1.add(en1);
				}
				
				//Add big slow enemies
				if(gameTimer % 50 == 0 && gameTimer >= 200 && gameTimer <= 400){
					en1 = new simpleEnemy(ScreenWidth / 4, 0, 0.05f, 0.1f, 0);
					Enemies1.add(en1);
				}
				if(gameTimer % 50 == 0 && gameTimer >= 225 && gameTimer <= 325){
					en1 = new simpleEnemy(3 * ScreenWidth / 4, 0, -0.05f, 0.1f, 0);
					Enemies1.add(en1);
				}
			}
			if(level == 4){
				if(gameTimer < 0){
					shipsCreated = 0;
					shipsDestroyed = 0;
				}
				if(gameTimer % 50 == 0 && gameTimer >=0 && gameTimer <= 200){
					
				}
			}
			
			//Add shots from boss
			if(boss.canShoot){
				boss.shotDelay = 0;
				boss.canShoot = false;
				eShot = new enemyLaser(boss.getX() - 53, boss.getY() + 85, new Vector(0f, 0.3f));
				eShots.add(eShot);
				eShot = new enemyLaser(boss.getX() + 53, boss.getY() + 85, new Vector(0f, 0.3f));
				eShots.add(eShot);
			}
			
			//Update boss
			if(boss.getCoarseGrainedMaxX() > ScreenWidth){
				boss.setVelocity(new Vector(-0.1f, 0f));
			}
			if(boss.getCoarseGrainedMinX() < 0){
				boss.setVelocity(new Vector(0.1f, 0f));
			}
			boss.update(delta);
			
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
			
			//Update where the shield powerup is on the screen
			for(int i = 0; i < sh1.size(); i++){
				shield = sh1.get(i);
				shield.update(delta);
				if(shield.getCoarseGrainedMinY() >= ScreenHeight)
					sh1.remove(i);
			}
			
			//Update extra lives
			for(int i = 0; i < extras.size(); i++){
				extra = extras.get(i);
				extra.update(delta);
				if(extra.getCoarseGrainedMinY() >= ScreenHeight)
					extras.remove(i);
			}
			
			//Update rapid fire powerup
			for(int i = 0; i < rfs.size(); i++){
				RF = rfs.get(i);
				RF.update(delta);
				if(RF.getCoarseGrainedMinY() >= ScreenHeight)
					rfs.remove(i);
			}
			
			//Update three way powerup
			for(int i = 0; i < threeWay.size(); i++){
				threeP = threeWay.get(i);
				threeP.update(delta);
				if(threeP.getCoarseGrainedMinY() >= ScreenHeight)
					threeWay.remove(i);
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
			for(int i = 0; i < Enemies1.size(); i++){
				en1 = Enemies1.get(i);
				if(en1.moveTimer >= 2500 && en1.canChangeV == true){
					en1.canChangeV = false;
					en1.setVelocity(en1.velocity.add(new Vector(-0.15f, 0.1f)));
				}
				if(en1.canShoot){
					en1.shotDelay = 0;
					en1.canShoot = false;
					eShot = new enemyLaser(en1.getX(), en1.getY(), en1.shotVector(en1.getX(),
							en1.getY(), playerShip.getX(), playerShip.getY()));
					eShots.add(eShot);
				}
				if(en1.type == 5){
					if(en1.chaseTimer > 150 && playerShip.getY() - en1.getY() > -10){
						en1.chaseTimer = 0;
						en1.setVelocity(en1.chase(en1.getX(), en1.getY(), playerShip.getX(),
								playerShip.getY(), en1.face));
					}
				}
				en1.update(delta);
				if(en1.getCoarseGrainedMinY() >= ScreenHeight
						|| en1.getCoarseGrainedMaxX() >= ScreenWidth
						|| en1.getCoarseGrainedMinX() <= 0){
					Enemies1.remove(i);
				};
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
			/*
			System.out.println("Player shots: " + pShots.size());
			System.out.println("Enemy shots: " + eShots.size());
			System.out.println("Enemies: " + Enemies1.size());
			*/
			//Increment game timer 
			gameTimer += 1;
			//System.out.println(gameTimer);
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
	
	public static void close() throws SQLException{
		rs.close();
		stmt.close();
		db_conn.close();
	}
	
	public static void addScore(int score){
		try{
			stmt.executeUpdate("INSERT INTO Scores VALUES('" + score + "')");
		} catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public int getHighScore() throws SQLException{
			rs = stmt.executeQuery("SELECT * FROM Scores order BY DESC");
			return (Integer.parseInt(rs.getString("score")));
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
			if(timeDead <= 0 && !playerShip.isAlive){
				playerShip.isAlive = true;
				playerShip.addImageWithBoundingBox(ResourceManager.getImage("resource/playerShip.png"));
			}
		}
	}
	
	class simpleEnemy extends Entity{
		private Vector velocity;
		public int moveTimer;
		private boolean canChangeV;
		public int hits;
		public int type, face;
		public int shotDelay;
		public boolean canShoot;
		private int chaseTimer;
		private String images[] = new String[]{"resource/enemy.png","resource/enemy45.png",
			"resource/enemy90.png", "resource/enemy-45.png", "resource/enemy-90.png"};
		
		public simpleEnemy(final float x, final float y, final float vx, final float vy, int flag){
			super(x,y);
			velocity = new Vector(vx, vy);
			moveTimer = 1;
			hits = 0;
			type = flag;
			face = 0;
			chaseTimer = 0;
			if(type == 0)
				addImageWithBoundingBox(ResourceManager.getImage("resource/enemy2.png"));
			if(type == 1){
				addImageWithBoundingBox(ResourceManager.getImage("resource/enemy1.png"));
				canChangeV = true;
			}
			else if(type == 4){
				addImageWithBoundingBox(ResourceManager.getImage("resource/enemy1.png"));
				canChangeV = true;
			}
			else
				canChangeV = false;
			if(type == 2){
				addImageWithBoundingBox(ResourceManager.getImage("resource/enemy3.png"));
				canShoot = true;
			}
			else
				canShoot = false;
			if(type == 5){
				addImageWithBoundingBox(ResourceManager.getImage(images[0]));
			}
			shotDelay = 0;
		}
		
		public Vector getVelocity(){
			return velocity;
		}
		
		public void setVelocity(final Vector v){
			velocity = v;
		}
		public Vector shotVector(final float x1, final float y1, final float x2, final float y2){
			Vector shotV;
			double distance;
			float newX, newY;
			distance = Math.sqrt(Math.pow((x2 - x1),2) + Math.pow((y2 - y1),2));
			newX = x2 - x1;
			newY = y2 - y1;
			newX /= (3 * distance);
			newY /= (3 * distance);
			shotV = new Vector(newX, newY);
			return shotV;
		}

		public Vector chase(final float x1, final float y1, final float x2, final float y2, int face){
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
				removeImage(ResourceManager.getImage(images[en1.face]));
				en1.face = 4;
				addImageWithBoundingBox(ResourceManager.getImage(images[en1.face]));
			}
			//Mostly to right, little y diff
			else if(x2 - x1 <= -150 && Math.abs(y2 - y1) < 50){
				removeImage(ResourceManager.getImage(images[en1.face]));
				en1.face = 2;
				addImageWithBoundingBox(ResourceManager.getImage(images[en1.face]));
			}
			//Angled to left and below
			else if(x2 - x1 >= 200 && Math.abs(y2 - y1) > 100){
				removeImage(ResourceManager.getImage(images[en1.face]));
				en1.face = 3;
				addImageWithBoundingBox(ResourceManager.getImage(images[en1.face]));
			}
			//Angled to right and below
			else if(x2 - x1 <= -200 && Math.abs(y2 - y1) > 100){
				removeImage(ResourceManager.getImage(images[en1.face]));
				en1.face = 1;
				addImageWithBoundingBox(ResourceManager.getImage(images[en1.face]));
			}
			//Close to straight down
			else{
				removeImage(ResourceManager.getImage(images[en1.face]));
				en1.face = 0;
				addImageWithBoundingBox(ResourceManager.getImage(images[en1.face]));
			}
			chaseVector = new Vector(newX, newY);
			return chaseVector;
		}
		public void update(final int delta) {
			translate(velocity.scale(delta));
			moveTimer += delta;
			if(moveTimer >= 2500 && canChangeV == true && type == 1){
				canChangeV = false;
				System.out.println("Vector changed");
				setVelocity(velocity.add(new Vector(0.15f, 0.1f)));
			}
			if(moveTimer >= 2500 && canChangeV == true && type == 4){
				canChangeV = false;
				System.out.println("Vector changed");
				setVelocity(velocity.add(new Vector(-0.15f, 0.1f)));
			}
			shotDelay += 1;
			if(shotDelay >= 150 && type == 3)
				canShoot = true;
			chaseTimer += delta;
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
			ResourceManager.getSound("resource/explosion.wav").play();
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
		public laser(final float x, final float y, final float vx, final float vy, int way){
			super(x,y);
			if(way == 0)
				addImageWithBoundingBox(ResourceManager.getImage("resource/laser.png"));
			if(way == 1)
				addImageWithBoundingBox(ResourceManager.getImage("resource/laserR.png"));
			if(way == 2)
				addImageWithBoundingBox(ResourceManager.getImage("resource/laserL.png"));
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
	
	class extraLife extends Entity{
		private Vector speed;
		public extraLife(final float x, final float y, final float vx, final float vy){
			super(x,y);
			addImageWithBoundingBox(ResourceManager.getImage("resource/heart.png"));
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
		public boolean isAlive;
		public boss1(final float x, final float y, final float vx, final float vy){
			super(x,y);
			addImageWithBoundingBox(ResourceManager.getImage("resource/boss1.png"));
			speed = new Vector(vx, vy);
			shotDelay = 0;
			canShoot = false;
			hits = 0;
			isAlive = false;
		}
		
		public void setVelocity(final Vector v){
			speed = v;
		}
		
		public void update(final int delta){
			translate(speed.scale(delta));
			shotDelay += delta;
			if(shotDelay >= 500 && boss.isAlive)
				canShoot = true;				
		}
	}
}