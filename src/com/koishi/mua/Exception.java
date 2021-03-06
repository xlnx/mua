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

class DivideByZeroException extends Exception {
	DivideByZeroException(Facility facility, int index) {
		super("divide by zero");
		facility.astBuilder.push(facility.astBuilder.top().children.get(index));
	}
}

class SqrtNegativeException extends Exception {
	SqrtNegativeException(Facility facility, int index, double value) {
		super("sqrt negative number " + value);
		facility.astBuilder.push(facility.astBuilder.top().children.get(index));
	}
}

class EmptyException extends Exception {
	EmptyException(Facility facility, int index, String list) {
		super("can't open empty " + list);
		facility.astBuilder.push(facility.astBuilder.top().children.get(index));
	}
}

class NullArgException extends Exception {
	NullArgException() { super("null value in function argument"); }
}