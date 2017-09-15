//package summative;
/* gravitational fields demo
 * Run full screen 
 * by Mac Linh Pham
 */
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;
import java.awt.event.*;
import java.util.Random;

class FieldsSim extends JFrame implements MouseListener, PhysicsConstants{
  JPanel ani; // this panel will do the animations
  JPanel control; // this panel will hold text fields for user input
  
  ArrayList<Body> bodies; // list of active celestial bodies
  ArrayList<Projectile> projectiles; // list of active projectiles
  
  Thread animationThread; // animation cycle will be performed in this thread
  
  JTextField inputVx; // initial x velocity input
  JTextField inputVy; // initial y velocity input
  JMenuBar menuBar;
  JMenu help;
  JMenu options;
  JMenuItem instructions;
  
  double space_scale = 1;//1E7;
  double planet_scale = 1;//3E5;
  double SPEED_FACTOR = 1;//1000;
  public FieldsSim() {
    init();
  }
  public void init() {
    setTitle("Fields");
    setVisible(true);
    setSize(800,600);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLayout(new GridLayout(0,1));
    setLocationRelativeTo(null);
    
    bodies = new ArrayList<Body>();
    projectiles = new ArrayList<Projectile>();
    
    ani = new JPanel() {
      // override paint method since this panel will be doing the animations
      @Override
      public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBodies(g);
      }
    };
    ani.setFocusable(true); // since this will recieve events
    ani.addMouseListener(this); // to read mouse clicks
    ani.setDoubleBuffered(true); // its already true by default: this is just a reminder
    ani.setBackground(Color.black); // since it is in space
    ani.setLayout(new FlowLayout(FlowLayout.LEADING,0,0)); // start from left side, no gaps 
    add(ani);
    
    control = new JPanel();
    control.setPreferredSize(new Dimension(420,40));
    control.setLayout(new FlowLayout(FlowLayout.LEADING,10,10));
    control.setBackground(new Color(0,0,0));
    ani.add(control);
    
    inputVx = new JTextField();
    inputVx.setPreferredSize(new Dimension(100,25));
    control.add(inputVx);
    
    // html copied: http://stackoverflow.com/questions/2966334/how-do-i-set-the-colour-of-a-label-coloured-text-in-java
    // I wanted to keep the labels as an anonymous class to avoid clutter
    control.add(new JLabel("<html> <font color='white'>m/s EAST</font></html>"));
    
    inputVy = new JTextField();
    inputVy.setPreferredSize(new Dimension(100,25));
    control.add(inputVy);
    
    control.add(new JLabel("<html> <font color='white'>m/s NORTH</font></html>"));
    
    menuBar = new JMenuBar();
    setJMenuBar(menuBar);
    
    help = new JMenu("help");
    menuBar.add(help);
    
    instructions = new JMenuItem("instructions");
    instructions.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.out.println("clicked");
        JFrame f = new JFrame("help");
        f.setSize(300,300);
        f.setVisible(true);
        f.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        f.setLocationRelativeTo(null);
        f.setLayout(new GridLayout(0,1));
        
