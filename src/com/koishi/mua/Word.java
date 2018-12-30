package com.koishi.mua;

class Word extends Value {

	public enum Type {
		word          (0x0400),
		literal       (0x0001),
		value         (0x0002),

		bracket       (0x0004),
		right         (0x1000|bracket.mask),
		left          (0x2000|bracket.mask);

		private final int mask;

		Type(int mask) {
			this.mask = mask;
		}

		private int getMask() {
			return this.mask;
		}

		boolean is(Type prototype) {
			return (this.getMask() & prototype.getMask()) == prototype.getMask();
		}
	}

	protected final Type type;
	protected final String value;

	String getValue() {
		return value;
	}

	Type getType() {
		return type;
	}

	@Override
	public String toString() {
		return "word<" + value + ">";
	}

	@Override
	public void print() {
		System.out.print(value);
	}

	boolean is(Type prototype) {
		return this.type.is(prototype);
	}

	Word(Type type, String value) {
		this.type = type;
		this.value = value;
	}

	<T>Word(T value) {
		this.type = Type.word;
		this.value = "" + value;
	}
}
