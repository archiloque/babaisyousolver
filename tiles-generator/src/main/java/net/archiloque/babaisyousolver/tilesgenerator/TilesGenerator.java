package net.archiloque.babaisyousolver.tilesgenerator;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.json.JSONObject;

import javax.annotation.processing.Generated;
import javax.lang.model.element.Modifier;
import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
    URI jsonResourceUri = TilesGenerator.
        class.
        getClassLoader().
        getResource(TILES_JSON_FILES).
        toURI();
    String tilesFileContent = Files.
        readString(Path.of(jsonResourceUri));
    JSONObject jsonObject = new JSONObject(tilesFileContent);
    System.out.println(jsonObject.length() + " tiles found");

    // create a list with all values, being sure empty is first
    List<String> fields = new ArrayList<>();
    for (String fieldName : jsonObject.keySet()) {
      if (!fieldName.equals("empty")) {
        fields.add(fieldName);
      }
    }
    fields.sort(String::compareToIgnoreCase);
    fields.add(0, "empty");

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
    TypeSpec.Builder tileInterface = TypeSpec.
        interfaceBuilder("Tiles").
        addModifiers(Modifier.PUBLIC).
        addAnnotation(
            generatedAnnotation);

    List<String> fieldsNames = new ArrayList<>();

    // declare the XXX_STRING String constants
    for (String field : fields) {
      // create the constant from the field name
      String fieldConstantName =
          field.
              toUpperCase().
              replace(' ', '_');
      fieldsNames.add(fieldConstantName);
      FieldSpec tileField = FieldSpec.
          builder(
              String.class,
              fieldConstantName + "_STRING",
              Modifier.PUBLIC,
              Modifier.STATIC,
              Modifier.FINAL).
          initializer("$S", field).build();
      tileInterface.addField(tileField);
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

    FieldSpec allStringsField = FieldSpec.
        builder(
            ArrayTypeName.of(String.class),
            "ALL_STRINGS",
            Modifier.PUBLIC,
            Modifier.STATIC,
            Modifier.FINAL).
        initializer(allStringCode.build()).
        build();
    tileInterface.addField(allStringsField);

    // the int values
    for (int i = 0; i < fieldsNames.size(); i++) {
      String fieldName = fieldsNames.get(i);
      FieldSpec valueField = FieldSpec.
          builder(
              TypeName.INT,
              fieldName,
              Modifier.PUBLIC,
              Modifier.STATIC,
              Modifier.FINAL).
          initializer("" + i).build();
      tileInterface.addField(valueField);
    }

    // the masks (no masks for empty)
    for (int i = 1; i < fieldsNames.size(); i++) {
      String fieldName = fieldsNames.get(i);
      FieldSpec maskField = FieldSpec.
          builder(
              TypeName.INT,
              fieldName + "_MASK",
              Modifier.PUBLIC,
              Modifier.STATIC,
              Modifier.FINAL).
          initializer("1 << " + (i - 1)).build();
      tileInterface.addField(maskField);
    }

    // create the file
    JavaFile javaFile =
        JavaFile.builder(
            "net.archiloque.babaisyousolver",
            tileInterface.build()).
            build();
    javaFile.writeTo(new File(targetDir));
  }
}