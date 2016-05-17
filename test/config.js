const TCP_BROKER_URI = process.env.TCP_BROKER_URI || "tcp://localhost"
const HTTP_BROKER_URI = process.env.HTTP_BROKER_URI || "http://localhost:8080"

const QUERY_URL = `${HTTP_BROKER_URI}/query`
const JSON_URL = `${HTTP_BROKER_URI}/json`

module.exports = {
  TCP_BROKER_URI,
  QUERY_URL,
  JSON_URL
}