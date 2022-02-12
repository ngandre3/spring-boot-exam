package ca.sheridancollege.beans;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Name: Yin Tung Ng
 * Student #: 991602581
 * Assignment: Final Exam
 * Course: PROG32758 - Java 3
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie implements Serializable {

	private static final long serialVersionUID = -5779373648146959999L;
	private Long movieId;
	private String movieName;

	/**
	 * Gets the length of the movie name string.
	 * 
	 * @return number of characters and spaces in the name
	 */
	public int getLength() {
		return getMovieName().length();
	}

	/**
	 * Gets the blanked out hidden string of the movie name. If movie="Toy Story", then
	 * "***_*****" is returned.
	 * 
	 * @return if movie="Toy Story", then "***_*****" is returned.
	 */
	public String getHiddenString() {

		String hidden = "";
		String name = getMovieName().toLowerCase();

		for (int i = 0; i < name.length(); i++) {
			char ch = name.charAt(i);

			// compare each character in movie name string
			if (ch == ' ') {
				hidden += "_"; // if space, add underscore
			} else {
				hidden += "*"; // if not a space, add star sign
			}
		}
		return hidden;
	}

	/**
	 * Gets the number of times a letter or char appears in a movie name. Used to check if
	 * the letter can be found in the movie name. If 0 times, then letter not found.
	 * 
	 * @param letter the letter or character String
	 * @return number of times the letter was found in the name
	 */
	public int getNumLettersInStr(String letter) {

		int count = 0;
		String name = getMovieName().toLowerCase();

		if (name.contains(letter)) {

			for (int i = 0; i < name.length(); i++) {
				char ch = name.charAt(i);

				if (letter.equalsIgnoreCase(String.valueOf(ch))) {
					count++;
				}
			}
		}
		return count;
	}

}
