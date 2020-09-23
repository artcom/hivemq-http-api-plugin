const mqtt = require("mqtt")
const Promise = require("bluebird")

const config = require("./config")

function connectMqttClient(done) {
  this.client = Promise.promisifyAll(mqtt.connect(config.TCP_BROKER_URI))
  this.client.on("connect", () => done())
}

function preparePublish(prefix) {
  return function() {
    this.testTopic = `${prefix}/hivemq-api-${Date.now()}`
    this.publishedTopics = new Set()

    this.publish = (data) => {
      const topics = Object.keys(data)

      topics.forEach((topic) => this.publishedTopics.add(topic))

      return Promise.all(topics.map((topic) =>
        this.client.publishAsync(topic, data[topic], { retain: true, qos: 2 })
      ))
    }
  }
}

function unpublishTestData() {
  return Promise.all(Array.from(this.publishedTopics).map((topic) =>
    this.client.publishAsync(topic, null, { retain: true, qos: 2 })
  ))
}

function disconnectMqttClient() {
  this.client.end()
}

module.exports = {
  connectMqttClient,
  preparePublish,
  unpublishTestData,
  disconnectMqttClient
}