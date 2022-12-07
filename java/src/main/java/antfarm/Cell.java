package antfarm;

public class Cell {

  public float pheromone, foodPheromone, foodCount;
  public boolean food, terrain, nest;
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
  Cell(int x, int y, boolean nest){
    location = new Pair(x,y);
    this.nest = nest;
  }

  float getValue() {
    float tempVal = 5+ foodPheromone + pheromone + ((food) ? 150 : 0) ;
    if(tempVal < 0.1)
      tempVal = 1;
    return tempVal;
  }

  Pair getLocation() {
    return location;
  }
}
