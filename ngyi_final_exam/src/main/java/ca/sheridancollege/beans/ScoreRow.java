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
public class ScoreRow implements Serializable {

	private static final long serialVersionUID = 4416013498550586391L;
	private Long scoreId;
	private Long userId;
	private String userName;
	private int games_played;
	private int score;
		
}
