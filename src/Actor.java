import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public abstract class Actor {
  Color color;
  Cell loc;
  List<Polygon> display;
  boolean humanPlayer;
  int moves;
  int turns;
  Function<List<Cell>,Cell> strat;

  protected Actor(Cell inLoc, Color inColor, Boolean isHuman, int inMoves) {
    setLocation(inLoc);
    color = inColor;
    humanPlayer = isHuman;
    moves = inMoves;
    turns = 1;
    setPoly();
  }

  public void paint(Graphics g) {
    // There are a number of ways we could utilise AnimationBeat
    // So, let's adjust the brightness of the Actors
    // We'll cycle the brightness between 40% and 100%
    // Normally Java represents Color values via RGB (Red, Green, Blue)
    // However, it's also possible to represent Color as HSB (Hue, Saturation, Brightness)
    // Conveniently, the Color class includes methods for converting between these two representations
    float phase = AnimationBeat.getInstance().phaseCompletion()/100f;
    float[] hsbValues = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
    Color animColor = new Color(Color.HSBtoRGB(hsbValues[0], hsbValues[1], 0.4f+phase*0.6f));
    for(Polygon p: display) {
      g.setColor(animColor);
      g.fillPolygon(p);
      g.setColor(Color.GRAY);
      g.drawPolygon(p);
    }
  }

  protected abstract void setPoly();

  public boolean isHuman() {
    return humanPlayer;
  }

  public void setLocation(Cell inLoc) {
    loc = inLoc;
    if(loc.row % 2 == 0) {
      strat = (List<Cell> possibleLocs) -> {
                int i = (new Random()).nextInt(possibleLocs.size());
                return possibleLocs.get(i);
              };
    } else {
      strat = (List<Cell> possibleLocs) -> {
                Cell currLM = possibleLocs.get(0);
                for(Cell c: possibleLocs) {
                  if(c.leftOfComparison(currLM) < 0) {
                    currLM = c;
                  }
                }
                return currLM;
              };
  
    }
    setPoly();
  }
}
