package ca.sheridancollege.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import ca.sheridancollege.beans.Movie;
import ca.sheridancollege.beans.ScoreRow;
import ca.sheridancollege.beans.User;

/*
 * Name: Yin Tung Ng
 * Student #: 991602581
 * Assignment: Final Exam
 * Course: PROG32758 - Java 3
 */

@Repository
public class DatabaseAccess {

	@Autowired
	protected NamedParameterJdbcTemplate jdbc;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	/**
	 * Returns the User of a given userName String
	 * 
	 * @param userName the username String
	 * @return User object
	 */
	public User findUserAccount(String userName) {

		MapSqlParameterSource parameters = new MapSqlParameterSource();
		// use NAMED PARAMETERS for userName
		String query = "SELECT * FROM sec_user WHERE userName=:userName";
		parameters.addValue("userName", userName); // add the value to query
		// query the table to see if userName exists in table
		ArrayList<User> users = (ArrayList<User>) jdbc.query(
				query, parameters, new BeanPropertyRowMapper<User>(User.class));

		// there should only be ONE/1 user match,
		// since userName is UNIQUE in SEC_USER table
		if (users.size() > 0)
			return users.get(0);
		else
			return null;
	}

	/**
	 * Returns a List of roles, given a userId number
	 * 
	 * @param userId the Long userId
	 * @return a List of user roles
	 */
	public List<String> getRolesById(long userId) {

		ArrayList<String> roles = new ArrayList<String>(); // results container

		MapSqlParameterSource parameters = new MapSqlParameterSource();
		// prepare the query with NAMED PARAMETERS
		String query = "SELECT user_role.userId, sec_role.roleName "
				+ "FROM user_role, sec_role "
				+ "WHERE user_role.roleId=sec_role.roleId "
				+ "AND userId=:userId";
		// add the value of the actual userID for the NAMED PARAMETER
		parameters.addValue("userId", userId);
		// query the table to get the list of ROLES
		List<Map<String, Object>> rows = jdbc.queryForList(query, parameters);

		// now add each roleName value from the Map TO the returning container "roles"
		for (Map<String, Object> row : rows) {
			roles.add((String) row.get("roleName"));
		}
		return roles; // return the list of <String> roles
	}

	/**
	 * Registers a new user by adding to the database. The password will be encrypted
	 * before insertion into the SEC_User table.
	 * 
	 * @param userName the new username String
	 * @param password the plaintext password
	 */
	public void addUser(String userName, String password) {

		MapSqlParameterSource parameters = new MapSqlParameterSource();
		// prepared query with NAMED PARAMETERS
		String query = "INSERT INTO SEC_User "
				+ "(userName, encryptedPassword, ENABLED) "
				+ "VALUES (:userName, :encryptedPassword, 1)";
		// add the values for the named parameters
		parameters.addValue("userName", userName);
		// encrypts the plaintext password, prior to adding
		parameters.addValue("encryptedPassword", passwordEncoder.encode(password));
		jdbc.update(query, parameters); // update the table with INSERT
	}

	/**
	 * Assigns a role to a user. The userId and roleId will be inserted into the user_role
	 * intersection table
	 * 
	 * @param userId the userId number
	 * @param roleId the roleId number
	 */
	public void addRole(long userId, long roleId) {

		MapSqlParameterSource parameters = new MapSqlParameterSource();
		// prepared query with NAMED PARAMETERS
		String query = "INSERT INTO user_role (userId, roleId) "
				+ "VALUES (:userId, :roleId)";
		// add the values for the named parameters
		parameters.addValue("userId", userId);
		parameters.addValue("roleId", roleId);
		jdbc.update(query, parameters); // update the table with INSERT
	}

