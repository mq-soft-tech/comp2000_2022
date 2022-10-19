import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;

public class StageReader {
  //
  // The following three methods may need modification
  //
  private static Actor makeActor(Cell loc, String desc, Actor.Player player) {
    Actor actor;
    BufferedImage img = images.get(desc);
    int rate = Integer.valueOf(speed.get(desc));
    int harm = Integer.valueOf(damage.get(desc));
    if(desc.equals("Cat")) {
      actor = new Cat(loc, img, desc, player, rate, harm);
    } else if(desc.equals("Dog")) {
      actor = new Dog(loc, img, desc, player, rate, harm);
    } else if(desc.equals("Bird")) {
      actor = new Bird(loc, img, desc, player, rate, harm);
    } else {
      actor = null;
      System.err.println("Unsupported Actor: " + desc);
      System.exit(1);
    }
    return actor;
  }

  private static Item makeItem(Cell loc, String desc) {
    Item item;
    BufferedImage img = images.get(desc);
    int value = Integer.valueOf(points.get(desc));
    if(desc.equals("Fish")) {
      item = new Fish(loc, img, desc, value);
    } else if(desc.equals("Catnip")) {
      item = new Catnip(loc, img, desc, value);
    } else {
      item = null;
      System.err.println("Unsupported Item: " + desc);
      System.exit(1);
    }
    return item;
  }

  private static Cell makeCell(int col, int row, char sym) {
    Cell cell;
    String desc = symbols.get(sym);
    BufferedImage img = images.get(desc);
    if(desc.equals("Fence")) {
      cell = new Fence(col, row, img);
    } else if(desc.equals("Wall")) {
      cell = new Wall(col, row, img);
    } else if(desc.equals("Grass")) {
      cell = new Grass(col, row, img);
    } else if(desc.equals("Sand")) {
      cell = new Sand(col, row, img);
    } else if(desc.equals("Stone")) {
      cell = new Stone(col, row, img);
    } else if(desc.equals("Water")) {
      cell = new Water(col, row, img);
    } else {
      cell = null;
      System.err.println("Unsupported Cell: " + desc);
      System.exit(1);
    }
    return cell;
  }

  //
  // Don't modify any code below this line!
  //
  private static Map<Character,String> symbols;
  private static Map<String,BufferedImage> images;
  private static Map<String,String> items;
  private static Map<String,String> players;
  private static Map<String,String> enemies;
  private static Map<String,String> speed;
  private static Map<String,String> damage;
  private static Map<String,String> points;
  private static List<String> worldMap;

  public static Stage buildStage(String path) {
    Stage stage = new Stage();
    parseStageFile(path);
    if(players.size() > 1) {
      fail("Only 1 player currently supported!");
    }
    buildWorld(stage);
    buildItems(stage, items);
    buildPlayers(stage, players, Actor.Player.Human);
    for(Actor a: stage.actors) {
      if(a.isHuman()) {
        stage.player = a;
      }
    }
    buildPlayers(stage, enemies, Actor.Player.Bot);
    return stage;
  }

  private static void buildWorld(Stage stage) {
    int maxCols = 0;
    for(String line: worldMap) {
      maxCols = Math.max(maxCols, line.length());
    }
    stage.grid.maxCols = maxCols;
    stage.grid.maxRows = worldMap.size();
    stage.grid.cells = new Cell[maxCols][worldMap.size()];
    int row = 0;
    for(String line: worldMap) {
      int col = 0;
      for(Character sym: line.toCharArray()) {
        stage.grid.cells[col][row] = makeCell(col, row, sym);
        col++;
      }
      row++;
    }
  }

  private static void buildItems(Stage stage, Map<String,String> itemList) {
    for(Entry<String,String> entry: itemList.entrySet()) {
      String coords = entry.getKey();
      Pattern commaPair = Pattern.compile("(\\d+),(\\d+)");
      Matcher m = commaPair.matcher(coords);
      if(m.matches()) {
        int col = Integer.parseInt(m.group(1).trim());
        int row = Integer.parseInt(m.group(2).trim());
        Optional<Cell> loc = stage.grid.cellAtColRow(col, row);
        if(loc.isPresent()) {
          stage.items.add(makeItem(loc.get(), entry.getValue()));
        }
      }
    }
  }

