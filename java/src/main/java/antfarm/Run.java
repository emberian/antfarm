package antfarm;

import java.io.IOException;

import processing.core.*;

public class Run extends PApplet {
  UnityConnector uc;
  CellMap sim;

  public Run() {
    
  }
  public Run(CellMap cm) {
    sim = cm;
    try {
      uc = new UnityConnector(sim);
    } catch (IOException e) {
      System.out.println("Failed to start UnityConnector!!!");
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    if (args.length != 0 && args[0] == "headless") {
      Run r = new Run(new CellMap(502, 502));
      r.uc.awaitTermination();
    } else {
      PApplet.main("antfarm.Run");
    }
  }

  @Override
  public void settings() {
    size(1002, 1002);
  }

  @Override
  public void setup() {
    sim = new CellMap(width, height);
    new Run(sim);
    ellipseMode(CORNER);
  }

  @Override
  public void draw() {
    
    sim.drawMap(this.g);
    sim.antNavigation();
  }

  @Override
  public void mousePressed() {
    sim.antNavigation();
  }
}