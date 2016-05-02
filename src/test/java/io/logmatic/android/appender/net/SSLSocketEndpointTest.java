package io.logmatic.android.appender.net;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.io.OutputStream;

import javax.net.ssl.SSLSocket;

import io.logmatic.android.endpoint.SecureTCPEndpoint;

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

        String expected = "fake data as bytes";

        // mock stuff
        SSLSocket fakeSocket = mock(SSLSocket.class);
        OutputStream fakeStream = mock(OutputStream.class);
        when(fakeSocket.getOutputStream()).thenReturn(fakeStream);

        SecureTCPEndpoint endpoint = new SecureTCPEndpoint(fakeSocket);

        // GIVEN a not connected socket
        when(fakeSocket.isConnected()).thenReturn(true);

        // WHEN data is sent to the socket
        boolean isSent = endpoint.send(expected);

        // THEN the manager have to just send data
        verify(fakeStream).write(expected.getBytes());
        assertThat(isSent, is(true));


    }


    @Test
    public void shouldReturnFailedOnLostConnection() throws IOException {

        String expected = "fake data as bytes";

        // mock stuff
        SSLSocket fakeSocket = mock(SSLSocket.class);
        OutputStream fakeStream = mock(OutputStream.class);
        when(fakeSocket.getOutputStream()).thenReturn(fakeStream);

        SecureTCPEndpoint endpoint = spy(new SecureTCPEndpoint(fakeSocket));


        // GIVEN a disconnected manager
        when(fakeSocket.isConnected()).thenReturn(false);

        // WHEN data is sent to the socket
        boolean isSent = endpoint.send(expected);
        assertThat(isSent, is(false));


    }


}