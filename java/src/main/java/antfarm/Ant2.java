package antfarm;

import java.util.*;

import processing.core.*;

public class Ant2 {
  static CountUp antCounter = new CountUp();
  public int id;
  public int[] coords;
  public Pair xRange = new Pair(0, 0), yRange = new Pair(0, 0);
  public ArrayList<Pair> paths;
  public Pair coordinates;
  public int walkLength = 0, tempMax = 0, tempMin = 0, scale = 10;
  public boolean food = false, trailBlazer;
  public PVector velocity = new PVector(0, 0);

  public Ant2(Ant2 ant2) {
    id = ant2.id;
    walkLength = ant2.walkLength;
    tempMax = ant2.tempMax;
    tempMin = ant2.tempMin;
    scale = ant2.scale;
    food = ant2.food;
    trailBlazer = ant2.trailBlazer;

    coords = ant2.coords.clone();
    xRange = new Pair(xRange);
    paths = new ArrayList<>();
    for (Pair p : paths) {
      paths.add(new Pair(p));
    }
    velocity = velocity.copy();
  }

  public Ant2(int x, int y) {
    id = antCounter.next();
    paths = new ArrayList<>();
    coords = new int[2];
    coordinates = new Pair(x, y);
    paths.add(coordinates);
    trailBlazer = false;
    food = false;
  }

  Pair navigate() {
    yRange.y = (velocity.y >= 0) ? 1 : 0;
    yRange.x = (velocity.y > 0) ? 1 : 0;
    xRange.y = (velocity.x >= 0) ? 1 : 0;
    xRange.x = (velocity.x > 0) ? 1 : 0;

    return coordinates;
  }

  void move(int x, int y) {
    // print(coords[0],x);
    // println(x, y);
    // fucking bullshit fix for my handed in values randomly being weird shit like
    // 20 or whatever
    if (x > 1 || x < -1)
      x = x / Math.abs(x);
    if (y > 1 || y < -1)
      y = y / Math.abs(y);

    velocity.x = x;
    velocity.y = y;
    coordinates.x = (boundsCheck(coordinates.x, x)) ? coordinates.x + x : coordinates.x - x;
    coordinates.y = (boundsCheck(coordinates.y, y)) ? coordinates.y + y : coordinates.y - y;
    velocity.x = (boundsCheck(coordinates.x, x)) ? x : -x;
    velocity.y = (boundsCheck(coordinates.y, y)) ? y : -y;
    paths.add(new Pair(coordinates));
  }

  void draw(processing.core.PGraphics g) {
    g.fill(50, 220, 60);
    g.circle((float) coordinates.x * scale, (float) coordinates.y * scale, (float) scale - scale % 5);
  }

  void move() {
    if (paths.size() == 0) {
      food = false;
      return;
    }
    // println(paths.toString());
    // println(paths.get(paths.size()-1).x,paths.get(paths.size()-1).y);
    coordinates.x = paths.get(paths.size() - 1).x;
    coordinates.y = paths.get(paths.size() - 1).y;
    paths.remove(paths.size() - 1);
    paths.trimToSize();
  }

  boolean boundsCheck(int init, int change) {
    boolean r = ((init + change > 0) && (init * scale + change * scale) < 985) ? true : false;
    if (r == false)
      System.out.println("Edge hit");
    return r;
  }

  void foodPickup() {
    food = true;
  }

  void foodDropOff() {
    food = false;
  }

  PVector getVelocity() {
    return velocity;
  }

  Pair getCoords() {
    return coordinates;
  }
}
