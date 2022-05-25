import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
    private static void defineType(String outputDir, String baseName, String className, String fieldLists)
            throws IOException {
        String path = outputDir + "/" + className + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");
        String visitorName = baseName + "Visitor";

        writer.println("package ast;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println("import util.Pair;");
        writer.println("import token.Token;");
        writer.println("import visitor." + visitorName + ";");
        writer.println();

        writer.println("public class " + className + " extends " + baseName + " {");

        // constructor
        writer.println("    public " + className + "(" + fieldLists.replace("|", ",") + ") {");
        String[] fields = (fieldLists.isEmpty() ? new String[0] : fieldLists.split(", "));
        for (String field : fields) {
            String name = field.split(" ")[1];
            writer.println("        this." + name + " = " + name + ";");
        }
        writer.println("    }");

        // Visitor patterns
        writer.println();
        writer.println("    @Override");
        writer.println("    public <R> R accept(" + visitorName + "<R> visitor) {");
        writer.println("        return visitor.visit(this);");
        writer.println("    }");

        // Fields
        writer.println();
        for (String field : fields) {
            writer.println("    public final " + field.replace("|", ",") + ";");
        }
        writer.println("}");
        writer.close();
    }

    private static void defineVisitor(String outputDir, String baseName, List<String> types) throws IOException {
        String visitorName = baseName + "Visitor";
        String path = outputDir + "/" + visitorName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        writer.println("package visitor;");
        writer.println();
        writer.println("import ast.*;");
        writer.println();

        writer.println("public interface " + visitorName + "<R> {");
        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println("    R visit (" + typeName + " " + baseName.toLowerCase() + ");");
        }
        writer.println("}");
        writer.close();
    }

    private static void defineAst(String srcDir, String baseName, List<String> types) throws IOException {
        String astDir = srcDir + "/ast";
        String visitorDir = srcDir + "/visitor";
        String visitorName = baseName + "Visitor";
        String path = astDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        writer.println("package ast;");
        writer.println();
        writer.println("import visitor." + visitorName + ";");
        writer.println();
        writer.println("public abstract class " + baseName + " {");
        writer.println("    public abstract <R> R accept(" + visitorName + "<R>" + " visitor);");
        writer.println("}");

        // define Visitor
        defineVisitor(visitorDir, baseName, types);

        // Define Ast Classes
        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(astDir, baseName, className, fields);
        }

        writer.close();
    }

    public static void main(String args[]) throws IOException {
        String srcPath = "D:/Development/project/jsweet/src";
        defineAst(srcPath, "Expr", Arrays.asList(
                "AssignExpr     : Token name, Expr value",
                "BinaryExpr     : Expr left, Token op, Expr right",
                "LogicalExpr     : Expr left, Token op, Expr right",
                "TernaryExpr    : Expr cond, Expr trueExpr, Expr falseExpr",
                "UnaryExpr      : Token op, Expr right",
                "CallExpr       : Expr callee, Token paren, List<Expr> arguments",
                "GroupExpr      : Expr expr",
                "LiteralExpr    : Object val",
                "VarExpr        : Token name"));

        defineAst(srcPath, "Stmt", Arrays.asList(
                "ExprStmt       : Expr expr",
                "PrintStmt      : List<Expr> arguments",
                "VarStmt        : List<Pair<Token|Expr>> vars", // here ',' is a first separator, so using '|'
                "FunStmt        : Token name, List<Token> params, List<Stmt> body",
                "BlockStmt      : List<Stmt> statements",
                "IfStmt         : Expr cond, Stmt thenStmt, Stmt elseStmt",
                "WhileStmt      : Expr cond, Stmt stmt",
                "ForStmt        : Stmt initializer, Expr cond, Expr increment, Stmt body",
                "BreakStmt      : ",
                "ContinueStmt   : ",
                "ReturnStmt     : Token keyword, Expr value"));
    }
}