	/**
	 * Gets the userId by user name.
	 * 
	 * @param userName String
	 * @return userId Long
	 */
	public Long getUserIdByName(String userName) {

		ArrayList<User> users = new ArrayList<User>();
		// query uses NAMED PARAMETERS
		String query = "SELECT * "
				+ "FROM sec_user "
				+ "WHERE userName=:userName";
		// the HashMap container
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		// add the id argument to as the named parameter
		parameters.addValue("userName", userName);
		// use as a List container for the table rows data
		List<Map<String, Object>> rows = jdbc.queryForList(query, parameters);

		// create the ScoreRow object from the queryForList results
		for (Map<String, Object> row : rows) {
			User u = new User();
			u.setUserId((Long) (row.get("userId")));
			users.add(u);
		}

		if (users.size() > 0) {
			return users.get(0).getUserId();
		}
		return null;
	}

	/**
	 * Insert a new scoreboard record for some new userId.
	 * 
	 * @param userId the newly registered user's Id number
	 */
	public void addUserToScoreboard(long userId) {

		// use NAMED PARAMETERS to set the query data
		String query = "INSERT INTO scoreboard(userId) VALUES (:userId)";
		// recall: this is our HashMap substitute
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		// .addValue() is like HashMap.put(key, value)
		parameters.addValue("userId", userId);
		// RECALL: jdbc.update needs 1. a SQL query, 2. (named) parameters
		jdbc.update(query, parameters);
	}

	/**
	 * Updates the user's score in the scoreboard.
	 * 
	 * @param score the new score
	 * @param userId the long userId number
	 */
	public void updateScoreByUserId(int score, long userId) {

		// use NAMED PARAMETERS to set the query data
		String query = "UPDATE scoreboard "
				+ "SET score=:score "
				+ "WHERE userId=:userId";
		// recall: this is our HashMap substitute
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		// .addValue() is like HashMap.put(key, value)
		parameters.addValue("score", score);
		parameters.addValue("userId", userId);
		// RECALL: jdbc.update needs 1. a SQL query, 2. (named) parameters
		jdbc.update(query, parameters);
	}

	/**
	 * Increments the number of games played by a registered user when a game is finished.
	 * 
	 * @param userId
	 */
	public void incrementGamesPlayed(Long userId) {

		// use NAMED PARAMETERS to set the query data
		String query = "UPDATE scoreboard "
				+ "SET games_played=games_played + 1 " // INCREMENT HERE
				+ "WHERE userId=:userId";
		// recall: this is our HashMap substitute
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		// .addValue() is like HashMap.put(key, value)
		parameters.addValue("userId", userId);
		// RECALL: jdbc.update needs 1. a SQL query, 2. (named) parameters
		jdbc.update(query, parameters);
	}

	/**
	 * Get the top 5 scores to populate the scoreboard, as a list of ScoreRow objects.
	 * Method stores the data in a ScoreRow bean.
	 * 
	 * @return list of top 5 score records
	 */
	public ArrayList<ScoreRow> getTop5Scores() {

		ArrayList<ScoreRow> scoreRows = new ArrayList<ScoreRow>(); // to be returned

		// Implicit JOIN 2 tables to get the required columns (ex. userName)
		// SORTS the table by descending score
		String query = "SELECT scoreId, sec_user.userId, userName, games_played, score "
				+ "FROM scoreboard, sec_user "
				+ "WHERE scoreboard.userId = sec_user.userId "
				+ "ORDER BY score DESC "
				+ "LIMIT 5"; // <-----------------------------------GET TOP 5 SCORES 
		// use as a List container for the table rows data
		List<Map<String, Object>> rows = jdbc.queryForList(query,
				new HashMap<String, Object>());

		// for each ROW (MAP) in the ROWS (LIST), make and add a new movie
		// for each MAP, key = table_column, value = cell field
		for (Map<String, Object> row : rows) {
			ScoreRow s = new ScoreRow();
			s.setScoreId((Long) (row.get("scoreId")));
			s.setUserId((Long) (row.get("userId")));
			s.setUserName((String) (row.get("userName")));
			s.setGames_played((Integer) (row.get("games_played")));
			s.setScore((Integer) (row.get("score")));
			scoreRows.add(s);
		}
		return scoreRows;
	}

	// SCOREBOARD ADMIN METHODS ---------------------------------------------

