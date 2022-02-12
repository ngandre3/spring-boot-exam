package ca.sheridancollege.controllers;

import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.sheridancollege.beans.Movie;
import ca.sheridancollege.beans.ScoreRow;
import ca.sheridancollege.database.DatabaseAccess;

/*
 * Name: Yin Tung Ng
 * Student #: 991602581
 * Assignment: Final Exam
 * Course: PROG32758 - Java 3
 */

@Controller
public class GameController {

	@Autowired
	@Lazy
	private DatabaseAccess da;

	@GetMapping("/")
	public String goHome(HttpSession session) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		// if user is logged in, get their old score from the database
		if (!(auth instanceof AnonymousAuthenticationToken)) {

			// 1. get the current userName
			String userName = auth.getName();
			// 2. get the userId by userName
			long userId = da.getUserIdByName(userName);
			// 3. get user's old score from scoreboard database
			ScoreRow userScoreRow = da.getScoreRowByUserId(userId);

			// maybe a user's score record was deleted by admins, so insert a new record
			if (userScoreRow == null) {
				da.addUserToScoreboard(userId);
				session.setAttribute("score", 0); // score reset to 0 bc of new record
			}
			// else, the user has an old score record in the scoreboard, so get that score
			else {
				int oldScore = userScoreRow.getScore();
				// 4. set the old score into the session
				session.setAttribute("score", oldScore);
			}
		}
		// user is NOT logged in, so score is set to 0
		else {
			session.setAttribute("score", 0);
		}

		// GET A RANDOM MOVIE NAME FROM THE DATABASE
		// get the list of movies from database
		List<Movie> movieList = da.getMovies();
		// get a random int index within the list size range
		Random rand = new Random();
		int randomInt = rand.nextInt(movieList.size());
		// get a random Movie from the list
		Movie randomMovie = movieList.get(randomInt);
		session.setAttribute("movie", randomMovie);

		// reset the fails counter to 0 for new and old players
		session.setAttribute("fails", 0);

