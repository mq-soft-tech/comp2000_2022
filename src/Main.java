import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.time.Instant;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main extends JFrame {
  class Canvas extends JPanel implements MouseListener, KeyListener {
    Stage stage;
    int stageno = 0;
    final int maxstages = 2;
    public final Color background = Color.DARK_GRAY;

    public Canvas() {
      setPreferredSize(new Dimension(960, 720));
      setBackground(background);
      setDoubleBuffered(true);
      addKeyListener(this);
      setFocusable(true);
    }

    @Override
    public void paint(Graphics g) {
      if(stage != null) {
          stage.paint(g);
      }
    }

    @Override
    public void keyPressed(KeyEvent e) {
      stage.keyPressed(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
      stage.keyReleased(e.getKeyCode());
    }

    // MouseClicked no longer used by this branch
    @Override
    public void mouseClicked(MouseEvent e) {}

    // Methods that are required by
    // implements MouseListener, KeyListener
    // but are unused by our application
    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
  }

  final Canvas canvas;

  private Main() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setResizable(false);
    setTitle("Cat Chase");
    // setLocationRelativeTo(null);
    canvas = new Canvas();
    setContentPane(canvas);
    pack();
    setVisible(true);
  }

  public static void main(String[] args) throws Exception {
    Main window = new Main();
    window.run();
  }

  public void run() {
    // Re-draw the screen 50 times per second
    Thread paintThread = new Thread(() -> {
      while(true) {
        Instant startTime = Instant.now();
        if(canvas.stage != null) {
          canvas.repaint();
        }
        Instant endTime = Instant.now();
        long howLong = Duration.between(startTime, endTime).toMillis();
        doSleep(20l - howLong);
      }
    });
    paintThread.start();

    Instant startUpdate;
    Instant endUpdate;
    long updateDuration;
    while(true) {
      // Build stage
      if(canvas.stage == null || canvas.stage.cleared) {
        canvas.stageno = (canvas.stageno % canvas.maxstages) + 1;
        String stagefile = "data/stage" + Integer.toString(canvas.stageno) + ".map";
        canvas.stage = StageReader.buildStage(stagefile);
        canvas.stage.cleared = false;
      }
      // Check user input every 15ms
      startUpdate = Instant.now();
      canvas.stage.update();
      endUpdate = Instant.now();
      updateDuration = Duration.between(startUpdate, endUpdate).toMillis();
      doSleep(15l - updateDuration);
    }
  }

  private void doSleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
