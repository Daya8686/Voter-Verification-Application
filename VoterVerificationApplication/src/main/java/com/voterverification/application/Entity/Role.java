package com.voterverification.application.Entity;

public enum Role {
	ADMIN("ADMIN"),VERIFIER("VERIFIER");

	private final String value;

	Role(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static Role fromString(String value) {
		for (Role role : Role.values()) {
			if (role.value.equalsIgnoreCase(value)) {
				return role;
			}
		}
		throw new IllegalArgumentException("Unknown role: " + value);
	}

	@Override
	public String toString() {
		return value;
	}
}
