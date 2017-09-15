import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class Grid2048 extends JFrame {
  // (y,x)
  JPanel p;
  JLabel scoreLabel = new JLabel("2");
  int [][] values = {
    {0,0,0,0},
    {0,0,0,0},
    {0,0,0,0},
    {0,0,0,0}
  };
  JLabel[][] labels = new JLabel[4][4];
  Color[] colors= {Color.white,Color.yellow,Color.orange,Color.red,Color.green,Color.blue,Color.cyan};
  
  public Grid2048() {
    super("2048");
    init();
  }
  private void init() {
    setVisible(true);
    setLayout(new FlowLayout());
    p = new JPanel(new GridLayout(4,4,3,3));
    
    scoreLabel.setPreferredSize(new Dimension(600,50));
    add(p);
    add(scoreLabel);
    p.setPreferredSize(new Dimension(600,600));
    
     pack();
    p.setBackground(Color.black);
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        //labels[i][j] = new JLabel(i + " , " + j,SwingConstants.CENTER);
        labels[i][j] = new JLabel("",SwingConstants.CENTER);
        labels[i][j].setOpaque(true);
        labels[i][j].setBackground(Color.yellow);
        p.add(labels[i][j]);
      }
    }
    addRandom();
    addRandom();
    p.addMouseListener(new MouseAdapter() {
      int x1, x2, y1, y2;
      public void mousePressed(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON3)
          System.out.println(getScore());
        x1 = e.getX();
        y1 = e.getY();
      }
      public void mouseReleased(MouseEvent e) {
        x2 = e.getX();
        y2 = e.getY();
        if(Math.abs(x2 - x1) < 10 && Math.abs(y2 - y1) < 10)
          return;
        if(Math.abs(x2 - x1) > Math.abs(y2 - y1)) {
          if((x2 - x1) > 0)  swipeRight(); 
          else swipeLeft();
        } else {
          if((y2 - y1) > 0)  swipeDown(); 
          else swipeUp();
        }
        addRandom();
        if (hasLost()) System.out.println("lose");
        else
        updateGrid();
      }
    });
    p.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        //System.out.println("sasS " +e);
        if(key == KeyEvent.VK_UP) {
          System.out.println("up");
          swipeDown();
        }
      }
    });
    p.setFocusable(true);
    System.out.println(p.isFocusable());
    p.requestFocus();
    updateGrid();
  }
  private void swipeRight() {
    for(int k = 0; k < 3; k++) {
      for(int i = 0; i < 4; i++) {
        for(int j = 0; j < 3; j++) {
          try{
            if(values[i][j+1] == 0 || values[i][j+1] == values[i][j]) {
              moveTo(i,j,i,j+1);
            } else {
            }
          }catch(Exception e) {
          }
        }
      }
    }
  }
  private void swipeLeft() {
    for(int k = 0; k < 3; k++) {
      for(int i = 0; i < 4; i++) {
        for(int j = 3; j >= 0; j--) {
          try{
            if(values[i][j-1] == 0 || values[i][j-1] == values[i][j]) {
              moveTo(i,j,i,j-1);
            } else {
            }
          }catch(Exception e) {
          }
        }
      }
    }
  }
  private void swipeUp() {
    for(int k = 0; k < 3; k++) {
      for(int i = 0; i < 4; i++) {
        for(int j = 3; j >= 0; j--) {
          try{
            if(values[j-1][i] == 0 || values[j-1][i] == values[j][i]) {
              moveTo(j,i,j-1,i);
            } else {
            }
          }catch(Exception e) {
          }
        }
      }
    }
  }
  private void swipeDown() {
    for(int k = 0; k < 3; k++) {
      for(int i = 0; i < 4; i++) {
        for(int j = 0; j < 3; j++) {
          try{
            if(values[j+1][i] == 0 || values[j+1][i] == values[j][i]) {
              moveTo(j,i,j+1,i);
            } else {
            }
          }catch(Exception e) {
          }
        }
      }
    }
  }
  private void addRandom() {
    int x = new Random().nextInt(4);
    int y = new Random().nextInt(4);
    if(values[x][y] == 0)
      values[x][y] = 1;
    else addRandom();
  }
  private void moveTo(int x1, int y1, int x2, int y2) {
    int tmp = values[x1][y1];
    values[x2][y2] += tmp;
    values[x1][y1] = 0;
  }
  
  private void updateGrid() {
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        labels[i][j].setText(values[i][j] == 0? "" : ""+values[i][j] );
        labels[i][j].setBackground(getColor(values[i][j]));
      }
     }
  }
  private Color getColor(double arg) {
    if (arg == 1) return new Color(255,255,50);
    int n = (int)Math.round(Math.log(arg)/Math.log(2));
    if (n > 6) return colors[6];
    return colors[n];
  }
  private int getScore() {
    int value = 0;
    for(int[] item : values)
    for(int oitem : item)
      value += oitem;
    return value;
      
  }
  private boolean hasLost() {
    int n = 1;
    for(int i = 0; i < 4; i++) for(int j = 0; j < 4; j++) n*= values[i][j];
    return n != 0;
  }
  public static void main(String [] args) {
    new Grid2048();
  }
}