package net.java.gotr4j;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import net.java.gotr4j.crypto.GotrException;

public class GotrUser implements Comparable<GotrUser>{

	private final String username;
	
	public GotrUser(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	@Override
	public String toString() {
		return username;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GotrUser gotrUser = (GotrUser) o;

        if (!username.equals(gotrUser.username)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
	public int compareTo(GotrUser other) {
		return username.compareTo(other.getUsername());
	}

	
	
}
