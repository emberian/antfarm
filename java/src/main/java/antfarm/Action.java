package antfarm;

import com.fasterxml.jackson.databind.JsonNode;

public class Action {
  public enum Actions {
    KillAnt, SpawnAnt, FlickAnt, PickUpAnt, PlaceDownAnt
  };

  public Actions which;
  public JsonNode args;

  public static Action make(Actions w, JsonNode args) {
    Action a = new Action();
    a.which = w;
    a.args = args;
    return a;
  }
}
