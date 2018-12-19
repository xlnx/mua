package com.koishi.mua;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Context {

	private Context parent;
	private Map<String, Value> context;
	private Vector<Value> args;
	private Value result;
	private boolean accept;

	Context derive() {
		var derived = new Context();
		derived.parent = this;
		derived.context = new HashMap<>(context);
		return derived;
	}
	Context copy() throws Exception {
		if (parent != null) {
			throw new Exception("internel error cloning non-global context");
		} else {
			var copied = new Context();
			copied.context = new HashMap<>(context);
			return copied;
		}
	}
	Context getParent() {
		return parent;
	}
	void assign(Context other) throws Exception {
		if (parent != null) {
			throw new Exception("internel error cloning non-global context");
		} else {
			context = other.context;
		}
	}
	void export() {
		var namesp = this;
		while (namesp.parent != null) {
			namesp = namesp.parent;
		}
		namesp.context = new HashMap<>(this.context);
	}

	Value get(String key) throws Exception {
		if (!context.containsKey(key)) {
			throw new Exception("no such key named: " + key);
		} else {
			return context.get(key);
		}
	}

	void put(String key, Value value) {
		context.put(key, value);
	}

	boolean containsKey(String key) {
		return context.containsKey(key);
	}

	void erase(String key) {
		context.remove(key);
	}

	Value getResult() {
		return result;
	}

	void setResult(Value result) throws Exception {
		if (!accept) {
			if (parent != null) {
				parent.setResult(result);
			} else {
				throw new Exception("output value outside function is not allowed");
			}
		} else {
			this.result = result;
		}
	}

	void acceptResult() {
		this.accept = true;
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

	Vector<Value> getArgs()
	{
		return args;
	}

	Context() {
		this.accept = false;
		this.result = null;
		this.parent = null;
		this.context = new HashMap<>();
		this.args = new Vector<>();
	}
}
