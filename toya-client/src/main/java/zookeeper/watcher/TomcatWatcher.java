package zookeeper.watcher;

import org.apache.zookeeper.*;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.Watcher.Event.EventType;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by rmerriman on 2/7/14.
 */
public class TomcatWatcher implements Watcher, Runnable {

  private static final int SESSION_TIMEOUT = 5000;
  protected ZooKeeper zk;
  private String hosts = "";
  private String znode = "";
  private AsyncCallback.ChildrenCallback callbackHandler;
  private CountDownLatch connectedSignal = new CountDownLatch(1);


  public static void main(String[] args) throws IOException, InterruptedException, KeeperException, ClassNotFoundException, InstantiationException, IllegalAccessException {
    new TomcatWatcher("sandbox.hortonworks.com:2181", "/toya", "org.toya.core.handler.HAProxyHandler").run();
  }

  public TomcatWatcher(String hosts, String znode, String callbackHandlerClass) throws IOException, InterruptedException, KeeperException, ClassNotFoundException, IllegalAccessException, InstantiationException {
    this.hosts = hosts;
    this.znode = znode;
    callbackHandler = (AsyncCallback.ChildrenCallback) Class.forName(callbackHandlerClass).newInstance();
    connect();
  }

  public void connect() throws InterruptedException, IOException, KeeperException {
    zk = new ZooKeeper(hosts, SESSION_TIMEOUT, this);
    connectedSignal.await();
    zk.exists(znode, this);
  }

  @Override
  public void run() {
    zk.getChildren(znode, this, callbackHandler, null);
    while (true) {
      try {
        zk.getChildren(znode, this);
      } catch (KeeperException e) {
        e.printStackTrace();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void process(WatchedEvent watchedEvent) {
    if (watchedEvent.getState() == KeeperState.SyncConnected) {
      connectedSignal.countDown();
    }
    if (watchedEvent.getState() == KeeperState.Expired) {
      try {
        connect();
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (KeeperException e) {
        e.printStackTrace();
      }
    }
    if (watchedEvent.getType() == EventType.NodeChildrenChanged) {
      zk.getChildren(znode, this, callbackHandler, null);
    }

  }

}
