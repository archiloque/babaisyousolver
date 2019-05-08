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

  private @NotNull Map<Character, Integer> getTiles() {
    Map<Character, Integer> result = new HashMap<>();
    result.put('b', Tiles.BABA);
    result.put('B', Tiles.BABA_TEXT);
    result.put(' ', Tiles.EMPTY);
    return result;
  }

  private @NotNull Path getFilePath(String path) {
    return Path.of("src", "test", "resources", path);
  }

  private @NotNull Map<Character, Integer> readTiles(
      @NotNull String path
  ) throws IOException {
    return LevelReader.readTiles(getFilePath(path));
  }

  @NotNull
  private LevelReader.LevelReaderResult readContent(
      @NotNull String path,
      @NotNull Map<Character, Integer> tiles
  ) throws IOException {
    return LevelReader.readContent(getFilePath(path), tiles);
  }

  private void checkReadContentException(
      @NotNull String filePath,
      @NotNull String expectedMessage,
      @NotNull Class exceptedExceptionClass) {
    Throwable exception = assertThrows(exceptedExceptionClass, () ->
        readContent(filePath, getTiles())
    );
    assertEquals(expectedMessage, exception.getMessage());
  }

  private void checkReadTilesException(
      @NotNull String filePath,
      @NotNull String expectedMessage,
      @NotNull Class exceptedExceptionClass) {
    Throwable exception = assertThrows(exceptedExceptionClass, () ->
        readTiles(filePath)
    );
    assertEquals(expectedMessage, exception.getMessage());
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
    checkReadTilesException(
        "not found",
        getPath("not found", "tiles.txt"),
        FileNotFoundException.class);

  }

  private String getPath(String dir, String file) {
    return getFilePath(dir).resolve(file).toAbsolutePath().toString();
  }

  @Test
  void readTilesTilesNotFound() {
    checkReadTilesException(
        "tile-not-found",
        "Unknown tile [what?]",
        IllegalArgumentException.class);
  }

  @Test
  void readTilesInvalidSyntax() {
    checkReadTilesException(
        "tile-invalid-syntax",
        "Bad tile declaration [ba what?]",
        IllegalArgumentException.class);
  }

  @Test
  void readContentOK() throws IOException {
    LevelReader.LevelReaderResult levelReaderResult =
        readContent("content-ok", getTiles());
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
    checkReadContentException(
        "not found",
        getPath("not found", "content.txt"),
        FileNotFoundException.class);
  }

  @Test
  void readContentInvalidLineLength() {
    String absolutePath = getPath("content-bad-length", "content.txt");
    checkReadContentException(
        "content-bad-length",
        "[bb] is not 4 characters long at line 1 of " + absolutePath,
        IllegalArgumentException.class);
  }

  @Test
  void readContentUnknownTile() {
    String absolutePath = getPath("content-unknown-tile", "content.txt");
    checkReadContentException(
        "content-unknown-tile",
        "Unknown tile [c] at line 1 of " + absolutePath,
        IllegalArgumentException.class);
  }

}
