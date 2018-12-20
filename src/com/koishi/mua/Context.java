package com.koishi.mua;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayList;

class Context {

	private final Context global;
	private final Context parent;
	private final Map<String, Value> context;
	private final boolean accept;
	private Value result;

	void export() throws Exception {
		if (global == this) {
			throw new GlobalNamespaceException("export");
		}
		global.context.putAll(this.context);
	}

	boolean containsKey(String key) {
		return context.containsKey(key);
	}

	Value get(String key) throws Exception {
		if (!context.containsKey(key)) {
			if (!global.context.containsKey(key)) {
				throw new Exception("no such key named: " + key);
			} else {
				return global.context.get(key);
			}
		} else {
			return context.get(key);
		}
	}

	void put(String key, Value value) {
		context.put(key, value);
	}

	void erase(String key) {
		if (context.containsKey(key)) {
			context.remove(key);
		} else {
			global.context.remove(key);
		}
	}

	Value getResult() {
		return result;
	}

	void clear() {
		this.context.clear();
	}

	void list() {
		System.out.println("{");
		for (var entry: context.entrySet()) {
			System.out.print("  " + entry.getKey() + ": ");
			entry.getValue().print();
			System.out.println();
		}
		System.out.println("}");
	}

	void setResult(Value result) throws Exception {
		if (!accept) {
			if (parent != null) {
				parent.setResult(result);
			} else {
				throw new GlobalNamespaceException("output");
			}
		} else {
			this.result = result;
		}
	}

	Context() {
		this.accept = false;
		this.result = null;
		this.global = this;
		this.parent = null;
		this.context = new LinkedHashMap<>();
	}

	Context(Context other, boolean acceptResult) {
		this.accept = acceptResult;
		this.result = null;
		this.global = other.global;
		this.parent = other;
		this.context = new LinkedHashMap<>();
	}
}
