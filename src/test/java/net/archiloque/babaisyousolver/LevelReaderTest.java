package net.archiloque.babaisyousolver;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LevelReaderTest {

  @NotNull
  private Map<Character, Integer> getTiles() {
    Map<Character, Integer> result = new HashMap<>();
    result.put('b', Tiles.BABA);
    result.put('B', Tiles.BABA_TEXT);
    result.put(' ', Tiles.EMPTY);
    return result;
  }

  @NotNull
  private Path getFilePath(String path) {
    return Path.of("src", "test", "resources", path);
  }

  @NotNull
  private Map<Character, Integer> readTiles(@NotNull String path) throws IOException {
    return LevelReader.readTiles(getFilePath(path));
  }

  @NotNull
  private LevelReader.LevelReaderResult readContent(@NotNull String path, @NotNull Map<Character, Integer> tiles) throws IOException {
    return LevelReader.readContent(getFilePath(path), tiles);
  }

  @Test
  void readTilesOK() throws IOException {
    Map<Character, Integer> result = readTiles("tile-ok");
    assertEquals(3, result.size());
    assertEquals(result.get('b'), Tiles.BABA);
    assertEquals(result.get('B'), Tiles.BABA_TEXT);
    assertEquals(result.get(' '), Tiles.EMPTY);
  }

  @Test
  void readTilesFileNotFound() {
    Exception exception = assertThrows(FileNotFoundException.class, () ->
        readTiles("not found")
    );
    String absolutePath = getFilePath("not found").resolve("tiles.txt").toAbsolutePath().toString();
    assertEquals(absolutePath, exception.getMessage());
  }

  @Test
  void readTilesTilesNotFound() {
    Exception exception = assertThrows(IllegalArgumentException.class, () ->
        readTiles("tile-not-found")
    );
    assertEquals("Unknown tile [what?]", exception.getMessage());
  }

  @Test
  void readTilesInvalidSyntax() {
    Exception exception = assertThrows(IllegalArgumentException.class, () ->
        readTiles("tile-invalid-syntax")
    );
    assertEquals("Bad tile declaration [ba what?]", exception.getMessage());
  }

  @Test
  void readContentOK() throws IOException {
    LevelReader.LevelReaderResult levelReaderResult = readContent("content-ok", getTiles());
    assertEquals(2, levelReaderResult.height);
    assertEquals(3, levelReaderResult.width);
    assertArrayEquals(new int[]{
        Tiles.BABA,
        Tiles.EMPTY,
        Tiles.BABA_TEXT,
        Tiles.BABA,
        Tiles.BABA_TEXT,
        Tiles.BABA_TEXT,
    }, levelReaderResult.content);
  }

  @Test
  void readContentFileNotFound() {
    Exception exception = assertThrows(FileNotFoundException.class, () ->
        readContent("not found", getTiles())
    );
    String absolutePath = getFilePath("not found").resolve("content.txt").toAbsolutePath().toString();
    assertEquals(absolutePath, exception.getMessage());

  }

  @Test
  void readContentInvalidLineLength() {
    Exception exception = assertThrows(IllegalArgumentException.class, () ->
        readContent("content-bad-length", getTiles())
    );
    String absolutePath = getFilePath("content-bad-length").resolve("content.txt").toAbsolutePath().toString();
    assertEquals("[bb] is not 4 characters long at line 1 of " + absolutePath, exception.getMessage());
  }

  @Test
  void readContentUnknownTile() {
    Exception exception = assertThrows(IllegalArgumentException.class, () ->
        readContent("content-unknown-tile", getTiles())
    );
    String absolutePath = getFilePath("content-unknown-tile").resolve("content.txt").toAbsolutePath().toString();
    assertEquals("Unknown tile [c] at line 1 of " + absolutePath, exception.getMessage());
  }
}
