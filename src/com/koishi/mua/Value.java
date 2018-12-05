package com.koishi.mua;

public abstract class Value {

	<T extends Value> T as() {
		return (T) this;
	}

	Word asWord() throws Exception {
		if (this instanceof Word) {
			return as();
		} else {
			throw new Exception("expected word, got " + this);
		}
	}

	Number asNumber() throws Exception {
		if (this instanceof Number) {
			return as();
		} else {
			throw new Exception("expected number, got " + this);
		}
	}

	List asList() throws Exception {
		if (this instanceof List) {
			return as();
		} else {
			throw new Exception("expected list, got " + this);
		}
	}

	Bool asBool() throws Exception {
		if (this instanceof Bool) {
			return as();
		} else {
			throw new Exception("expected bool, got " + this);
		}
	}

	public abstract void print();
}
