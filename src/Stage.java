import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

public class Stage {
  Grid grid;
  List<Item> items;
  List<Actor> actors;
  Actor player;
  Map<String,List<Actor>> chaseLists;
  Map<String,List<Actor>> escapeLists;
  Map<String,List<Item>> itemLists;
  Map<String,Integer> itemCounts;

  boolean up = false;
  boolean down = false;
  boolean left = false;
  boolean right = false;

  boolean cleared = true;

  public Stage() {
    grid = new Grid();
    items = new ArrayList<Item>();
    actors = new ArrayList<Actor>();
    chaseLists = new HashMap<String,List<Actor>>();
    escapeLists = new HashMap<String,List<Actor>>();
    itemLists = new HashMap<String,List<Item>>();
  }

  public void update() {
    int worldX = player.getLocation().col;
    int worldY = player.getLocation().row;
    Optional<Cell> nextCell = Optional.empty();
    if(up && grid.screenY > -Grid.visibleRows/2) {
      nextCell = grid.cellAtColRow(worldX, worldY-1);
    }
    if(down && grid.screenY < grid.maxRows - Grid.visibleRows/2) {
      nextCell = grid.cellAtColRow(worldX, worldY+1);
    }
    if(left && grid.screenX > -Grid.visibleCols/2) {
      nextCell = grid.cellAtColRow(worldX-1, worldY);
    }
    if(right && grid.screenX < grid.maxCols - Grid.visibleCols/2) {
      nextCell = grid.cellAtColRow(worldX+1, worldY);
    }
    if(nextCell.isPresent()) {
      try {
        Surface currLoc = (Surface) player.getLocation();
        if(player.turns > currLoc.cost / player.speed) {
          player.turns = 0;
          Surface nextLoc = (Surface) nextCell.get();
          player.setLocation(nextLoc);
        } else {
          player.turns++;
        }
      } catch(ClassCastException e) {} // do nothing if boundary
    }
    grid.screenX = player.getLocation().col - Grid.visibleCols/2;
    grid.screenY = player.getLocation().row - Grid.visibleRows/2;
    // do we have AI moves to make?
    for(Actor a: actors) {
      if(a.isBot()) {
        try {
          Surface currLoc = (Surface) a.getLocation();
          if(a.turns > currLoc.cost / a.speed) {
            a.turns = 0;
            List<Cell> possibleLocs = getClearRadius(a.getLocation(), 1);
            Cell nextLoc = a.strat.chooseNextLoc(possibleLocs, player, actors);
            a.setLocation(nextLoc);
          } else {
            a.turns++;
          }
        } catch(ClassCastException e) {
          // Should never occur as players cannot move onto boundary cells.
          System.err.println("Fatal error: " + e);
          System.exit(1);
        }
        if(a.getLocation() == player.getLocation()) {
          player.damage +=  a.damage;
        }
      }
    }
    // Tabulate type and count of enemies
    if(chaseLists.isEmpty() || escapeLists.isEmpty()) {
      chaseLists = new HashMap<String,List<Actor>>();
      escapeLists = new HashMap<String,List<Actor>>();  
      for(Actor a: actors) {
        if(a == player) {
          continue;
        } else if(a.strat.toString() == "chase movement") {
          if(chaseLists.get(a.desc) == null) {
            chaseLists.put(a.desc, new ArrayList<Actor>());
          }
          chaseLists.get(a.desc).add(a);
        } else if(a.strat.toString() == "escape movement") {
          if(escapeLists.get(a.desc) == null) {
            escapeLists.put(a.desc, new ArrayList<Actor>());
          }
          escapeLists.get(a.desc).add(a);
        } else {}
      }
    }
    // Tabulate type and count of items
    if(itemLists.isEmpty()) {
      for(Item i: items) {
        if(itemLists.get(i.desc) == null) {
          itemLists.put(i.desc, new ArrayList<Item>());
        }
        itemLists.get(i.desc).add(i);
      }
    }
    int totalItems = 0;
    itemCounts = new HashMap<String,Integer>();
    for(Item i: items) {
      if(itemLists.get(i.desc) == null) {
        itemLists.put(i.desc, new ArrayList<Item>());
      }
      itemLists.get(i.desc).add(i);
      if(itemCounts.get(i.desc) == null) {
        itemCounts.put(i.desc, 0);
      }
      if(!i.collected) {
        totalItems++;
        if(i.getLocation() == player.getLocation()) {
          player.score += i.getPoints();
          i.collect();
        } else {
          itemCounts.put(i.desc, itemCounts.get(i.desc) + 1);
        }
      }
    }
    // Stage cleared?
    if(totalItems == 0) {
      try {
        Thread.sleep(500);
      } catch(Exception e) {}
      cleared = true;
    }
  }

