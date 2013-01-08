package demo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.vertx.java.test.TestVerticle;
import org.vertx.java.test.VertxConfiguration;
import org.vertx.java.test.VertxTestBase;
import org.vertx.java.test.junit.VertxJUnit4ClassRunner;

@RunWith(VertxJUnit4ClassRunner.class)
@VertxConfiguration
@TestVerticle(main="App.groovy", urls={"build/resources/main"})
public class AppTest extends VertxTestBase {

  @Before
  public void setup() {
    lightSleep(100L);
  }

  @Test
  public void simple() {
    lightSleep(30000L);
  }

}
