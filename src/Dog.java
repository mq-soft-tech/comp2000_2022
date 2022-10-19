import java.awt.image.BufferedImage;

public class Dog extends Actor {
  public Dog(Cell loc, BufferedImage img, String desc, Player player, int speed, int damage) {
    super(loc, img, desc, player, speed, damage);
    strat = new ChaseMove();
  }
}
