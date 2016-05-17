package de.artcom.hivemq_http_api_plugin.query;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.collect.Lists;

import javax.inject.Inject;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;

public abstract class Resource {
    final ObjectMapper objectMapper;
    private final QueryProcessor queryProcessor;

    @Inject
    Resource(QueryProcessor queryProcessor) {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new GuavaModule());

        this.queryProcessor = queryProcessor;
    }

    @OPTIONS
    public static Response options() {
        return Response.ok("")
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Methods", "POST")
                .build();
    }

    @POST
    public Response post(String body) {
        IQueryResult result = QueryResultError.queryFormat();

        try {
            JsonNode json = objectMapper.readTree(body);

            if (json.isObject()) {
                Query query = parseQuery(json);
                result = singleQuery(query);
            } else if (json.isArray()) {
                List<Query> queries = Lists.transform(Lists.newArrayList(json), this::parseQuery);
                result = batchQuery(queries);
            }
        } catch (IOException | IllegalArgumentException ignored) {
        }

        return createResponse(getStatus(result), getPayload(result));
    }

    abstract Query parseQuery(JsonNode json);

    abstract int getStatus(IQueryResult result);

    abstract String getPayload(IQueryResult result);

    IQueryResult singleQuery(Query query) {
        return queryProcessor.process(query);
    }

    IQueryResult batchQuery(List<Query> queries) {
        List<IQueryResult> results = Lists.transform(queries, this::singleQuery);
        return new QueryResultList(results);
    }

    static Response createResponse(int status, String payload) {
        return Response
                .status(status)
                .entity(payload)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Methods", "POST")
                .build();
    }
}