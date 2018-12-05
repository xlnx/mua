package com.koishi.mua;

public class Word extends Value {

	public enum Type {
		word(0x0000100), wordLiteral(0x0000200), wordValue(0x0000400), number(0x0000800), bracket(0x0002000),
		lbracket(0x0010000 | bracket.getMask()), rbracket(0x0020000 | bracket.getMask());

		private final int mask;

		private Type(int mask) {
			this.mask = mask;
		}

		private int getMask() {
			return this.mask;
		}

		public boolean is(Type prototype) {
			return (this.getMask() & prototype.getMask()) == prototype.getMask();
		}
	}

	private final Type type;
	private final String value;

	String getValue() {
		return value;
	}

	Type getType() {
		return type;
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
}
