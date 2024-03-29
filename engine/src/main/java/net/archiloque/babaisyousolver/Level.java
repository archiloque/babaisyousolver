package net.archiloque.babaisyousolver;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

class Level {

  final int width;
  final int height;
  final int size;

  /**
   * This set will be able to handle duplication of {@link State}
   * The custom {@link Comparator} is required to avoid
   * only comparing the arrays' addresses
   */
  private final Set<int[]> pastStates =
      new TreeSet<>(new Comparator<>() {
        @Override
        public int compare(int[] o1, int[] o2) {
          for (int i = 0; i < size; i++) {
            int cmp = o2[i] - o1[i];
            if (cmp != 0) {
              return cmp;
            }
          }
          return 0;
        }
      });

  private final @NotNull int[] originalContent;

  private final @NotNull FiFoQueue<StateLite> states =
      new FiFoQueue<>();

  Level(
      int width,
      int height,
      @NotNull int[] originalContent) {
    this.width = width;
    this.height = height;
    this.size = width * height;
    this.originalContent = originalContent;
  }

  void createInitStates() {
    int[] contentForState = new int[size];
    for (int i = 0; i < size; i++) {
      int originalTile = originalContent[i];
      if (originalTile != 0) {
        // the bit mask is derived from the tile index
        contentForState[i] = (1 << originalTile - 1);
      }
    }
    addState(contentForState, new byte[0]);
  }

  void addState(
      @NotNull int[] content,
      @NotNull byte[] movements
  ) {
    // only add if not already visited
    if (pastStates.add(content)) {
      states.add(new StateLite(content, movements));
    }
  }

  @Nullable byte[] solve() {
    byte[] result;
    while (!states.isEmpty()) {
      StateLite state = states.pop();
      result = new State(
          this,
          state.content,
          state.previousMovements).
          processState();
      if (result != null) {
        return result;
      }
    }
    return null;
  }

  static class StateLite {
    private final @NotNull int[] content;

    private final @NotNull byte[] previousMovements;

    StateLite(@NotNull int[] content, @NotNull byte[] previousMovements) {
      this.content = content;
      this.previousMovements = previousMovements;
    }
  }

}
