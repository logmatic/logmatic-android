package io.logmatic.asynclogger.net;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.io.OutputStream;

import javax.net.ssl.SSLSocket;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SSLSocketEndpointTest {


    @Test
    public void shouldSendData() throws IOException {

        byte[] expected = "fake data as bytes".getBytes();

        // mock stuff
        SSLSocket fakeSocket = mock(SSLSocket.class);
        OutputStream fakeStream = mock(OutputStream.class);
        when(fakeSocket.getOutputStream()).thenReturn(fakeStream);

        SSLSocketEndpoint endpoint = new SSLSocketEndpoint(fakeSocket);

        // GIVEN a not connected socket
        when(fakeSocket.isConnected()).thenReturn(true);

        // WHEN data is sent to the socket
        boolean isSent = endpoint.send(expected);

        // THEN the endpoint have to just send data
        verify(fakeStream).write(expected);
        verify(fakeStream).flush();
        assertThat(isSent, is(true));


    }


    @Test
    public void shouldReturnFailedOnLostConnection() throws IOException {

        byte[] expected = "fake data as bytes".getBytes();

        // mock stuff
        SSLSocket fakeSocket = mock(SSLSocket.class);
        OutputStream fakeStream = mock(OutputStream.class);
        when(fakeSocket.getOutputStream()).thenReturn(fakeStream);

        SSLSocketEndpoint endpoint = spy(new SSLSocketEndpoint(fakeSocket));


        // GIVEN a disconnected endpoint
        when(fakeSocket.isConnected()).thenReturn(false);

        // WHEN data is sent to the socket
        boolean isSent = endpoint.send(expected);
        assertThat(isSent, is(false));


    }


    @Test
    public void shouldCloseConnectionBeforeStartNewOne() throws IOException {

        // mock stuff
        SSLSocket fakeSocket = mock(SSLSocket.class);
        OutputStream fakeStream = mock(OutputStream.class);
        when(fakeSocket.getOutputStream()).thenReturn(fakeStream);


        // GIVEN a disconnected socket, then a connected socket
        when(fakeSocket.isConnected()).thenReturn(false);

        // WHEN a connection is attempted
        SSLSocketEndpoint endpoint = spy(new SSLSocketEndpoint(fakeSocket));
        boolean isConnected = endpoint.tryConnnection(); // force for the tests

        // THEN ...
        verify(endpoint).isConnected(); // check the connection, here not connected
        verify(endpoint).closeConnection(); // the previous connection is closed
        verify(endpoint).openConnection(); // a new connection is attempted
        assertThat(isConnected, is(true)); // the connection is opened


    }



}