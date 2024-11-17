package com.example.th.Database;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class Converter {
    private static final Gson gson = new Gson();

    @TypeConverter
    public static String fromList(List<String> list) {
        return gson.toJson(list);
    }

    @TypeConverter
    public static List<String> toList(String json) {
        Type listType = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(json, listType);
    }
}

