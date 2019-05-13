package net.archiloque.babaisyousolver;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class State {

  private final @NotNull Level level;

  /**
   * Bit field, contain values at positions indicated by the items in
   * {@link Tiles}
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
   * @return a list of {@link Direction} if we found a solution,
   * else null
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

  /**
   * Try to go on a direction from a position
   * @return a list of {@link Direction} if we found a solution,
   * else null
   */
  @Nullable byte[] tryToGo(
      int currentPosition,
      byte direction) {
    int targetPosition = calculatePosition(currentPosition, direction);
    int targetPositionContent = content[targetPosition];

    int[] newContent;
    if((targetPositionContent & Tiles.WALL_MASK) != 0) {
      return null;
    }
    newContent = content.clone();

    // target is empty
    if(targetPositionContent == 0) {
      newContent[targetPosition] = newContent[targetPosition] | Tiles.BABA_MASK;
      newContent[currentPosition] = newContent[currentPosition] ^ Tiles.BABA_MASK;
      level.addState(newContent, addMovement(direction));
      return null;
    }
    if((targetPositionContent & Tiles.ROCK_MASK) != 0) {
      // did we reach the border of the level ?
      if (!canGoThere(targetPosition, direction)) {
        return null;
      }
      // the position behind  the rock
      int behindTheRockPosition = calculatePosition(targetPosition, direction);
      int behindTheRockPositionContent = content[behindTheRockPosition];
      // it it a rock
      if ((behindTheRockPositionContent & (Tiles.ROCK_MASK | Tiles.WALL_MASK)) != 0) {
        return null;
      }
      // nice, we build the new content
      targetPositionContent = newContent[targetPosition] ^ Tiles.ROCK_MASK;
      newContent[targetPosition] = targetPositionContent;
      newContent[behindTheRockPosition] = newContent[behindTheRockPosition] | Tiles.ROCK_MASK;
    }
    if((targetPositionContent & Tiles.FLAG_MASK) != 0) {
      return addMovement(direction);
    }

    newContent[targetPosition] = newContent[targetPosition] | Tiles.BABA_MASK;
    newContent[currentPosition] = newContent[currentPosition] ^ Tiles.BABA_MASK;
    level.addState(newContent, addMovement(direction));
    return null;
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

  /**
   * Calculate the index of a position after a move
   */
  private int calculatePosition(int position, byte direction) {
    // Content is stored as a single array one line after another
    switch (direction) {
      case Direction.UP:
        // up: go back one row
        return position - level.width;
      case Direction.DOWN:
        // down: go further one row
        return position + level.width;
      case Direction.LEFT:
        // left : go back one item
        return position - 1;
      case Direction.RIGHT:
        // left : go further one item
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
      if ((content[i] & Tiles.BABA_MASK) != 0) {
        return i;
      }
    }
    return -1;
  }
}
