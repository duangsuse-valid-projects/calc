package calc.inst;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public final class Main {
    static InputStream cin = System.in;
    static PrintStream cout = System.out;
    static final Scanner scan = new Scanner(cin);

    public static void main(String... args) {
        prompt(); while (scan.hasNextLine()) {
            String line = scan.nextLine();
            Parser parser = new Parser("<stdin>", line);
            parser.mark();
            Ast expr;
            try { expr = parser.expr();
            } catch (Error e) { e.printStackTrace(); continue; }

            cout.println(expr.accept(new Ast.ShowVisitor()));
            cout.print("  = "); parser.reset(); cout.println(parser.tokens());
            cout.print("  = "); cout.println(expr.accept(Evaluator.INSTANCE));
            prompt();
        }
    }

    private static void prompt() {
        cout.print("> ");
        cout.flush();
    }
}
