package net.archiloque.babaisyousolver;

interface Direction {

  byte UP = 0;
  byte DOWN = 1;
  byte LEFT = 2;
  byte RIGHT = 3;

  byte NO_DIRECTION = -1;

  char[] VISUAL = new char[]{
      '↑', '↓', '←', '→'
  };

}
