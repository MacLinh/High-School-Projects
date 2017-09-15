//package summative;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class LightSim extends JFrame implements PhysicsSummativeConstants{
  JTextField [] inputs = new JTextField[3];
  JComboBox type;
  JButton startButton;
  public LightSim() {
    init();
  }
  
  /**
   * sets up the components
   */
  private void init() {
    setTitle("Light Interference");
    setVisible(true);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setResizable(true);
    setLayout(new FlowLayout());
    // end frame setup
    
    String[] labels = {"seperation in um","wavelength in nm","distance in m"};
    for(int i = 0; i < inputs.length; i++) {
      inputs[i] = new JTextField();
      inputs[i].setPreferredSize(new Dimension(100,30));
      add(inputs[i]);
      add(new JLabel(labels[i]));
    }
    // end inputs setup
    
    String[] types = {"single slit","double slit","grating"};
    type = new JComboBox(types);
    add(type);
    
    startButton = new JButton("Start");
    startButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        startSim();
      }
    });
    add(startButton);
    // end button setup
    
    pack(); // resizes the frame to fit the components
  }
  private void startSim() {
    double spacing, wavelength, distance;
    try {
      // read the inputs
      spacing = new Double(inputs[0].getText());
      wavelength = new Double(inputs[1].getText());
      distance = new Double(inputs[2].getText());
      
      // read the type selector
      String choice = (String)type.getSelectedItem();
      int n = -1;
      if(choice.equals("single slit"))
        n = SINGLE_SLIT;
      else if(choice.equals("double slit"))
        n = DOUBLE_SLIT;
      else if(choice.equals("grating"))
        n = DIFFRACTION_GRATING;
      else {}
      
      new LightPanel(n,spacing,wavelength,distance);
      
    }catch(Exception e) {
      // do nothing if bad input
    }
  }
  public static void main(String [] args){
      new LightSim();
  }
}

interface PhysicsSummativeConstants {
  final int LIGHT_BLOCK_HEIGHT = 100;
  final int PPI = 96;
  final int SINGLE_SLIT = 0, DOUBLE_SLIT = 1, DIFFRACTION_GRATING = 2;
}



