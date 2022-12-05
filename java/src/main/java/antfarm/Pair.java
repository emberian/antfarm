
package antfarm;

public class Pair {
  public int x, y;

  Pair(int X, int Y) {
    x = X;
    y = Y;
  }

  Pair(Pair p) {
    this.x = p.x;
    this.y = p.y;
  }

  
  @Override
  public String toString() {
    return Integer.toString(x) + Integer.toString(y);
  }
  // int x(){
  // return x;
  // }
  // int y(){
  // return y;
  // }
}
