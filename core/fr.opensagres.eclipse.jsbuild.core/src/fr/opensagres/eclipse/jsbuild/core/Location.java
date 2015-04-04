package fr.opensagres.eclipse.jsbuild.core;

public class Location {

	private final int start;
	private final int length;

	public Location(int start, int length) {
		this.start = start;
		this.length = length;
	}

	public int getStart() {
		return start;
	}

	public int getLength() {
		return length;
	}

}
