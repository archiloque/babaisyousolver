package net.archiloque.babaisyousolver;

/**
 * All the tiles.
 * Should be declared in sorted order so the index
 * of the items in {@link Tiles#ALL_STRINGS}
 * matches the values of the int items.
 */
interface Tiles {

  String BABA_STRING = "Baba";
  String BABA_TEXT_STRING = "Baba text";
  String EMPTY_STRING = "empty";
  String FLAG_STRING = "flag";
  String FLAG_TEXT_STRING = "flag text";
  String IS_TEXT_STRING = "is text";
  String PUSH_TEXT_STRING = "push text";
  String ROCK_STRING = "rock";
  String ROCK_TEXT_STRING = "rock text";
  String STOP_TEXT_STRING = "stop text";
  String WALL_STRING = "wall";
  String WALL_TEXT_STRING = "wall text";
  String WIN_TEXT_STRING = "win text";
  String YOU_TEXT_STRING = "you text";

  String[] ALL_STRINGS = new String[]{
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

  int BABA = 0;
  int BABA_TEXT = BABA + 1;
  int EMPTY = BABA_TEXT + 1;
  int FLAG = EMPTY + 1;
  int FLAG_TEXT = FLAG + 1;
  int IS_TEXT = FLAG_TEXT + 1;
  int PUSH_TEXT = IS_TEXT + 1;
  int ROCK = PUSH_TEXT + 1;
  int ROCK_TEXT = ROCK + 1;
  int STOP_TEXT = ROCK_TEXT + 1;
  int WALL = STOP_TEXT + 1;
  int WALL_TEXT = WALL + 1;
  int WIN_TEXT = WALL_TEXT + 1;
  int YOU_TEXT = WIN_TEXT + 1;
}
