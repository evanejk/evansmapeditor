package com.evansgame.newproject.mapeditor;
public class DrawMe{
  public Face[] faces;
  public Point[] points;
  public static DrawMe color(DrawMe d,float redmin, float redmax, float greenmin, float greenmax, float bluemin, float bluemax){
      for(int i = 0;i < d.points.length;i++){
        d.points[i].r = randColor(redmin,redmax);
        d.points[i].g = randColor(greenmin,greenmax);
        d.points[i].b = randColor(bluemin,bluemax);
      }
    return d;
  }
  public static DrawMe colorClone(DrawMe srx,float redmin, float redmax, float greenmin, float greenmax, float bluemin, float bluemax){
      return color(DrawMe.cloneStatic(srx),redmin,redmax,greenmin,greenmax,bluemin,bluemax);
  }
  public static DrawMe cloneStatic(DrawMe srx){
      DrawMe out = new DrawMe();
      out.faces = new Face[srx.faces.length];
      out.points = new Point[srx.points.length];
      for(int i = 0;i < out.points.length;i++){
        out.points[i] = new Point(srx.points[i].x,srx.points[i].y,srx.points[i].z,srx.points[i].r,srx.points[i].g,srx.points[i].b,srx.points[i].a);
        srx.points[i].tempPointNumber = i;
      }
    return srx;
  }
  /*public DrawMe cloneInstance(){
      DrawMe out = new DrawMe();
      out.faces = new Face[this.faces.length];
      out.points = new Point[this.points.length];
      for(int i = 0;i < out.points.length;i++){
        out.points[i] = Point.withReal(this.points[i].x,this.points[i].y,this.points[i].z,this.points[i].r,this.points[i].g,this.points[i].b,this.points[i].a,this.points[i].texture,this.points[i].realS,this.points[i].realT);
        this.points[i].tempPointNumber = i;
      }
  }*/
  public static float randColor(float min, float max){
    float diff = max - min;
    return (float)(Math.random() * diff) + min;
  }
}