  private static void buildPlayers(Stage stage, Map<String,String> playerList, Actor.Player role) {
    for(Entry<String,String> entry: playerList.entrySet()) {
      String coords = entry.getKey();
      Pattern commaPair = Pattern.compile("(\\d+),(\\d+)");
      Matcher m = commaPair.matcher(coords);
      if(m.matches()) {
        int col = Integer.parseInt(m.group(1).trim());
        int row = Integer.parseInt(m.group(2).trim());
        Optional<Cell> loc = stage.grid.cellAtColRow(col, row);
        if(loc.isPresent()) {
          stage.actors.add(makeActor(loc.get(), entry.getValue(), role));
        }
      }
    }
  }

  private static void parseStageFile(String path) {
    Pattern sectionHeader = Pattern.compile("\\s*\\[([^]]*)\\]\\s*");
    Pattern keyValuePair = Pattern.compile("\\s*([^=]*)=(.*)");
    Map<String,Map<String,String>> entries = new HashMap<String,Map<String,String>>();
    try {
      BufferedReader buff = new BufferedReader(new FileReader(path));
      String line;
      String section = null;
      while((line = buff.readLine()) != null) {
        Matcher m = sectionHeader.matcher(line);
        if(m.matches()) {
          section = m.group(1).trim();
        } else if(section != null) {
          m = keyValuePair.matcher(line);
          if(m.matches()) {
            String k = m.group(1).trim();
            String v = m.group(2).trim();
            Map<String, String> kv = entries.get(section);
            if(kv == null) {
              entries.put(section, kv = new HashMap<>());
            }
            kv.put(k, v);
          }
        } else {
          fail("Syntax error in stage file: " + line);
        }
      }
      buff.close();
    } catch(IOException e) {
        fail("Unable to read stage file.");
    }
    items = entries.get("Items");
    if(items == null) {
      fail("Stage file is missing [Items] section.");
    }
    players = entries.get("Player");
    if(players == null) {
      fail("Stage file is missing [Player] section.");
    }
    enemies = entries.get("Enemies");
    if(enemies == null) {
      fail("Stage file is missing [Enemies] section.");
    }
    speed = entries.get("Speed");
    if(speed == null) {
      fail("Stage file is missing [Speed] section.");
    }
    damage = entries.get("Damage");
    if(damage == null) {
      fail("Stage file is missing [Damage] section.");
    }
    points = entries.get("Points");
    if(points == null) {
      fail("Stage file is missing [Points] section.");
    }
    Map<String,String> mapLines = entries.get("World");
    if(mapLines == null) {
      fail("Stage file is missing [World] section.");
    } else {
      worldMap = new ArrayList<String>();
      List<String> keys;
      keys = entries.get("World").keySet().stream().sorted().collect(Collectors.toList());
      for(String k: keys) {
        worldMap.add(entries.get("World").get(k));
      }
    }
    Map<String,String> CellLines = entries.get("Symbols");
    if(CellLines == null) {
      fail("Stage file is missing [Symbols] section.");
    } else {
      symbols = new HashMap<Character,String>();
      for(Entry<String,String> entry: entries.get("Symbols").entrySet()) {
        Pattern quotedChar = Pattern.compile("\\s*'(.)'\\s*");
        Matcher m = quotedChar.matcher(entry.getValue());
        if(m.matches()) {
          symbols.put(m.group(1).charAt(0), entry.getKey());
        } else {
          fail("Syntax error in stage file: " + entry);
        }
      }
    }
    Map<String,String> CellFiles = entries.get("Images");
    if(CellFiles == null) {
      fail("Stage file is missing [Images] section.");
    } else {
      images = new HashMap<String,BufferedImage>();
      for(Entry<String,String> entry: entries.get("Images").entrySet()) {
        try {
          images.put(entry.getKey(), ImageIO.read(new File("assets/"+entry.getValue())));
        } catch (IOException e) {
          fail("Couldn't open " + entry.getValue());
        }
      }
    }
  }

  private static void fail(String msg, int err) {
    System.err.println(msg);
    System.exit(err);
  }

  private static void fail(String msg) {
    fail(msg, 1);
  }
}
