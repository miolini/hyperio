package org.hyperio.http;


public class HttpGetRequest extends HttpRequest
{
    private final String url;
    private HttpClient httpClient;

    public HttpGetRequest(String url, HttpClient httpClient)
    {
        this.url = url;
        this.httpClient = httpClient;
    }

    public HttpResponse execute()
    {
        HttpResponse response = new HttpResponse(this);
        return response;
    }
}
