package net.archiloque.babaisyousolver;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class Level {

  final int width;

  final int height;

  final @NotNull int[] content;

  final @NotNull FiFoQueue<State> states = new FiFoQueue<>();

  Level(
      int width,
      int height,
      @NotNull int[] content) {
    this.width = width;
    this.height = height;
    this.content = content;
  }

  void createInitStates() {
    State state = new State(this, content);
    states.add(state);
  }

  void addState(@NotNull int[] content) {
    states.add(new State(this, content));
  }

  @Nullable State solve() {
    while (!states.isEmpty()) {
      State state = states.pop();
      if (state.processState()) {
        return state;
      }
    }
    return null;
  }

}
