package engine.SVariable;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class SVariablesMap {

    int y;
    Int2IntOpenHashMap xVariables;
    Int2IntOpenHashMap zVariables;

    public SVariablesMap(){
        this.y = 0;
        this.xVariables = new Int2IntOpenHashMap();
        this.zVariables = new Int2IntOpenHashMap();
    }

    public SVariablesMap(SVariablesMap other){
        this.y = other.y;
        this.xVariables = new Int2IntOpenHashMap(other.xVariables);
        this.zVariables = new Int2IntOpenHashMap(other.zVariables);
    }

    public int get(SVariable key) {
        switch (key.getType()) {
            case 'y':
                return this.y;
            case 'x': {
                int num = key.getNumber();
                if (!this.xVariables.containsKey(num)) {
                    this.xVariables.put(num, 0);  // insert 0 if absent
                }
                return this.xVariables.get(num);
            }
            case 'z': {
                int num = key.getNumber();
                if (!this.zVariables.containsKey(num)) {
                    this.zVariables.put(num, 0);  // insert 0 if absent
                }
                return this.zVariables.get(num);
            }
            default:
                return 0;
        }
    }

    public void put(SVariable key, int value){
        switch (key.getType()) {
            case 'y' -> this.y = value;
            case 'x' -> this.xVariables.put(key.getNumber(), value);
            case 'z' -> this.zVariables.put(key.getNumber(), value);
        }
    }

    public void putAll(HashMap<String, Integer> map){
        for (HashMap.Entry<String, Integer> entry : map.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            SVariable sv = new SVariable(key);
            put(sv, value);
        }
    }

    public LinkedHashMap<String, Integer> getOrderedMap(){
        LinkedHashMap<String, Integer> result = new LinkedHashMap<>();

        // Always put "y" (default to 0 if missing)
        result.put("y", this.y);

        // Handle all x{i} keys
        xVariables.keySet().stream()
                .sorted() // sort integers ascending
                .forEach(k -> result.put("z" + k, xVariables.get(k)));

        // Handle all z{j} keys
        zVariables.keySet().stream()
                .sorted() // sort integers ascending
                .forEach(k -> result.put("z" + k, zVariables.get(k)));

        return result;
    }


}
