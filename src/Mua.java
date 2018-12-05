import com.koishi.mua.Exception;
import com.koishi.mua.Interpreter;
import com.koishi.mua.Parser;

import java.util.Scanner;

public class Mua {

	public static void main(String[] args) {
		var interpreter = new Interpreter();
		var scanner = new Scanner(System.in);
		boolean ueof = false;
		while (true) {
			System.out.print(ueof ? " > " : "M> ");
			var line = scanner.nextLine();
			try {
				var result = interpreter.interpret(line);
				if (result != null) {
					System.out.print("< ");
					result.print();
					System.out.println();
				}
				ueof = false;
			} catch (Parser.EOFException e) {
				ueof = true;
			} catch (Exception e) {
				ueof = false;
				System.out.println(e.getMessage());
			}
		}
	}
}