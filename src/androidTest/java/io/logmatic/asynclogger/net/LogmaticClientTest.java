package io.logmatic.asynclogger.net;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.net.Socket;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class LogmaticClientTest { //extends AndroidTestCase {


    @Mock
    SSLSocketEndpoint endpoint;


    final String apiKey = "fake_key";


    @Test
    public void shouldOpenAConnectionToLogmatic() {

        // mock stuff
        when(endpoint.isConnected()).thenReturn(true);

        // GIVEN a connection to Logmatic
        LogmaticClient client = new LogmaticClient(apiKey, endpoint);

        // THEN the endpoint should have received the event
        assertThat(client.isConnected(), is(true));
        assertThat(client.getAPIKey(), is(apiKey));

    }


    @Test
    public void shouldLogAMessage() throws IOException {


        // GIVEN a connection to Logmatic
        LogmaticClient client = new LogmaticClient(apiKey, endpoint);

        // WHEN messages are logged
        client.log("message one");

        //THEN they arrived to the endpoint
        verify(endpoint).send(any(byte[].class));


    }

    @Test
    public void messageShouldBeLogInJson() throws IOException {


        ArgumentCaptor<byte[]> data = ArgumentCaptor.forClass(byte[].class);


        // GIVEN a connection to Logmatic
        LogmaticClient client = new LogmaticClient(apiKey, endpoint);

        // WHEN messages are logged
        client.log("message one");


        //THEN messages must be arrived to the endpoint
        verify(endpoint).send(data.capture());


        String expected = apiKey + " {\"message\":\"message one\"}";
        String output = new String(data.getValue());


        assertThat(output, is(expected));


    }


    @Test
    public void shouldAddMeta() throws IOException {


        ArgumentCaptor<byte[]> data = ArgumentCaptor.forClass(byte[].class);


        // GIVEN a connection to Logmatic
        LogmaticClient client = new LogmaticClient(apiKey, endpoint);

        // WHEN metas are added
        client.addMeta("long", 123L);
        client.addMeta("double", 1.0);
        client.addMeta("string", "string");
        client.addMeta("int", 1);
        client.addMeta("float", 2.0);
        client.addMeta("string", "string");
        client.log("message one");


        //THEN messages must be arrived to the endpoint
        verify(endpoint).send(data.capture());


        String expected = apiKey + " {\"long\":123,\"double\":1.0,\"string\":\"string\",\"int\":1,\"float\":2.0,\"message\":\"message one\"}";
        String output = new String(data.getValue());


        assertThat(output, is(expected));


    }


}