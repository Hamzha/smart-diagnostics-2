package model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public abstract class Extensible extends Message {
    private Map<String, Object> attributes = new LinkedHashMap();

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(Map<String, Object> map) {
        this.attributes = map;
    }

    public void set(String str, boolean z) {
        this.attributes.put(str, Boolean.valueOf(z));
    }

    public void set(String str, int i) {
        this.attributes.put(str, Integer.valueOf(i));
    }

    public void set(String str, long j) {
        this.attributes.put(str, Long.valueOf(j));
    }

    public void set(String str, double d) {
        this.attributes.put(str, Double.valueOf(d));
    }

    public void set(String str, String str2) {
        if (str2 != null && !str2.isEmpty()) {
            this.attributes.put(str, str2);
        }
    }

    public void add(Entry<String, Object> entry) {
        if (entry != null && entry.getValue() != null) {
            this.attributes.put(entry.getKey(), entry.getValue());
        }
    }
}
