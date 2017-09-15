import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

class SudokuGrid extends JFrame{
  private int[][] values ={
    {0,0,0,0,0,0,0,0,0},
    {0,0,0,0,0,0,0,0,0},
    {0,0,0,0,0,0,0,0,0},
    {0,0,0,0,0,0,0,0,0},
    {0,0,0,0,0,0,0,0,0},
    {0,0,0,0,0,0,0,0,0},
    {0,0,0,0,0,0,0,0,0},
    {0,0,0,0,0,0,0,0,0},
    {0,0,0,0,0,0,0,0,0},
  };
  private int[][] answerKey;
  private JPanel p;
  private JLabel labels[][] = new JLabel[9][9];
  
  public SudokuGrid() {
    super("Sudoku");
    setVisible(true);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLayout(new FlowLayout());
    init();
    
    setGrid();
    updateGrid();
  }
  
  private void init() {
    p = new JPanel(new GridLayout(3,3,5,5)) {
      @Override
      public void paintComponent(Graphics g) {
        super.paintComponent(g);
      }
    };
    p.setPreferredSize(new Dimension(630,630));
    p.setBackground(Color.black);
    p.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        clickTile((int)Math.floor(e.getY()/70),(int)Math.floor(e.getX()/70));
      }
    });
    
    //-----------------------------layout------------------------------------------------------------------------------
    JPanel p1 = new JPanel(new GridLayout(3,3,2,2));
    p1.setBackground(Color.blue);
    for (int i = 0; i < 3; i++) {
      for(int j = 0; j < 3; j++) {
        labels[i][j] = new JLabel(""+values[i][j],SwingConstants.CENTER);
        //labels[i][j].setFont(new Font("Arial",Font.BOLD,20)); no effect
        labels[i][j].setOpaque(true);
        p1.add(labels[i][j]);
      }
    }
    p.add(p1);
    
    JPanel p2 = new JPanel(new GridLayout(3,3,2,2));
    p2.setBackground(Color.blue);
    for (int i = 0; i < 3; i++) {
      for(int j = 3; j < 6; j++) {
        labels[i][j] = new JLabel(""+values[i][j],SwingConstants.CENTER);
        labels[i][j].setOpaque(true);
        p2.add(labels[i][j]);
      }
    }
    p.add(p2);
    
    JPanel p3 = new JPanel(new GridLayout(3,3,2,2));
    p3.setBackground(Color.blue);
    for (int i = 0; i < 3; i++) {
      for(int j = 6; j < 9; j++) {
        labels[i][j] = new JLabel(""+values[i][j],SwingConstants.CENTER);
        labels[i][j].setOpaque(true);
        p3.add(labels[i][j]);
      }
    }
    p.add(p3);
    
    JPanel p4 = new JPanel(new GridLayout(3,3,2,2));
    p4.setBackground(Color.blue);
    for (int i = 3; i < 6; i++) {
      for(int j = 0; j < 3; j++) {
        labels[i][j] = new JLabel(""+values[i][j],SwingConstants.CENTER);
        labels[i][j].setOpaque(true);
        p4.add(labels[i][j]);
      }
    }
    p.add(p4);
    
    JPanel p5 = new JPanel(new GridLayout(3,3,2,2));
    p5.setBackground(Color.blue);
    for (int i = 3; i < 6; i++) {
      for(int j = 3; j < 6; j++) {
        labels[i][j] = new JLabel(""+values[i][j],SwingConstants.CENTER);
        labels[i][j].setOpaque(true);
        p5.add(labels[i][j]);
      }
    }
    p.add(p5);
    
    JPanel p6 = new JPanel(new GridLayout(3,3,2,2));
    p6.setBackground(Color.blue);
    for (int i = 3; i < 6; i++) {
      for(int j = 6; j < 9; j++) {
        labels[i][j] = new JLabel(""+values[i][j],SwingConstants.CENTER);
        labels[i][j].setOpaque(true);
        p6.add(labels[i][j]);
      }
    }
    p.add(p6);
    
    JPanel p7 = new JPanel(new GridLayout(3,3,2,2));
    p7.setBackground(Color.blue);
    for (int i = 6; i < 9; i++) {
      for(int j = 0; j < 3; j++) {
        labels[i][j] = new JLabel(""+values[i][j],SwingConstants.CENTER);
        labels[i][j].setOpaque(true);
        p7.add(labels[i][j]);
      }
    }
    p.add(p7);
    
    JPanel p8 = new JPanel(new GridLayout(3,3,2,2));
    p8.setBackground(Color.blue);
    for (int i = 6; i < 9; i++) {
      for(int j = 3; j < 6; j++) {
        labels[i][j] = new JLabel(""+values[i][j],SwingConstants.CENTER);
        labels[i][j].setOpaque(true);
        p8.add(labels[i][j]);
      }
    }
    p.add(p8);
    
    JPanel p9 = new JPanel(new GridLayout(3,3,2,2));
    p9.setBackground(Color.blue);
    for (int i = 6; i < 9; i++) {
      for(int j = 6; j < 9; j++) {
        labels[i][j] = new JLabel(""+values[i][j],SwingConstants.CENTER);
        labels[i][j].setOpaque(true);
        p9.add(labels[i][j]);
      }
    }
    p.add(p9);
    
    add(p);
    pack();
    setGrid();
    hideRandom(50);
    //---------------------------end layout----------------------------------------------------------------------------
  }
  private void clickTile(int x, int y) {
    JButton[] nbs = new JButton[9];
    JDialog dia = new JDialog(this,"Select a value",true);
    dia.setVisible(true);
    dia.setLayout(new GridLayout(1,0,10,10));
    dia.setSize(200,50);
    for(int i = 0; i < 9; i++) {
      nbs[i] = new JButton(""+(i+1));
      //nbs[i].setPreferredSize(new Dimension(50,50));
      dia.add(nbs[i]);
    }
    dia.add(new JLabel("djhasdsadbasdba"));
    JOptionPane pane = new JOptionPane("dsada",nbs);
  }
  private void setGrid() {
    answerKey = copyOf(SEED);
    Random r = new Random();
    int n = r.nextInt(3), m = r.nextInt(3);
    for(int j = 0; j < 2; j++) { // twice cus minimum need for random distribution
      for(int i = 0; i < 3; i++) {
        answerKey = swapSingleRow(answerKey,n+3*i,m+3*i);
        n = r.nextInt(3);
        m = r.nextInt(3);
        answerKey = swapSingleColumn(answerKey,n+3*i,m+3*i);
      }
      n = r.nextInt(3);
      m = r.nextInt(3);
      answerKey = swapLargeRow(answerKey,n,m);
      n = r.nextInt(3);
      m = r.nextInt(3);
      answerKey = swapLargeColumn(answerKey,n,m);
    }
  }
  private void hideRandom(int amount) {
    values = copyOf(answerKey);
    Random r = new Random();
    
    for(int i = 0; i < amount; i++) {
      int x = r.nextInt(9), y = r.nextInt(9);
      if(values[x][y] == 0)
        i--;
      else
        values[x][y] = 0;
    }
  }
  //------------------------permutation methods------------------------------------------------------------------------
  public static int[][] swapSingleRow(int[][] initial, int row1, int row2) {
    int [][] newArray = initial.clone();
    //this works cus clone
    newArray[row2] = initial[row1];
    newArray[row1] = initial[row2];
    return newArray;
  }
  public static int[][] swapSingleColumn(int[][] initial, int col1, int col2) {
    int [][] newArray = initial.clone();
    for(int i = 0; i < initial[0].length; i++) {
      int tmp = initial[i][col2]; // ?????
      newArray[i][col2] = initial[i][col1];
      newArray[i][col1] = tmp;//initial[i][col2];
    }
    return newArray;
  }
  public static int[][] swapLargeRow(int[][] initial, int row1, int row2) {
    for(int i = 0; i < 3; i++) {
      initial = swapSingleRow(initial,row1*3+i,row2*3+i);
    }
    return initial;
  }
  public static int[][] swapLargeColumn(int[][] initial, int col1, int col2) {
    for(int i = 0; i < 3; i++) {
      initial = swapSingleColumn(initial,col1*3+i,col2*3+i);
    }
    return initial;
  }
  //-----------------------end permutation methods---------------------------------------------------------------------
  private void fillGrid() {
    for(int i = 0; i < 2; i++) {
      fillRow(i);
    }
  }
  private void fillRow(int number) { // broken
    Random r = new Random();
    for (int i = 0; i < 9; i++) {
      int n = r.nextInt(9)+1;
      while(contains(getBoxGroup(number,0),n) > 1 || 
            contains(getVerticalGroup(number,0),n) > 1||
            contains(getHorizontalGroup(number,0),n) > 1) {
        n = r.nextInt(9)+1;
      }
      values[number][i] = n;
    }
  }
  private void showAnswer() {
    values = answerKey;
    updateGrid();
  }
  private void updateGrid() {
    for (int i = 0; i < 9; i++)
      for(int j = 0; j < 9; j++) {
      labels[i][j].setText(values[i][j] == 0? "" : "" + values[i][j]);
    }
  }
  public int[] getVerticalGroup(int x, int y) {
    int[] tmp = new int[9];
    int n = 0;
    for(int i = x; i >= 0; i--,n++)
      tmp[n] = values[x-i][y];
    for(int i = x+1; i < 9; i++,n++)
      tmp[n] = values[i][y];
    return tmp;
  }
  public int[] getHorizontalGroup(int x, int y) {
    int[] tmp = new int[9];
    int n = 0;
    for(int i = y; i >= 0; i--,n++)
      tmp[n] = values[x][y-i];
    for(int i = y+1; i < 9; i++,n++)
      tmp[n] = values[x][i];
    return tmp;
  }
  public int[] getBoxGroup(int x, int y) {
    int xGroup = (int) Math.floor(x/3);
    int yGroup = (int) Math.floor(y/3);
    
    int startX = 3*xGroup;
    int startY = 3*yGroup;
    int[] tmp = new int[9];
    
    int n = 0;
    for(int i = 0; i < 3; i++) {
      for(int j = 0; j < 3; j++) {
        tmp[n] = values[startX+i][startY+j];
        n++;
      }
    }
    return tmp;
  }
  public static int contains(int[] group, int value) {
    int count = 0;
    for (int item : group) {
      if (item == value)
        count++;
    }
    return count;
  }
  public static int[][] copyOf(int[][] arr) {
    int[][] value = new int[arr.length][];
      for(int i = 0; i < arr.length; i++)
      value[i] = arr[i].clone();
    return value;
  }
  public static void main(String [] args) {
    new SudokuGrid();
  }
  final int[][] SEED = {
    {4,3,5,2,6,9,7,8,1},
    {6,8,2,5,7,1,4,9,3},
    {1,9,7,8,3,4,5,6,2},
    {8,2,6,1,9,5,3,4,7},
    {3,7,4,6,8,2,9,1,5},
    {9,5,1,7,4,3,6,2,8},
    {5,1,9,3,2,6,8,7,4},
    {2,4,8,9,5,7,1,3,6},
    {7,6,3,4,1,8,2,5,9}
  };
  
}




