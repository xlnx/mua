package com.koishi.mua;

public class Number extends Value {

	private final double value;

	double getValue() {
		return value;
	}

	@Override
	public void print() {
		System.out.print(value);
	}

	Number(double value) {
		this.value = value;
	}
}
