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
    int targetPosition = calculatePosition(position, direction);
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
        // did we reach the border of the level ?
        if(!canGoThere(targetPosition, direction)) {
          return false;
        }
        // the position after the rock

        // @TODO implements this
        return false;
      case Tiles.FLAG:
        return true;
      default:
        throw new IllegalArgumentException("" + targetPositionContent);
    }
  }

  private int calculatePosition(int position, char direction) {
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
  private boolean canGoThere(int targetPosition, char direction) {
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
