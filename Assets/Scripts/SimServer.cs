using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.Threading.Tasks;
using UnityEngine.Networking;
using UnityEngine.Serialization;

[Serializable]
public class Updates {
public Frame nextFrame;
  public Updates(Frame f) {
    nextFrame = f;
  }
}

[Serializable]
public class Frame {
  public List<SimAnt> all_ants;
  public Cell[][] map;
  public int id;
}

// convert Ant from java to c#
[Serializable]
public class SimAnt {
  public int id;
  public int[] coords;
  public Pair xRange = new Pair(0, 0), yRange = new Pair(0, 0);
  public List<Pair> paths;
  public Pair coordinates;
  public int walkLength = 0, tempMax = 0, tempMin = 0, scale = 10;
  public bool food = false, trailBlazer;
  public PVector velocity = new PVector(0, 0);
}

[Serializable]
public class Ant : MonoBehaviour
{
    public SimAnt simdata;
}

[Serializable]
public class Cell {
  public Pair location;
  public float pheromone, foodPheromone, foodCount;
  public bool food, terrain;
}
[Serializable]
public class Pair {
  public int x, y;
  public Pair(int x, int y) {
    this.x = x;
    this.y = y;
  }
}
[Serializable]
public class CountUp {
  public int count = 0;
  public int next() {
    return count++;
  }
}
[Serializable]
public class PVector {
  public float x, y;
  public PVector(float x, float y) {
    this.x = x;
    this.y = y;
  }
}

[Serializable]
public class SimAction {
    [Serializable]
    public enum Actions {
    KillAnt, SpawnAnt, FlickAnt, PickUpAnt, PlaceDownAnt
  };

  public Actions which;
  public object args;

  public static SimAction make(Actions w, object args) {
    SimAction a = new SimAction();
    a.which = w;
    a.args = args;
    return a;
  }
}

public class SimTransport
{
    public Updates recentUpd;
    public Frame cur;

    public SimTransport()
    {
    }

    public void currentState(Action<Frame> cb) {
        var req = UnityWebRequest.Get("http://localhost:8081/current_state");
            req.SendWebRequest();

            if (req.result != UnityWebRequest.Result.Success) {
                Debug.LogError(req.error);
            } else {
                Frame f = JsonUtility.FromJson<Frame>(req.downloadHandler.text);
                cur = f;
                cb(f);
            }
        }

    public void nextSimulationFrame(Action<Updates> cb) {
        var req = UnityWebRequest.Get("http://localhost:8081/next_sim_frame");

        var inprog = req.SendWebRequest();
        inprog.completed += (AsyncOperation j) =>
        {
            if (req.result != UnityWebRequest.Result.Success)
            {
                var e = req.error;
                Debug.LogError(e);
                Application.Quit();
            }
            else
            {
                Debug.Log(req.downloadHandler.text);
                var up = JsonUtility.FromJson<Updates>(req.downloadHandler.text);
                req.Dispose();
                recentUpd = up;
                Debug.Log(up);
                cb(up);
            }

        };
    }


    public void doAction(SimAction act) {
        using (UnityWebRequest req = UnityWebRequest.Post("http://localhost:8081/action", JsonUtility.ToJson(act)))
        {
            var inprog = req.SendWebRequest();
            inprog.completed += (AsyncOperation j) =>
            {
                if (req.result != UnityWebRequest.Result.Success)
                {
                    Debug.LogError(req.error);
                }
                else
                {
                    Updates up = JsonUtility.FromJson<Updates>(req.downloadHandler.text);
                    recentUpd = up;
                }

            };
        }
    }
}

public class SimServer : MonoBehaviour
{
    public GameObject antPrefab;
    public int zLevel;
    SimTransport trp;
    Dictionary<int, SimAnt> ants = new Dictionary<int, SimAnt>();
    Dictionary<int, GameObject> antEnts = new Dictionary<int, GameObject>();
    // Start is called before the first frame update
    public void Start()
    {
        var s = this;
        trp = new SimTransport();
        s.GetFrameForever();
       /* trp.currentState(frm =>
        {
            s.Refresh(frm);
            s.GetFrameForever();
        });*/
    }

    void GetFrameForever()
    {
        SimServer s = this;
       trp.nextSimulationFrame(upd => { s.Refresh(upd.nextFrame); s.GetFrameForever(); });
    }

    void Refresh(Frame f) {
        var notseen = new HashSet<int>();
        foreach (var a in ants.Values) {
            notseen.Add(a.id);
        }
        foreach (var ant in f.all_ants)
        {
            notseen.Remove(ant.id);
            if (!antEnts.ContainsKey(ant.id))
            {
                Debug.Log("spawned ant: " + ant.id);
                var go = Instantiate(antPrefab, new Vector3(ant.coordinates.x, ant.coordinates.y, zLevel), Quaternion.identity);
                antEnts[ant.id] = go;
            }
            var antObj = antEnts[ant.id].GetComponent<Ants>();
            antObj.transform.position = new Vector3(ant.coordinates.x, ant.coordinates.y, zLevel);
        }
    }

    // Update is called once per frame
    void Update()
    {
        
    }
}
