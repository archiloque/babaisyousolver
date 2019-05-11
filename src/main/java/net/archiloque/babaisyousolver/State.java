package net.archiloque.babaisyousolver;

import org.jetbrains.annotations.NotNull;

class State {

  private final @NotNull Level level;

  /**
   * Probably not the right format, just drafting
   */
  private final @NotNull int[] content;

  State(
      @NotNull Level level,
      @NotNull int[] content) {
    this.level = level;
    this.content = content;
  }

  /**
   * Process the current state
   *
   * @return true if we found a solution
   */
  boolean processState() {
    int babaPosition = findBaba();
    int babaLine = babaPosition / level.width;
    int babaColumn = babaPosition % level.width;

    // Up
    if (babaLine > 0) {
      if (tryToGo(babaPosition, Direction.UP)) {
        return true;
      }
    }

    // Down
    if (babaLine < (level.height - 1)) {
      if (tryToGo(babaPosition, Direction.DOWN)) {
        return true;
      }
    }

    // Left
    if (babaColumn > 0) {
      if (tryToGo(babaPosition, Direction.LEFT)) {
        return true;
      }
    }

    // Right
    if (babaColumn < (level.width - 1)) {
      if (tryToGo(babaPosition, Direction.RIGHT)) {
        return true;
      }
    }

    return false;
  }

  boolean tryToGo(int position, char direction) {
    int targetPosition;
    switch (direction) {
      case Direction.UP:
        targetPosition = position - level.width;
        break;
      case Direction.DOWN:
        targetPosition = position + level.width;
        break;
      case Direction.LEFT:
        targetPosition = position - 1;
        break;
      case Direction.RIGHT:
        targetPosition = position + 1;
        break;
      default:
        throw new IllegalArgumentException("" + direction);
    }

    int targetPositionContent = content[targetPosition];

    int[] newContent;
    switch (targetPositionContent) {
      case Tiles.WALL:
        return false;
      case Tiles.EMPTY:
        newContent = content.clone();
        newContent[targetPosition] = Tiles.BABA;
        newContent[position] = Tiles.EMPTY;
        level.addState(newContent);
        return false;
      case Tiles.ROCK:
        // @TODO implements this
        return false;
      case Tiles.FLAG:
        return true;
      default:
        throw new IllegalArgumentException("" + targetPositionContent);
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
