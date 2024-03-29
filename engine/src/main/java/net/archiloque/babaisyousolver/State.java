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

  int pushTilesMask = Tiles.TEXT_MASKS;

  int stopTilesMask = Tiles.EMPTY;

  int youTilesMask = Tiles.EMPTY;

  int winTilesMask = Tiles.EMPTY;

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
   * @return a list of {@link Direction} if we found a solution,
   * else null
   */
  @Nullable byte[] processState() {
    processRules();
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
   *
   * @return a list of {@link Direction} if we found a solution,
   * else null
   */
  @Nullable byte[] tryToGo(
      int currentPosition,
      byte direction) {
    int targetPosition = calculatePosition(currentPosition, direction);
    int targetPositionContent = content[targetPosition];

    // target contains something that stops me
    if ((targetPositionContent & stopTilesMask) != Tiles.EMPTY) {
      return null;
    }
    int[] newContent = content.clone();

    // target is empty
    if (targetPositionContent == Tiles.EMPTY) {
      newContent[targetPosition] |= Tiles.BABA_MASK;
      newContent[currentPosition] ^= Tiles.BABA_MASK;
      level.addState(newContent, addMovement(direction));
      return null;
    }

    int currentPushingMask = targetPositionContent & pushTilesMask;
    if (currentPushingMask != Tiles.EMPTY) {
      // remove the pushed elements
      targetPositionContent &= (~pushTilesMask);

      int candidatePosition = targetPosition;
      // explore the next cells until we find the right stop
      // or until we are blocked or the end of the level
      while (currentPushingMask != Tiles.EMPTY) {
        // did we reach the border of the level?
        if (!canGoThere(candidatePosition, direction)) {
          return null;
        }
        // the position behind the current position
        int behindCandidatePosition =
            calculatePosition(candidatePosition, direction);
        int behindCandidatePositionContent =
            newContent[behindCandidatePosition];

        // is it something that stop me
        if ((behindCandidatePositionContent & stopTilesMask) != Tiles.EMPTY) {
          return null;
        }

        // is it another thing that should be pushed?
        int behindCandidatePushingMask = behindCandidatePositionContent & pushTilesMask;
        if ((behindCandidatePushingMask) != Tiles.EMPTY) {
          // yes another thing to push

          // remove the pushed thing from next cell
          // and add the thing that was being pushed
          behindCandidatePositionContent =
              behindCandidatePositionContent &
                  (~behindCandidatePushingMask) |
                  currentPushingMask;
          newContent[behindCandidatePosition] = behindCandidatePositionContent;
          currentPushingMask = behindCandidatePushingMask;
          candidatePosition = behindCandidatePosition;
        } else {
          // we found a cell that suits us!
          // add the thing that was being pushed

          // we build the new content
          newContent[targetPosition] = targetPositionContent;
          // add the thing that was being pushed
          newContent[behindCandidatePosition] |= currentPushingMask;
          currentPushingMask = Tiles.EMPTY;
        }
      }
    }

    if ((targetPositionContent & winTilesMask) != Tiles.EMPTY) {
      return addMovement(direction);
    }

    // move Baba
    newContent[targetPosition] |= Tiles.BABA_MASK;
    newContent[currentPosition] ^= Tiles.BABA_MASK;
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
    return switch (direction) {
      // up: go back one row
      case Direction.UP -> position - level.width;
      // down: go further one row
      case Direction.DOWN -> position + level.width;
      // left : go back one item
      case Direction.LEFT -> position - 1;
      // right : go further one item
      case Direction.RIGHT -> position + 1;
      default -> throw new IllegalArgumentException(Integer.toString(direction));
    };
  }

  /**
   * Test if we can go on a direction from a position
   */
  private boolean canGoThere(int targetPosition, byte direction) {
    int targetPositionLine = targetPosition / level.width;
    int targetPositionColumn = targetPosition % level.width;

    return switch (direction) {
      case Direction.UP -> targetPositionLine != 0;
      case Direction.DOWN -> targetPositionLine != (level.height - 1);
      case Direction.LEFT -> targetPositionColumn != 0;
      case Direction.RIGHT -> targetPositionColumn != (level.width - 1);
      default -> throw new IllegalArgumentException(Integer.toString(direction));
    };
  }

  void processRules() {
    // locate the "IS"
    for (int i = 0; i < level.size; i++) {
      if ((content[i] & Tiles.IS_TEXT_MASK) != Tiles.EMPTY) {
        // any room to make an horizontal sentence ?
        int isLine = i / level.width;
        if ((isLine > 0) && (isLine < (level.height - 1))) {
          checkRule(
              i - level.width,
              i + level.width);
        }

        // any room to make a vertical sentence ?
        int isColumn = i % level.width;
        if ((isColumn > 0) && (isColumn < (level.width - 1))) {
          checkRule(
              i - 1,
              i + 1);
        }
      }
    }
  }

  private void checkRule(
      int beforeCellIndex,
      int afterCellIndex) {
    // validate it's a rule
    int subject = content[beforeCellIndex] &
        Tiles.SUBJECT_MASKS;
    if (subject == Tiles.EMPTY) {
      return;
    }
    int definition = content[afterCellIndex] &
        Tiles.DEFINITION_MASKS;
    if (definition == Tiles.EMPTY) {
      return;
    }

    // apply the result
    int targetMask = Tiles.getTarget(subject);
    switch (definition) {
      case Tiles.PUSH_TEXT_MASK -> pushTilesMask |= targetMask;
      case Tiles.STOP_TEXT_MASK -> stopTilesMask |= targetMask;
      case Tiles.WIN_TEXT_MASK -> winTilesMask |= targetMask;
      case Tiles.YOU_TEXT_MASK -> youTilesMask |= targetMask;
      default -> throw new IllegalArgumentException(Integer.toString(definition));
    }
  }

  /**
   * Find the index of the baba position.
   *
   * @return the position or -1 if not found
   */
  private int findBaba() {
    for (int i = 0; i < level.size; i++) {
      if ((content[i] & Tiles.BABA_MASK) != Tiles.EMPTY) {
        return i;
      }
    }
    return -1;
  }
}
