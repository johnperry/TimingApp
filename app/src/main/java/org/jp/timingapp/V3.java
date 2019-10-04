package org.jp.timingapp;

/**
 * Created by John on 4/1/2016.
 */
public class V3 {
    double x;
    double y;
    double z;
    public V3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public V3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public V3(float[] a) {
        x = a[0];
        y = a[1];
        z = a[2];
    }
    public static V3 zero() {
        return new V3(0.0, 0.0, 0.0);
    }
    public V3 add(V3 v) {
        return new V3(x + v.x, y + v.y, z + v.z);
    }
    public V3 normalize() {
        double len = length();
        if (len > 0.0) return scale(1.0/length());
        else return this;
    }
    public V3 scale(double r) {
        return new V3(x * r, y * r, z * r);
    }
    public double length() {
        return Math.sqrt(dot(this));
    }
    public double dot(V3 v) {
        return x * v.x + y * v.y + z * v.z;
    }
     public V3 cross(V3 v) {
        return new V3(
                y * v.z - z * v.y,
                z * v.x - x * v.z,
                x * v.y - y * v.x );
    }
}
