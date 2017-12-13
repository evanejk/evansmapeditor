package com.evansgame.newproject.mapeditor;
import java.util.*;
public class IslandShape{
    public static DrawMe island = null;
    public static void genIsland(){
      if(island != null)MapEditor.drawMes.remove(island);
      island = new DrawMe();
      MapEditor.drawMes.addFirst(island);
      LinkedList<Face> faces = new LinkedList<>();
      LinkedList<Point> allPoints = new LinkedList<>();
      /*float red = 0;
      float green = 0;
      float blue = 0;
      float alpha = 0;
      int index = 0;
      for(int x = -10; x < 11; x += 1){for(int y = -10; y < 11; y += 1){for(int z = -10; z < 11; z += 1){
        red += .1f;
        green += .2f;
        blue += .3f;
        alpha += .1f;
        if(index % 2 == 0)blue += .1f;
        if(index % 3 == 0)green += .1f;
        if(index % 4 == 0)red += .1f;
        if(1 < red)red -= 1;
        if(1 < green)green -= 1;
        if(1 < blue)blue -= 1;
        if(1 < alpha)alpha -= 1;
        Point p1 = new Point(x,y,z,red,green,blue,alpha);
        Point p2 = new Point(x - .1f,y - .2f,z - .1f,red,green,blue,alpha);
        Point p3 = new Point(x - .1f,y - .2f,z + .1f,red,green,blue,alpha);
        Point p4 = new Point(x + .1f,y - .2f,z + .1f,red,green,blue,alpha);
        Point p5 = new Point(x + .1f,y - .2f,z - .1f,red,green,blue,alpha);
        faces.add(new Face(p1,p3,p4));
        faces.add(new Face(p1,p4,p5));
        faces.add(new Face(p1,p5,p2));
        faces.add(new Face(p1,p2,p3));
        faces.add(new Face(p4,p3,p2));
        faces.add(new Face(p2,p5,p4));
        index++;
      }}}*/
      //make half sphere
      //morph
      int res = 5;
      float size = 5;
      float startAngle = 0;
      LinkedList<Point> previousPoints = new LinkedList<>();
      for(int layer = 1;layer < res;layer++){
        float y = (layer * size / (res - 1)) - size;
        float radius = (float)Math.sqrt((size * size) - (y * y));
        int numPoints = Math.round(((radius * 4 * res) - (4 * radius)) / size);
        LinkedList<Point> points = new LinkedList<>();
        for(int point = 0; point < numPoints;point++){
            float angle = (360 * point / numPoints) + startAngle;
            float x = (float)Math.sin(Math.toRadians(angle)) * radius;
            float z = (float)Math.cos(Math.toRadians(angle)) * radius;
/*******************************************************************************
                                        Point p1 = new Point(x,y,z,.9f,.2f,.9f,1f);
                                        Point p2 = new Point(x - .1f,y - .2f,z - .1f,.9f,.2f,.9f,1f);
                                        Point p3 = new Point(x - .1f,y - .2f,z + .1f,.9f,.2f,.9f,1f);
                                        Point p4 = new Point(x + .1f,y - .2f,z + .1f,.9f,.2f,.9f,1f);
                                        Point p5 = new Point(x + .1f,y - .2f,z - .1f,.9f,.2f,.9f,1f);
                                        faces.add(new Face(p1,p3,p4));
                                        faces.add(new Face(p1,p4,p5));
                                        faces.add(new Face(p1,p5,p2));
                                        faces.add(new Face(p1,p2,p3));
                                        faces.add(new Face(p4,p3,p2));
                                        faces.add(new Face(p2,p5,p4));
********************************************************************************/
            Point p;
            if(layer == res - 1){//all green
              p = new Point(x,y,z,0 + random(-.1f,.1f),1 + random(-.5f,0),0 + random(-.1f,.1f),1);
            }else{
              float pointTwoP = .2f * layer / (res - 2);
              p = new Point(x,y,z,.4f + pointTwoP + random(-.1f,.1f),.2f + pointTwoP + random(-.1f,.1f),pointTwoP + random(-.1f,.1f),1f);
            }
            points.add(p);
        }
        startAngle += 180 / numPoints;
        if(previousPoints.size() == 0){
          Point bottom = new Point(0,-size,0,.4f,.2f,0,1);
          allPoints.add(bottom);
          Iterator<Point> i = points.iterator();
          Point previousPoint = i.next();
          for(;i.hasNext();){
            Point next = i.next();
            faces.add(new Face(bottom,next,previousPoint));
            previousPoint = next;
          }
          faces.add(new Face(bottom,points.getFirst(),points.getLast()));
        }else{
          Iterator<Point> innerIterator = previousPoints.iterator();
          Iterator<Point> outerIterator = points.iterator();
          Point innerPoint = innerIterator.next();
          Point nextInnerPoint = innerIterator.next();
          Point outerPoint = outerIterator.next();
          for(;outerIterator.hasNext();){
            Point nextOuterPoint = outerIterator.next();
            faces.add(new Face(innerPoint,nextOuterPoint,outerPoint));
            outerPoint = nextOuterPoint;
            if(nextInnerPoint != null && distance(outerPoint,nextInnerPoint) < distance(outerPoint,innerPoint)){
              faces.add(new Face(outerPoint,innerPoint,nextInnerPoint));
              innerPoint = nextInnerPoint;
              if(innerIterator.hasNext()){nextInnerPoint = innerIterator.next();}
              else{nextInnerPoint = null;}
            }
          }
          faces.add(new Face(previousPoints.getLast(),points.getFirst(),points.getLast()));
          faces.add(new Face(points.getFirst(),previousPoints.getLast(),previousPoints.getFirst()));
        }
        if(layer + 1 == res){
          Point top = new Point(0,0,0,0,.5f + random(-.5f,0),0,1);
          allPoints.add(top);
          Iterator<Point> i = points.iterator();
          Point evan = i.next();
          for(;i.hasNext();){
            Point kizer = i.next();
            faces.add(new Face(top,evan,kizer));
            evan = kizer;
          }
          faces.add(new Face(top,points.getLast(),points.getFirst()));
        }
        allPoints.addAll(points);
        previousPoints = points;
      }
      //morph island
      float scaleX = random(4,10);
      float scaleZ = random(4,10);
      float scaleY = (scaleX + scaleZ) / 2;
      for(Point point:allPoints){
        point.x *= scaleX;
        point.z *= scaleZ;
        point.y *= scaleY;
      }
      for(int i = Math.round(random(0,8));0 < i;i--){//z morph
        float morphZ = allPoints.get((int)random(0,allPoints.size())).z;
        float ammount = random(-5,5);
        for(Point point:allPoints){
          point.z += (ammount * (1 - (Math.abs(point.z - morphZ) / (1.5 * size)))); 
        }
      }
      for(int i = Math.round(random(0,8));0 < i;i--){//x morph
        float morphX = allPoints.get((int)random(0,allPoints.size())).x;
        float ammount = random(-5,5);
        for(Point point:allPoints){
          point.x += (ammount * (1 - (Math.abs(point.x - morphX) / (1.5 * size)))); 
        }
      }
      island.faces = new Face[faces.size()];
      faces.toArray(island.faces);
      island.points = new Point[allPoints.size()];
      allPoints.toArray(island.points);
    }
    public static float random(float low, float high){
      return (float)(Math.random() * (high - low)) + low;
    }
    public static float distance(Point p1, Point p2){return (float)Math.sqrt(Math.pow(p2.x - p1.x,2)+Math.pow(p2.y-p1.y,2)+Math.pow(p2.z-p1.z,2));}
}
