# logmatic-android
*Link to the [Logmatic.io documentation](http://doc.logmatic.io)*

Client-side Android logging library for Logmatic.io.
Currently in development. Send your feedback, issues at support@logmatic.io or tweet to @gpolaert and @logmatic_.

## Features

- Use the library as a logger. Everything is forwarded to Logmatic.io as JSON documents.
- Custom fields, meta and extra attributes
- Track real client IP address and user-agent (currently in development)
- Automatic bulk

## Quick Start

### Load and initialize logger

android-log is a pure android-lib. Until, we release it to jCenter repository you need to follow these steps.

First, edit the `build.gradle` file of your project and add the Logmatic bintray repository.
```gradle
allprojects {
    repositories {
        jcenter()
        maven {
            url  "http://dl.bintray.com/logmatic/maven"
        }
    }
}
```

Then, you have to just add the android-log dependency as shown below. This must be done in the
`build.gradle` file of your module.

```gradle
dependencies {
    ...
    compile 'io.logmatic:android-log:0.1'
    ...
}
```

Finally, you have to initialize a logger in your MainActivity. We recommend to do it during the creation step.
Here is an example.

```java
...


import io.logmatic.android.Logger;
import io.logmatic.android.LoggerBuilder;

public class MainActivity extends AppCompatActivity {

    private Logger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // init a new instance using your APIKey
        logger = new LoggerBuilder()
                .init("YOUR-API-KEY")
                .build();

        ...

        // log a event
        logger.i("This is a first info event from an android device");
        logger.d("Another debug event with one context to debug. The context will be parsed as Json", anEntityObjectForInstance);


        ...
    }
```

The `LoggerRegistry` class helps you to reuse the logger in another activity or class.

```java


        // Somewhere in your project
        private Logger logger = LoggerRegistry.getDefaultLogger(); // use it if your never set the logger name

```
### Log some events

#### Fire your own events

To log some events, you simply call logger's methods.

 * `logger.v`: verbose level/severity
 * `logger.d`: debug level/severity
 * `logger.i`: informational level/severity
 * `logger.w`: warning level/severity
 * `logger.e`: error level/severity

Each method could be called with a simple message, just a piece of text.

#### Disable legacy logging
By default, the lib log all events to the Logact leggay logger. You can disable it during the build.
```java
        // init a new instance using your APIKey
        logger = new LoggerBuilder()
                .init("YOUR-API-KEY")
                .disableLegacyLogging()
                .build();
```
logger.i("This is a first info event from an android device");
```

Or a context. In the case the message is still a piece of text, the context is an object that you want to associate to the message.


```java
logger.d("Another debug event with one context to debug. The context will be parsed as Json", anEntityObjectForInstance);

```


To clearly explain what happens here, in this exact situation where everything is configured as above the API send the following JSON content to *Logmatic.io*'s API.:

```json
// First event
{
    "datetime": "2016-05-03T17:43:17.401+0200",
    "appname": "android-log",
    "level": "INFO",
    "message": "This is a first info event from an android device"
}

// Second event with a context
{
    "datetime": "2016-05-03T17:43:18.311+0200",
    "appname": "android-log",
    "level": "DEBUG",
    "message": "Another debug event with one context to debug. The context will be parsed as Json",
    "context": {

        "attribute_string": "1",
        "attribute_float": 1.0,
        "attribute_int": 1.0,
        "attribute_list": [ "1", "2" ]
        ...

    }
}

```
You can add global fields and metas to all fired events by just calling the `addField` method directly from the logger.

```java
logger.addField("some_field",some_value);

```


### Others features
#### Disable legacy logging
By default, the lib logs all events to the Logact leggay logger. You can disable it during the build.
```java
        // init a new instance using your APIKey
        logger = new LoggerBuilder()
                .init("YOUR-API-KEY")
                .disableLegacyLogging()
                .build();
```
#### Disable auto-timestamping
By default, the lib adds the field `datetime` to all events. For testing purpose, you might be want to disable it.
```java
        // init a new instance using your APIKey
        logger = new LoggerBuilder()
                .init("YOUR-API-KEY")
                .disableTimestamping()
                .build();
```