
import org.springframework.integration.dsl.groovy.MessageFlow
import org.springframework.integration.dsl.groovy.builder.IntegrationBuilder


/*
 * Start of vert.x configuration and setup.
 */
def eb = vertx.getEventBus()

println 'user.dir' + System.getProperty('user.dir')

def webappConf = [
  port: 8080,
  host: 'localhost',
  web_root: 'webdocs',
  bridge: true,
  inbound_permitted: [[:]],
  outbound_permitted: [[:]],
  sjs_config: ["prefix": "/eventbus"]
]

container.with {
  deployModule('vertx.web-server-v1.0', webappConf, 1, {
    eb.registerHandler 'vertx.tweets', { msg->
      println "tweet: ${msg.body.tweet}"
    }
  })
}


/*
 * The Spring Integration Groovy DSL
 * 
 */
def builder = new IntegrationBuilder('amqp')

builder.doWithSpringIntegration {
  namespaces('int-twitter')

  doWithRabbit {
    connectionFactory(host: 'localhost', port: '5672', username: 'guest', password: 'guest')
    admin connectionFactory:'connectionFactory'
    template 'rabbitTemplate', connectionFactory:'connectionFactory'
    queue 'tweets'
    fanoutExchange 'twitter', bindings:[[queue:'tweets']]
  }

  springXml {
    'int-twitter:search-inbound-channel-adapter'(id:'twitter-search', 'query':'#cheese', channel:'tweets-in')
  }

  poll('poller', 'default':true, fixedDelay:1000)
  channel('tweets-in')
  channel('tweet-text')
  transform(inputChannel:'tweets-in', outputChannel:'tweet-text', {payload->payload.getText()})

  messageFlow {
    amqpSend amqpTemplate:'rabbitTemplate', exchangeName:'twitter', requestChannel: 'tweet-text'
  }

  messageFlow {
    amqpListen queueNames:'tweets', connectionFactory:'connectionFactory', requestChannel:'amqp-tweets'
    handle { eb.publish('vertx.tweets', ['tweet':it]) }
  }

}
