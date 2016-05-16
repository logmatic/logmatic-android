# logmatic-android
*Link to the [Logmatic.io documentation](http://doc.logmatic.io/docs/android)*

Client-side Android logging library for Logmatic.io.

## Features

- Use the library as a logger. Everything is forwarded to Logmatic.io as JSON documents.
- Custom fields, meta and extra attributes
- Automatic bulk
- Network and battery life optimized

## Quick Start

### Load and initialize logger
#### Install via jCenter

**Logmatic-android** is a pure android-lib. We release it to the jCenter repository,
so you just have to add the library as dependency as shown below. Edit the
`build.gradle` file of your module.

```gradle
dependencies {
    ...
    compile 'io.logmatic:android-log:0.1'
    ...
}
```
### Initialization
You can start the logger whenever you want. The `onCreate` method of the main activity is probably
a good choice in order to use logging as soon as possible.

Set your API key, add optional info if needed before build the logger.

Here is an example

```java
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
                .withName("my-application-name")
                .build();

        // OPTIONAL
        // Add extra fields for all events fired to Logmatic.io
        logger.addField("string","some piece of text");
        logger.addField("int",1.0);
        logger.addField("float",1.337);
        logger.addField("a_date",new Date());

```

### Log some events

#### Fire your own events

To log some events, you simply call logger's methods.

```java

        // log an event (info)
        logger.i("This is a first info event from an android device");

```

To clearly explain what happens here, in this exact situation where everything is configured as above the Logger send the following JSON content to Logmatic.io's API.:
```
{
    "date": "2016-05-16T13:46:06.338Z",
    "appname": "my-application-name",
    "severity": "INFO",
    "message": "This is a first info event from an android device",

    // OPTIONAL
    "string": "some piece of text",
    "int": 1,
    "float": 1.337,
    "a_date": "2016-05-16T13:46:05.980Z"
}
```
You have the possibility to attach a context to your event. The context object must be serialisable to
a JsonObject. The simplest way to use it, is to attach a Map as is illustrated below:

```java

        Map<String, Object> context = new HashMap();
        context.put("session", getSessionId());
        context.put("emails", Arrays.toList("1@foo.com", "2@bar.io", "3@logmatic.io"));
        logger.d("Another event with a context.", context);
.
    }
```
The output will be:

```json
{
    "date": "2016-05-16T13:46:06.338Z",
    "appname": "my-application-name",
    "severity": "DEBUG",
    "message": "Another event with a context.",
    "emails": ["1@foo.com", "2@bar.io", "3@logmatic.io"],
    "session": "2cf23b5b6a54614c"
}
```
#### Retrieve your logger anywhere

The `LoggerRegistry` class helps you to reuse the logger in another activity or class.

```java

    // Somewhere in your project

    // use it if your never set the logger name
    private Logger logger = LoggerRegistry.getDefaultLogger();

    // or that if you set the name
    private Logger logger = LoggerRegistry.getLogger("my-application-name");

```



## API

You have to build your logger with the `LoggerBuilder` helper class.
At minimum, you have to set your API Key.

```java
        // init a new instance using your APIKey
        logger = new LoggerBuilder()
                .init("YOUR-API-KEY")
                .build();
```

By default, the lib logs all events to the Logcat leggay logger. You can disable it during the build.
```java
        // init a new instance using your APIKey
        logger = new LoggerBuilder()
                .init("YOUR-API-KEY")
                .disableLegacyLogging()
                .build();
```

It's the same for the timestamping', our lib adds the field `date` to all events.
However, you might be want to disable it for your tests.
```java
        // init a new instance using your APIKey
        logger = new LoggerBuilder()
                .init("YOUR-API-KEY")
                .disableTimestamping()
                .build();
```

As reminder, here are  the exhautivie method list offered by the **Logmatic-android** lib.
### LoggerBuilder

| Method        | Description           |  Example  |
| ------------- | ------------- |  ----- |
| init(api_key) | Initialize the logger with a Logmatic API Key |  |
| withName() | Set the logger and the application name. This method customize the extra field `appname`. By default, `appname` is `android`  | `"appname": "my-application-name"`|
| disableTimestamping() | Remove the `date` field from all events | |
| disableLegacyLogging() | By default, the lib uses `Logcat` as logger, if the method is called, only logs to Logmatic.io are kept | |
| addField(key, value) | Add and extra field to all events. The value's type will be kept| `"key": "value"`|
| build()| Build a new logger instance with all options set | |

### Logger

| Method        | Description           |  Example  |
| ------------- | ------------- |  ----- |
| v(message) | Log as verbose with a piece of text |`logger.v("my message")`|
| v(message, object) | Log as verbose with a piece of text and a custom context |`logger.v("my message", context)`|
| d(message) | Log as debug with a piece of text |`logger.d("my message")`|
| d(message, object) | Log as debug with a piece of text and a custom context |`logger.d("my message", context)`|
| i(message) | Log as info with a piece of text |`logger.i("my message")`|
| i(message, object) | Log as info with a piece of text and a custom context |`logger.i("my message", context)`|
| w(message) | Log as warning with a piece of text |`logger.w("my message")`|
| w(message, object) | Log as warning with a piece of text and a custom context |`logger.w("my message", context)`|
| e(message) | Log as error with a piece of text |`logger.e("my message")`|
| e(message, object) | Log as error with a piece of text and a custom context |`logger.e("my message", context)`|
| wtf(message) | Log as error with a piece of text |`logger.wtf("my message")`|
| wtf(message, object) | Log as error with a piece of text and a custom context |`logger.wtf("my message", context)`|
| addField(key, value) | add and extra field to all events. The value's type will be kept| `logger.addField("key", 123)`|
=======
Send log entries to Logmatic.io directly from your Android apps
