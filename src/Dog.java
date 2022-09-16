import java.awt.Color;
import java.awt.Polygon;
import java.util.ArrayList;

public class Dog extends Actor {
  public Dog(Cell inLoc, boolean isHuman) {
    initActor(inLoc, Color.YELLOW, isHuman, 1);
  }

  @Override
  protected void setPoly() {
    display = new ArrayList<Polygon>();
    Polygon ear1 = new Polygon();
    ear1.addPoint(getLocation().x + 5, getLocation().y + 5);
    ear1.addPoint(getLocation().x + 15, getLocation().y + 5);
    ear1.addPoint(getLocation().x + 5, getLocation().y + 15);
    Polygon ear2 = new Polygon();
    ear2.addPoint(getLocation().x + 20, getLocation().y + 5);
    ear2.addPoint(getLocation().x + 30, getLocation().y + 5);
    ear2.addPoint(getLocation().x + 30, getLocation().y + 15);
    Polygon face = new Polygon();
    face.addPoint(getLocation().x + 8, getLocation().y + 7);
    face.addPoint(getLocation().x + 27, getLocation().y + 7);
    face.addPoint(getLocation().x + 27, getLocation().y + 25);
    face.addPoint(getLocation().x + 8, getLocation().y + 25);
    display.add(face);
    display.add(ear1);
    display.add(ear2);
  }
}
