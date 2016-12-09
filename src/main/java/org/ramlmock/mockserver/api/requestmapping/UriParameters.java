package org.ramlmock.mockserver.api.requestmapping;

import java.util.HashMap;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Created by arielsegura on 12/8/16.
 */
public class UriParameters{
    private HashMap<String, String> content = new HashMap<>();

    public UriParameters add(String key, String value){
        content.put(key, value);
        return this;
    }

    public Set<String> keys(){
        return content.keySet();
    }

    public String get(String key){
        return content.get(key);
    }

    public void forEach(BiConsumer<String, String> function){
        content.forEach(function);
    }
}
