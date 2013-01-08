load('vertx.js')

var config = {
}

vertx.deployModule('demo.si-dsl-amqp-twitter-v1.0', config, 1, function(id) {
  console.log('deployed module: demo.si-dsl-amqp-twitter-v1.0 with id:' + id)
})

//vertx.deployVerticle('App.groovy', config, 1, function(id) {
//  console.log('deployed verticle: demo.si-dsl with id:' + id)
//})
