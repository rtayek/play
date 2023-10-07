package p;
import java.util.*;
public class LruMap<K,V> extends LinkedHashMap<K,V> {
        /**
     * 
     */
    private static final long serialVersionUID=1L;
    public LruMap() {
                super(defaultMaxSize+1,.75f,true);
                maxSize=defaultMaxSize;
        }
        public LruMap(int arg0) {
                super(arg0+1,.75f, true);
                maxSize=arg0;
        }
        public LruMap(int arg0,float arg1) {
                super(arg0+1,arg1,true);
                maxSize=arg0;
        }
        public LruMap(int arg0,float arg1,boolean arg2) {
                super(arg0+1,arg1,arg2);
                if(!arg2) throw new RuntimeException("you did not construct an lru map!");
                maxSize=arg0;
        }
        public LruMap(Map<K,V> arg0) {
                super(arg0);
                throw new RuntimeException("you did not construct an lru map!");
        }
        public boolean removeEldestEntry(Map.Entry<K,V> eldest) {
                return size()>maxSize;
        }
        public static void main(String[] args) {}
        public final int maxSize;
        public static final int defaultMaxSize=100;
}
