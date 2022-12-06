package antfarm;

import processing.core.*;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CellMap implements Simulator {
  CountUp simSteps = new CountUp();
  Random rng = new Random();
  int pixelsPerCell = 10, totalX, totalY, antCount = 10;
  PVector velocity = new PVector(0, 0);
  Cell[][] map;
  ArrayList<Cell> moveCells = new ArrayList<>();
  Pair pair = new Pair(0, 0);
  float randomRange = 0, tally = 0;
  int[] movementTiles = new int[4];
  ArrayList<Ant2> ants = new ArrayList<>();
  final Lock lock = new ReentrantLock();
  final Condition frameReady = lock.newCondition();

  public CellMap(int width, int height) {
    map = new Cell[(width - 2) / pixelsPerCell][(height - 2) / pixelsPerCell];
    totalX = (width - 2) / pixelsPerCell;
    totalY = (height - 2) / pixelsPerCell;
    System.out.println(width);
    for (int i = 0; i < (width - 2) / pixelsPerCell; i++) {
      for (int j = 0; j < (height - 2) / pixelsPerCell; j++) {
        if (i > 8 && i < 15 && j > 8 && j < 15) {
          map[i][j] = new Cell(i, j, true, 100);
        } else {
          map[i][j] = new Cell(i, j);
        }
      }
    }
    for (int k = 0; k < antCount; k++) {
      ants.add(new Ant2((width - 2) / pixelsPerCell / 2, (height - 2) / pixelsPerCell / 2));
    }
  }

  void drawMap(PGraphics g) {
    for (int i = 0; i < totalX; i++) {
      for (int j = 0; j < totalY; j++) {
        // noStroke();
        g.fill(255 - (3 * map[i][j].pheromone));
        if (map[i][j].food)
          g.fill(200, 50, 50);
        if (map[i][j].foodPheromone != 0)
          g.fill(100, 60);
        pheromoneDecay(i, j);
        g.square((float) i * pixelsPerCell, (float) j * pixelsPerCell, (float) pixelsPerCell);
      }
    }

    for (Ant2 ant2 : ants) {
      ant2.draw(g);
    }
  }

  void pheromoneChange(int x, int y, float increase) {
    map[x][y].pheromone = map[x][y].pheromone + (float) Math.pow(1.01f, -map[x][y].pheromone);
  }

  void foodPheroChange(int x, int y) {
    map[x][y].foodPheromone = map[x][y].foodPheromone + 10;
  }

  void pheromoneDecay(int x, int y) {
    map[x][y].pheromone = (float) (map[x][y].pheromone - .001);
  }

  float random(float high) {
    // avoid an infinite loop when 0 or NaN are passed in
    if (high == 0 || high != high) {
      return 0;
    }

    // for some reason (rounding error?) Math.random() * 3
    // can sometimes return '3' (once in ~30 million tries)
    // so a check was added to avoid the inclusion of 'howbig'
    float value;
    do {
      value = rng.nextFloat() * high;
    } while (value == high);
    return value;
  }

  float random(float low, float high) {
    if (low >= high)
      return low;
    float diff = high - low;
    float value;
    // because of rounding error, can't just add low, otherwise it may hit high
    // https://github.com/processing/processing/issues/4551
    do {
      value = random(diff) + low;
    } while (value == high);
    return value;
  }
  //spawns a new ant at the nest food was returned too
  void spawnAnt(Pair p){
    ants.add(new Ant2(p.x,p.y));
  }

  void antNavigation() {
    lock.lock();
    try {
      for (int i = 0; i < ants.size(); i++) {
        pair = ants.get(i).getCoords();
        // pheromoneChange(pair.x, pair.y, 3);
        velocity = ants.get(i).getVelocity();
        if(ants.get(i).food && map[ants.get(i).coordinates.x][ants.get(i).coordinates.x].nest){
          ants.get(i).food = false;
          spawnAnt(ants.get(i).coordinates);
        }
        else if (ants.get(i).food) {
          ants.get(i).move();
          pair = ants.get(i).getCoords();
          foodPheroChange(pair.x, pair.y);
        } else {
          // movementTiles[2] = (velocity.y >= 0) ? 1:0;
          // movementTiles[2] = (velocity.y > 0) ? 1:0;
          // movementTiles[2] = (velocity.x >= 0) ? 1:0;
          // movementTiles[3] = (velocity.x > 0) ? 1:0;

          // Diagonal
          if (Math.abs(velocity.x) + Math.abs(velocity.y) == 2) {
            // println(pair.x -velocity.x,pair.y + velocity.y);
            moveCells.add(map[(int) (pair.x)][(int) (pair.y + velocity.y)]);
            moveCells.add(map[(int) (pair.x + velocity.x)][(int) (pair.y + velocity.y)]);
            moveCells.add(map[(int) (pair.x + velocity.x)][(int) (pair.y)]);
          } else {
            // y Dominant
            if (Math.abs(velocity.y) == 1) {
              moveCells.add(map[(int) (pair.x - 1)][(int) pair.y]);
              moveCells.add(map[(int) (pair.x - 1)][(int) (pair.y + velocity.y)]);
              moveCells.add(map[(int) pair.x][(int) (pair.y + velocity.y)]);
              moveCells.add(map[(int) pair.x][(int) (pair.y + velocity.y)]);
              moveCells.add(map[(int) (pair.x + 1)][(int) (pair.y + velocity.y)]);
              moveCells.add(map[(int) (pair.x + 1)][(int) pair.y]);
            }
            // x Dominant
            else if (Math.abs(velocity.x) == 1) {
              moveCells.add(map[(int) pair.x][(int) (pair.y - 1)]);
              moveCells.add(map[(int) (pair.x + velocity.x)][(int) (pair.y - 1)]);
              moveCells.add(map[(int) (pair.x + velocity.x)][(int) (pair.y)]);
              moveCells.add(map[(int) (pair.x + velocity.x)][(int) (pair.y)]);
              moveCells.add(map[(int) (pair.x + velocity.x)][(int) (pair.y)]);
              moveCells.add(map[(int) (pair.x + velocity.x)][(int) (pair.y + 1)]);
              moveCells.add(map[(int) pair.x][(int) (pair.y + 1)]);
            } else {
              for (int h = -1; h < 2; h++) {
                for (int k = -1; k < 2; k++) {
                  // println(pair.x, pair.y);
                  moveCells.add(map[(int) pair.x + h][(int) (pair.y + k)]);
                }
              }
              moveCells.remove(4);
            }
          }
          for (int l = 0; l < moveCells.size(); l++) {
            randomRange += moveCells.get(l).getValue();
          }
          randomRange = random(0, randomRange);
          for (int l = 0; l < moveCells.size(); l++) {
            tally += moveCells.get(l).getValue();
            if (tally >= randomRange || l == moveCells.size()) {
              if (moveCells.get(l).getLocation().x > totalX - 22)
                moveCells.get(l).getLocation().x = pair.x;

              if (moveCells.get(l).getLocation().y > totalY - 22)
                moveCells.get(l).getLocation().y = pair.y;

              // println(pair.x - moveCells.get(l).getLocation().x, pair.y -
              // moveCells.get(l).getLocation().y );
              ants.get(i).move(moveCells.get(l).getLocation().x - pair.x, moveCells.get(l).getLocation().y - pair.y);
              pheromoneChange(moveCells.get(l).getLocation().x, moveCells.get(l).getLocation().y, 2.0f);
              if (moveCells.get(l).food)
                ants.get(i).food = true;
              tally = 0;
              randomRange = 0;
              moveCells.clear();
              break;
            }
          }
        }
      }
      simSteps.next();
      frameReady.signal();

    } catch (Throwable e) {
      e.printStackTrace();
    } finally {
      lock.unlock();
    }
  }

  private Frame getState() {
    Frame f = new Frame();
    f.id = simSteps.peek();
    f.map = new Cell[map.length][map[0].length];
    for (int i = 0; i < map.length; i++) {
      for (int j = 0; j < map[0].length; j++) {
        f.map[i][j] = new Cell(map[i][j]);
      }
    }
    for (Ant2 ant : ants) {
      f.all_ants.add(new Ant2(ant));
    }
    return f;
  }

  @Override
  public Frame currentState() {
    lock.lock();
    try {
      lastObservedFrame = simSteps.peek();
      return getState();
    } finally {
      lock.unlock();
    }
  }

  @Override
  public ActionResponse doAction(Action a) {
    return new ActionResponse("TODO");
  }

  static volatile int lastObservedFrame = 0;

  @Override
  public Updates nextFrame() throws InterruptedException {
    lock.lock();
    try {
      do {
        frameReady.await();
      } while (lastObservedFrame == simSteps.peek());
      lastObservedFrame = simSteps.peek();
      return new Updates(getState());
    } finally {
      lock.unlock();
    }
  }
}
