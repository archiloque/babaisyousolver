package net.archiloque.babaisyousolver;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.archiloque.babaisyousolver.Direction.DOWN;
import static net.archiloque.babaisyousolver.Direction.LEFT;
import static net.archiloque.babaisyousolver.Direction.RIGHT;
import static net.archiloque.babaisyousolver.Direction.UP;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * Try every positions on a 3*3 level
 * and check where we can go
 */
class StateAvailableMovementsTest {

  /**
   * {@link State} that records the possible moves
   */
  private static final class StateToTestAvailableMovements extends State {

    private final List<Character> movements = new ArrayList<>();

    StateToTestAvailableMovements(
        @NotNull Level level,
        @NotNull int[] content) {
      super(level, content, new char[0]);
    }

    @Override
    char[] tryToGo(int currentPosition, char position) {
      movements.add(position);
      return null;
    }
  }

  private void checkAvailableMovements(
      int babaIndex,
      @NotNull Character[] movements) {
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
        new Character[]{DOWN, RIGHT});
    checkAvailableMovements(1,
        new Character[]{DOWN, LEFT, RIGHT});
    checkAvailableMovements(2,
        new Character[]{DOWN, LEFT});
    checkAvailableMovements(3,
        new Character[]{UP, DOWN, RIGHT});
    checkAvailableMovements(4,
        new Character[]{UP, DOWN, LEFT, RIGHT});
    checkAvailableMovements(5,
        new Character[]{UP, DOWN, LEFT});
    checkAvailableMovements(6,
        new Character[]{UP, RIGHT});
    checkAvailableMovements(7,
        new Character[]{UP, LEFT, RIGHT});
    checkAvailableMovements(8,
        new Character[]{UP, LEFT});
  }

}
