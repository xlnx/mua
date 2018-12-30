package com.koishi.mua;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.io.InputStream;
import java.util.Scanner;

public class VM {
	public void interpret(InputStream in) {
		var interpreter = new Interpreter();
		var scanner = new Scanner(in);
		boolean ueof = false;
		AnsiConsole.systemInstall();
		while (true) {
			System.out.print(ueof ? " > " : "M> ");
			var line = scanner.nextLine();
			try {
				var result = interpreter.interpret(line);
				if (result != null) {
					System.out.print(Ansi.ansi().fg(Ansi.Color.GREEN).a(" < ").reset());
					result.print();
					System.out.println();
				}
				ueof = false;
			} catch (EOFException e) {
				ueof = true;
			} catch (Exception e) {
				ueof = false;
				System.out.println(e.getMessage());
			}
		}
	}
}
