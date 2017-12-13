package com.evansgame.newproject.mapeditor;
public class Point{
  public float x;
  public float y;
  public float z;
  public float r;
  public float g;
  public float b;
  public float a;
  public int tempPointNumber;
  Point(){}
  Point(float x, float y, float z, float r, float g, float b, float a){
    this.x = x;
    this.y = y;
    this.z = z;
    this.r = r;
    this.g = g;
    this.b = b;
    this.a = a;
  }
  static Point color(float r, float g, float b, float a){
    Point p = new Point();
    p.r = r;
    p.g = g;
    p.b = b;
    p.a = a;
    return p;
  }
}