        JTextArea label = new JTextArea(HELP_MESSAGE);
        f.add(label);
      }
    });
    help.add(instructions);
    
    options = new JMenu("options");
    menuBar.add(options);
    
    animationThread = new Thread(new AnimationRunner());
    animationThread.start();
    
    JMenuItem clear = new JMenuItem("clear");
    clear.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        projectiles.clear();
        bodies.clear();
      }
    });
    options.add(clear);
    pack();
  }
  
  @Override
  public void mousePressed(MouseEvent e) {
    if (e.getButton() == MouseEvent.BUTTON3)
      new PlanetSpawner(e.getX(),e.getY());
    else if(e.getButton() == MouseEvent.BUTTON1)
      spawnProjectile(e.getX(),e.getY());
  }
  public void spawnProjectile(int x, int y) {
    Projectile pro = new Projectile(x,y);
    
    // I kept vx and vy seperate so if user messes up one, both are not set to zero
    try {
      pro.vx = new Double(inputVx.getText())/space_scale;
    }catch(NumberFormatException e){
      pro.vx = 0; // if incorrect format sets as zero
    }
    try {
      pro.vy = -(new Double(inputVy.getText()))/space_scale;
    }catch(NumberFormatException e){
      pro.vy = 0;
    }
    // adds to list so the animator considers it
    projectiles.add(pro);
  }
  
  /**
   * an animation runner object animates any elemnts in the Body and Projectile array list
   */
  class AnimationRunner implements Runnable {
    boolean isRunning;
    public AnimationRunner() {
      isRunning = true;
    }
    public void run() {
      while(isRunning) {
        long startTime, waitTime;
        
        // measure the time taken per animation cycle
        startTime = System.currentTimeMillis();
        
        // resolves every projectile in the list
        for(int i = 0; i < projectiles.size(); i++) {
          projectiles.get(i).performCalculations();
          projectiles.get(i).checkAlive();
        }
        
        // repaints the panel with updated changes
        ani.repaint();
        
        // subtract the cycle time to ensure each cycle is exactly 20 ms and not over
        waitTime = 20 -System.currentTimeMillis() + startTime;
        if (waitTime < 0) // if lag don't want an illegal argument exception
          waitTime = 0;
        try{
          Thread.sleep(waitTime);
        }catch(InterruptedException e){
          System.err.println("error : please restart program");
        }
      }
    }
    public void stop() {
      isRunning = false;
    }
  }
  /**
   * a displacement is a vector quantity where the angle is measured in radians
   * the class also has fields for dx and dy compoennts
   */
  class Displacement {
    double magnitude, rad, dx, dy;
    public Displacement(double magnitude, double rad) {
      this.magnitude = magnitude;
      this.rad = rad;
      // get components
      dx = magnitude*Math.cos(rad);
      dy = magnitude*Math.sin(rad);
    }
    public Displacement(){} // no args
    
    /**
     * provides the resultant value from the x and y components
     * angle is computed within the domain 0 - 2pi so I can rely on CAST rule for directions
     */
    public void updateResultant() {
      magnitude = Math.sqrt(dx*dx+dy*dy);
      // I had to use a custom method since there is two solutions for all angles
      rad = inverseTan(dy,dx);
    }
    // usefull for testing
    public String toString() {
      return magnitude + " m " + rad + " rad";
    }
  }
  class Point {
    double x, y;
    public Point(double x, double y) {
      this.x = x;
      this.y = y;
    }
    public Point(){}
    /**
     * returns the direct distance to the center of two points using distance formula
     * since circle is an extension of point, it may be used as well in the parameter
     */
    public Displacement displacementTo(Point ref) {
      Displacement value = new Displacement();
      value.dx = (ref.x-this.x);
      value.dy = -(this.y-ref.y);
      value.updateResultant();
      return value;
    }
    public double distanceTo(Point ref) {
      return displacementTo(ref).magnitude;
    }
    public double angleTo(Point ref) {
      return displacementTo(ref).rad;
    }
  } // end class Point
  
  class Body extends Point{
    double radius, mass; 
    BufferedImage image;
    public Body(double x, double y, double radius, double mass) {
      super(x,y);
      this.radius = radius/planet_scale;
      this.mass = mass;
      bodies.add(this);
      //image = null;
    }
    // if there is an image
    public Body(BufferedImage image, double x, double y, double radius, double mass) {
      this(x,y,radius,mass);
      this.image = image;
      if (image != null)this.fixImage();
    }
    public void fixImage() {
      image = scaleImage(image,(int)(2*this.radius),(int)(2*this.radius));
    }
    public Body(){}
  } // end class Body
  
  class Projectile extends Body {
    // adds new fields velocity and acceleration in x and y direction
    double vx, vy, ax, ay;
    // the list of celestial bodies this projectile must take into account 
    //ArrayList<Body> activeBodies; 
    
    public Projectile(double x, double y) {
      super();
      this.x = x;
      this.y = y;
      radius = 7; 
      //mass = 0; // since the mass is negligible compared to the others and because it is not required
      //activeBodies = new ArrayList<Body>();
      //this.image = loadImage(ORBS[new Random().nextInt(ORBS.length)]);
      this.image= loadImage(ORBS[0]);
      if (image != null) this.fixImage();
    }
    
    private void updateA() {
      ax = 0; // reset
      ay = 0;
      try {
        for (Body item : bodies) {
          ax += getAx(item);
          ay += getAy(item);
        }
      }catch(Exception e) {
        updateA();
      }
    }
    public double getA(Body ref) {
      //System.out.println(UNIVERSAL_GRAVITATION*ref.mass/Math.pow(this.distanceTo(ref)*space_scale,2));
      //System.out.println(ref.mass);
      return UNIVERSAL_GRAVITATION*ref.mass/Math.pow(this.distanceTo(ref)*space_scale,2);
    }
    // Ax is the acceleration x component towards referenced body
    public double getAx(Body ref) {
      // System.out.println(getA(ref)*Math.cos(angleTo(ref)));
      return getA(ref)*Math.cos(angleTo(ref));
    }
    public double getAy(Body ref) {
      return getA(ref)*Math.sin(angleTo(ref));
    }
    private void checkAlive() {
      for (Body item : bodies) {
        if (hasCollided(this,item)) {
          projectiles.remove(this);
        }
      }
      // I used hage numbers because these can orbit off screen. they are only removed if they reach escape velocity
      if (Math.abs(this.x) > 5000 || Math.abs(this.y) > 5000)
        projectiles.remove(this);
    }
    
    private void performCalculations() {
      x += vx*SPEED_FACTOR; 
      y += vy*SPEED_FACTOR;
      vx += ax*SPEED_FACTOR*SPEED_FACTOR/space_scale;
      vy += ay*SPEED_FACTOR*SPEED_FACTOR/space_scale;
      updateA();
    }
    
  } // end class projectile
  
  /**
   * a planet spawner object is a small frame that takes measurements for a new planet to be spawned
   */
  class PlanetSpawner extends JFrame implements ActionListener{
    int x, y;
    double radius, mass;
    BufferedImage planetImage;
    JTextField rField = new JTextField();
    JTextField mField = new JTextField();
    public PlanetSpawner(int x, int y) {
      super("Choose Celestial Body Stats");
      this.x = x;
      this.y = y;
      init();
    }
    public void init() {
      // do nothing so user must create a planet when clicked
      //setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
      setVisible(true);
      setSize(200,100);
      setLocationRelativeTo(null);
      setLayout(new GridLayout(0,2));
      
      // add text fields and it's labels
      add(rField);
      add(new JLabel("radius: m"));
      add(mField);
      add(new JLabel("mass: kg"));
      
      // when enter is typed on either text field the planet Spawner Object attempts to create a planet body
      rField.addActionListener(this);
      mField.addActionListener(this);
      
      // random planet image
      planetImage = loadImage(PLANETS[new Random().nextInt(PLANETS.length)]);
    }
    public void actionPerformed(ActionEvent e) {
      try{
        radius = new Double(rField.getText());
        mass = new Double(mField.getText());
        bodies.add(new Body(planetImage,x,y,radius,mass));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        ani.repaint();
      }catch(NumberFormatException nfe) {
        // do nothing if incorrect input
      }catch(NullPointerException npe) {
      }
    }
  }
  // to be called by the animation panel in the paint component method
  public void drawBodies(Graphics g) {
    for (Body item: bodies) {
      if (item.image == null) {
        g.setColor(Color.red);
        g.fillOval((int)(item.x-item.radius),(int)(item.y-item.radius),
                   (int)(2*item.radius),(int)(2*item.radius));
      }
      else {
        g.setColor(Color.black);
        g.drawImage(item.image,(int)(item.x-item.radius),(int)(item.y-item.radius),null);
      }
    }
    for (Projectile item: projectiles) {
      if (item.image == null) {
        g.setColor(Color.blue);
        g.fillOval((int)(item.x-item.radius),(int)(item.y-item.radius),
                   (int)(2*item.radius),(int)(2*item.radius));
      }
      else {
        g.setColor(Color.black);
        g.drawImage(item.image,(int)(item.x-item.radius),(int)(item.y-item.radius),null);
      }
    }
  }
  public static boolean hasCollided(Body a, Body b) {
    return a.distanceTo(b) <= a.radius + b.radius;
  }
  /**
   * I had to make this method since there is two angles per ratio in the domain 0 - 2pi
   */
  public static double inverseTan(double ycom, double xcom) {
    if (xcom < 0 && ycom < 0) // quadrant 2 rad
      return Math.PI+Math.atan(ycom/xcom); 
    else if (xcom < 0) // quadrant 3
      return Math.PI-Math.abs(Math.atan(ycom/xcom));
    else // quadrant 1 and 4
      return Math.atan(ycom/xcom);
  }
  /**
   * mouse stuff : do nothing 
   */
  public void mouseEntered(MouseEvent e){}
  public void mouseExited(MouseEvent e){}
  public void mouseReleased(MouseEvent e){}
  public void mouseClicked(MouseEvent e) {}
  
  /**
   * copied from JCanvas because I had difficulties loading image
   */
  public static BufferedImage loadImage(String s){ 
    try{
      return ImageIO.read(new File(s));
    }catch(Exception e){
      System.out.println("cannot load image "+s);
      return null;}
  }
  public static BufferedImage scaleImage(BufferedImage bi, int w, int h) {
    BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    Graphics2D g2 = image.createGraphics();
    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    g2.setPaint(Color.white);//getBackground());
    g2.fillRect(0, 0, w, h);
    g2.drawImage(bi, 0, 0, w, h, null);//this);
    g2.dispose();
    return image;
  }
  // I changed this method to axcept a radian angle argument (originally rotates 90 degrees)
  public static BufferedImage rotateImage(BufferedImage bi, double rad) {
    int w=bi.getWidth();
    int h=bi.getHeight();
    BufferedImage image = new BufferedImage(h, w, BufferedImage.TYPE_INT_RGB);
    Graphics2D g2 = image.createGraphics();
    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    g2.setPaint(Color.white);//getBackground());
    g2.fillRect(0, 0, h, w);
    g2.rotate(rad);
    g2.drawImage(bi,0,-h,w,h,null);//this);
    g2.dispose();
    return image;
  }
  /////////////////////////////// end copied methods from JCanvas ///////////////////////////////////
  public static void main(String [] args) {
    new FieldsSim();
  }
  
}

interface PhysicsConstants {
  final double UNIVERSAL_GRAVITATION = 6.67/1E11;
  
  final int SPAWN_PLANETS = 0;
  final int SPAWN_PROJECTILE = 1;
  final int MOVE_PLANETS = 2;
  final String[] PLANETS = {"earthb.jpg","planet1.jpg","planet2.jpg","planet3.jpg"};
  final String[] ORBS = {"blueBall.jpg","orb1.jpg","orb2.jpg","orb3.jpg","orb4.jpg","orb5.jpg"};
  final String HELP_MESSAGE = "Enter initial x and y projectile speed using the boxes in top right corner \n"
    +"Then left click anywhere in the black area to fire the projectile \n"
    +"Right click anywhere to spawn a planet \n";
}



