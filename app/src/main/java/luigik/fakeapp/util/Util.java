package luigik.fakeapp.util;

import java.util.HashMap;
import java.util.Map;


public abstract class Util {


    public static class MapBuilder<K, V> {

        private HashMap<K, V> map;

        public MapBuilder() {
            map = new HashMap<>();
        }

        public MapBuilder put(K key, V value) {
            map.put(key, value);
            return this;
        }

        public Map<K, V> build() {
            return map;
        }

    }

    public static String formatPhpString(String string) {
        return string.replace("%20"," ");
    }
}
