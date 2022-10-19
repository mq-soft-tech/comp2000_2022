import java.awt.image.BufferedImage;
import java.awt.Graphics;

public abstract class Actor {
  static int size = Cell.size;
  private Cell loc;
  enum Player {Human, Bot};
  private Player player;
  int speed;
  int turns;
  int damage;
  int score;
  protected MoveStrategy strat;
  protected String desc;
  protected BufferedImage img;

  public Actor(Cell l, BufferedImage i, String d, Player p, int s, int harm) {
    loc = l;
    img = i;
    player = p;
    speed = s;
    desc = d;
    strat = new RandomMove();
    turns = speed;
    damage = harm;
  }

  public void paint(Graphics g) {
    g.drawImage(img, loc.x, loc.y, size, size, null);
  }

  public boolean isHuman() {
    return player == Player.Human;
  }

  public boolean isBot() {
    return player == Player.Bot;
  }

  public void setLocation(Cell inLoc) {
    loc = inLoc;
  }

  public Cell getLocation() {
    return loc;
  }
}
