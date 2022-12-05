package antfarm;

import java.util.concurrent.atomic.AtomicInteger;

public class CountUp {
    AtomicInteger count = new AtomicInteger(0);

    public int next() {
        System.out.println("increasing ctr");
      return count.incrementAndGet();
    }
    public int peek() {
        System.out.println(String.format("counter is %d\n", count.get()));
        return count.get();
    }
  }
