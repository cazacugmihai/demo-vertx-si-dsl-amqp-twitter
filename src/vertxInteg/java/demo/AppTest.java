package demo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.vertx.java.test.TestModule;
import org.vertx.java.test.TestVerticle;
import org.vertx.java.test.VertxConfiguration;
import org.vertx.java.test.VertxTestBase;
import org.vertx.java.test.junit.VertxJUnit4ClassRunner;

@RunWith(VertxJUnit4ClassRunner.class)
@VertxConfiguration
public class AppTest extends VertxTestBase {

  @Before
  public void setup() {
    lightSleep(100L);
  }

  @Test
  @TestVerticle(main="App.groovy", urls={"build/resources/main"})
  public void simpleVerticle() {
    lightSleep(10000L);
  }

//  @Test
//  @TestModule(name="demo.si-dsl-amqp-twitter-v1.0")
//  public void simpleModule() {
//    lightSleep(10000L);
//  }

}
