package net.archiloque.babaisyousolver;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Entry point
 */
public class App {

  private static final SimpleDateFormat DATE_FORMAT =
      new SimpleDateFormat("yyyy-MM-dd kk:mm:ss.SSS");

  public static void main(
      @NotNull String[] args)
      throws IOException {
    if (args.length == 0) {
      throw new IllegalArgumentException(
          "A level path should be specified");
    }
    processLevel(Path.of(args[0]));

  }

  private static final String SOLUTION_FILES = "solution.txt";

  private static void processLevel(
      @NotNull Path path
  ) throws IOException {
    print(path, "Reading level");
    LevelReader.LevelReaderResult levelReaderResult =
        LevelReader.readLevel(path);
    Level level = new Level(
        levelReaderResult.width,
        levelReaderResult.height,
        levelReaderResult.content);
    level.createInitStates();
    Path solutionFile = path.resolve(SOLUTION_FILES);
    Files.deleteIfExists(solutionFile);
    print(path, "Solving level");
    long startTime = System.nanoTime();
    byte[] solution = level.solve();
    long stopTime = System.nanoTime();
    String endTime =
        LocalTime.MIN.plusNanos((stopTime - startTime)).
            toString();
    if (solution != null) {
      print(
          path,
          "Solved in " + endTime);
      writeSolution(solution, solutionFile);
    } else {
      print(
          path,
          "Failed in " + endTime);
    }
  }

  private static void writeSolution(
      @NotNull byte[] solution,
      @NotNull Path solutionPath)
      throws IOException {
    List<String> steps = new ArrayList<>();
    byte currentMovement = -1;
    int numberOfMovesThisWay = 1;
    for (byte c : solution) {
      if (c != currentMovement) {
        if (currentMovement != -1) {
          steps.
              add(
                  Integer.toString(numberOfMovesThisWay) +
                      Direction.VISUAL[currentMovement]);
        }
        currentMovement = c;
        numberOfMovesThisWay = 1;
      } else {
        numberOfMovesThisWay += 1;
      }
    }
    // complete with last move
    steps.
        add(
            Integer.toString(numberOfMovesThisWay) +
                Direction.VISUAL[currentMovement]);

    String content = String.join(" ", steps);
    Files.write(solutionPath, content.getBytes());
  }

  private static void print(
      @NotNull Path path,
      @NotNull String message) {
    System.out.println(
        DATE_FORMAT.format(new Date()) +
            " " +
            path.toAbsolutePath() +
            " " +
            message);
  }

}
