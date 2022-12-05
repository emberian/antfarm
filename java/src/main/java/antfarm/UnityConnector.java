package antfarm;

import org.apache.hc.core5.http.ContentType;

import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.impl.bootstrap.HttpServer;
import org.apache.hc.core5.http.impl.bootstrap.ServerBootstrap;

import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.util.TimeValue;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.io.PrintStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class UnityConnector {
  HttpServer srv;
  antfarm.Simulator sim;

  public void awaitTermination() {
    while (true) {
      try {
        srv.awaitTermination(TimeValue.MAX_VALUE);
      } catch (InterruptedException e) {
        System.out.println("Interrupted!");
      }
    }
  }

  public UnityConnector(antfarm.Simulator sim) throws IOException {
    this.sim = sim;
    SocketConfig socketConfig = SocketConfig.custom()
        .setSoTimeout(15, TimeUnit.SECONDS)
        .setTcpNoDelay(true)
        .build();
    srv = ServerBootstrap.bootstrap().setListenerPort(8081).setSocketConfig(socketConfig)
        .register("*", (req, resp, cont) -> {
          System.out.printf("New request! Path: %s\n", req.getPath());
          HttpEntity ent = req.getEntity();
          byte[] body;
          if (ent != null) {
            body = IOUtils.toByteArray(ent.getContent());
            System.out.printf("Request body: %s\n", new String(body));
          } else {
            body = null;
          }

          ObjectMapper m = new ObjectMapper();
          ByteArrayOutputStream os = new ByteArrayOutputStream();

          try {
            switch (req.getPath()) {
              case "/current_state":
                m.writeValue(os, sim.currentState());
                break;
              case "/next_sim_frame":
              System.out.println("nextsim");

                try {
                  m.writeValue(os, sim.nextFrame());
                  System.out.println("value written");
                } catch (InterruptedException e) {
                  e.printStackTrace();
                  m.writeValue(os, "timeout");
                }
                break;
              case "/action":
              System.out.println("action");

                Action a = m.readValue(new ByteArrayInputStream(body), Action.class);
                m.writeValue(os, sim.doAction(a));
                break;
              default:
                throw new IOException("no such path");
            }

            resp.setCode(200);
            resp.setEntity(new ByteArrayEntity(os.toByteArray(), ContentType.APPLICATION_JSON));
          } catch (Exception e) {
            e.printStackTrace();
            resp.setCode(500);
            os = new ByteArrayOutputStream(); // in case it got filled with crap before the error!

            // make json for the exception
            ByteArrayOutputStream prb = new ByteArrayOutputStream();
            PrintStream prs = new PrintStream(prb);
            e.printStackTrace(prs);
            ObjectNode rt = m.createObjectNode();
            rt.put("stacktrace", prb.toByteArray().toString());
            rt.put("message", e.getMessage());
            System.out.println(prb.toByteArray().toString());
            // full send
            m.writeValue(os, rt);
            resp.setEntity(new ByteArrayEntity(os.toByteArray(), ContentType.APPLICATION_JSON));
          }
          System.out.println("Closing connection!");
        })
        .create();

    srv.start();
  }
}
