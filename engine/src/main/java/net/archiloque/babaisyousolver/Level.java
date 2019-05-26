package net.archiloque.babaisyousolver;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

class Level {

  final int width;
  final int height;
  final int size;
  private final Database database;
  byte[] possibleTiles = new byte[4096 * 2];
  int[] reversePossibleTiles = new int[Byte.MAX_VALUE];
  byte currentTileNumber = 0;
  final int databaseHeaderSize;

  private final @NotNull int[] originalContent;


  Level(
      int width,
      int height,
      @NotNull int[] originalContent) {
    this.width = width;
    this.height = height;
    this.size = width * height;
    databaseHeaderSize = (int) (Math.ceil(((float)size) / 8));
    this.originalContent = originalContent;
    this.database = new Database();
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
    addState(contentForState, (byte) -1, -1);
  }

  void addState(
      @NotNull int[] content,
      byte movement,
      int parent
  ) {
    database.addCandidate(content, this, movement, parent);
  }

  @Nullable byte[] solve() {
    while (true) {
      State state = database.nextCandidate(this);
      if (state == null) {
        return null;
      }
      byte result = state.
          processState();
      if (result != -1) {
        List<Byte> resultPath = new ArrayList<>();
        resultPath.add(result);
        database.fetchParents(state.id, resultPath);
        byte[] resultArray = new byte[resultPath.size()];
        for (int i = 0; i < resultPath.size(); i++) {
          resultArray[resultPath.size() - i - 1] = resultPath.get(i);
        }
        return resultArray;
      }
    }
  }

}
