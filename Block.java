package com.evansgame.newproject.mapeditor;
import java.util.*;
class Block extends DrawMe{
  static LinkedList<Block> blocks = new LinkedList<>();
  int size;
  float r;
  float g;
  float b;
  float a;
  int x;
  int y;
  int z;
  Block(){}
  static void placeBlock(){
    if(blockThere(MapEditor.xs,MapEditor.ys,MapEditor.zs,MapEditor.cubeSizeInt))return;
    Block b = new Block();
    b.size = MapEditor.cubeSizeInt;
    b.x = MapEditor.xs;
    b.y = MapEditor.ys;
    b.z = MapEditor.zs;
    b.r = MapEditor.cubeR;
    b.g = MapEditor.cubeG;
    b.b = MapEditor.cubeB;
    b.a = MapEditor.cubeA;
    b.points = new Point[8];
    for(int i = 0;i < 8;i++)b.points[i] = new Point(MapEditor.reachBox.points[i].x,MapEditor.reachBox.points[i].y,MapEditor.reachBox.points[i].z,MapEditor.reachBox.points[i].r,MapEditor.reachBox.points[i].g,MapEditor.reachBox.points[i].b,MapEditor.reachBox.points[i].a);
    b.faces = makeFaces(b.points);
    MapEditor.drawMes.add(b);
    blocks.add(b);
  }
  Block(int size,float r,float g,float b,float a,int x,int y,int z){
    this.size = size;
    this.x = x;
    this.y = y;
    this.z = z;
    this.r = r;
    this.g = g;
    this.b = b;
    this.a = a;
    draw();
    MapEditor.drawMes.add(this);
    blocks.add(this);
  }
  static Block waitToDraw(int size,float r,float g,float b,float a,int x,int y,int z){
    Block bl = new Block();
    bl.size = size;
    bl.x = x;
    bl.y = y;
    bl.z = z;
    bl.r = r;
    bl.g = g;
    bl.b = b;
    bl.a = a;
    return bl;
  }
  static Face[] makeFaces(Point[] points){
    Face[] faces = new Face[12];
    faces[0] = new Face(points[0],points[1],points[3]);
    faces[1] = new Face(points[2],points[3],points[1]);
    faces[2] = new Face(points[4],points[5],points[0]);
    faces[3] = new Face(points[1],points[0],points[5]);
    faces[4] = new Face(points[4],points[0],points[7]);
    faces[5] = new Face(points[3],points[7],points[0]);
    faces[6] = new Face(points[3],points[2],points[7]);
    faces[7] = new Face(points[6],points[7],points[2]);
    faces[8] = new Face(points[7],points[6],points[4]);
    faces[9] = new Face(points[5],points[4],points[6]);
    faces[10] = new Face(points[1],points[5],points[2]);
    faces[11] = new Face(points[6],points[2],points[5]);
    return faces;
  }
  static Block getBlock(){
    for(Block b:blocks){
      if(b.x == MapEditor.xs && b.y == MapEditor.ys && b.z == MapEditor.zs && b.size == MapEditor.cubeSizeInt){
        return b;
      }
    }
    return null;
  }
  static boolean blockThere(int x, int y, int z, int size){
    for(Block b:blocks){
      if(b.x == x && b.y == y && b.z == z && b.size == size){
        return true;
      }
    }
    return false;
  }
  static void searchBlocks(int xx, int yy, int zz, int xxx, int yyy, int zzz, ProcessBlock processBlock){
        int x1;
        int x2;
        int y1;
        int y2;
        int z1;
        int z2;
        if(xx < xxx){
          x1 = xx;
          x2 = xxx;
        }else{
          x1 = xxx;
          x2 = xx;
        }
        if(yy < yyy){
          y1 = yy;
          y2 = yyy;
        }else{
          y1 = yyy;
          y2 = yy;
        }
        if(zz < zzz){
          z1 = zz;
          z2 = zzz;
        }else{
          z1 = zzz;
          z2 = zz;
        }
        for(Iterator<Block> i = blocks.iterator();i.hasNext();){
          Block b = i.next();
          if(b.size == MapEditor.cubeSizeInt && x1 <= b.x && b.x <= x2 && y1 <= b.y && b.y <= y2 && z1 <= b.z && b.z <= z2){
            if(processBlock.processBlock(b))i.remove();
          }
        }
  }
  abstract static class ProcessBlock{
    abstract boolean processBlock(Block b);
  }
  void draw(){
    points = new Point[8];
    for(int i = 0; i < 8; i++)points[i] = Point.color(r,g,b,a);
    float cubeX = size * .2f * x;
    float cubeY = size * .2f * y;
    float cubeZ = size * .2f * z;
    float cubeSize = size * .1f;
    points[0].x = cubeX - cubeSize;
    points[1].x = points[0].x;
    points[5].x = points[0].x;
    points[4].x = points[0].x;
    points[3].x = cubeX + cubeSize;
    points[2].x = points[3].x;
    points[6].x = points[3].x;
    points[7].x = points[3].x;
    points[0].y = cubeY + cubeSize;
    points[3].y = points[0].y;
    points[4].y = points[0].y;
    points[7].y = points[0].y;
    points[1].y = cubeY - cubeSize;
    points[2].y = points[1].y;
    points[5].y = points[1].y;
    points[6].y = points[1].y;
    points[0].z = cubeZ + cubeSize;
    points[1].z = points[0].z;
    points[2].z = points[0].z;
    points[3].z = points[0].z;
    points[4].z = cubeZ - cubeSize;
    points[5].z = points[4].z;
    points[6].z = points[4].z;
    points[7].z = points[4].z;
    faces = makeFaces(points);
  }
  void redrawColor(){
    for(Point p:points){
      p.r = r;
      p.g = g;
      p.b = b;
      p.a = a;
    }
  }
}
