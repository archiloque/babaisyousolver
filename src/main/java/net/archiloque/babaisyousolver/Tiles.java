package net.archiloque.babaisyousolver;

/**
 * All the tiles.
 * Should be declared in sorted order so the index
 * of the items in {@link Tiles#ALL_STRINGS}
 * matches the values of the int items.
 */
final class Tiles {

  private Tiles() {
  }

  static final String BABA_STRING = "Baba";
  static final String BABA_TEXT_STRING = "Baba text";
  static final String EMPTY_STRING = "empty";
  static final String FLAG_STRING = "flag";
  static final String FLAG_TEXT_STRING = "flag text";
  static final String IS_TEXT_STRING = "is text";
  static final String PUSH_TEXT_STRING = "push text";
  static final String ROCK_STRING = "rock";
  static final String ROCK_TEXT_STRING = "rock text";
  static final String STOP_TEXT_STRING = "stop text";
  static final String WALL_STRING = "wall";
  static final String WALL_TEXT_STRING = "wall text";
  static final String WIN_TEXT_STRING = "win text";
  static final String YOU_TEXT_STRING = "you text";

  static final String[] ALL_STRINGS = new String[]{
      BABA_STRING,
      BABA_TEXT_STRING,
      EMPTY_STRING,
      FLAG_STRING,
      FLAG_TEXT_STRING,
      IS_TEXT_STRING,
      PUSH_TEXT_STRING,
      ROCK_STRING,
      ROCK_TEXT_STRING,
      STOP_TEXT_STRING,
      WALL_STRING,
      WALL_TEXT_STRING,
      WIN_TEXT_STRING,
      YOU_TEXT_STRING,
  };

  static final int BABA = 0;
  static final int BABA_TEXT = BABA + 1;
  static final int EMPTY = BABA_TEXT + 1;
  static final int FLAG = EMPTY + 1;
  static final int FLAG_TEXT = FLAG + 1;
  static final int IS_TEXT = FLAG_TEXT + 1;
  static final int PUSH_TEXT = IS_TEXT + 1;
  static final int ROCK = PUSH_TEXT + 1;
  static final int ROCK_TEXT = ROCK + 1;
  static final int STOP_TEXT = ROCK_TEXT + 1;
  static final int WALL = STOP_TEXT + 1;
  static final int WALL_TEXT = WALL + 1;
  static final int WIN_TEXT = WALL_TEXT + 1;
  static final int YOU_TEXT = WIN_TEXT + 1;
}
