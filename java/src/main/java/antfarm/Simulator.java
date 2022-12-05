package antfarm;

public interface Simulator {
  public Frame currentState();

  public ActionResponse doAction(Action a);

  /**
   * Wait for the next frame to be ready.
   * 
   * @return Changed state.
   * @throws InterruptedException
   */
  public Updates nextFrame() throws InterruptedException;
}
