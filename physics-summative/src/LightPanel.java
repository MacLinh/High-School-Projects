//package summative;

import javax.swing.*;
import java.awt.*;

public class LightPanel extends JFrame implements PhysicsSummativeConstants {
  JCanvas animation_canvas;
  BackGroundPanel main_panel;
  
  public LightPanel(int type, double spacing, double wavelength, double length) {
    super("Light Interference");
    init();
    switch (type) {
      case DOUBLE_SLIT: 
        new DoubleSlit(spacing,wavelength,length);
        break;
        
      case SINGLE_SLIT:
        new SingleSlit(spacing,wavelength,length);
        break;
        
      case DIFFRACTION_GRATING:
        new DiffractionGrating(spacing,wavelength,length);
        break;
      
      default: 
        System.err.println("invalid");
    }
  }
  /**
   * sets up the necessary components
   */
  private void init() {
    setVisible(true);
    setSize(816,132);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null);
    setResizable(false);
    //setLayout(null);
    
    // end frame setup
    main_panel = new BackGroundPanel();
    main_panel.setPreferredSize(new Dimension(800,100));
    main_panel.setLayout(null);
    main_panel.setVisible(true);
    main_panel.setBackground(new Color(100,10,10));
    add(main_panel);
    
    animation_canvas = new JCanvas();
    animation_canvas.setBounds(0,0,800,100);
    animation_canvas.setOpaque(false);
    main_panel.add(animation_canvas);
    
