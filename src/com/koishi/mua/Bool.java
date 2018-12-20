package com.koishi.mua;

class Bool extends Value {

	private final boolean value;

	boolean getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "bool<" + value + ">";
	}

	@Override
	public void print() {
		System.out.print(value);
	}

	Bool(boolean value) {
		this.value = value;
	}
}
