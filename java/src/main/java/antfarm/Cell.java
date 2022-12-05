package antfarm;

public class Cell {

  public float pheromone, foodPheromone, foodCount;
  public boolean food, terrain;
  public Pair location;

  Cell(Cell cell2) {
    pheromone = cell2.pheromone;
    foodPheromone = cell2.foodPheromone;
    foodCount = cell2.foodCount;
    food = cell2.food;
    terrain = cell2.terrain;
    location = cell2.location;
  }
  Cell(int x, int y) {
    location = new Pair(x, y);
    pheromone = 0;
    foodPheromone = 0;
    food = false;
    terrain = false;
  }

  Cell(boolean terrain) {
    this.terrain = terrain;
  }

  Cell(int x, int y, boolean food, float foodCount) {
    location = new Pair(x, y);
    this.food = food;
    this.foodCount = foodCount;
  }

  float getValue() {
    return pheromone + 4 * foodPheromone + 5;
  }

  Pair getLocation() {
    return location;
  }
}
