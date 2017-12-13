package com.evansgame.newproject.mapeditor;
import java.nio.*;
import org.lwjgl.*;
public class Matrix4f{
  float m00 = 1;float m01 = 0;float m02 = 0;float m03 = 0;
  float m10 = 0;float m11 = 1;float m12 = 0;float m13 = 0;
  float m20 = 0;float m21 = 0;float m22 = 1;float m23 = 0;
  float m30 = 0;float m31 = 0;float m32 = 0;float m33 = 1;
  public FloatBuffer getBuffer(){
    return (FloatBuffer)BufferUtils.createFloatBuffer(16)
    .put(m00).put(m10).put(m20).put(m30)
    .put(m01).put(m11).put(m21).put(m31)
    .put(m02).put(m12).put(m22).put(m32)
    .put(m03).put(m13).put(m23).put(m33)
    .flip();
  }
  public Matrix4f mul(Matrix4f other){
    Matrix4f result = new Matrix4f();
    result.m00 = this.m00 * other.m00 + this.m01 * other.m10 + this.m02 * other.m20 + this.m03 * other.m30;
    result.m10 = this.m10 * other.m00 + this.m11 * other.m10 + this.m12 * other.m20 + this.m13 * other.m30;
    result.m20 = this.m20 * other.m00 + this.m21 * other.m10 + this.m22 * other.m20 + this.m23 * other.m30;
    result.m30 = this.m30 * other.m00 + this.m31 * other.m10 + this.m32 * other.m20 + this.m33 * other.m30;
    result.m01 = this.m00 * other.m01 + this.m01 * other.m11 + this.m02 * other.m21 + this.m03 * other.m31;
    result.m11 = this.m10 * other.m01 + this.m11 * other.m11 + this.m12 * other.m21 + this.m13 * other.m31;
    result.m21 = this.m20 * other.m01 + this.m21 * other.m11 + this.m22 * other.m21 + this.m23 * other.m31;
    result.m31 = this.m30 * other.m01 + this.m31 * other.m11 + this.m32 * other.m21 + this.m33 * other.m31;
    result.m02 = this.m00 * other.m02 + this.m01 * other.m12 + this.m02 * other.m22 + this.m03 * other.m32;
    result.m12 = this.m10 * other.m02 + this.m11 * other.m12 + this.m12 * other.m22 + this.m13 * other.m32;
    result.m22 = this.m20 * other.m02 + this.m21 * other.m12 + this.m22 * other.m22 + this.m23 * other.m32;
    result.m32 = this.m30 * other.m02 + this.m31 * other.m12 + this.m32 * other.m22 + this.m33 * other.m32;
    result.m03 = this.m00 * other.m03 + this.m01 * other.m13 + this.m02 * other.m23 + this.m03 * other.m33;
    result.m13 = this.m10 * other.m03 + this.m11 * other.m13 + this.m12 * other.m23 + this.m13 * other.m33;
    result.m23 = this.m20 * other.m03 + this.m21 * other.m13 + this.m22 * other.m23 + this.m23 * other.m33;
    result.m33 = this.m30 * other.m03 + this.m31 * other.m13 + this.m32 * other.m23 + this.m33 * other.m33;
    return result;
  }
  public static Matrix4f yaw(float d){
    double toRadian = Math.toRadians(d);
    Matrix4f m = new Matrix4f();
    m.m00 = (float)Math.cos(toRadian);
    m.m10 = (float)Math.sin(toRadian);
    m.m01 = -m.m10;
    m.m11 = m.m00;
    return m;    
  }
  public static Matrix4f pitch(float d){
    double toRadian = Math.toRadians(d);
    Matrix4f m = new Matrix4f();
    m.m00 = (float)Math.cos(toRadian);
    m.m02 = (float)Math.sin(toRadian);
    m.m20 = -m.m02;
    m.m22 = m.m00;
    return m;
  }
  public static Matrix4f roll(float d){
    double toRadian = Math.toRadians(d);
    Matrix4f m = new Matrix4f();
    m.m11 = (float)Math.cos(toRadian);
    m.m21 = (float)Math.sin(toRadian);
    m.m12 = -m.m21;
    m.m22 = m.m11;
    return m;
  }
  public static Matrix4f projection(){
        Matrix4f frustum = new Matrix4f();

    final float left = -.2f;
    final float right = .2f;
    final float top = .2f;
    final float bottom = -.2f;
    final float near = .1f;
    final float far = 1000;

        float a = (right + left) / (right - left);
        float b = (top + bottom) / (top - bottom);
        float c = -(far + near) / (far - near);
        float d = -(2f * far * near) / (far - near);

        frustum.m00 = (2f * near) / (right - left);
        frustum.m11 = (2f * near) / (top - bottom);
        frustum.m02 = a;
        frustum.m12 = b;
        frustum.m22 = c;
        frustum.m32 = -1f;
        frustum.m23 = d;
        frustum.m33 = 0f;

        return frustum;
  }
  public static Matrix4f translate(float x, float y, float z){
      Matrix4f m = new Matrix4f();
      m.m03 = x;
      m.m13 = y;
      m.m23 = z;
      return m;
  }
}
