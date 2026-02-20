package ch.njol.skript.util;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

// chatgpt converted to minestom
/**
 * Iterates through blocks in a straight line from a start to end point (inclusive).
 *
 * Start/end points are treated as positions (not necessarily block-centered).
 * Iterates through every block the segment passes through, in order.
 */
public final class BlockLineIterator implements Iterator<BlockVec> {

	private Vec current;
	private final Vec end;
	private final Vec centeredEnd;
	final Vec step; // package-private for tests
	private boolean finished;

	/**
	 * @param start start point
	 * @param end end point
	 */
	public BlockLineIterator(@NotNull Point start, @NotNull Point end) {
		this.current = new Vec(start.x(), start.y(), start.z());
		this.end = new Vec(end.x(), end.y(), end.z());
		this.centeredEnd = centered(this.end);

		Vec delta = this.end.sub(this.current);
		double len = delta.length();
		this.step = len == 0.0 ? Vec.ZERO : delta.div(len);
	}

	/**
	 * @param start first block
	 * @param end last block
	 */
	public BlockLineIterator(@NotNull BlockVec start, @NotNull BlockVec end) {
		this(centerOf(start), centerOf(end));
	}

	/**
	 * @param start start point
	 * @param direction direction to travel in
	 * @param distance maximum distance to travel
	 */
	public BlockLineIterator(@NotNull Point start, @NotNull Vec direction, double distance) {
		Vec dir = normalize(direction);
		Point end = start.add(dir.mul(distance));
		this.current = new Vec(start.x(), start.y(), start.z());
		this.end = new Vec(end.x(), end.y(), end.z());
		this.centeredEnd = centered(this.end);

		Vec delta = this.end.sub(this.current);
		double len = delta.length();
		this.step = len == 0.0 ? Vec.ZERO : delta.div(len);
	}

	/**
	 * @param start first block
	 * @param direction direction to travel in
	 * @param distance maximum distance to travel
	 */
	public BlockLineIterator(@NotNull BlockVec start, @NotNull Vec direction, double distance) {
		this(centerOf(start), direction, distance);
	}

	@Override
	public boolean hasNext() {
		return !finished;
	}

	@Override
	public BlockVec next() {
		if (!hasNext()) throw new NoSuchElementException("Reached the final block destination");

		// start == end: return exactly one block
		if (step == Vec.ZERO || (step.x() == 0 && step.y() == 0 && step.z() == 0)) {
			finished = true;
			return current.asBlockVec();
		}

		// sanity check: current->end should not point away from step
		Vec toEnd = end.sub(current);
		double dot = toEnd.dot(step);
		if (dot < -1.0) throw new NoSuchElementException("Overshot the final block!");

		// get current block + check end (by block centers, inclusive)
		Vec center = centered(current);
		BlockVec block = center.asBlockVec(); // same as current block coords
		if (center.equals(centeredEnd)) finished = true;

		// advance ray to just past the next block face
		double t = stepsToNextFace(current, step, center) + Math.ulp(1.0);
		current = current.add(step.mul(t));
		return block;
	}

	/**
	 * Calculates the number of steps to the next closest block face this ray, defined by start and step, will encounter.
	 * Block faces are determined by the center vector, interpreted as the center of the current block.
	 *
	 * @param start  current ray position
	 * @param step   normalized ray direction
	 * @param center center of the block the ray is currently inside
	 * @return scalar t such that start + step * t reaches the closest block face
	 */
	static double stepsToNextFace(@NotNull Vec start, @NotNull Vec step, @NotNull Vec center) {
		double sx = step.x(), sy = step.y(), sz = step.z();

		double tx = Double.POSITIVE_INFINITY;
		double ty = Double.POSITIVE_INFINITY;
		double tz = Double.POSITIVE_INFINITY;

		if (sx != 0.0) {
			double faceX = center.x() + 0.5 * Math.signum(sx);
			tx = (faceX - start.x()) / sx;
		}
		if (sy != 0.0) {
			double faceY = center.y() + 0.5 * Math.signum(sy);
			ty = (faceY - start.y()) / sy;
		}
		if (sz != 0.0) {
			double faceZ = center.z() + 0.5 * Math.signum(sz);
			tz = (faceZ - start.z()) / sz;
		}

		// pick smallest positive (or just smallest, matching original behavior)
		double t = Math.min(tx, Math.min(ty, tz));

		// If we're exactly on a face and numerical noise makes t <= 0, jump to the next one.
		// (The +ulp in next() is the primary guard; this is just extra safety.)
		if (!(t > 0.0)) {
			t = 0.0;
		}
		return t;
	}

	/**
	 * Returns the center of the block containing {@code v}.
	 */
	@Contract("_ -> new")
	private static @NotNull Vec centered(@NotNull Vec v) {
		int bx = floorToInt(v.x());
		int by = floorToInt(v.y());
		int bz = floorToInt(v.z());
		return new Vec(bx + 0.5, by + 0.5, bz + 0.5);
	}

	private static @NotNull Point centerOf(@NotNull BlockVec b) {
		return new Vec(b.x() + 0.5, b.y() + 0.5, b.z() + 0.5);
	}

	private static @NotNull Vec normalize(@NotNull Vec v) {
		double len = v.length();
		if (len == 0.0) return Vec.ZERO;
		return v.div(len);
	}

	private static int floorToInt(double d) {
		int i = (int) d;
		return d < i ? i - 1 : i;
	}
}