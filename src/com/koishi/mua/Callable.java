package com.koishi.mua;

import java.util.ArrayList;

interface Callable {
	int getParamCount();
	Value execute(Facility facility, ArrayList<Value> params, Context context) throws Exception;
}

class Util {
	static void putArg(String arg, Value value, Context context) throws Exception {
		if (value == null) {
			throw new NullArgException();
		} else {
			context.put(arg, value);
		}
	}
}