		return "index.html";
	}

	// GAME MAPPINGS ===========================================================

	@GetMapping("/start")
	public String goGuessLetter(HttpSession session) {

		if (session.getAttribute("wrong") == null) {
			session.setAttribute("wrong", false);
		}

		// add a hidden string to help the user guess the movie: "***_****_****"
		Movie movie = (Movie) session.getAttribute("movie");
		String hidden = movie.getHiddenString();
		session.setAttribute("hidden", hidden);

		return "guessLetter.html";
	}

	@GetMapping("/guessletter")
	public String guessLetter(HttpSession session, Model model,
			@RequestParam String letter) {

		Movie movie = (Movie) session.getAttribute("movie");
		int numTimes = movie.getNumLettersInStr(letter);

		// FAIL: if letter = "z" or letter not in movie name (numTimes = 0)
		if (letter.equalsIgnoreCase("z") || numTimes == 0) {

			// letter is wrong, not found -> increase fails by 1
			int fails = (int) session.getAttribute("fails");
			fails++;
			session.setAttribute("fails", fails);

			// check if fails >= 7, if game is over and lost
			if (fails < 7) { // RETRY: less than 7 fails

				// wrong boolean shows the error message
				model.addAttribute("wrong", true);
				return "guessLetter.html";

			} else { // LOSE THE GAME: # of fails is equal to or greater than 7

				// decrease score by 5 points for the loser
				int score = (int) session.getAttribute("score");
				score -= 5;
				session.setAttribute("score", score);

				// update the new score into the scoreboard table/database,
				// only update if user is logged in
				Authentication auth = SecurityContextHolder.getContext()
						.getAuthentication();
				if (!(auth instanceof AnonymousAuthenticationToken)) {

					// 1. get the current userName
					String userName = auth.getName();
					// 2. get the userId by userName
					long userId = da.getUserIdByName(userName);
					// 3. update the user's score by userId
					da.updateScoreByUserId(score, userId);
					// 4. increment and update the number of games_played
					da.incrementGamesPlayed(userId);
				}
				return "loser.html";
			}
		} else { // CORRECT: letter is correct, go guess the movie

			model.addAttribute("letter", letter);
			model.addAttribute("numTimes", numTimes);
			return "guessMovie.html";
		}
	}

	@GetMapping("/guessmovie")
	public String guessMovie(HttpSession session, Model model,
			@RequestParam String movieguess) {

		// check if guess string matches the random movie name
		// get the random Movie object from session
		Movie randomMovie = (Movie) session.getAttribute("movie");
		// get the Movie's name string
		String randomMovieName = randomMovie.getMovieName();
		// CHECK: case-insensitive, does "guess" match the "movie name string"?
		boolean match = randomMovieName.equalsIgnoreCase(movieguess);

		if (match) { // WINNER: user has WON the game round

			// increase score by 5 points for the winner
			int score = (int) session.getAttribute("score");
			score += 5;
			session.setAttribute("score", score);

			// update the new score into the scoreboard table/database,
			// only update if user is logged in
			Authentication auth = SecurityContextHolder.getContext()
					.getAuthentication();
			if (!(auth instanceof AnonymousAuthenticationToken)) {

				// 1. get the current userName
				String userName = auth.getName();
				// 2. get the userId by userName
				long userId = da.getUserIdByName(userName);
				// 3. update the user's score by userId
				da.updateScoreByUserId(score, userId);
				// 4. increment and update the number of games_played
				da.incrementGamesPlayed(userId);
			}
			return "winner.html";
		}
		// WRONG: PLAYER's GUESS DID NOT MATCH FULL MOVIE NAME
		else {

			// name guess is wrong, not found -> increase fails by 1
			int fails = (int) session.getAttribute("fails");
			fails++;
			session.setAttribute("fails", fails);

			// check if fails >= 7, then the game is over and lost
			if (fails < 7) { // RETRY GUESSING LETTERS: less than 7 fails

				// "wrong" boolean shows the error message
				model.addAttribute("wrong", true);
				return "guessLetter.html";

			} else { // LOSE THE GAME: # of fails is equal to or greater than 7

				// decrease score by 5 points for the loser
				int score = (int) session.getAttribute("score");
				score -= 5;
				session.setAttribute("score", score);

				// update the new score into the scoreboard table/database,
				// only update if user is logged in
				Authentication auth = SecurityContextHolder.getContext()
						.getAuthentication();
				if (!(auth instanceof AnonymousAuthenticationToken)) {

					// 1. get the current userName
					String userName = auth.getName();
					// 2. get the userId by userName
					long userId = da.getUserIdByName(userName);
					// 3. update the user's score by userId
					da.updateScoreByUserId(score, userId);
					// 4. increment and update the number of games_played
					da.incrementGamesPlayed(userId);
				}
				return "loser.html";
			}
		}
	}

	@GetMapping("/scoreboard")
	public String goScoreboard(HttpSession session, Model model) {

		// get the top 5 scores (sorted descending by points),
		// and add it to the model for the scoreboard table on HTML page
		model.addAttribute("users", da.getTop5Scores());
		return "scoreboard.html";
	}

	// LOGIN MAPPINGS ==================================================

	@GetMapping("/login")
	public String goLoginPage() {
		return "login.html";
	}

	@GetMapping("/access-denied")
	public String goAccessDenied() {
		return "/error/access-denied.html";
	}

	@GetMapping("/register")
	public String goRegistration() {
		return "registration.html";
	}

	@PostMapping("/register")
	public String processRegistration(Model model, @RequestParam String name,
			@RequestParam String password) {

		// error handling: if name was already taken, ask the new user to try again
		if (da.findUserAccount(name) != null) {
			model.addAttribute("nameTaken", true);
			return "registration.html";

		} else {

			// 1. add the NEW USER to SEC_User table
			da.addUser(name, password);
			// 2. get the userID by userName > User obj > userId getter
			long userId = da.findUserAccount(name).getUserId();
			// 3. add the USER role for the related userId
			da.addRole(userId, 1);
			// 4. add the NEW user to scoreboard table
			da.addUserToScoreboard(userId);
			// 5. turn ON the boolean switch to show the success message
			model.addAttribute("registerSuccess", true);
			return "redirect:/login"; // return new user to login page when done
		}
	}

	// ADMIN MAPPINGS ---------------------------------------------

	@GetMapping("/admin")
	public String goAdmin() {
		return "admin/admin-index.html";
	}

	// SCOREBOARD ADMIN MAPPINGS ------------------------------

	@GetMapping("/admin/viewscore")
	public String viewScores(Model model) {
		model.addAttribute("scoreRows", da.getScoreRows());
		return "admin/viewScores.html";
	}

	// used in the viewScores page to link to the EDIT page
	@GetMapping("/admin/editscore/{id}")
	public String editScore(Model model, @PathVariable int id) {
		ScoreRow scoreRow = da.getScoreRowById(id);
		model.addAttribute("scoreRow", scoreRow);
		return "admin/editScore.html";
	}

	// used to UPDATE the scoreboard table (R in CRUD)
	@GetMapping("/admin/updatescore")
	public String updateScore(Model model, @ModelAttribute ScoreRow scoreRow) {
		da.updateScoreRow(scoreRow);
		return "redirect:/admin/viewscore";
	}

	// used to DELETE a scoreboard record (D in CRUD)
	@GetMapping("/admin/deletescore/{id}")
	public String deleteScore(Model model, @PathVariable int id) {
		da.deleteScoreRow(id);
		return "redirect:/admin/viewscore";
	}

	// MOVIES ADMIN MAPPINGS ------------------------------

	// go to view the movies table
	@GetMapping("/admin/viewmovie")
	public String viewMovies(Model model) {
		model.addAttribute("movies", da.getMovies());
		return "admin/viewMovies.html";
	}

	// go to the add movies page
	@GetMapping("/admin/addmovie")
	public String addMovie() {
		return "admin/addMovie.html";
	}

	// used to INSERT the new movie (C in CRUD)
	@GetMapping("/admin/insertmovie")
	public String addMovie(@RequestParam String movieName) {
		da.addMovie(movieName);
		return "redirect:/admin/viewmovie";
	}

	// used in the viewMovies page to link to the EDIT page
	@GetMapping("/admin/editmovie/{id}")
	public String editMovie(Model model, @PathVariable int id) {
		Movie movie = da.getMovieById(id);
		model.addAttribute("movie", movie);
		return "admin/editMovie.html";
	}

	// used to UPDATE the movie table (R in CRUD)
	@GetMapping("/admin/updatemovie")
	public String updateMovie(Model model, @ModelAttribute Movie movie) {
		da.updateMovie(movie);
		return "redirect:/admin/viewmovie";
	}

	// used to DELETE a movie record (D in CRUD)
	@GetMapping("/admin/deletemovie/{id}")
	public String deleteMovie(Model model, @PathVariable int id) {
		da.deleteMovie(id);
		return "redirect:/admin/viewmovie";
	}

}
