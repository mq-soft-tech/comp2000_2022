import java.awt.image.BufferedImage;
import java.awt.Graphics;

public abstract class Item {
  static int size = Cell.size;
  private Cell loc;
  protected String desc;
  protected BufferedImage img;
  protected int value;
  protected boolean collected;

  public Item(Cell l, BufferedImage i, String d, int v) {
    loc = l;
    img = i;
    desc = d;
    value = v;
    collected = false;
  }

  public void paint(Graphics g) {
    if(!collected) {
      g.drawImage(img, loc.x, loc.y, size, size, null);
    }
  }

  public void setLocation(Cell inLoc) {
    loc = inLoc;
  }

  public Cell getLocation() {
    return loc;
  }

  public int getPoints() {
    return value;
  }

  public void collect() {
    collected = true;
  }
}
