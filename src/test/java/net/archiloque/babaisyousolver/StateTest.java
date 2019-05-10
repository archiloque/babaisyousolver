package net.archiloque.babaisyousolver;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class StateTest {

  private static final int[] UP = new int[]{-1, 0};
  private static final int[] DOWN = new int[]{1, 0};
  private static final int[] LEFT = new int[]{0, -1};
  private static final int[] RIGHT = new int[]{0, 1};

  /**
   * {@link State} that records the possible moves
   */
  private static final class StateToTestAvailableMovements extends State {

    private final List<int[]> movements = new ArrayList<>();

    StateToTestAvailableMovements(
        @NotNull Level level,
        @NotNull int[] content) {
      super(level, content);
    }

    @Override
    boolean tryToGo(int babaPosition, int deltaLine, int deltaColumn) {
      movements.add(new int[]{deltaLine, deltaColumn});
      return false;
    }
  }

  private void checkAvailableMovements(
      int babaIndex,
      @NotNull int[][] movements) {
    int[] levelContent = new int[9];
    Arrays.fill(levelContent, Tiles.EMPTY);
    levelContent[babaIndex] = Tiles.BABA;
    Level level = new Level(3, 3, levelContent);
    StateToTestAvailableMovements state =
        new StateToTestAvailableMovements(level, levelContent);
    state.processState();
    assertArrayEquals(movements, state.movements.toArray());
  }

  @Test
  void testAvailableMovement() {
    checkAvailableMovements(0,
        new int[][]{DOWN, RIGHT});
    checkAvailableMovements(1,
        new int[][]{DOWN, LEFT, RIGHT});
    checkAvailableMovements(2,
        new int[][]{DOWN, LEFT});
    checkAvailableMovements(3,
        new int[][]{UP, DOWN, RIGHT});
    checkAvailableMovements(4,
        new int[][]{UP, DOWN, LEFT, RIGHT});
    checkAvailableMovements(5,
        new int[][]{UP, DOWN, LEFT});
    checkAvailableMovements(6,
        new int[][]{UP, RIGHT});
    checkAvailableMovements(7,
        new int[][]{UP, LEFT, RIGHT});
    checkAvailableMovements(8,
        new int[][]{UP, LEFT});
  }

}
