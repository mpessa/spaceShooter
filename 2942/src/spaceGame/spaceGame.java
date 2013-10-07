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
 * A Simple Game of Bounce.
 * 
 * The game has three states: StartUp, Playing, and GameOver, the game
 * progresses through these states based on the user's input and the events that
 * occur. Each state is modestly different in terms of what is displayed and
 * what input is accepted.
 * 
 * In the playing state, our game displays a moving rectangular "ball" that
 * bounces off the sides of the game container. The ball can be controlled by
 * input from the user.
 * 
 * When the ball bounces, it appears broken for a short time afterwards and an
 * explosion animation is played at the impact site to add a bit of eye-candy
 * additionally, we play a short explosion sound effect when the game is
 * actively being played.
 * 
 * Our game also tracks the number of bounces and syncs the game update loop
 * with the monitor's refresh rate.
 * 
 * Graphics resources courtesy of qubodup:
 * http://opengameart.org/content/bomb-explosion-animation
 * 
 * Sound resources courtesy of DJ Chronos:
 * http://www.freesound.org/people/DJ%20Chronos/sounds/123236/
 * 
 * 
 * @author wallaces
 * 
 */
public class spaceGame extends BasicGame {
	private static final int START_UP = 1;
	private static final int PLAYING = 2;
	private static final int GAME_OVER = 3;
	private static final int PAUSE = 4;
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
	private int lives = 3;
	private Random random;
	public int score = 0;
	private int gameTimer = 0;
	
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
		pShots = new ArrayList<laser>(10);
		REnemies1 = new ArrayList<simpleEnemy>(10);
		LEnemies1 = new ArrayList<simpleEnemy>(10);
		explosions = new ArrayList<Bang>(10);
		sh1 = new ArrayList<shieldPowerUp>(3);
		sIcons = new ArrayList<shieldIcon>(3);
		pShields = new ArrayList<shipShield>(2);
		bossQ = new ArrayList<boss1>(1);
		eShots = new ArrayList<enemyLaser>(10);
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
	 */
	public void pauseGame(){
		gameState = PAUSE;
	}


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
			for(int i = 0; i < bossQ.size(); i++){
				boss = bossQ.get(i);
				boss.render(g);
			}
			for(int i = 0; i < eShots.size(); i ++){
				eShot = eShots.get(i);
				eShot.render(g);
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

			break;
		case PAUSE:
			for (Bang b : explosions)
				b.render(g);
			break;
		case WIN:

		}
	}

	public boolean dropPowerUp(){
		random = new Random();
		float x = random.nextFloat();
		System.out.println(x);
		if(x <= 0.25)
			return true;
		else
			return false;
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
			//ResourceManager.getSound("resource/Omens.wav").stop();
			if (gameOverTimer <= 0)
				startUp(container);
		} else {
			// get user input
			Input input = container.getInput();

			if (gameState == START_UP) {
				if (input.isKeyDown(Input.KEY_SPACE)){
					newGame(container);
					//ResourceManager.getSound("resource/Omens.wav").play();
				}
			} else if(gameState == PAUSE){
				if(input.isKeyDown(Input.KEY_SPACE)){
					gameState = PLAYING;
				}
			} else if(gameState == WIN){
				//ResourceManager.getSound("resource/Omens.wav").stop();
				if(input.isKeyDown(Input.KEY_SPACE)){
					startUp(container);
					//ball.setVelocity(new Vector(0.1f, 0.2f));
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
					if(input.isKeyDown(Input.KEY_RSHIFT) && playerShip.canShield > 0){
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
								}
								if(collVector.getY() != 0){
									explosions.add(new Bang(en1.getX(), en1.getY()));
									LEnemies1.remove(i);
								}
							}
						}
						for(int i = 0; i < REnemies1.size(); i++){
							en1 = REnemies1.get(i);
							if(playerShip.collides(en1) != null){
								Collision collision = playerShip.collides(en1);
								Vector collVector = collision.getMinPenetration();
								if(collVector.getX() != 0){
									explosions.add(new Bang(en1.getX(), en1.getY()));
									REnemies1.remove(i);
								}
								if(collVector.getY() != 0){
									explosions.add(new Bang(en1.getX(), en1.getY()));
									REnemies1.remove(i);
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
									shield = new shieldPowerUp(en1.getX(), en1.getY(), 0f, 0.2f);
									sh1.add(shield);
								}
								explosions.add(new Bang(en1.getX(), en1.getY()));
								REnemies1.remove(j);
								pShots.remove(i);
							}
							if(collVector.getY() != 0){
								if(dropPowerUp()){
									shield = new shieldPowerUp(en1.getX(), en1.getY(), 0f, 0.2f);
									sh1.add(shield);
							}
								explosions.add(new Bang(en1.getX(), en1.getY()));
								REnemies1.remove(j);
								pShots.remove(i);
							}
							score += 10;
						}
					}
					for(int j = 0; j < LEnemies1.size(); j++){
						en1 = LEnemies1.get(j);
						if(pShot.collides(en1) != null){
							Collision collision = pShot.collides(en1);
							Vector collVector = collision.getMinPenetration();
							if(collVector.getX() != 0){
								if(dropPowerUp()){
									shield = new shieldPowerUp(en1.getX(), en1.getY(), 0f, 0.2f);
									sh1.add(shield);
								}
								explosions.add(new Bang(en1.getX(), en1.getY()));
								LEnemies1.remove(j);
								pShots.remove(i);
							}
							if(collVector.getY() != 0){
								if(dropPowerUp()){
									shield = new shieldPowerUp(en1.getX(), en1.getY(), 0f, 0.2f);
									sh1.add(shield);
								}
								explosions.add(new Bang(en1.getX(), en1.getY()));
								LEnemies1.remove(j);
								pShots.remove(i);
							}
							score += 10;
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
			
			//Add enemies
			if((gameTimer % 75 == 0 && gameTimer <= 225) || (gameTimer % 75 == 0 && (gameTimer >= 525 && gameTimer <= 750))){
				en1 = new simpleEnemy(4 * ScreenWidth / 5, 0, -0.05f, 0.1f);
				REnemies1.add(en1);
			}
			
			//Add enemies
			if((gameTimer % 75 == 0 && (gameTimer >= 225 && gameTimer <= 450)) || (gameTimer % 75 == 0 && (gameTimer >= 825 && gameTimer <= 1050))){
				en1 = new simpleEnemy(ScreenWidth / 5, 0, 0.05f, 0.1f);
				LEnemies1.add(en1);
			}
			
			//Add boss to screen
			if(gameTimer == 1300){
				boss = new boss1(ScreenWidth / 2, 50, 0.1f, 0);
				bossQ.add(boss);
			}
			
			//Add shots from boss
			for(int i = 0; i < bossQ.size(); i++){
				boss = bossQ.get(i);
				if(boss.canShoot){
					boss.shotDelay = 0;
					boss.canShoot = false;
					eShot = new enemyLaser(boss.getX() - 53, boss.getY() + 85, 0f, 0.3f);
					eShots.add(eShot);
					eShot = new enemyLaser(boss.getX() + 53, boss.getY() + 85, 0f, 0.3f);
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
			
			//Update where the shield powerup is on the screen
			for(int i = 0; i < sh1.size(); i++){
				shield = sh1.get(i);
				shield.update(delta);
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
					System.out.println("Change Vector");
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
			//Increment game timer 
			gameTimer += 1;
			//System.out.println(gameTimer);
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
			if(shotDelay >= 500)
				canShoot = true;
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
		public enemyLaser(final float x, final float y, final float vx, final float vy){
			super(x,y);
			addImageWithBoundingBox(ResourceManager.getImage("resource/enemyLaser.png"));
			speed = new Vector(vx, vy);
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
	/**
	 * Paddle class will hold all of the elements of the paddle. 
	 
	class Paddle extends Entity{
		private Vector speed;	
		
		public Paddle(final float x, final float y){
			super(x,y);
			addImageWithBoundingBox(ResourceManager
					.getImage("resource/paddleBlu.png"));
			speed = new Vector(0f, 0f);
		}
		
		public void setVelocity(final Vector v){
			speed = v;
		}
		
		public Vector getVelocity(){
			return speed;
		}
		
		public void update(final int delta) {
			translate(speed.scale(delta));
		}
		
	}
	
	class Brick extends Entity{
		
		public int hits;
		
		public Brick(final float x, final float y){
			super(x,y);
			addImageWithBoundingBox(ResourceManager.getImage("resource/brick1.png"));
			hits = 0;
		}
	}
	
	class HardBrick extends Entity{
		
		public int hits;
		
		public HardBrick(final float x, final float y){
			super(x,y);
			addImageWithBoundingBox(ResourceManager.getImage("resource/brick2.png"));
			hits = 0;
		}
	}
	*/
}