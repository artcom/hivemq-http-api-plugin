package de.artcom.hivemq_http_api_plugin.query;

class Query implements IQuery {
    public String topic;
    public int depth;
    public boolean flatten;

    @Override
    public String getTopic() {
        return topic;
    }

    @Override
    public int getDepth() {
        return depth;
    }

    @Override
    public boolean getFlatten() {
        return flatten;
    }
}
