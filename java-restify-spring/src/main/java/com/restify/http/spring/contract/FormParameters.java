package com.restify.http.spring.contract;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class FormParameters implements MultiValueMap<String, Object> {

	private final MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();

	public FormParameters(Parameter...parameters) {
		Arrays.stream(parameters).forEach(p -> map.add(p.name(), p.value()));
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override
	public List<Object> get(Object key) {
		return map.get(key);
	}

	@Override
	public List<Object> put(String key, List<Object> value) {
		return map.put(key, value);
	}

	@Override
	public List<Object> remove(Object key) {
		return map.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ? extends List<Object>> m) {
		map.putAll(m);
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public Set<String> keySet() {
		return map.keySet();
	}

	@Override
	public Collection<List<Object>> values() {
		return map.values();
	}

	@Override
	public Set<java.util.Map.Entry<String, List<Object>>> entrySet() {
		return map.entrySet();
	}

	@Override
	public Object getFirst(String key) {
		return map.getFirst(key);
	}

	@Override
	public void add(String key, Object value) {
		map.add(key, value);
	}

	@Override
	public void set(String key, Object value) {
		map.set(key, value);
	}

	@Override
	public void setAll(Map<String, Object> values) {
		map.setAll(values);
	}

	@Override
	public Map<String, Object> toSingleValueMap() {
		return map.toSingleValueMap();
	}

	public static class Parameter {

		private final String name;
		private final Object value;

		public Parameter(String name, Object value) {
			this.name = name;
			this.value = value;
		}

		public String name() {
			return name;
		}

		public Object value() {
			return value;
		}
	}
}
