package net.archiloque.babaisyousolver;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StateTryToGoTest {

  private static final class LevelToTestTryToGo extends Level {

    private final List<int[]> states = new ArrayList<>();

    LevelToTestTryToGo(
        int width,
        int height,
        @NotNull int[] content) {
      super(width, height, content);
    }

    @Override
    void addState(
        @NotNull int[] content,
        byte movement,
        int parent) {
      states.add(content);
    }
  }

  /**
   * Cases are tested with a level of ?x1
   * Baba is in the first position and tries to go left
   */
  void checkMoveSimple(
      @NotNull int[] content,
      byte result,
      @NotNull int[][] possibleNextMoves) {
    LevelToTestTryToGo level = new LevelToTestTryToGo(
        content.length,
        1,
        new int[content.length]);
    State state = new State(level, content, 0);
    state.stopTilesMask = Tiles.WALL_MASK;
    state.winTilesMask = Tiles.FLAG_MASK;
    state.pushTilesMask = Tiles.ROCK_MASK | Tiles.TEXT_MASKS;
    assertEquals(
        result,
        state.tryToGo(0, Direction.RIGHT));
    assertEquals(possibleNextMoves.length, level.states.size());
    for (int i = 0; i < possibleNextMoves.length; i++) {
      assertArrayEquals(
          possibleNextMoves[i],
          level.states.get(i));
    }
  }

  @Test
  void testMoveEmpty() {
    // empty
    checkMoveSimple(
        new int[]{
            Tiles.BABA_MASK,
            Tiles.EMPTY},
        (byte) -1,
        new int[][]{new int[]{
            Tiles.EMPTY,
            Tiles.BABA_MASK}});
  }

  @Test
  void testMoveWall() {
    // wall
    checkMoveSimple(
        new int[]{
            Tiles.BABA_MASK,
            Tiles.WALL_MASK},
        (byte) -1,
        new int[0][]);
  }

  @Test
  void testMoveFlag() {
    // flag
    checkMoveSimple(
        new int[]{
            Tiles.BABA_MASK,
            Tiles.FLAG_MASK},
        Direction.RIGHT,
        new int[0][]);
  }

  @Test
  void testMoveRock() {
    // rock
    checkMoveSimple(
        new int[]{
            Tiles.BABA_MASK,
            Tiles.ROCK_MASK},
        (byte) -1,
        new int[0][]);

    // rock + flag
    checkMoveSimple(
        new int[]{
            Tiles.BABA_MASK,
            Tiles.ROCK_MASK | Tiles.FLAG_MASK},
        (byte) -1,
        new int[0][]);

    // rock | rock
    checkMoveSimple(
        new int[]{
            Tiles.BABA_MASK,
            Tiles.ROCK_MASK,
            Tiles.ROCK_MASK},
        (byte) -1,
        new int[0][]);

    // rock | wall
    checkMoveSimple(
        new int[]{
            Tiles.BABA_MASK,
            Tiles.ROCK_MASK,
            Tiles.WALL_MASK},
        (byte) -1,
        new int[0][]);

    // rock | empty
    checkMoveSimple(
        new int[]{
            Tiles.BABA_MASK,
            Tiles.ROCK_MASK,
            Tiles.EMPTY},
        (byte) -1,
        new int[][]{new int[]{
            Tiles.EMPTY,
            Tiles.BABA_MASK,
            Tiles.ROCK_MASK}});

    // rock + flag | empty
    checkMoveSimple(
        new int[]{
            Tiles.BABA_MASK,
            Tiles.ROCK_MASK | Tiles.FLAG_MASK,
            Tiles.EMPTY},
        Direction.RIGHT,
        new int[0][]);

    // rock | flag
    checkMoveSimple(
        new int[]{
            Tiles.BABA_MASK,
            Tiles.ROCK_MASK,
            Tiles.FLAG_MASK},
        (byte) -1,
        new int[][]{new int[]{
            Tiles.EMPTY,
            Tiles.BABA_MASK,
            Tiles.ROCK_MASK | Tiles.FLAG_MASK}});

    // rock | rock | empty
    checkMoveSimple(
        new int[]{
            Tiles.BABA_MASK,
            Tiles.ROCK_MASK,
            Tiles.ROCK_MASK,
            Tiles.EMPTY},
        (byte) -1,
        new int[][]{new int[]{
            Tiles.EMPTY,
            Tiles.BABA_MASK,
            Tiles.ROCK_MASK,
            Tiles.ROCK_MASK}});

    // rock | flag text | empty
    checkMoveSimple(
        new int[]{
            Tiles.BABA_MASK,
            Tiles.ROCK_MASK,
            Tiles.FLAG_TEXT_MASK,
            Tiles.EMPTY},
        (byte) -1,
        new int[][]{new int[]{
            Tiles.EMPTY,
            Tiles.BABA_MASK,
            Tiles.ROCK_MASK,
            Tiles.FLAG_TEXT_MASK}});

    // rock | flag text + flag| empty
    checkMoveSimple(
        new int[]{
            Tiles.BABA_MASK,
            Tiles.ROCK_MASK,
            Tiles.FLAG_TEXT_MASK | Tiles.FLAG_MASK,
            Tiles.EMPTY},
        (byte) -1,
        new int[][]{new int[]{
            Tiles.EMPTY,
            Tiles.BABA_MASK,
            Tiles.ROCK_MASK | Tiles.FLAG_MASK,
            Tiles.FLAG_TEXT_MASK}});

    // rock | flag text | flag
    checkMoveSimple(
        new int[]{
            Tiles.BABA_MASK,
            Tiles.ROCK_MASK,
            Tiles.FLAG_TEXT_MASK,
            Tiles.FLAG_MASK},
        (byte) -1,
        new int[][]{new int[]{
            Tiles.EMPTY,
            Tiles.BABA_MASK,
            Tiles.ROCK_MASK,
            Tiles.FLAG_TEXT_MASK | Tiles.FLAG_MASK}});

    // rock | rock | wall | empty
    checkMoveSimple(
        new int[]{
            Tiles.BABA_MASK,
            Tiles.ROCK_MASK,
            Tiles.ROCK_MASK,
            Tiles.WALL_MASK,
            Tiles.EMPTY},
        (byte) -1,
        new int[0][]);

    // rock + flag | rock | empty
    checkMoveSimple(
        new int[]{
            Tiles.BABA_MASK,
            Tiles.ROCK_MASK | Tiles.FLAG_MASK,
            Tiles.ROCK_MASK,
            Tiles.EMPTY},
        Direction.RIGHT,
        new int[0][]);
  }

}
