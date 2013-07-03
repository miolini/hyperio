package org.hyperio.http;


public class HttpResponse extends HttpMessage
{
    private HttpRequest request;

    public HttpResponse(HttpRequest request)
    {
        this.request = request;
    }
}
