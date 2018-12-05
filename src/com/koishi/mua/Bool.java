package com.koishi.mua;

public class Bool extends Value {

	private boolean value;

	boolean getValue() {
		return value;
	}

	@Override
	public void print() {
		System.out.print(value);
	}

	Bool(boolean value) {
		this.value = value;
	}
}
