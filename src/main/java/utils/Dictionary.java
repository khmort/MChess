package utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

public class Dictionary<K, V> implements Cloneable, Serializable {

	public Dictionary() {
		keys = new ArrayList<>();
		values = new ArrayList<>();
	}

	public Dictionary(int size) {
		keys = new ArrayList<>(size);
		values = new ArrayList<>(size);
	}

	public void add(int index, K key, V val) {
		keys.add(index, key);
		values.add(index, val);
	}

	public void add(K key, V val) {
		keys.add(key);
		values.add(val);
	}

	public void addAll(Dictionary<K, V> dict) {
		keys.addAll(dict.getKeys());
		values.addAll(dict.getValues());
	}

	public void remove(int index) {
		keys.remove(index);
		values.remove(index);
	}

	public void removeByKey(K key) {
		int index = keys.indexOf(key);
		if (index != -1) {
			keys.remove(index);
			values.remove(index);
		}
	}

	public void removeByValue(V val) {
		int index = values.indexOf(val);
		if (index != -1) {
			keys.remove(index);
			values.remove(index);
		}
	}

	public int size() {
		if (keys.size() == values.size()) {
			return keys.size();
		}
		return -1;
	}

	public void setKeys(ArrayList<K> keys) {
		this.keys = keys;
	}

	public void setKey(int index, K key) {
		keys.set(index, key);
	}

	public void setValues(ArrayList<V> values) {
		this.values = values;
	}

	public void setValue(int index, V value) {
		values.set(index, value);
	}

	public ArrayList<K> getKeys() {
		return keys;
	}

	public K getKey(int index) {
		return keys.get(index);
	}

	public Object getKeyByValue(V value, Object orElse) {
		int index = values.indexOf(value);
		if (index == -1) {
			return orElse;
		}
		return keys.get(index);
	}

	public Object getKeyByValue(V value) {
		return this.getKeyByValue(value, null);
	}

	public ArrayList<V> getValues() {
		return values;
	}

	public V getValue(int index) {
		return values.get(index);
	}

	public Object getValueByKey(K key, Object orElse) {
		int index = keys.indexOf(key);
		if (index == -1) {
			return orElse;
		}
		return values.get(index);
	}

	public Object getValueByKey(K key) {
		return this.getValueByKey(key, null);
	}

	public Dictionary<K, V> subdict(int from, int to) {
		Dictionary<K, V> subdict = new Dictionary<>(to - from);
		for (int i = from; i < to; i++) {
			subdict.add(getKey(i), getValue(i));
		}
		return subdict;
	}

	public K getLastKey() {
		return keys.get(keys.size() - 1);
	}

	public V getLastValue() {
		return values.get(values.size() - 1);
	}

	public void setValue(V val, K key) {
		values.set(keys.indexOf(key), val);
	}

	public void setKey(K key, V val) {
		keys.set(values.indexOf(val), key);
	}

	@SuppressWarnings("unchecked")
	public Dictionary<V, K> getReverse() {
		Dictionary<V, K> dict = new Dictionary<>();
		dict.setKeys((ArrayList<V>) values.clone());
		dict.setValues((ArrayList<K>) keys.clone());
		return dict;
	}

	public void clear() {
		keys.clear();
		values.clear();
	}

	public boolean isEmpty() {
		return keys.isEmpty() && values.isEmpty();
	}

	public void sortByKeys(Comparator<K> comp) {
		ArrayList<Entry<K, V>> ents = new ArrayList<>(size());
		for (int i = 0; i < size(); i++) {
			ents.add(Map.entry(keys.get(i), values.get(i)));
		}
		clear();
		ents.sort(new Comparator<Entry<K, V>>() {
			@Override
			public int compare(Entry<K, V> o1, Entry<K, V> o2) {
				return comp.compare(o1.getKey(), o2.getKey());
			}
		});
		for (Entry<K, V> ent : ents) {
			add(ent.getKey(), ent.getValue());
		}
	}

	public void sortByValues(Comparator<V> comp) {
	}

	public void printElements() {
		System.out.println("______elements______");
		System.out.println(toString().replaceAll("\\] \\[", "]\n["));
		System.out.println("____________________");
	}

	protected String getString(int i) {
		return "[key: " + getKey(i).toString() + ", val: " + getValue(i).toString() + "]";
	}

	@Override
	public String toString() {
		if (size() > 0) {
			String res = "";
			for (int i = 0; i < size() - 1; i++) {
				res = res + getString(i) + " ";
			}
			res = res + getString(size() - 1);
			return res;
		}
		return "empty";
	}

	@SuppressWarnings("unchecked")
	public Object clone() throws CloneNotSupportedException {
		Dictionary<K, V> dict = new Dictionary<>();
		dict.setKeys((ArrayList<K>) keys.clone());
		dict.setValues((ArrayList<V>) values.clone());
		return dict;
	}

	private ArrayList<K> keys;
	private ArrayList<V> values;

	private static final long serialVersionUID = 6856097499276626807L;
}