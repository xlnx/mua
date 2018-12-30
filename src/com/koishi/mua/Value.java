package com.koishi.mua;

import java.math.BigDecimal;

abstract class Value {

	private static String number_r = "^-?[0-9]+(?:\\.[0-9]+)?$";
	private static String bool_r = "^true|false$";

	boolean isWord() throws Exception {
		return this instanceof Word;
	}

	boolean isNumber() throws Exception {
		if (isWord()) {
			var value = asWord(null, 0);
			if (value.length() >= 1) {
				var first = value.charAt(0);
				if (first == '-' || first >= '0' && first <= '9') {
					try {
						new BigDecimal(value);
					} catch (java.lang.Exception e) {
						return false;
					}
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
		return false;
	}

	boolean isBool() throws Exception {
		return isWord() && asWord(null, 0).matches(bool_r);
	}

	boolean isList() throws Exception {
		return this	instanceof List;
	}

	String asWord(Facility facility, int index) throws Exception {
		if (this instanceof Word) {
			Word word = (Word)this;
			var type = word.getType();
			if (type.is(Word.Type.literal)) {
				return word.getValue().substring(1);
			} else {
				return word.getValue();
			}
		} else {
			throw new ExpectedException(facility, index, "word", this);
		}
	}

	double asNumber(Facility facility, int index) throws Exception {
		if (this instanceof Word) {
			if (isNumber()) {
				return Double.parseDouble(this.asWord(facility, index));
			} else {
				throw new ExpectedException(facility, index, "number", this);
			}
		} else {
			throw new ExpectedException(facility, index, "number", this);
		}
	}

	List asList(Facility facility, int index) throws Exception {
		if (this instanceof List) {
			return (List) this;
		} else {
			throw new ExpectedException(facility, index, "list", this);
		}
	}

	boolean asBool(Facility facility, int index) throws Exception {
		if (this instanceof Word) {
			if (isBool()) {
				return Boolean.parseBoolean(this.asWord(facility, index));
			} else {
				throw new ExpectedException(facility, index, "bool", this);
			}
		} else {
			throw new ExpectedException(facility, index, "bool", this);
		}
	}

	public abstract void print();
}
