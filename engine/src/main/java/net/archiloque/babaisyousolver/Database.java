package net.archiloque.babaisyousolver;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

class Database {

  private static final String FIND_STATE_BY_CONTENT =
      "select count(*) from states where content = ?";

  private static final String INSERT_STATE =
      "insert into states (content, parent, direction) values (?, ?, ?)";

  private static final String INSERT_UNPROCESSED_STATE =
      "insert into unprocessed_states (state_id) values (?)";

  private static final String FIND_STATE_BY_CANDIDATE =
      "SELECT id, content FROM STATES where id in (select min(state_id) from UNPROCESSED_STATES)";

  private static final String DELETE_UNPROCESSED_STATES =
      "delete from UNPROCESSED_STATES where state_id = ?";

  private final Connection connection;

  private final PreparedStatement insertUnprocessedState;

  private final PreparedStatement selectStateByContent;

  private final PreparedStatement insertState;

  private final PreparedStatement selectStateByCandidate;

  private final PreparedStatement deleteUnprocessedState;

  private final PreparedStatement selectStateById;

  Database() {
    try {
      connection = DriverManager.
          getConnection("jdbc:h2:~/babaisyousolver", "sa", "");
      Statement statement = connection.createStatement();
      statement.execute("DROP TABLE IF EXISTS states");
      statement = connection.createStatement();
      statement.execute("DROP TABLE IF EXISTS unprocessed_states");
      statement = connection.createStatement();
      statement.execute("DROP TABLE IF EXISTS unprocessed_states");
      statement = connection.createStatement();
      statement.execute("CREATE TABLE states\n" +
          "(\n" +
          "    id INT auto_increment NOT NULL,\n" +
          "    content BINARY NOT NULL,\n" +
          "    parent INT,\n" +
          "    direction TINYINT,\n" +
          "    PRIMARY KEY (id),\n" +
          "    CONSTRAINT content_unique UNIQUE (content),\n" +
          "    CONSTRAINT states_parent FOREIGN KEY (parent)\n" +
          "        REFERENCES states (id)\n" +
          ")");
      statement = connection.createStatement();
      statement.execute("CREATE TABLE unprocessed_states\n" +
          "(\n" +
          "    id INT auto_increment NOT NULL,\n" +
          "    state_id INT NOT NULL,\n" +
          "    PRIMARY KEY (id),\n" +
          "    CONSTRAINT unprocessed_states_state_id FOREIGN KEY (state_id)\n" +
          "        REFERENCES public.states (id)\n" +
          ")");
      insertUnprocessedState = connection.prepareStatement(INSERT_UNPROCESSED_STATE);
      selectStateByContent = connection.prepareStatement(FIND_STATE_BY_CONTENT);
      insertState = connection.prepareStatement(INSERT_STATE, Statement.RETURN_GENERATED_KEYS);
      selectStateByCandidate = connection.prepareStatement(FIND_STATE_BY_CANDIDATE);
      deleteUnprocessedState= connection.prepareStatement(DELETE_UNPROCESSED_STATES);
      selectStateById= connection.prepareStatement(FIND_STATE_BY_ID);

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  void addCandidate(
      @NotNull int[] content,
      @NotNull Level level,
      byte movement,
      int parent
  ) {
    try {
      byte[] contentAsByteArray = stateToDatabase(content, level);
      selectStateByContent.setBytes(1, contentAsByteArray);
      ResultSet findResultSet = selectStateByContent.executeQuery();
      findResultSet.next();
      int findNumber = findResultSet.getInt(1);
      if (findNumber == 1) {
        return;
      }

      insertState.setBytes(1, contentAsByteArray);
      if (parent != -1) {
        insertState.setInt(2, parent);
      } else {
        insertState.setNull(2, Types.INTEGER);
      }
      if (movement != -1) {
        insertState.setByte(3, movement);
      } else {
        insertState.setNull(3, Types.TINYINT);
      }
      insertState.execute();
      ResultSet generatedKeys = insertState.getGeneratedKeys();
      generatedKeys.next();
      int stateId = generatedKeys.getInt(1);

      insertUnprocessedState.setInt(1, stateId);
      insertUnprocessedState.execute();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  void close() {
    try {
      connection.close();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private @NotNull byte[] stateToDatabase(
      @NotNull int[] source,
      @NotNull Level level) {
    int numberNotEmpty = 0;
    for (int cell : source) {
      if (cell != Tiles.EMPTY) {
        numberNotEmpty += 1;
      }
    }
    int resultSize = level.databaseHeaderSize + numberNotEmpty;
    byte[] result = new byte[resultSize];
    int currentTargetIndex = level.databaseHeaderSize;
    for (int i = 0; i < level.size; i++) {
      int cell = source[i];
      if (cell != Tiles.EMPTY) {
        int cellByte = i / 8;
        int cellPosInByte = i % 8;
        result[cellByte] |= (1 << cellPosInByte);
        byte cellValue = level.possibleTiles[cell];
        if (cellValue == 0) {
          level.currentTileNumber++;
          cellValue = level.currentTileNumber;
          level.possibleTiles[cell] = cellValue;
          level.reversePossibleTiles[cellValue] = cell;
        }
        result[currentTargetIndex] = cellValue;
        currentTargetIndex++;
      }
    }
    return result;
  }

  private @NotNull int[] databaseToState(
      @NotNull byte[] source,
      @NotNull Level level) {
    int[] result = new int[level.size];
    int currentSourceIndex = level.databaseHeaderSize;
    for (int i = 0; i < level.size; i++) {
      int cellByte = i / 8;
      int cellPosInByte = i % 8;
      if((source[cellByte] & (1 << cellPosInByte)) != 0) {
        result[i] = level.reversePossibleTiles[source[currentSourceIndex]];
        currentSourceIndex++;
      }
    }
    return result;
  }

  @Nullable State nextCandidate(@NotNull Level level) {
    try {
      selectStateByCandidate.execute();
      ResultSet findResultSet = selectStateByCandidate.getResultSet();
      if (findResultSet.next()) {
        int id = findResultSet.getInt(1);
        int[] content = databaseToState(findResultSet.getBytes(2), level);
        deleteUnprocessedState.setInt(1, id);
        deleteUnprocessedState.execute();
        return new State(level, content, id);
      } else {
        return null;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private static final String FIND_STATE_BY_ID =
      "SELECT parent, direction FROM STATES where id = ?";

  void fetchParents(int id, List<Byte> path) {
    while (id != -1) {
      try {
        selectStateById.setInt(1, id);
        selectStateById.execute();
        ResultSet findResultSet = selectStateById.getResultSet();
        findResultSet.next();
        id = findResultSet.getInt(1);
        if (findResultSet.wasNull()) {
          id = -1;
        }
        byte direction = findResultSet.getByte(2);
        if (!findResultSet.wasNull()) {
          path.add(direction);
        }
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
  }

}
