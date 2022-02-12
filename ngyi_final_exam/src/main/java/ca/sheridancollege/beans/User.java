package ca.sheridancollege.beans;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

	private static final long serialVersionUID = -2236104756441383295L;
	private Long userId;
	private String userName;
	private String encryptedPassword;
	private int games_played;
	private int score;

}