	/**
	 * Get all the Scoreboard Rows from the table.
	 * 
	 * @return list of ScoreRows
	 */
	public ArrayList<ScoreRow> getScoreRows() {

		ArrayList<ScoreRow> scoreRows = new ArrayList<ScoreRow>(); // to be returned

		// Implicit JOIN 2 tables to get required columns (ex. userName)
		// SORTS the table by descending score
		String query = "SELECT scoreId, u.userId, userName, games_played, score "
				+ "FROM scoreboard s, sec_user u "
				+ "WHERE s.userId = u.userId "
				+ "ORDER BY score DESC";
		// use as a List container for the table rows data
		List<Map<String, Object>> rows = jdbc.queryForList(query,
				new HashMap<String, Object>());

		// for each ROW (MAP) in the ROWS (LIST), make and add a new movie
		// for each MAP, key = table_column, value = cell field
		for (Map<String, Object> row : rows) {
			ScoreRow s = new ScoreRow();
			s.setScoreId((Long) (row.get("scoreId")));
			s.setUserId((Long) (row.get("userId")));
			s.setUserName((String) (row.get("userName")));
			s.setGames_played((Integer) (row.get("games_played")));
			s.setScore((Integer) (row.get("score")));
			scoreRows.add(s);
		}
		return scoreRows;
	}

	/**
	 * Gets the ScoreRow record by score Id number.
	 * 
	 * @param id the scoreId integer
	 * @return ScoreRow record object
	 */
	public ScoreRow getScoreRowById(int id) {

		ArrayList<ScoreRow> scores = new ArrayList<ScoreRow>();
		// query uses NAMED PARAMETERS
		String query = "SELECT * "
				+ "FROM scoreboard "
				+ "WHERE scoreId=:scoreId";
		// the HashMap container
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		// add the id argument to as the named parameter
		parameters.addValue("scoreId", id);
		// use as a List container for the table rows data
		List<Map<String, Object>> rows = jdbc.queryForList(query, parameters);

		// create the ScoreRow object from the queryForList results
		for (Map<String, Object> row : rows) {
			ScoreRow s = new ScoreRow();
			s.setScoreId((Long) (row.get("scoreId")));
			s.setUserId((Long) (row.get("userId")));
			s.setGames_played((Integer) (row.get("games_played")));
			s.setScore((Integer) (row.get("score")));
			scores.add(s);
		}

		if (scores.size() > 0) {
			return scores.get(0); // return the first row of that id
		}
		return null;
	}

	/**
	 * Get the Scoreboard record details for a long userId argument.
	 * 
	 * @param id the user Id as a long
	 * @return ScoreRow object
	 */
	public ScoreRow getScoreRowByUserId(long userId) {

		ArrayList<ScoreRow> scores = new ArrayList<ScoreRow>();
		// query uses NAMED PARAMETERS
		String query = "SELECT * "
				+ "FROM scoreboard "
				+ "WHERE userId=:userId";
		// the HashMap container
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		// add the id argument to as the named parameter
		parameters.addValue("userId", userId);
		// use as a List container for the table rows data
		List<Map<String, Object>> rows = jdbc.queryForList(query, parameters);

		// create the ScoreRow object from the queryForList results
		for (Map<String, Object> row : rows) {
			ScoreRow s = new ScoreRow();
			s.setScoreId((Long) (row.get("scoreId")));
			s.setUserId((Long) (row.get("userId")));
			s.setGames_played((Integer) (row.get("games_played")));
			s.setScore((Integer) (row.get("score")));
			scores.add(s);
		}

		if (scores.size() > 0) {
			return scores.get(0); // return the first row of that id
		}
		return null;
	}

	/**
	 * Updates the games_played and/or score fields in the scoreboard table.
	 * 
	 * @param scoreRow the scoreboard record object
	 */
	public void updateScoreRow(ScoreRow scoreRow) {

		// use NAMED PARAMETERS to set the query data
		String query = "UPDATE scoreboard "
				+ "SET games_played=:games_played, score=:score "
				+ "WHERE scoreId=:scoreId";
		// recall: this is our HashMap substitute
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		// .addValue() is like HashMap.put(key, value)
		parameters.addValue("scoreId", scoreRow.getScoreId());
		parameters.addValue("games_played", scoreRow.getGames_played());
		parameters.addValue("score", scoreRow.getScore());

		// RECALL: jdbc.update needs 1. a SQL query, 2. (named) parameters
		jdbc.update(query, parameters);
	}

