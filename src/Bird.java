import java.awt.image.BufferedImage;

public class Bird extends Actor {
  public Bird(Cell loc, BufferedImage img, String desc, Player player, int speed, int damage) {
    super(loc, img, desc, player, speed, damage);
    strat = new EscapeMove();
  }
}
