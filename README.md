KVS Schema
==========

KVS Schema is a library to manage key-value data for Android.
This library generates methods from annotated fields in compile time.
For example, when a schema class has `@Key("user_id") String userId` like below,

```java
@Table(name = "example")
public class ExamplePrefsSchema {
    @Key("user_id") String userId;
}
```

KVS Schema generates accessor methods below.

- `ExamplePrefs#getUserId`
- `ExamplePrefs#putUserId`
- `ExamplePrefs#removeUserId`
- `ExamplePrefs#hasUserId`

Values are stored on SharedPreferences through generated class.

How to use
----------

### Create Schema

Class name should be `*Schema`.

```java
@Table(name = "example")
public class ExamplePrefsSchema {
    @Key("user_id") int userId;
    @Key("user_name") String userName;
}
```

### Read and Write

`put*`, `get*`, `has*` and `remove*` methods will be generated in compile time.

```java
ExamplePrefs prefs = ExamplePrefs.get(context);
prefs.putUserId(123);
prefs.putUserName("Jack");
prefs.hasUserName(); // => true
prefs.getUserName(); // => Jack
prefs.removeUserName();
prefs.hasUserName(); // => false
```

Initialize method (ExamplePrefs.get) is also generated. It provides singleton instance of Prefs.
You can change initialize method of Prefs by specifying builder class.

```java
@Table(name = "example", builder = ExamplePrefsBuilder.class)
```

See: https://github.com/rejasupotaro/kvs-schema/pull/12

### Supported types

kvs-schema supports these types for now.

- boolean
- String
- float
- int
- long
- String set

### Saved XML

Table's name becomes SharedPreferences' name.

```xml
root@android:/data/data/com.example.android.kvs/shared_prefs # cat example.xml
<?xml version='1.0' encoding='utf-8' standalone='yes' ?>
<map>
<string name="user_id">123</string>
</map>
```

### Installation

Add dependencies to your build.gradle.

```groovy
apt 'com.rejasupotaro:kvs-schema-compiler:4.1.0'
compile 'com.rejasupotaro:kvs-schema:4.1.0'
```

Migration
----------

Even if you have already used SharedPreferences directly, migration is easy. KVS Schema simply maps the structure of SharedPreferences.

For example, if you are using default SharedPreferences like below,

```java
prefs = PreferenceManager.getDefaultSharedPreferences(this);
Editor editor = prefs.edit();
editor.putString("user_id", "1");
editor.putString("user_name", "rejasupotaro");
editor.apply();
```

your data is saved on `path/to/app/shared_prefs/package_name_preferences.xml`. The schema becomes below.

```java
@Table("package_name_preferences")
public abstract class ExamplePrefsSchema {
    @Key("user_id") int userId;
    @Key("user_name") String userName;
}
```

See concrete example: https://github.com/konifar/droidkaigi2016/pull/311

In addition, SharedPreferencesInfo may help you to migrate existing app. You can get existing SharedPreferences through `SharedPreferencesInfo.getAllPrefsAsTable`.

```java
List<SharedPreferencesTable> tables = SharedPreferencesInfo.getAll(this);
        for (SharedPreferencesTable table : tables) {
            Log.d("DEBUG", table.toString());
        }
```

You can see what kind of data is saved in your app like below.

```
   name: com.example.android.kvs_preferences
   path: /data/data/com.example.android.kvs/shared_prefs/com.example.android.kvs_preferences.xml
 ╔═══════════╤══════════════╤════════╗
 ║ Key       │ Value        │ Type   ║
 ╠═══════════╪══════════════╪════════╣
 ║ user_name │ rejasupotaro │ String ║
 ╟───────────┼──────────────┼────────╢
 ║ user_id   │ 1            │ String ║
 ╚═══════════╧══════════════╧════════╝
 ```
