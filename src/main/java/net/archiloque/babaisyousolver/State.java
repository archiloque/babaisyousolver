package net.archiloque.babaisyousolver;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class State {

  private final @NotNull Level level;

  /**
   * Probably not the right format, just drafting
   */
  private final @NotNull int[] content;

  private final @NotNull byte[] previousMovements;

  State(
      @NotNull Level level,
      @NotNull int[] content,
      @NotNull byte[] movements) {
    this.level = level;
    this.content = content;
    this.previousMovements = movements;
  }

  /**
   * Process the current state
   *
   * @return the path to the solution if found, null if not.
   */
  @Nullable byte[] processState() {
    int babaPosition = findBaba();
    int babaLine = babaPosition / level.width;
    int babaColumn = babaPosition % level.width;

    byte[] result;
    // Up
    if (babaLine > 0) {
      result = tryToGo(babaPosition, Direction.UP);
      if (result != null) {
        return result;
      }
    }

    // Down
    if (babaLine < (level.height - 1)) {
      result = tryToGo(babaPosition, Direction.DOWN);
      if (result != null) {
        return result;
      }
    }

    // Left
    if (babaColumn > 0) {
      result = tryToGo(babaPosition, Direction.LEFT);
      if (result != null) {
        return result;
      }
    }

    // Right
    if (babaColumn < (level.width - 1)) {
      result = tryToGo(babaPosition, Direction.RIGHT);
      if (result != null) {
        return result;
      }
    }

    return null;
  }

  @Nullable byte[] tryToGo(
      int currentPosition,
      byte direction) {
    int targetPosition = calculatePosition(currentPosition, direction);
    int targetPositionContent = content[targetPosition];

    int[] newContent;
    switch (targetPositionContent) {
      case Tiles.WALL:
        return null;
      case Tiles.EMPTY:
        newContent = content.clone();
        newContent[targetPosition] = Tiles.BABA;
        newContent[currentPosition] = Tiles.EMPTY;
        level.addState(newContent, addMovement(direction));
        return null;
      case Tiles.ROCK:
        // did we reach the border of the level ?
        if (!canGoThere(targetPosition, direction)) {
          return null;
        }
        // the position behind  the rock
        int behindTheRockPosition = calculatePosition(targetPosition, direction);
        int behindTheRockPositionContent = content[behindTheRockPosition];
        // it it empty?
        if (behindTheRockPositionContent != Tiles.EMPTY) {
          return null;
        }
        // nice, we build the new content
        newContent = content.clone();
        newContent[targetPosition] = Tiles.BABA;
        newContent[currentPosition] = Tiles.EMPTY;
        newContent[behindTheRockPosition] = Tiles.ROCK;
        level.addState(newContent, addMovement(direction));
        return null;
      case Tiles.FLAG:
        return addMovement(direction);
      default:
        throw new IllegalArgumentException("" + targetPositionContent);
    }
  }

  /**
   * Add a new movement at the end of the array
   */
  private @NotNull byte[] addMovement(byte movement) {
    int previousLength = previousMovements.length;
    byte[] result = new byte[previousLength + 1];
    System.arraycopy(previousMovements, 0, result, 0, previousLength);
    result[previousLength] = movement;
    return result;
  }

  private int calculatePosition(int position, byte direction) {
    switch (direction) {
      case Direction.UP:
        return position - level.width;
      case Direction.DOWN:
        return position + level.width;
      case Direction.LEFT:
        return position - 1;
      case Direction.RIGHT:
        return position + 1;
      default:
        throw new IllegalArgumentException("" + direction);
    }
  }

  /**
   * Test if we can go on a direction from a position
   */
  private boolean canGoThere(int targetPosition, byte direction) {
    int targetPositionLine = targetPosition / level.width;
    int targetPositionColumn = targetPosition % level.width;

    switch (direction) {
      case Direction.UP:
        return targetPositionLine != 0;
      case Direction.DOWN:
        return targetPositionLine != (level.height - 1);
      case Direction.LEFT:
        return targetPositionColumn == 0;
      case Direction.RIGHT:
        return targetPositionColumn != (level.width - 1);
      default:
        throw new IllegalArgumentException("" + direction);
    }
  }

  /**
   * Find the index of the baba position.
   *
   * @return the position or -1 if not found
   */
  private int findBaba() {
    for (int i = 0; i < content.length; i++) {
      if (content[i] == Tiles.BABA) {
        return i;
      }
    }
    return -1;
  }
}
