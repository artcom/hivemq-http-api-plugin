package de.artcom.hivemq_http_api_plugin.query.exceptions;

public class LeadingSlashException extends QueryException {
    public LeadingSlashException(String topic) {
        super(topic);
    }
}
