//package summative;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class RunSummative extends JFrame{
  JPanel p;
  JButton b1, b2, b3;
  public RunSummative() {
    init();
  }
  private void init() {
    setTitle("Computers/Physics Summative");
    setVisible(true);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    
    p = new JPanel();
    p.setPreferredSize(new Dimension(600,100));
    p.setLayout(new FlowLayout());
    add(p);
    
    b1 = new JButton("Fields and Forces");
    b1.setPreferredSize(new Dimension(150,30));
    b1.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        new FieldsSim();
      }
    });
    p.add(b1);
    
    b2 = new JButton("Light Waves");
    b2.setPreferredSize(new Dimension(150,30));
    b2.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        new LightSim();
      }
    });
    p.add(b2);
    
    b3 = new JButton("Projectiles Game");
    b3.setPreferredSize(new Dimension(150,30));
    b3.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        new ProjectileDemo();
      }
    });
    p.add(b3);
    
    p.add(new JLabel("If game not loading run from file"));
    pack();
  }
  public static void main(String [] args) {
    new RunSummative();
  }
}