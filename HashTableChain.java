// A seperate chaining hash table that uses linked lists.
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class HashTableChain<K, V> implements KWHashMap<K, V> {
    public static class Entry<K, V> {
        private final K key;
        private V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            // Returns the key
            return key;
        }

        public V getValue() {
            // Returns the value
            return value;
        }

        public V setValue(V val) {
            // Changes the value and returns the old value
            V old_value = value;
            value = val;

            return old_value;
        }

        public String toString() {
            // Returns a string of the key, value pair
            return key.toString() + " : " + value.toString(); 
        }
    }

    private LinkedList<Entry<K, V>>[] table;
    private int numKeys, numRehashes; 

    private static final int CAPACITY = 11;
    private static final double LOAD_FACTOR = 15; 

    public HashTableChain() {
        table = new LinkedList[CAPACITY];
        numKeys = 0;
        numRehashes = 0;
    }

    public HashTableChain(int capacity) {
        table = new LinkedList[capacity];
        numKeys = 0;
        numRehashes = 0;
    }

    public V get(Object key) {
        // Returns the value at the specified key
        int index = key.hashCode() % table.length; 

        if (index < 0) {
            index += table.length;
        }

        if (table[index] == null) {
            return null;
        } 

        for (Entry<K, V>next:table[index]) { 
            if (next.getKey().equals(key)) {
                return next.getValue();
            }
        }

        return null;
    }

    public V put(K key, V value) {
        // Places a value in a specified location
        int index = key.hashCode() % table.length;

        if (index < 0) {
            index += table.length;
        }
        
        if (table[index] == null) {
            table[index] = new LinkedList<>();
        }

        for (Entry<K, V> nextItem:table[index]) {
            if (nextItem.getKey().equals(key)) {
                V oldVal = nextItem.getValue();
                nextItem.setValue(value);
                return oldVal;
            }
        }

        table[index].addFirst(new Entry<>(key, value));
        numKeys++;
        
        if (numKeys > (LOAD_FACTOR * table.length)) {
            rehash();
        }
        
        return null;
    }

    private void rehash() {
        // Rehashes the hash table
        LinkedList<Entry<K, V>>[] oldTable = table;

        int table_length = 2 * oldTable.length + 1;

        while (!isPrime(table_length)) {
            table_length += 2;
        }

        table = new LinkedList[table_length]; 
        
        numKeys = 0;

        for (int i = 0; i < oldTable.length; i++) {
            if ((oldTable[i] != null)) {
                for (Entry<K, V> nextItem:oldTable[i]) {
                    put(nextItem.getKey(), nextItem.getValue());
                }
            }
        }

        numRehashes++;
    }

    public V remove(Object key) {
        // Removes a value specified by the key
        int index = key.hashCode() % table.length;  

        if (index < 0) {
            index += table.length;
        }

        if (table[index] == null) {
            return null;
        } 

        Iterator<Entry<K,V>> iter = table[index].iterator();

        while (iter.hasNext()) {
            Entry<K, V> current = iter.next();
            if (current.getKey().equals(key)) {
                iter.remove();

                if (table[index].size() == 0) {
                    table[index] = null;
                }

                numKeys--;
                return current.getValue();
            }
        }

        return null;
    }

    public boolean isPrime(int num) {
        // Checks if num is a prime number
        for(int i = 2; i <= Math.sqrt(num); i++) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }

    public int size() {
        // Returns the size of the table
        return numKeys;
    }

    public int rehashes() {
        // Returns the number of time the hash table is rehashed
        return numRehashes;
    }
    public boolean isEmpty() {
        // Returns true if the table is empty
        return numKeys == 0;
    }
}
