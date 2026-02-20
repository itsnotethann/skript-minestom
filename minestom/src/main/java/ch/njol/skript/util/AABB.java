package ch.njol.skript.util;

import ch.njol.skript.Skript;
import ch.njol.util.Math2;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.NoSuchElementException;

// almost entirely converted using chatgpt to minestom
/**
 * AABB = Axis-Aligned Bounding Box
 *
 * Minestom port:
 * - world -> Instance
 * - Location/Vector -> Point/Vec
 * - Block -> BlockVec (block coordinates)
 */
public final class AABB implements Iterable<BlockVec> {

	final Instance instance;
	final Vec lowerBound, upperBound;

	public AABB(@NotNull Point p1, @NotNull Point p2, @NotNull Instance instance) {
		this.instance = instance;

		// use block coordinates like the Bukkit version (getBlockX/Y/Z)
		int x1 = p1.blockX(), y1 = p1.blockY(), z1 = p1.blockZ();
		int x2 = p2.blockX(), y2 = p2.blockY(), z2 = p2.blockZ();

		this.lowerBound = new Vec(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2));
		this.upperBound = new Vec(Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2));
	}

	public AABB(@NotNull BlockVec b1, @NotNull BlockVec b2, @NotNull Instance instance) {
		this.instance = instance;
		this.lowerBound = new Vec(
			Math.min(b1.x(), b2.x()),
			Math.min(b1.y(), b2.y()),
			Math.min(b1.z(), b2.z())
		);
		this.upperBound = new Vec(
			Math.max(b1.x(), b2.x()),
			Math.max(b1.y(), b2.y()),
			Math.max(b1.z(), b2.z())
		);
	}

	public AABB(@NotNull Point center, double rX, double rY, double rZ, @NotNull Instance instance) {
		assert rX >= 0 && rY >= 0 && rZ >= 0 : rX + "," + rY + "," + rZ;
		this.instance = instance;

		DimensionType type = instance.getCachedDimensionType();
		int minY = type.minY();
		int maxY = type.height() - 1;

		this.lowerBound = new Vec(
			center.x() - rX,
			Math.max(center.y() - rY, minY),
			center.z() - rZ
		);
		this.upperBound = new Vec(
			center.x() + rX,
			Math.min(center.y() + rY, maxY),
			center.z() + rZ
		);
	}

	public AABB(@NotNull Instance instance, @NotNull Vec v1, @NotNull Vec v2) {
		this.instance = instance;
		this.lowerBound = new Vec(
			Math.min(v1.x(), v2.x()),
			Math.min(v1.y(), v2.y()),
			Math.min(v1.z(), v2.z())
		);
		this.upperBound = new Vec(
			Math.max(v1.x(), v2.x()),
			Math.max(v1.y(), v2.y()),
			Math.max(v1.z(), v2.z())
		);
	}

	public AABB(@NotNull Chunk chunk) {
		this.instance = chunk.getInstance();

		DimensionType type = instance.getCachedDimensionType();
		int minY = type.minY();
		int maxY = type.height() - 1;

		int chunkX = chunk.getChunkX();
		int chunkZ = chunk.getChunkZ();

		this.lowerBound = new Vec((chunkX << 4), minY, (chunkZ << 4));
		this.upperBound = new Vec((chunkX << 4) + 15, maxY, (chunkZ << 4) + 15);
	}

	public boolean contains(@NotNull Point p) {
		return lowerBound.x() - Skript.EPSILON < p.x() && p.x() < upperBound.x() + Skript.EPSILON
			&& lowerBound.y() - Skript.EPSILON < p.y() && p.y() < upperBound.y() + Skript.EPSILON
			&& lowerBound.z() - Skript.EPSILON < p.z() && p.z() < upperBound.z() + Skript.EPSILON;
	}

	public boolean contains(@NotNull BlockVec b) {
		// same logic as Bukkit version: block is inside if both corners are inside
		Vec p1 = new Vec(b.x(), b.y(), b.z());
		Vec p2 = new Vec(b.x() + 1, b.y() + 1, b.z() + 1);
		return contains(p1) && contains(p2);
	}

	public Vec getDimensions() {
		return upperBound.sub(lowerBound);
	}

	public Instance getInstance() {
		return instance;
	}

	/**
	 * Returns an iterator over all block positions in this AABB (inclusive).
	 *
	 * Note: this does NOT check chunk loaded state (matches old behavior of "getBlockAt").
	 * Your caller already filters chunk loaded in ExprBlocks for the line case; for AABB you
	 * may want to add loaded checks if needed.
	 */
	@Override
	public Iterator<BlockVec> iterator() {
		return new Iterator<>() {
			private final int minX = (int) Math2.ceil(lowerBound.x());
			private final int minY = (int) Math2.ceil(lowerBound.y());
			private final int minZ = (int) Math2.ceil(lowerBound.z());
			private final int maxX = (int) Math2.floor(upperBound.x());
			private final int maxY = (int) Math2.floor(upperBound.y());
			private final int maxZ = (int) Math2.floor(upperBound.z());

			private int x = minX - 1; // next() increments immediately
			private int y = minY;
			private int z = minZ;

			@Override
			public boolean hasNext() {
				return y <= maxY && (x != maxX || y != maxY || z != maxZ);
			}

			@Override
			public BlockVec next() {
				if (!hasNext()) throw new NoSuchElementException();
				x++;
				if (x > maxX) {
					x = minX;
					z++;
					if (z > maxZ) {
						z = minZ;
						y++;
					}
				}
				if (y > maxY) throw new NoSuchElementException();
				return new BlockVec(x, y, z);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = 31 * result + lowerBound.hashCode();
		result = 31 * result + upperBound.hashCode();
		result = 31 * result + instance.hashCode();
		return result;
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof AABB other)) return false;
		if (!lowerBound.equals(other.lowerBound)) return false;
		if (!upperBound.equals(other.upperBound)) return false;
		return instance.equals(other.instance);
	}
}