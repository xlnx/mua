package com.koishi.mua;

abstract class Value {

	<T extends Value> T as() {
		return (T) this;
	}

	Word asWord(Facility facility, int index) throws Exception {
		if (this instanceof Word) {
			return as();
		} else {
			throw new ExpectedException(facility, index, "word", this);
		}
	}

	Number asNumber(Facility facility, int index) throws Exception {
		if (this instanceof Number) {
			return as();
		} else {
			throw new ExpectedException(facility, index, "number", this);
		}
	}

	List asList(Facility facility, int index) throws Exception {
		if (this instanceof List) {
			return as();
		} else {
			throw new ExpectedException(facility, index, "list", this);
		}
	}

	Bool asBool(Facility facility, int index) throws Exception {
		if (this instanceof Bool) {
			return as();
		} else {
			throw new ExpectedException(facility, index, "bool", this);
		}
	}

	public abstract void print();
}
