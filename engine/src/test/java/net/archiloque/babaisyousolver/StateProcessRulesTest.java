package net.archiloque.babaisyousolver;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StateProcessRulesTest {

  void testRules(
      int levelWidth,
      int levelHeight,
      @NotNull int[] stateContent,
      int youTiles,
      int pushTiles,
      int stopTiles,
      int winTiles
  ) throws SQLException {
    Level level = new Level(
        levelWidth,
        levelHeight,
        new int[levelHeight * levelWidth]
    );
    State state = new State(
        level,
        stateContent,
        0
    );
    state.processRules();
    assertEquals(state.youTilesMask, youTiles);
    assertEquals(state.pushTilesMask, pushTiles);
    assertEquals(state.stopTilesMask, stopTiles);
    assertEquals(state.winTilesMask, winTiles);
  }

  void testRules(
      int levelWidth,
      int levelHeight,
      @NotNull int[] stateContent,
      int youTiles
  ) throws SQLException {
    testRules(
        levelWidth,
        levelHeight,
        stateContent,
        youTiles,
        Tiles.TEXT_MASKS,
        Tiles.EMPTY,
        Tiles.EMPTY);
  }

  @Test
  void simpleHorizontalRule() throws SQLException {
    testRules(
        1,
        3,
        new int[]{
            Tiles.BABA_TEXT_MASK,
            Tiles.IS_TEXT_MASK,
            Tiles.YOU_TEXT_MASK},
        Tiles.BABA_MASK
    );
  }

  @Test
  void simpleVerticalRule() throws SQLException {
    testRules(
        3,
        1,
        new int[]{
            Tiles.BABA_TEXT_MASK,
            Tiles.IS_TEXT_MASK,
            Tiles.YOU_TEXT_MASK},
        Tiles.BABA_MASK
    );
  }

  @Test
  void simpleBadOrder() throws SQLException {
    testRules(
        3,
        1,
        new int[]{
            Tiles.BABA_TEXT_MASK,
            Tiles.YOU_TEXT_MASK,
            Tiles.IS_TEXT_MASK},
        Tiles.EMPTY
    );
    testRules(
        3,
        1,
        new int[]{
            Tiles.IS_TEXT_MASK,
            Tiles.YOU_TEXT_MASK,
            Tiles.BABA_TEXT_MASK},
        Tiles.EMPTY
    );
  }

  @Test
  void simpleBorder() throws SQLException {
    testRules(
        2,
        1,
        new int[]{
            Tiles.BABA_TEXT_MASK,
            Tiles.IS_TEXT_MASK,
            Tiles.YOU_TEXT_MASK,
            Tiles.EMPTY},
        Tiles.EMPTY
    );
    testRules(
        2,
        1,
        new int[]{
            Tiles.EMPTY,
            Tiles.BABA_TEXT_MASK,
            Tiles.IS_TEXT_MASK,
            Tiles.YOU_TEXT_MASK},
        Tiles.EMPTY
    );
  }

  @Test
  void simpleYouRule() throws SQLException {
    testRules(
        1,
        3,
        new int[]{
            Tiles.BABA_TEXT_MASK,
            Tiles.IS_TEXT_MASK,
            Tiles.YOU_TEXT_MASK},
        Tiles.BABA_MASK
    );
  }

  @Test
  void simplePushRule() throws SQLException {
    testRules(
        1,
        3,
        new int[]{
            Tiles.BABA_TEXT_MASK,
            Tiles.IS_TEXT_MASK,
            Tiles.PUSH_TEXT_MASK},
        Tiles.EMPTY,
        Tiles.TEXT_MASKS | Tiles.BABA_MASK,
        Tiles.EMPTY,
        Tiles.EMPTY
    );
  }

  @Test
  void simpleStopRule() throws SQLException {
    testRules(
        1,
        3,
        new int[]{
            Tiles.BABA_TEXT_MASK,
            Tiles.IS_TEXT_MASK,
            Tiles.STOP_TEXT_MASK},
        Tiles.EMPTY,
        Tiles.TEXT_MASKS,
        Tiles.BABA_MASK,
        Tiles.EMPTY
    );
  }

  @Test
  void simpleWinRule() throws SQLException {
    testRules(
        1,
        3,
        new int[]{
            Tiles.BABA_TEXT_MASK,
            Tiles.IS_TEXT_MASK,
            Tiles.WIN_TEXT_MASK},
        Tiles.EMPTY,
        Tiles.TEXT_MASKS,
        Tiles.EMPTY,
        Tiles.BABA_MASK
    );
  }
}
