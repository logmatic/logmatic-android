package io.logmatic.asynclogger.appender.net;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.logmatic.asynclogger.Logger;
import io.logmatic.asynclogger.appender.LogmaticAppender;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LogmaticClientTest {



    String apiKey = "fake_key";

    @Mock
    EndpointManager manager;
    LogmaticAppender appender;

    @Before
    public void setUp(){
        appender = new LogmaticAppender(apiKey, manager);

    }


    @Test
    public void shouldLogASimpleMessage() throws IOException {


        // GIVEN a connection to Logmatic
        Logger client = new Logger(apiKey, appender);


        // WHEN messages are logged
        client.d("message one");

        //THEN they arrived to the manager
        verify(manager).write(anyString());


    }

    @Test
    public void messageShouldBeLogInJson() throws IOException {


        ArgumentCaptor<String> data = ArgumentCaptor.forClass(String.class);


        // GIVEN a connection to Logmatic
        Logger client = new Logger(apiKey, appender);
        client.disableTimestamping();

        // WHEN messages are logged
        client.d("message one");


        //THEN messages must be arrived to the manager
        verify(manager).write(data.capture());


        String expected = apiKey + " {\"message\":\"message one\"}";

        assertThat(data.getValue(), is(expected));


    }


    @Test
    public void shouldAddMeta() throws IOException {


        ArgumentCaptor<String> data = ArgumentCaptor.forClass(String.class);


        // GIVEN a connection to Logmatic
        Logger client = new Logger(apiKey, appender);
        client.disableTimestamping();

        // WHEN metas are added
        client.addField("long", 123L);
        client.addField("double", 1.0);
        client.addField("string", "string");
        client.addField("int", 1);
        client.addField("float", 2.0);
        client.addField("string", "string");
        client.addField("date", new Date(1460041488000L));
        client.d("message one");


        //THEN messages must be arrived to the manager
        verify(manager).write(data.capture());


        String expected = apiKey + " {\"long\":123,\"double\":1.0,\"string\":\"string\",\"int\":1,\"float\":2.0,\"date\":1460041488000,\"message\":\"message one\"}";


        assertThat(data.getValue(), is(expected));


    }


    @Test
    public void souldLogAJavaObjectAsMessage() {


        ArgumentCaptor<String> data = ArgumentCaptor.forClass(String.class);


        // GIVEN a connection to Logmatic
        Logger client = new Logger(apiKey, appender);
        client.disableTimestamping();

        // WHEN messages are logged
        client.d(new AnonymousObject().getAString());


        //THEN messages must be arrived to the manager
        verify(manager).write(data.capture());

        String expected = apiKey + " {\"message\":{\"a_string\":\"string\",\"a_double\":1.0,\"an_array_of_strings\":[\"string_one\",\"string_two\"]}}";

        assertThat(data.getValue(), is(expected));

    }

    private class AnonymousObject {

        String aString = "string";
        Double aDouble = 1.0;
        List<String> anArrayOfStrings = Arrays.asList("string_one", "string_two");

        public String getAString() {
            return aString;
        }

        public Double getADouble() {
            return aDouble;
        }

        public List<String> getAnArrayOfStrings() {
            return anArrayOfStrings;
        }
    }

}