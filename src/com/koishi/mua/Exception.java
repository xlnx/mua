package com.koishi.mua;

class Exception extends java.lang.Exception {
	Exception(String value) {
		super(value);
	}
}

class InternalException extends Exception {
	InternalException() {
		super("internal exception");
	}
}

class GlobalNamespaceException extends Exception {
	GlobalNamespaceException(String op) {super("'" + op + "' in global namespace is not allowed");}
}

class FunctionStop extends GlobalNamespaceException {
	FunctionStop() { super("stop"); }
}

class ExpectedException extends Exception {
	ExpectedException(String types, Object object) {
		super("expected " + types + ", got " + object);
	}
	ExpectedException(Facility facility, int index, String types, Object object) {
		super("expected " + types + ", got " + object);
		facility.astBuilder.push(facility.astBuilder.top().children.get(index));
	}
}

class EOFException extends Exception {
	EOFException(String what) {
		super(what);
	}
}

class UnexpectedRightBracketException extends Exception {
	UnexpectedRightBracketException() {
		super("unexpected ']'");
	}
}

class EmptyException extends Exception {
	EmptyException(String list) { super("can't open empty "); }
}

class NullArgException extends Exception {
	NullArgException() { super("null value in function argument"); }
}