using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Ants : MonoBehaviour
{
    // Start is called before the first frame update
    void Start()
    {
        
    }

    // Update is called once per frame
    void Update()
    {
        
    }

    public void OnInteracted()
    {
        Debug.Log("Hi, I'm -> " + gameObject.name);
    }
}
