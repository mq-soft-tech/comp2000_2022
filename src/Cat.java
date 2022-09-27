import java.awt.Color;
import java.awt.Polygon;
import java.util.ArrayList;

public class Cat extends Actor {
  public Cat(Cell inLoc, boolean isHuman) {
    initActor(inLoc, Color.BLUE, isHuman, 2);
  }

  @Override
  protected void setPoly() {
    display = new ArrayList<Polygon>();
    Polygon ear1 = new Polygon();
    ear1.addPoint(getLocation().x + 11, getLocation().y + 5);
    ear1.addPoint(getLocation().x + 15, getLocation().y + 15);
    ear1.addPoint(getLocation().x + 7, getLocation().y + 15);
    Polygon ear2 = new Polygon();
    ear2.addPoint(getLocation().x + 22, getLocation().y + 5);
    ear2.addPoint(getLocation().x + 26, getLocation().y + 15);
    ear2.addPoint(getLocation().x + 18, getLocation().y + 15);
    Polygon face = new Polygon();
    face.addPoint(getLocation().x + 5, getLocation().y + 15);
    face.addPoint(getLocation().x + 29, getLocation().y + 15);
    face.addPoint(getLocation().x + 17, getLocation().y + 30);
    display.add(face);
    display.add(ear1);
    display.add(ear2);
  }
}
