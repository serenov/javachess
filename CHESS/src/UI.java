import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.*;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class UI extends JFrame {

  public static void main(String[] args) {
    UI opdr = new UI();
  }//from  w  w  w .  ja va 2  s  .  c o  m

  public UI() {
    super("line");
    setSize(600, 600);
    setTitle("2D Line");
    setDefaultCloseOperation(EXIT_ON_CLOSE);

    JPanel panel = new JPanel(new BorderLayout());
    JPanel topPanel = new JPanel(new FlowLayout());
    panel.add(topPanel, BorderLayout.NORTH);

    JLabel statusLabel = new JLabel("Draw a Line");
    topPanel.add(statusLabel);

    JPanel centerPanel = new Opdracht2_4a();
    panel.add(centerPanel, BorderLayout.CENTER);
    centerPanel.addMouseListener(new MouseAdapter(){
      @Override
      public void mousePressed(MouseEvent e) {
          // TODO Auto-generated method stub
          System.out.print(e.getPoint());
      }

    });

    JPanel bottomPanel = new JPanel(new FlowLayout());
    panel.add(bottomPanel, BorderLayout.SOUTH);

    setContentPane(panel);
    setVisible(true);
  }

  public class Opdracht2_4a extends JPanel {
    public Opdracht2_4a() {
      setPreferredSize(new Dimension(600, 400));
    }

    public void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D) g;
      g2.drawRect(100, 0, 400, 400);
      int z = 2;
      int y = 0;
      for (int i = 0; i < 8; i++) {
        for (int x = 100; x < 550; x += 50) {
          z++;
          g2.drawRect(x, y, 50, 50);
          if ((z % 2) == 0) {
            g2.fillRect(x, y, 50, 50);
          }

        }
        y = y + 50;
      }
    }
  }

}