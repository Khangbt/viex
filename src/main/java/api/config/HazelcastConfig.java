package api.config;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import java.util.Objects;

public class HazelcastConfig {

    private static final HazelcastInstance hz;
    public static IMap<String, Object> map;
    public static HazelcastConfig instance;


    public static HazelcastConfig getInstance() {
        if (instance == null) {
            instance = new HazelcastConfig();
        }
        return instance;
    }

    static {
        hz = HazelcastClient.newHazelcastClient();
        map = hz.getMap("user");
    }

    public Object getObject(String key) {
        return map.get(key);
    }


    public void saveObject(Object object, String key) {
        if (Objects.isNull(object)) {
            return;
        }
        map.set(key, object);
    }

    public void remote(String key) {
        if (Objects.nonNull(key)) {
            map.remove(key);
        }
    }

}
