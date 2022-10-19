import java.awt.image.BufferedImage;

public class Cat extends Actor {
  public Cat(Cell loc, BufferedImage img, String desc, Player player, int speed, int damage) {
    super(loc, img, desc, player, speed, damage);
  }
}
