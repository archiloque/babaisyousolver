package net.archiloque.babaisyousolver;

/**
 * All the tiles.
 * Should be declared in sorted order so the index
 * of the items in {@link Tiles#ALL_STRINGS}
 * matches the values of the int items.
 */
interface Tiles {

  String EMPTY_STRING = "empty";
  String BABA_STRING = "Baba";
  String BABA_TEXT_STRING = "Baba text";
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
      EMPTY_STRING,
      BABA_STRING,
      BABA_TEXT_STRING,
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

  int EMPTY = 0;
  int BABA = EMPTY + 1;
  int BABA_TEXT = BABA + 1;
  int FLAG = BABA_TEXT + 1;
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

  int BABA_MASK = 1;
  int BABA_TEXT_MASK = 1 << 1;
  int FLAG_MASK = 1 << 2;
  int FLAG_TEXT_MASK = 1 << 3;
  int IS_TEXT_MASK = 1 << 4;
  int PUSH_TEXT_MASK = 1 << 5;
  int ROCK_MASK = 1 << 6;
  int ROCK_TEXT_MASK = 1 << 7;
  int STOP_TEXT_MASK = 1 << 8;
  int WALL_MASK = 1 << 10;
  int WALL_TEXT_MASK = 1 << 11;
  int WIN_TEXT_MASK = 1 << 12;
  int YOU_TEXT_MASK = 1 << 13;

}
