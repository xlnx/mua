package com.koishi.mua;

import java.util.Vector;

public interface Callable {
	int getParamCount();
	Value execute(Vector<Value> params, Context context) throws Exception;
}

class Util {
	static void putArg(String arg, Value value, Context context) throws Exception {
		if (value == null) {
			throw new Exception("null argument in function call");
		} else {
			context.put(arg, value);
		}
	}
}