	/**
	 * Deletes the scoreboard row in the scoreboard table by scoreId.
	 * 
	 * @param id the scoreId #
	 */
	public void deleteScoreRow(int id) {

		String query = "DELETE FROM scoreboard "
				+ "WHERE scoreId=:scoreId";
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("scoreId", id);

		// RECALL: jdbc.update needs 1. a SQL query, 2. (named) parameters
		jdbc.update(query, parameters);
	}

	// MOVIE ADMIN METHODS ---------------------------------------------

	/**
	 * Adds a movie (by name) into the movie table.
	 * 
	 * @param movieName the movie name String
	 */
	public void addMovie(String movieName) {

		String query = "INSERT INTO movie (movieName) "
				+ "VALUES (:movieName)";
		// create the Map container
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		// prepare the named parameter
		parameters.addValue("movieName", movieName);
		// RECALL: jdbc.update needs 1. a SQL query, 2. (named) parameters
		jdbc.update(query, parameters);
	}

	/**
	 * Gets the Movie table records.
	 * 
	 * @return list of Movie objects
	 */
	public ArrayList<Movie> getMovies() {

		ArrayList<Movie> movies = new ArrayList<Movie>(); // to be returned
		// query uses NAMED PARAMETERS
		String query = "SELECT * "
				+ "FROM movie "
				+ "ORDER BY movieName";
		// use as a List container for the table rows data
		List<Map<String, Object>> rows = jdbc.queryForList(query,
				new HashMap<String, Object>());

		// for each ROW (MAP) in the ROWS (LIST), make and add a new movie
		// for each MAP, key = table_column, value = cell field
		for (Map<String, Object> row : rows) {
			Movie m = new Movie();
			m.setMovieId((Long) (row.get("movieId")));
			m.setMovieName((String) (row.get("movieName")));
			movies.add(m);
		}
		return movies;
	}

	/**
	 * Gets the Movie by movieId number.
	 * 
	 * @param id movie Id number
	 * @return Movie object
	 */
	public Movie getMovieById(int id) {

		ArrayList<Movie> movies = new ArrayList<Movie>();
		// query uses NAMED PARAMETERS
		String query = "SELECT * "
				+ "FROM movie "
				+ "WHERE movieId=:movieId";
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		// add the id argument to as the named parameter
		parameters.addValue("movieId", id);
		// use as a List container for the table rows data
		List<Map<String, Object>> rows = jdbc.queryForList(query, parameters);

		// create the ScoreRow object from the queryForList results
		for (Map<String, Object> row : rows) {
			Movie m = new Movie();
			m.setMovieId((Long) (row.get("movieId")));
			m.setMovieName((String) (row.get("movieName")));
			movies.add(m);
		}

		if (movies.size() > 0) {
			return movies.get(0); // return the first row of that id
		}
		return null;
	}

	/**
	 * Updates the movie record with the new name.
	 * 
	 * @param movie the movie object with new name
	 */
	public void updateMovie(Movie movie) {

		String query = "UPDATE movie "
				+ "SET movieName=:movieName "
				+ "WHERE movieId=:movieId";
		// recall: this is our HashMap substitute
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		// .addValue() is like HashMap.put(key, value)
		parameters.addValue("movieId", movie.getMovieId());
		parameters.addValue("movieName", movie.getMovieName());
		// RECALL: jdbc.update needs 1. a SQL query, 2. (named) parameters
		jdbc.update(query, parameters);
	}

	/**
	 * Deletes a movie from the database by movie Id number.
	 * 
	 * @param id the movie Id number
	 */
	public void deleteMovie(int id) {

		String query = "DELETE FROM movie "
				+ "WHERE movieId=:movieId";
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("movieId", id);
		// RECALL: jdbc.update needs 1. a SQL query, 2. (named) parameters
		jdbc.update(query, parameters);
	}

}
