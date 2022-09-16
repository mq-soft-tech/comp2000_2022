import java.awt.Color;
import java.awt.Polygon;
import java.util.ArrayList;

public class Bird extends Actor {
  public Bird(Cell inLoc, boolean isHuman) {
    initActor(inLoc, Color.GREEN, isHuman, 3);
  }

  @Override
  protected void setPoly() {
    display = new ArrayList<Polygon>();
    Polygon wing1 = new Polygon();
    wing1.addPoint(getLocation().x + 5, getLocation().y + 5);
    wing1.addPoint(getLocation().x + 15, getLocation().y + 17);
    wing1.addPoint(getLocation().x + 5, getLocation().y + 17);
    Polygon wing2 = new Polygon();
    wing2.addPoint(getLocation().x + 30, getLocation().y + 5);
    wing2.addPoint(getLocation().x + 20, getLocation().y + 17);
    wing2.addPoint(getLocation().x + 30, getLocation().y + 17);
    Polygon body = new Polygon();
    body.addPoint(getLocation().x + 15, getLocation().y + 10);
    body.addPoint(getLocation().x + 20, getLocation().y + 10);
    body.addPoint(getLocation().x + 20, getLocation().y + 25);
    body.addPoint(getLocation().x + 15, getLocation().y + 25);
    display.add(body);
    display.add(wing1);
    display.add(wing2);
  }
}