    pack();
  }
  class BackGroundPanel extends JPanel {
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      g.setColor(new Color(0,0,0));
      g.fillRect(0,0,800,100);
    }
  }
  abstract class InterferencePattern {
    // spacing is distance between slits or slit width depending on situation meters
    // wavelength is lambda in meters
    // length is distance to screen meters
    // delta is change in x or y meters between maxima
    // all in meters
    double spacing, wavelength, length, delta; 
    // the pixel equivalent of the delta x (the only value actually used for drawing)
    double delta_pixels;
    Color original_color;
    public InterferencePattern(double spacing, double wavelength, double length) {
      this.spacing = spacing/1E6;
      this.wavelength = wavelength/1E9;
      this.length = length;
      original_color = getColor((int)wavelength);
    }
    // do the necessary calculations
    abstract void initPattern();
    // draw the pattern
    abstract void drawPattern();
  }
  class DoubleSlit extends InterferencePattern {
    public DoubleSlit(double spacing, double wavelength, double length) {
      // read
      super(spacing,wavelength,length);
      initPattern();
      drawPattern();
    }
    // calculates delta x and it's pixel equivalent
    void initPattern() {
      delta = length*wavelength/spacing;
      delta_pixels = delta/(0.0254/PPI);
    }
    void drawPattern() {
      animation_canvas.startBuffer();
      animation_canvas.setColor(original_color);
      
      // draws central maxima at the center of 400 pixels since the board is 800 pixels
      // the - 0.5(delta_pixels) is to center since java reads the top left as the coordinate
      double half_block = 0.5*delta_pixels;
      drawLightBlock(400-(int)half_block,0,(int)delta_pixels,LIGHT_BLOCK_HEIGHT,original_color);
      
      // draws maximas on the right of the central spaced delta x pixels apart 
      // an initial shift is present to account for the width of the central maxima which is only x/2
      Color color = original_color;
      for (int i = (int)delta_pixels; i < 400; i += (int)delta_pixels) {
        color = lower(color,0.85);
        drawLightBlock((400-(int)half_block)+i,0,(int)delta_pixels,LIGHT_BLOCK_HEIGHT,color);
        drawLightBlock((400-(int)half_block)-i,0,(int)delta_pixels,LIGHT_BLOCK_HEIGHT,color);
      }
      animation_canvas.endBuffer();
    }
  }
  class SingleSlit extends InterferencePattern {
    double central_maxima_pixel_width;
    public SingleSlit(double spacing, double wavelength, double length) {
      // read
      super(spacing,wavelength,length);
      initPattern();
      drawPattern();
    }
    void initPattern() {
      delta = length*wavelength/spacing;
      delta_pixels = delta/(0.0254/PPI);
      central_maxima_pixel_width = 2*delta_pixels;
    }
    void drawPattern() {
      animation_canvas.startBuffer();
      // draw central maxima
      drawLightBlock((int)400-(int)central_maxima_pixel_width/2,0,(int)central_maxima_pixel_width,
                     LIGHT_BLOCK_HEIGHT,original_color);
      // draw the rest
      Color color = original_color;
      for (int i = (int)delta_pixels; i < 400; i += (int)delta_pixels) {
        color = lower(color,0.75);
        drawLightBlock(400+i,0,(int)delta_pixels,LIGHT_BLOCK_HEIGHT,color);
        drawLightBlock(400-i-(int)central_maxima_pixel_width/2,0,(int)delta_pixels,LIGHT_BLOCK_HEIGHT,color);
        animation_canvas.endBuffer();
      }
    }
  }
   class DiffractionGrating extends DoubleSlit {
    public DiffractionGrating(double spacing, double wavelength, double length) {
       //read 
      super(spacing,wavelength,length);
    }
    @Override
    void drawPattern() {
      animation_canvas.startBuffer();
      animation_canvas.setColor(original_color);
      
      // draws central maxima at the center of 400 pixels since the board is 800 pixels
      // the - 0.5(delta_pixels) is to center since java reads the top left as the coordinate
      double half_block = 0.5*delta_pixels/5;
      drawLightBlock(400-(int)half_block,0,(int)delta_pixels/5,LIGHT_BLOCK_HEIGHT,original_color);
      
      // draws maximas on the right of the central spaced delta x pixels apart 
      // an initial shift is present to account for the width of the central maxima which is only x/2
      Color color = original_color;
      for (int i = (int)delta_pixels; i < 400; i += (int)delta_pixels) {
        color = lower(color,0.95);
        drawLightBlock((400-(int)half_block)+i,0,(int)delta_pixels/5,LIGHT_BLOCK_HEIGHT,color);
        drawLightBlock((400-(int)half_block)-i,0,(int)delta_pixels/5,LIGHT_BLOCK_HEIGHT,color);
      }
      animation_canvas.endBuffer();
    }
  }
  
  /**
   * creates a clear version of the color (used for paint gradient)
   */
  public Color setClear(Color a) {
    return new Color(a.getRed(),a.getGreen(),a.getBlue(),0);
  }
  
  /**
   * draws a maxima with half minima on either side
   * for the single and double slit, a chain of these will automatically create the intensity
   * curve expected since the half minimas will add to a full minima
   * note : method is private since it depends on the animation canvas
   */
  private void drawLightBlock(int x, int y, int w, int h, Color color) {
    GradientPaint gp = new GradientPaint(x+w/2,0,color,x,0,setClear(color),true);
    animation_canvas.setPaint(gp);
    animation_canvas.fillRect(x,y,w,h);
  }
  /**
   * lowers the color's opacity and thus relative brightness when placed on black background
   */
  public static Color lower(Color c, double factor) {
      return new Color(c.getRed(),c.getGreen(),c.getBlue(),(int)(c.getAlpha()*factor));
  }
  
  /**
   * code modified from 
   * www.efg2.com/Lab/ScienceAndEngineering/Spectra.htm
   * into a java equivalent (I didn't write this code, I only converted it to java)
   */
  public static Color getColor(int nm) {
    if (nm > 780 || nm < 380) {
      System.out.println("Color not in visible spectrum " + nm);
      return new Color(0,0,0);
    }
    float r, g, b, factor;
    if (nm >= 645) {
      r = 1.0f;
      g = 0.0f;
      b = 0.0f;
    }
    else if (nm >= 580) {
      r = 1.0f;
      g = -(nm - 645f) / (645f - 580f);
      b = 0.0f;
    }
    else if (nm >= 510) {
      r = (nm - 510f) / (580f - 510f);
      g = 1.0f;
      b = 0.0f;
    }
    else if (nm >= 490) {
      r = 0.0f;
      g = 1.0f;
      b = -(nm - 510) / (510 - 490);
    }
    else if (nm >= 440) {
      r = 0.0f;
      g = (nm - 440) / (490 - 440);
      b = 1.0f;
    }
    else {
      r = -(nm - 440) / (440 - 380);
      g = 0.0f;
      b = 1.0f;
    }
    
    // lowers intensity as approach end of spectrum
    if (nm < 420) 
      factor = 0.3f + 0.7f*(nm - 380) / (420 - 380);
    else if (nm < 700)
      factor = 1.0f;
    else
      factor = 0.3f + 0.7f*(780 - nm) / (780 - 700);
    
    // updates intensity
    r *= factor;
    g *= factor;
    b *= factor;
    
    return new Color(r,g,b,1.0f);
  }
  ////////////END OF "INSPIRED" CODE/////////////////////////////////////////////////
  
  public static void main(String [] args) {
    //new LightPanel(SINGLE_SLIT,69,380,2);
    new LightSim();
  }
}
