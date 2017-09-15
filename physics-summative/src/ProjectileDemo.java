//package summative;

/* Game involving the physics of projectile motion
 * by Mac Linh Pham grade 11 Computers ICSU
 * Compile > run
 * use Arrow keys to move ball up and down and to increas the spring compression
 * use W and S to change the firing angle
 * press SPACE to fire 
 * don;t let the blocks get you!
 */
  
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

class ProjectileDemo extends JFrame {
  JPanel p;
  Player player;
  
  ArrayList<Projectile> playerShots;
  ArrayList<Projectile> enemyShots;
  ArrayList<Enemy> enemies;
  
  JLabel compressLabel;
  JLabel angleLabel;
  JLabel heightLabel;
  
  int difficultyModifier = 160;
  boolean isRunning;
  public ProjectileDemo() {
    init();
  }
  private void init() {
    // start the program
    isRunning = true;
    
    // set the frame
    setTitle("Circles vs Blocks");
    setSize(1000,600);
    setVisible(true);
    setResizable(false);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    // end frame setup
    
    // these arrays hold items that are active and will be drawn
    playerShots = new ArrayList<Projectile>();
    enemies = new ArrayList<Enemy>();
    enemyShots = new ArrayList<Projectile>();
    // end arrays setup
    
    // creates a new player
    player = new Player(25,400);
    
    // this panel will do the animations so the paintComponent method is overriden
    p = new JPanel() {
      // draw the player then every element in the active arrays
      @Override
      public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.blue);
        player.draw(g);
        for(Projectile item : playerShots) {
          item.draw(g);
        }
        g.setColor(Color.red);
        for(Enemy item : enemies) {
          item.draw(g);
        }
        for(Projectile item : enemyShots) {
          item.draw(g);
        }
      }
    };
    p.setPreferredSize(new Dimension(1000,600));
    p.setBackground(Color.black);
    p.setFocusable(true); // since I want to recieve events here
    p.setLayout(new FlowLayout(FlowLayout.TRAILING,10,10));// left to right 10 pixel v and h gap
    p.addKeyListener(new KeyAdapter() { // key listener to handle user input
      @Override
      public void keyPressed(KeyEvent e) {
        player.handleKeyPressed(e);
      }
      @Override
      public void keyReleased(KeyEvent e) {
        player.handleKeyReleased(e);
      }
    });
    add(p);
    // end panel setup
    
    // adds information labels
    p.add(new JLabel("<html> <font color='white'>Firing at </font></html>"));
    
    compressLabel = new JLabel("0.1 m");
    compressLabel.setForeground(Color.white);
    p.add(compressLabel);
    
    angleLabel = new JLabel("0 deg");
    angleLabel.setForeground(Color.white);
    p.add(angleLabel);
    
    heightLabel = new JLabel((600-player.y)/PIXELS_PER_METER + " m high");
    heightLabel.setForeground(Color.white);
    p.add(heightLabel);
    // end label setup
    
    // resizes the frame to fit all components which is just the jpanel here
    pack();
    
    //try{Thread.sleep(1000);}catch(Exception e){}
    
    // this thread will do the animations
    new Thread(new Runnable() {
      @Override
      public void run() {
        while(isRunning) {
          long startTime, waitTime;
          
          // measure the time taken per animation cycle
          startTime = System.currentTimeMillis();
          
          // resolves every projectile in the list
          for(int i = 0; i < playerShots.size(); i++) {
            playerShots.get(i).update();
            // playerShots.get(i).checkAlive();
          }
          for(int i = 0; i < enemies.size(); i++) {
            enemies.get(i).update();
            // playerShots.get(i).checkAlive();
          }
          player.update();
          
          for(int i = 0; i < enemyShots.size(); i++) {
            enemyShots.get(i).update();
            // playerShots.get(i).checkAlive();
          }
          if (isRunning)
          p.repaint();
          
          // subtract the cycle time to ensure each cycle is exactly 20 ms and not over
          waitTime = 20 -System.currentTimeMillis() + startTime;
          if (waitTime < 0) // if lag don't want a negative wait time
            waitTime = 0;
          try{
            Thread.sleep(waitTime);
          }catch(InterruptedException e){
            System.err.println("error : please restart program");
          }
        }
        
      }
    }).start();
    
    // spawn enemies every 5 seconds
    while(isRunning) {
      try{Thread.sleep(5000);}catch(Exception e){}
      // pick a random height
      new Enemy(975,new Random().nextInt(575));
      // makes the enemies fire faster as the game goes on
      difficultyModifier -= 5;
    }
  }
  
  /**
   * a sprite is a moving object with fields for current position and speed and radius
   */
  abstract class Sprite{
    double x, y, vx, vy, radius;
    // the default drawing is a simple circle
    public void draw(Graphics g) {
      // - radius because I want the coordinates to be the center and not the top left corner
      g.fillOval((int)(x-radius),(int)(y-radius),2*(int)radius,2*(int)radius);
    }
    /**
     * changes it's current position based on the speed
     */
    void update() {
      x += vx;
      y += vy;
    }
    /**
     * uses the distance formula to calculate the distance of (this) sprite to the referenc sprite
     */
    double distanceTo(Sprite ref) {
      return Math.sqrt(Math.pow(x-ref.x,2)+Math.pow(y-ref.y,2));
    }
  } // end class sprite
  
  class Player extends Sprite{
    double fireRad = 0; // firing angle in radians
    int shotDelay = 0; // will be used to prevent autofire
    // the player's spring launcher (k value, projectile mass)
    // creates the default launcher of k value 4000 and firing a 1 kg bomb
    SpringLauncher spring = new SpringLauncher(4000,1);
    public Player(double x, double y) {
      this.x = x;
      this.y = y;
      radius = 25;
      //spring.x = compressedX;
      spring.x = 0.1;
    }
    @Override
    synchronized void update() {
      // stops player from going offscreen
      if (y <= 30) y = 30;
      if (y >= 570) y = 570;
      
      super.update();
      heightLabel.setText((600-player.y)/PIXELS_PER_METER + " m high"); // updates label
      for (int i = 0; i < enemyShots.size(); i++) { // checks if lose
        if(distanceTo(enemyShots.get(i)) <= radius + enemyShots.get(i).radius){
          // lose
          isRunning = false; // stops the game
          // gets the graohics object of the panel and draws a message
          Graphics2D g = (Graphics2D) p.getGraphics();
          g.setColor(Color.white);
          g.setFont(new Font("Arial",Font.PLAIN,60));
          g.drawString("YOU LOSE",200,200);
        }
      }
      shotDelay--; // decrements firing cooldown
    }
    private void handleKeyPressed(KeyEvent e) {
      int key = e.getKeyCode(); // gets the key code
      if(key == KeyEvent.VK_UP) {
        player.vy = -4;
      }
      if(key == KeyEvent.VK_DOWN) {
        player.vy = 4;
      }
      if(key == KeyEvent.VK_SPACE) {
        if(shotDelay <= 0) {
          // adds a new projectile object to the list
          playerShots.add(new Projectile(player.x,player.y,player.spring.getLaunchSpeed(),player.fireRad));
          shotDelay = 40; // sets firing cooldown
        }
      }
      if(key == KeyEvent.VK_RIGHT) {
        player.spring.x += 0.1;
        compressLabel.setText(player.spring.x + " m");
      }
      if(key == KeyEvent.VK_LEFT) {
        player.spring.x -= 0.1;
        compressLabel.setText(player.spring.x + " m");
      }
      if(key == KeyEvent.VK_W) {
        if(player.fireRad >= Math.PI/2 + 0.2) player.fireRad = Math.PI/2 - 0.2;
        player.fireRad += Math.PI/32;
        angleLabel.setText(toDeg(player.fireRad) + " deg");
      }
      if(key == KeyEvent.VK_S) {
        if(player.fireRad <= -Math.PI/2 - 0.2) player.fireRad = -Math.PI/2 + 0.2;
        player.fireRad -= Math.PI/32;
        angleLabel.setText(toDeg(player.fireRad) + " deg");
      }
      
    }
    private void handleKeyReleased(KeyEvent e) {
      int key = e.getKeyCode();
      if(key == KeyEvent.VK_UP) {
        player.vy = 0;
      }
      if(key == KeyEvent.VK_DOWN) {
        player.vy = 0;
      }
    }
  } // end class player
  
  class Enemy extends Sprite{
    int fire;
    /**
     * creates a new enemy with speed of 1 pixel/hz
     */
    public Enemy(double x, double y) {
      this.x = x;
      this.y = y;
      vx = -1;
      radius = 25;
      // start shooting
      fire = 0;
      // adds to list
      enemies.add(this);
    }
    @Override
    synchronized void update() {
      super.update();
      fireCycle();
      for (int i = 0; i < playerShots.size(); i++) {
        if(distanceTo(playerShots.get(i)) <= radius + playerShots.get(i).radius
          || x < 50){
          enemies.remove(this);
          playerShots.remove(i);
        }
      }
      fire--;
    }
    private void fireCycle() {
      if (fire == 0)
        shootAtPlayer();
    }
    private void shootAtPlayer() {
      fire = difficultyModifier;
      // calc projectile using physics formulas
      double v = 0, rad = Math.PI/4, dx = player.x-x, dy = player.y - y;
      dx = dx/PIXELS_PER_METER;
      dy = dy/PIXELS_PER_METER;
      if (dy > 10) { // below enemy
        rad = 0;
        v = dx/(Math.sqrt(2*dy/GRAVITY));
      }
      else if (dy > -20){ // slightly above
        dy = Math.abs(dy);
        double t = (20 + Math.sqrt(400 - 4*-4.9*-dy))/9.8;
        double vx = dx/t;
        
        v = -(Math.sqrt(vx*vx + 400));
        rad = Math.atan(20/-Math.abs(vx));
      }
      else if (dy > -30){
        dy = Math.abs(dy);
        double t = (30 + Math.sqrt(900 - 4*-4.9*-dy))/9.8;
        double vx = dx/t;
        v = -(Math.sqrt(vx*vx + 900));
        rad = Math.atan(30/-Math.abs(vx));
      }
      else{
        dy = Math.abs(dy);
        double t = (60 + Math.sqrt(3600 - 4*-4.9*-dy))/9.8;
        double vx = dx/t;
        v = -(Math.sqrt(vx*vx + 3600));
        rad = Math.atan(60/-Math.abs(vx));
      }
      //fires after calculating to hit the player
      enemyShots.add(new Projectile(x,y,v,rad));
    }
    // draws squares instead
    @Override 
    public void draw(Graphics g) {
      g.fillRect((int)(x-radius),(int)(y-radius),2*(int)radius,2*(int)radius);
    }
  } // end class enemy
  /**
   * a projectile also has acceeration
   */
  class Projectile extends Sprite{
    double ay;
    //double acceleration_gravity;
    
    public Projectile(double x, double y, double v, double rad) {
      this.x = x;
      this.y = y;
      this.vx = v*Math.cos(rad);
      this.vy = v*Math.sin(rad);
      radius = 5;
      initProjectile();
    }
    
    /**
     * unit conversions
     */
    private void initProjectile() {
      // changes the initial speeds to pixel based values
      vx *= PIXELS_PER_METER*CYCLE_PERIOD;
      vy *= -PIXELS_PER_METER*CYCLE_PERIOD;
      ay = GRAVITY*PIXELS_PER_METER*Math.pow(CYCLE_PERIOD,2);
      
    }
    @Override
    void update() {
      super.update();
      vy += ay;
      if(x > 1000 || 
         y > 600 || 
         y < 0)
        playerShots.remove(this);
    }
    
  } // end projectile class
  class SpringLauncher {
    double k, x, m;
    public SpringLauncher(double k, double m) {
      this.k = k;
      this.m = m;
    }
    public void setX(double x) {
      this.x = x;
    }
    /**
     * calculates the launch speed given a compression
     */
    public double getLaunchSpeed() {
      return Math.sqrt(k*x*x/m);
    }
  }
  
  // for output purposes
  public static double toDeg(double rad) {
    return Math.round((rad/Math.PI)*180);
  }
  
  public static void main(String [] args) {
    new ProjectileDemo();
  }
  // constants
  final double GRAVITY = 9.8;
  final int PIXELS_PER_METER = 10;
  final double CYCLE_PERIOD = 0.02;
}


