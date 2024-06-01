package dev.sudip;

import dev.sudip.client.InstanceUrl;
import dev.sudip.endpoints.ServerStatus;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;

public class Main {
    public static void main(String[] args) throws Throwable {
        InstanceUrl instance = new InstanceUrl(new URI("https://vid.puffyan.us/api/v1").toURL());

        HttpClient client = HttpClient.newHttpClient();

        var serverStatusEndpoint = new ServerStatus(client);
        var serverStatus = serverStatusEndpoint.getServerStatus(instance);
        System.out.println(serverStatus);
    }
}