  public void keyPressed(int code) {
    if(code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
      up = true;
    }
    if(code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
      left = true;
    }
    if(code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
      down = true;
    }
    if(code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
      right = true;
    }
  }

  public void keyReleased(int code) {
    if(code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
      up = false;
    }
    if(code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
      left = false;
    }
    if(code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
      down = false;
    }
    if(code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
      right = false;
    }
  }

  public List<Cell> getClearRadius(Cell from, int size) {
    List<Cell> clearCells = grid.getRadius(from, size);
    List<Cell> surfaceCells = new ArrayList<Cell>();
    for(Cell cell: clearCells) {
      try {
        Surface surfaceCell = (Surface) cell;
        surfaceCells.add(cell);
      } catch(ClassCastException e) {} // do nothing if boundary
    }
    return surfaceCells;
  }

  public Optional<Item> itemAtCell(Cell c) {
    for(Item i: items) {
      if(i.getLocation().equals(c)) {
        return Optional.of(i);
      }
    }
    return Optional.empty();
  }

  public Optional<Actor> actorAtCell(Cell c) {
    for(Actor a: actors) {
      if(a.getLocation().equals(c)) {
        return Optional.of(a);
      }
    }
    return Optional.empty();
  }

  public void paint(Graphics g) {
    grid.paint(g, this);
    // where to draw text in the information area
    final int hTab = 10;
    final int blockVT = 32;
    final int margin = (Grid.visibleCols)*blockVT+4*Grid.offset;
    int yLoc = 20;

    // state display
    g.setColor(Color.LIGHT_GRAY);
    yLoc = yLoc + blockVT;

    // agent display
    final int vTab = 15;
    final int labelIndent = margin + hTab;
    final int valueIndent = margin + 3*blockVT;

    g.drawImage(player.img, valueIndent, yLoc-12, 16, 16, null);
    g.drawString(player.desc, margin, yLoc);
    g.drawString("location:", labelIndent, yLoc+vTab);
    g.drawString(Integer.toString(player.getLocation().col) + "," + Integer.toString(player.getLocation().row), valueIndent, yLoc+vTab);
    g.drawString("Health:", labelIndent, yLoc+3*vTab);
    g.drawString(Integer.toString(-player.damage), valueIndent, yLoc+3*vTab);
    g.drawString("Score:", labelIndent, yLoc+4*vTab);
    g.drawString(Integer.toString(player.score), valueIndent, yLoc+4*vTab);

    yLoc = yLoc + 6*vTab;

    Actor chasers;
    for(Entry<String,List<Actor>> entry: chaseLists.entrySet()) {
      chasers = entry.getValue().get(0);
      g.drawImage(chasers.img, valueIndent, yLoc-12, 16, 16, null);
      g.drawString(chasers.desc, margin, yLoc);
      g.drawString("Count:", labelIndent, yLoc+vTab);
      g.drawString(Integer.toString(entry.getValue().size()), valueIndent, yLoc+vTab);
      g.drawString("Damage", labelIndent, yLoc+2*vTab);
      g.drawString(Integer.toString(Math.abs(chasers.damage)), valueIndent, yLoc+2*vTab);
      yLoc = yLoc + 4*vTab;
    }

    Actor escapers;
    for(Entry<String,List<Actor>> entry: escapeLists.entrySet()) {
      escapers = entry.getValue().get(0);
      g.drawImage(escapers.img, valueIndent, yLoc-12, 16, 16, null);
      g.drawString(escapers.desc, margin, yLoc);
      g.drawString("Count:", labelIndent, yLoc+vTab);
      g.drawString(Integer.toString(entry.getValue().size()), valueIndent, yLoc+vTab);
      g.drawString("Health:", labelIndent, yLoc+2*vTab);
      g.drawString(Integer.toString(Math.abs(escapers.damage)), valueIndent, yLoc+2*vTab);
      yLoc = yLoc + 4*vTab;
    }

    Item item;
    for(Entry<String,List<Item>> entry: itemLists.entrySet()) {
      item = entry.getValue().get(0);
      g.drawImage(item.img, valueIndent, yLoc-12, 16, 16, null);
      g.drawString(item.desc, margin, yLoc);
      g.drawString("Count:", labelIndent, yLoc+vTab);
      g.drawString(Integer.toString(itemCounts.get(item.desc)), valueIndent, yLoc+vTab);
      g.drawString("Points:", labelIndent, yLoc+2*vTab);
      g.drawString(Integer.toString(item.value), valueIndent, yLoc+2*vTab);
      yLoc = yLoc + 4*vTab;
    }
  }
}
