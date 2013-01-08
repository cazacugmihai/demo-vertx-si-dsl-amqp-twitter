
import org.springframework.integration.dsl.groovy.MessageFlow
import org.springframework.integration.dsl.groovy.builder.IntegrationBuilder

/*
 * Start of vert.x configuration and setup.
 */
def eb = vertx.getEventBus()

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
  deployModule('vertx.web-server-v1.0', webappConf)
}

eb.registerHandler 'vertx.tweets', { msg->
  println "vertx: $msg"
}

/*
 * Now the Spring Integration Groovy DSL
 * 
 */
def builder = new IntegrationBuilder('amqp')

builder.doWithSpringIntegration {
  namespaces('rabbit')
  namespaces('int-twitter')

  springXml {
    'rabbit:connection-factory'(id:'connectionFactory', host: 'localhost', port: '5672', username: 'guest', password: 'guest')
    'int-twitter:search-inbound-channel-adapter'(id:'twitter-search', 'query':'cheese', channel:'searched-tweets')
  }

  poll('poller','default':true, fixedDelay:1000)
  channel('searched-tweets')

  doWithRabbit {
    admin connectionFactory:'connectionFactory'
    template 'rabbitTemplate', connectionFactory:'connectionFactory'
    queue 'tweets'
    fanoutExchange 'twitter', bindings:[[queue:'tweets']]
  }

  messageFlow {
    amqpSend amqpTemplate:'rabbitTemplate', exchangeName:'twitter', channel:'searched-tweets'
    amqpListen queueNames:'tweets', connectionFactory:'connectionFactory'
    handle { msg->
      def payload = "text: $msg.text"
      eb.publish('vertx.tweets', payload) 
    }
  }
}


