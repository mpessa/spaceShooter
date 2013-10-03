package spaceGame;


import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;

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
	}

	/**
	 * Initialize the game after the container has been set up. This is one-time
	 * initialization, and a good place to do things like load sounds and
	 * graphics...
	 * 
	 */
	@Override
	public void init(GameContainer container) throws SlickException {
		
		// the sound resource takes a particularly long time to load,
		// we preload it here to (1) reduce latency when we first play it
		// and (2) because loading it will load the audio libraries and
		// unless that is done now, we can't *disable* sound as we
		// attempt to do in the startUp() method.

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

			break;
		case START_UP:

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
				ResourceManager.getSound("resource/Omens.wav").stop();
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
				if(input.isKeyDown(Input.KEY_A)){
					//paddle.setVelocity(new Vector(-0.75f, 0f));
				}
				if(!input.isKeyDown(Input.KEY_A) && !input.isKeyDown(Input.KEY_D)){
					//paddle.setVelocity(new Vector(0f, 0f));
				}
				if(input.isKeyDown(Input.KEY_D)){
					//paddle.setVelocity(new Vector(0.75f, 0f));
				}
				if (input.isKeyDown(Input.KEY_ENTER)) {
					//ball.setVelocity(new Vector(0, 0));
				}
				if(input.isKeyDown(Input.KEY_P)){
					pauseGame();
				}/*
				if (input.isKeyDown(Input.KEY_S)) {
					ball.setVelocity(new Vector(0f, 0.25f));
				}
				if (input.isKeyDown(Input.KEY_A)) {
					ball.setVelocity(new Vector(-.25f, 0));
				}
				if (input.isKeyDown(Input.KEY_D)) {
					ball.setVelocity(new Vector(0.25f, 0));
				}*/
			}
			// bounce the ball...
/*
			if(paddle.getCoarseGrainedMaxX() > ScreenWidth){
				paddle.setVelocity(new Vector(-0.05f, 0f));
			}
			if(paddle.getCoarseGrainedMinX() < 0){
				paddle.setVelocity(new Vector(0.05f, 0f));
			}
			if(ball.canHit == true && ball.sideHit != 0){
				if(ball.collides(paddle) != null){
					ball.bounce(0);
					bounces++;
					ball.sideHit = 0;
					ResourceManager.getSound("resource/misc1.wav").play(); 
				}
				if(ball.collides(paddle) != null && ball.getX() >= paddle.getCoarseGrainedMaxX()){
					ball.bounce(130);
					ball.setVelocity(ball.velocity.add(new Vector(-0.1f, -0.1f)));
					bounces++;
					ball.sideHit = 0;
					ResourceManager.getSound("resource/misc1.wav").play();
				}
				if(ball.collides(paddle) != null && ball.getX() <= paddle.getCoarseGrainedMinX()){
					ball.bounce(70);
					ball.setVelocity(ball.velocity.add(new Vector(0.1f, -0.1f)));
					bounces++;
					ball.sideHit = 0;
					ResourceManager.getSound("resource/misc1.wav").play();
				}
			}
			for(int i = 0; i < bricks.size(); i++){
				brick = bricks.get(i);
				if(ball.collides(brick) != null){
					Collision collision = ball.collides(brick);
					Vector collVector = collision.getMinPenetration();
					if(collVector.getX() != 0){
						ball.bounce(90);
						bounced = true;
						brick.hits += 1;
						ball.sideHit = 5;
					}
					if(collVector.getY() != 0){
						ball.bounce(0);
						bounced = true;
						brick.hits += 1;
						ball.sideHit = 5;
					}
				}
				if(brick.hits >= 1){
					bricks.remove(i);
					score += 10;
				}
			}
			for(int i = 0; i < hardBricks.size(); i++){
				hardBrick = hardBricks.get(i);
				if(ball.collides(hardBrick) != null){
					Collision collision2 = ball.collides(hardBrick);
					Vector collVector2 = collision2.getMinPenetration();
					if(collVector2.getX() != 0){
						ball.bounce(90);
						bounced = true;
						hardBrick.hits += 1;
						ball.sideHit = 5;
					}
					if(collVector2.getY() != 0){
						ball.bounce(0);
						bounced = true;
						hardBrick.hits += 1;
						ball.sideHit = 5;
					}
					if(hardBrick.hits >= 2){
						hardBricks.remove(i);
						score += 20;
					}
				}
			}
			if (bounced) {
				explosions.add(new Bang(ball.getX(), ball.getY()));
			}
			ball.update(delta);
			paddle.update(delta);
			if(bricks.size() == 0 && hardBricks.size() == 0 && gameState == PLAYING){
				level += 1;
				setLevel();
			}*/
		}
/*
		// check if there are any finished explosions, if so remove them
		for (Iterator<Bang> i = explosions.iterator(); i.hasNext();) {
			if (!i.next().isActive()) {
				i.remove();
			}
		}

		if (gameState == PLAYING) {
			gameOver();
		}
		*/
	}

	public static void main(String[] args) throws Exception{
		AppGameContainer app;
		//Class.forName("org.sqlite.JDBC");
		//db_conn = DriverManager.getConnection("jdbc:sqlite:Scores.db");
		//stmt = db_conn.createStatement();
		try {
			app = new AppGameContainer(new spaceGame("2942", 800, 600));
			app.setDisplayMode(800, 600, false);
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
	/*
	class Ball extends Entity {

		private Vector velocity;
		public boolean canHit;
		public int sideHit;
		public int countdown;

		public Ball(final float x, final float y, final float vx, final float vy) {
			super(x, y);
			addImageWithBoundingBox(ResourceManager
					.getImage("resource/yellow_2.png"));
			velocity = new Vector(vx, vy);
			canHit = true;
			sideHit = -1;
			countdown = 0;
		}

		public void setVelocity(final Vector v) {
			velocity = v;
		}

		public Vector getVelocity() {
			return velocity;
		}

		/**
		 * Bounce the ball off a surface. This simple implementation, combined
		 * with the test used when calling this method can cause "issues" in
		 * some situations. Can you see where/when? If so, it should be easy to
		 * fix!
		 * 
		 * @param surfaceTangent
		 
		public void bounce(float surfaceTangent) {
			ball.removeImage(ResourceManager.getImage("resource/yellow_2.png"));
			ball.addImageWithBoundingBox(ResourceManager
					.getImage("resource/red_eye.png"));
			countdown = 500;
			velocity = velocity.bounce(surfaceTangent);
		}

		/**
		 * Update the Ball based on how much time has passed...
		 * 
		 * @param delta
		 *            the number of milliseconds since the last update
		 
		public void update(final int delta) {
			translate(velocity.scale(delta));
			while(countdown >= 0){
				countdown -= delta;
				if(countdown <= 0){
					ball.removeImage(ResourceManager.getImage("resource/red_eye.png"));
					ball.addImageWithBoundingBox(ResourceManager.getImage("resource/yellow_2.png"));
				}
			}
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