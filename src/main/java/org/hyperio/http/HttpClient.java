package org.hyperio.http;

import org.hyperio.event.EventEmitter;

public class HttpClient extends EventEmitter
{
    public HttpGetRequest createGetRequest(String url)
    {
        return new HttpGetRequest(url, this);
    }
}
