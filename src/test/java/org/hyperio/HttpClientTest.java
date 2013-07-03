package org.hyperio;

import org.hyperio.core.Callback;
import org.hyperio.event.Event;
import org.hyperio.http.HttpClient;
import org.hyperio.http.HttpGetRequest;
import org.junit.Test;

public class HttpClientTest
{

    @Test
    public void httpGetTest()
    {
        HttpClient client = new HttpClient();
        HttpGetRequest request = client.createGetRequest("http://google.com");
        request.on("connect", new Callback<Event>(){
            @Override
            public void call(Event data)
            {
            }
        });
        request.execute();
    }
}
