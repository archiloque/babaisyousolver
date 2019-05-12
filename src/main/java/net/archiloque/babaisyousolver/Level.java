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
  private final Set<int[]> pastStates = new TreeSet<>(new Comparator<>() {
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

  final @NotNull int[] content;

  final @NotNull FiFoQueue<State> states = new FiFoQueue<>();

  Level(
      int width,
      int height,
      @NotNull int[] content) {
    this.width = width;
    this.height = height;
    this.size = width * height;
    this.content = content;
  }

  void createInitStates() {
    addState(content, new byte[0]);
  }

  void addState(
      @NotNull int[] content,
      @NotNull byte[] movements
  ) {
    if (!pastStates.contains(content)) {
      pastStates.add(content);
      states.add(new State(this, content, movements));
    }
  }

  @Nullable byte[] solve() {
    byte[] result;
    while (!states.isEmpty()) {
      State state = states.pop();
      result = state.processState();
      if (result != null) {
        return result;
      }
    }
    return null;
  }

}
