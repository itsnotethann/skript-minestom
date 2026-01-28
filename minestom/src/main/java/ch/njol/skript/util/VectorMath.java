package ch.njol.skript.util;

import net.minestom.server.coordinate.Vec;

/**
 * @author bi0qaw
 */
public class VectorMath {

	public static final double PI = Math.PI;
	public static final double HALF_PI = PI / 2;
	public static final double DEG_TO_RAD = PI / 180;
	public static final double RAD_TO_DEG =  180 / PI;

	public static Vec fromSphericalCoordinates(double radius, double theta, double phi) {
		double r = Math.abs(radius);
		double t = theta * DEG_TO_RAD;
		double p = phi * DEG_TO_RAD;
		double sinp = Math.sin(p);
		double x = r * sinp * Math.cos(t);
		double y = r * Math.cos(p);
		double z = r * sinp * Math.sin(t);
		return new Vec(x, y, z);
	}

	public static Vec fromCylindricalCoordinates(double radius, double phi, double height) {
		double r = Math.abs(radius);
		double p = phi * DEG_TO_RAD;
		double x = r * Math.cos(p);
		double z = r * Math.sin(p);
		return new Vec(x, height, z);

	}

	public static Vec fromYawAndPitch(float yaw, float pitch) {
		double y = Math.sin(pitch * DEG_TO_RAD);
		double div = Math.cos(pitch * DEG_TO_RAD);
		double x = Math.cos(yaw * DEG_TO_RAD);
		double z = Math.sin(yaw * DEG_TO_RAD);
		x *= div;
		z *= div;
		return new Vec(x,y,z);
	}

	public static float getYaw(Vec vector) {
		if (vector.x() == 0.0 && vector.z() == 0.0){
			return 0;
		}
		return (float) (Math.atan2(vector.z(), vector.x()) * RAD_TO_DEG);
	}

	public static float getPitch(Vec vector) {
		double xy = Math.sqrt(vector.x() * vector.x() + vector.z() * vector.z());
		return (float) (Math.atan(vector.y() / xy) * RAD_TO_DEG);
	}

	public static Vec setYaw(Vec vector, float yaw) {
		vector = fromYawAndPitch(yaw, getPitch(vector));
		return vector;
	}

	public static Vec setPitch(Vec vector, float pitch) {
		vector = fromYawAndPitch(getYaw(vector), pitch);
		return vector;
	}

	public static Vec rotX(Vec vector, double angle) {
		double sin = Math.sin(angle * DEG_TO_RAD);
		double cos = Math.cos(angle * DEG_TO_RAD);
		Vec vy = new Vec(0, cos, -sin);
		Vec vz = new Vec(0, sin, cos);
		return new Vec(vector.x(), vector.dot(vy), vector.dot(vz));
	}

	public static Vec rotY(Vec vector, double angle) {
		double sin = Math.sin(angle * DEG_TO_RAD);
		double cos = Math.cos(angle * DEG_TO_RAD);
		Vec vx = new Vec(cos, 0, sin);
		Vec vz = new Vec(-sin, 0, cos);
		return new Vec(vector.dot(vx), vector.y(), vector.dot(vz));
	}

	public static Vec rotZ(Vec vector, double angle) {
		double sin = Math.sin(angle * DEG_TO_RAD);
		double cos = Math.cos(angle * DEG_TO_RAD);
		Vec vx = new Vec(cos, -sin, 0);
		Vec vy = new Vec(sin, cos, 0);
		return new Vec(vector.dot(vx), vector.dot(vy), vector.z());
	}

	public static Vec rot(Vec vector, Vec axis, double angle) {
		double sin = Math.sin(angle * DEG_TO_RAD);
		double cos = Math.cos(angle * DEG_TO_RAD);
		Vec a = axis.normalize();
		double ax = a.x();
		double ay = a.y();
		double az = a.z();
		Vec rotx = new Vec(cos+ax*ax*(1-cos), ax*ay*(1-cos)-az*sin, ax*az*(1-cos)+ay*sin);
		Vec roty = new Vec(ay*ax*(1-cos)+az*sin, cos+ay*ay*(1-cos), ay*az*(1-cos)-ax*sin);
		Vec rotz = new Vec(az*ax*(1-cos)-ay*sin, az*ay*(1-cos)+ax*sin, cos+az*az*(1-cos));
		double x = rotx.dot(vector);
		double y = roty.dot(vector);
		double z = rotz.dot(vector);
		return new Vec(x, y, z);
	}

	public static float notchYaw(float yaw){
		float y = yaw - 90;
		if (y < -180){
			y += 360;
		}
		return y;
	}

	public static float notchPitch(float pitch){
		return -pitch;
	}

	public static float fromNotchYaw(float notchYaw){
		float y = notchYaw + 90;
		if (y > 180){
			y -= 360;
		}
		return y;
	}

	public static float fromNotchPitch(float notchPitch){
		return -notchPitch;
	}

	public static float skriptYaw(float yaw){
		float y = yaw - 90;
		if (y < 0){
			y += 360;
		}
		return y;
	}

	public static float skriptPitch(float pitch){
		return -pitch;
	}

	public static float fromSkriptYaw(float yaw){
		float y = yaw + 90;
		if (y > 360){
			y -= 360;
		}
		return y;
	}

	public static float fromSkriptPitch(float pitch){
		return -pitch;
	}

	public static float wrapAngleDeg(float angle) {
		angle %= 360f;
		if (angle <= -180) {
			return angle + 360;
		} else if (angle > 180) {
			return angle - 360;
		} else {
			return angle;
		}
	}

	/**
	 * Copies vector components of {@code vector2} into {@code vector1}.
	 */
	public static Vec copyVector(Vec vector1, Vec vector2) {
		return new Vec(vector2.x(), vector2.y(), vector2.z());
	}

	/**
	 * Check whether or not each component of this vector is equal to 0.
	 * <br>Replaces {@code Vector#isZero()} since that method was added in spigot 1.19.3
	 * @return true if equal to zero, false if at least one component is non-zero
	 */
	public static boolean isZero(Vec vector) {
		return (vector.x() == 0 && vector.y() == 0 && vector.z() == 0);
	}

}

