package dev.sudip.endpoints;

import com.hubspot.algebra.Result;
import dev.sudip.client.CallableEndpoint;
import dev.sudip.client.InstanceUrl;
import dev.sudip.endpoints.ServerStatus.Response.ServerStatusResponse;

import java.net.http.HttpClient;
import java.util.HashMap;

public class ServerStatus extends CallableEndpoint<ServerStatusResponse> {
    public static class Response {
        public static class Metadata {
            public int updatedAt;
            public int lastChannelRefreshedAt;
        }

        public static class Playback {
        }

        public static class ServerStatusResponse {
            public String version;
            public Software software;
            public boolean openRegistrations;
            public Usage usage;
            public Metadata metadata;
            public Playback playback;
        }

        public static class Software {
            public String name;
            public String version;
            public String branch;
        }

        public static class Usage {
            public Users users;
        }

        public static class Users {
            public int total;
            public int activeHalfyear;
            public int activeMonth;
        }
    }

    private final HttpClient httpClient;
    public ServerStatus(HttpClient httpClient) {
        super("/stats", "");
        this.httpClient = httpClient;
    }

    public Result<ServerStatusResponse, ResponseError<RuntimeException>> getServerStatus(InstanceUrl instance) {
        return super.callEndpoint(
                instance, null, HashMap.newHashMap(0), httpClient
        );
    }
}