package dev.sudip.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hubspot.algebra.Result;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.print.DocFlavor;

public abstract class CallableEndpoint<ResponseOk> {
    protected final String endpointPrefix;
    protected final String postDynamicPath;

    public CallableEndpoint(String endpointPrefix, String postDynamicPath) {
        this.endpointPrefix = endpointPrefix;
        this.postDynamicPath = postDynamicPath;
    }

    private String makeUrlString(InstanceUrl instance, String dynamicPath, HashMap<String, String> queries) {
        var newURL = new StringBuilder().append(instance.url()).append(endpointPrefix);
        if (dynamicPath != null && !dynamicPath.isBlank()) {
            newURL.append(dynamicPath);
            newURL.append(postDynamicPath);
        }

        for (var entry : queries.entrySet()) {
              // newURL += "&" + entry.getKey() + "=" + entry.getValue();
            newURL.append(entry.getKey());
            if (entry.getValue() != null) {
                newURL.append('=').append(entry.getValue());
            }
            newURL.append('&');
        }

        return newURL.toString();
    }

    protected <ClientError extends Throwable> Result<ResponseOk, ResponseError<ClientError>> callEndpoint(
            InstanceUrl instance,
            String dynamicPath,
            HashMap<String, String> queries,
            HttpClient httpClient
    ) {
        var endpointUrl = this.makeUrlString(instance, dynamicPath, queries);
        try {
            var request = HttpRequest.newBuilder().GET().uri(URI.create(endpointUrl)).build();
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray()).body();

            // This might throw ClientError, we propagate
            ResponseOk okResponse = new ObjectMapper().readValue(response, new TypeReference<ResponseOk>() {});
            return Result.ok(okResponse);
        } catch (MalformedURLException e) {
            return Result.err(new ResponseError<>(ErrorKind.InvalidUrl, e.getMessage()));
        } catch (IOException e) {
            return Result.err(new ResponseError<>(ErrorKind.DeserializationError, e.getMessage()));
        } catch (InterruptedException e) {
            return Result.err(new ResponseError<>(ErrorKind.Interrupted, e.getMessage()));
        }

    }

    public static enum ErrorKind {
        ClientError,
        InvalidUrl,
        DeserializationError,
        Interrupted,
    }

    public static interface HttpExecuteGet<Err extends Throwable> {
        public byte[] get(URI url) throws Err;
    }

    public static class ResponseError<ClientError> {
        public ErrorKind kind;
        public String errorMessage;

        public ResponseError(ErrorKind kind) {
            this.kind = kind;
            this.errorMessage = kind.name();
        }

        public ResponseError(ErrorKind kind, String errorMessage) {
            this.kind = kind;
            this.errorMessage = errorMessage;
        }

        @Override
        public String toString() {
            return "ResponseError{" +
                    "kind=" + kind +
                    ", errorMessage='" + errorMessage + '\'' +
                    '}';
        }
    }
}