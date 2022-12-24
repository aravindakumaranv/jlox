package com.craftinginterpreters.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import javax.print.attribute.PrintRequestAttribute;

public class GenerateAST {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }
        String outputDir = args[0];
        defineAST(outputDir, "Expr", Arrays.asList(
            "Binary : Expr left, Token operator, Expr right",
            "Grouping : Expr expression",
            "Literal : Object value",
            "Unary : Token operator, Expr right"
        ));
    }

    public static void defineAST(String outputDir, String baseName, List<String> types) throws IOException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter printWriter = new PrintWriter(path, "UTF-8");

        printWriter.println("package com.craftinginterpreters.lox;");
        printWriter.println();
        printWriter.println("import java.util.List;");
        printWriter.println();
        printWriter.println("abstract class " + baseName + " {");

        defineVisitor(printWriter, baseName, types);

        // The AST classes
        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(printWriter, baseName, className, fields);
        }

        // The base accept() method
        printWriter.println();
        printWriter.println("   abstract <R> R accept(Visitor<R> visitor);");

        printWriter.println("}");
        printWriter.close();
    }

    private static void defineVisitor(PrintWriter printWriter, String baseName, List<String> types) { 
        printWriter.println("   interface Visitor<R> {");

        for (String type: types) {
            String typeName = type.split(":")[0].trim();
            printWriter.println("       R visit" + typeName + baseName + "(" + typeName + " " + baseName.toLowerCase() + ");"); 
        }

        printWriter.println("   }");
    }

    private static void defineType(PrintWriter printWriter, String baseName, String className, String fieldList) {
        printWriter.println("   static class " + className + " extends " + baseName + " {");

        // Constructor
        printWriter.println("       " + className + "(" + fieldList + ") {");

        // Store parameters in fields
        String[] fields = fieldList.split(", ");
        for (String field : fields) {
            String name = field.split(" ")[1];
            printWriter.println("           this." + name + " = " + name + ";");
        }

        printWriter.println("       }");

        // Visitor pattern
        printWriter.println();
        printWriter.println("       @Override");
        printWriter.println("       <R> R accept(Visitor<R> visitor) {");
        printWriter.println("           return visitor.visit" + className + baseName + "(this);");
        printWriter.println("       }");            

        // Fields
        printWriter.println();
        for (String field : fields) {
            printWriter.println("       final " + field + ";");
        }

        printWriter.println("   }");
    }
}
