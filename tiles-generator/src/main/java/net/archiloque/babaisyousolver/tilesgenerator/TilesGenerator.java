package net.archiloque.babaisyousolver.tilesgenerator;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import javax.annotation.processing.Generated;
import javax.lang.model.element.Modifier;
import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Entry point to generate the tiles class
 */
public class TilesGenerator {

  private static final String TILES_JSON_FILES = "tiles.json";

  public static void main(String[] args)
      throws Exception {
    if (args.length != 1) {
      throw new IllegalArgumentException(
          "Need one argument and got " + args.length);
    }
    String targetDir = args[0];
    System.out.println("Will generate in [" + targetDir + "]");

    // get the JSON content
    URI jsonResourceUri = TilesGenerator.class.
        getClassLoader().
        getResource(TILES_JSON_FILES).
        toURI();
    String tilesFileContent = Files.
        readString(Path.of(jsonResourceUri));
    JSONObject jsonObject = new JSONObject(tilesFileContent);
    System.out.println(jsonObject.length() + " tiles found");
    new TilesGenerator(jsonObject).
        generate().
        writeTo(new File(targetDir));
  }

  private final @NotNull JSONObject jsonObject;

  private final @NotNull TypeSpec.Builder tileInterface =
      TypeSpec.
          interfaceBuilder("Tiles");

  private final @NotNull List<String> fields =
      new ArrayList<>();

  private TilesGenerator(
      @NotNull JSONObject jsonObject) {
    this.jsonObject = jsonObject;
  }

  private @NotNull JavaFile generate() {
    // annotation to indicates the class is generated
    AnnotationSpec generatedAnnotation =
        AnnotationSpec.builder(
            Generated.class).
            addMember(
                "value",
                "$S",
                TilesGenerator.class.getName()).
            build();

    // initialize the interface
    tileInterface.
        addModifiers(Modifier.PUBLIC).
        addAnnotation(
            generatedAnnotation);

    // create a list with all values
    // being sure empty is first
    for (String fieldName : jsonObject.keySet()) {
      if (!fieldName.equals("empty")) {
        fields.add(fieldName);
      }
    }
    fields.sort(String::compareToIgnoreCase);
    fields.add(0, "empty");

    List<String> fieldsNames = new ArrayList<>();

    // declare the XXX_STRING String constants
    for (String field : fields) {
      // create the constant from the field name
      String fieldConstantName =
          constantize(field);
      fieldsNames.add(fieldConstantName);
      addField(
          String.class,
          fieldConstantName + "_STRING",
          "$S", field
      );
    }

    // declare the ALL_STRINGS containing the XXX_STRING constants
    CodeBlock.Builder allStringCode = CodeBlock.
        builder().
        add(" new String[]{\n");
    for (String fieldName : fieldsNames) {
      allStringCode.add(fieldName + "_STRING,\n");
    }
    allStringCode.
        add("}");

    addField(
        ArrayTypeName.of(String.class),
        "ALL_STRINGS",
        allStringCode.build()
    );

    // the int values
    for (int i = 0; i < fieldsNames.size(); i++) {
      addField(
          TypeName.INT,
          fieldsNames.get(i),
          Integer.toString(i));
    }

    // the masks  (no masks for empty)
    for (int i = 1; i < fieldsNames.size(); i++) {
      addField(
          TypeName.INT,
          fieldsNames.get(i) + "_MASK",
          Integer.toString(1 << +(i - 1)));
    }

    createMask(
        "text",
        "TEXT_MASKS"
    );
    createMask(
        "subject",
        "SUBJECT_MASKS"
    );
    createMask(
        "definition",
        "DEFINITION_MASKS"
    );

    Map<String, String> subjectTargetMasks = new HashMap<>();
    for (int i = 0; i < fieldsNames.size(); i++) {
      String fieldName = fields.get(i);
      JSONObject fieldDeclaration = jsonObject.
          getJSONObject(fieldName);

      if (isA(fieldDeclaration, "subject")) {
        String targetName = fieldDeclaration.getString("subjectTarget");
        subjectTargetMasks.put(fieldName, targetName);
      }
    }

    MethodSpec.Builder getTargetMethodBuilder = MethodSpec.methodBuilder("getTarget")
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .returns(TypeName.INT)
        .addParameter(TypeName.INT, "sourceMask")
        .addCode("switch (sourceMask) {\n");
    for (Map.Entry<String, String> integerIntegerEntry : subjectTargetMasks.entrySet()) {
      getTargetMethodBuilder.addCode("case Tiles." + constantize(integerIntegerEntry.getKey()) + "_MASK:\n");
      getTargetMethodBuilder.addCode("\treturn " + constantize(integerIntegerEntry.getValue()) + "_MASK;\n");
    }

    getTargetMethodBuilder.addCode("default:\n");
    getTargetMethodBuilder.addCode("\tthrow new IllegalArgumentException(Integer.toString(sourceMask));\n");
    getTargetMethodBuilder.addCode("}\n");
    tileInterface.addMethod(getTargetMethodBuilder.build());


    // create the file
    return JavaFile.builder(
        "net.archiloque.babaisyousolver",
        tileInterface.build()).
        build();
  }

  @NotNull
  private String constantize(
      @NotNull String field) {
    return field.
        toUpperCase().
        replace(' ', '_');
  }

  /**
   * Create a field from parameters
   */
  private void addField(
      @NotNull TypeName typeName,
      @NotNull String fieldName,
      @NotNull String content) {
    tileInterface.addField(
        FieldSpec.
            builder(
                typeName,
                fieldName,
                Modifier.PUBLIC,
                Modifier.STATIC,
                Modifier.FINAL).
            initializer(content).
            build());
  }

  /**
   * Create a field from parameters
   */
  private void addField(
      @NotNull TypeName typeName,
      @NotNull String fieldName,
      @NotNull CodeBlock codeBlock) {
    tileInterface.addField(
        FieldSpec.
            builder(
                typeName,
                fieldName,
                Modifier.PUBLIC,
                Modifier.STATIC,
                Modifier.FINAL).
            initializer(codeBlock).
            build());
  }

  /**
   * Create a field from parameters
   */
  private void addField(
      @NotNull Class type,
      @NotNull String fieldName,
      @NotNull String format,
      Object... args) {
    tileInterface.addField(
        FieldSpec.
            builder(
                type,
                fieldName,
                Modifier.PUBLIC,
                Modifier.STATIC,
                Modifier.FINAL).
            initializer(format, args).
            build());
  }

  /**
   * Create a mask from the attribute
   */
  private void createMask(
      @NotNull String attributeName,
      @NotNull String fieldName) {
    int mask = 0;
    for (int i = 1; i < fields.size(); i++) {
      JSONObject fieldDeclaration = jsonObject.
          getJSONObject(fields.get(i));
      if (isA(fieldDeclaration, attributeName)) {
        mask += 1 << (i - 1);
      }
    }
    addField(
        TypeName.INT,
        fieldName,
        Integer.toString(mask));
  }

  private boolean isA(
      @NotNull JSONObject fieldObject,
      @NotNull String attributeName) {
    return fieldObject.has(attributeName) &&
        fieldObject.getBoolean(attributeName);
  }
}