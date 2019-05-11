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
        @NotNull char[] movement) {
      states.add(content);
    }
  }

  /**
   * Cases are tested with a level of ?x1
   * Baba is in the first position and tries to go left
   */
  void checkMoveSimple(
      int[] content,
      @Nullable char[] result,
      @NotNull int[][] possibleNextMoves) {
    LevelToTestTryToGo level = new LevelToTestTryToGo(
        content.length,
        1,
        content);
    State state = new State(level, content, new char[0]);
    assertArrayEquals(
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
    checkMoveSimple(
        new int[]{Tiles.BABA, Tiles.EMPTY},
        null,
        new int[][]{new int[]{Tiles.EMPTY, Tiles.BABA}}
    );
  }

  @Test
  void testMoveWall() {
    checkMoveSimple(
        new int[]{Tiles.BABA, Tiles.WALL},
        null,
        new int[0][]
    );
  }

  @Test
  void testMoveFlag() {
    checkMoveSimple(
        new int[]{Tiles.BABA, Tiles.FLAG},
        new char[]{Direction.RIGHT},
        new int[0][]
    );
  }

  @Test
  void testMoveRock() {
    checkMoveSimple(
        new int[]{Tiles.BABA, Tiles.ROCK},
        null,
        new int[0][]
    );

    checkMoveSimple(
        new int[]{Tiles.BABA, Tiles.ROCK, Tiles.ROCK},
        null,
        new int[0][]
    );

    checkMoveSimple(
        new int[]{Tiles.BABA, Tiles.ROCK, Tiles.EMPTY},
        null,
        new int[][]{new int[]{Tiles.EMPTY, Tiles.BABA, Tiles.ROCK}}
    );
  }

}
