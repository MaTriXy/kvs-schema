package com.example.android.kvs;

import android.content.Context;

import com.rejasupotaro.android.kvs.PrefSchema;
import com.rejaupotaro.android.kvs.annotations.Key;
import com.rejaupotaro.android.kvs.annotations.Table;

@Table(name = "example")
public abstract class ExampleSchema extends PrefSchema {
    @Key("user_id") int userId = -1;
    @Key("user_name") String userName = "guest";

    public static Example create(Context context) {
        return new Example(context);
    }
}
