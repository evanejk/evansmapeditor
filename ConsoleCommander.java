package com.evansgame.newproject.mapeditor;
import java.util.*;
import java.nio.file.*;
import java.io.*;
class ConsoleCommander{
  final static Path maps = Paths.get("com/evansgame/newproject/maps/");
  private static String oldName = null;
  static void process(String command){try{
    String responce = null;//ranny ranny ranny
    Scanner scanner = new Scanner(command);
    if(scanner.hasNext()){
      String next = scanner.next();
      if(next.equals("island")){
        IslandShape.genIsland();
        responce = ">island generated";
      }else if(next.equals("save")){
        if(scanner.hasNext())        oldName = scanner.next();
        else if(oldName == null)    oldName = new java.lang.Long(java.util.Calendar.getInstance().getTimeInMillis()).toString();
        Path path = maps.resolve(oldName);

        if(!path.getParent().equals(maps))return;//don't accidently tunnel out of directory
        Files.deleteIfExists(path.resolve("islandShape"));//will overwrite anyways?
        Files.deleteIfExists(path.resolve("blocks"));
        Files.createDirectories(path);
        DataOutputStream islandShape = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(path.resolve("islandShape").toFile())));
        islandShape.writeInt(IslandShape.island.points.length);
        int evan = 0;
        for(Point p : IslandShape.island.points){
          islandShape.writeFloat(p.x);islandShape.writeFloat(p.y);islandShape.writeFloat(p.z);
          islandShape.writeFloat(p.r);islandShape.writeFloat(p.g);islandShape.writeFloat(p.b);islandShape.writeFloat(p.a);
          p.tempPointNumber = evan++;
        }
        islandShape.writeInt(IslandShape.island.faces.length);
        for(Face f : IslandShape.island.faces){islandShape.writeInt(f.p1.tempPointNumber);islandShape.writeInt(f.p2.tempPointNumber);islandShape.writeInt(f.p3.tempPointNumber);}
        islandShape.close();
        DataOutputStream blocks = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(path.resolve("blocks").toFile())));
        blocks.writeInt(Block.blocks.size());
        for(Block b:Block.blocks){blocks.writeInt(b.size);blocks.writeFloat(b.r);blocks.writeFloat(b.g);blocks.writeFloat(b.b);blocks.writeFloat(b.a);blocks.writeInt(b.x);blocks.writeInt(b.y);blocks.writeInt(b.z);}
        blocks.close();
        responce = ">island saved";
      }else if(next.equals("open") || next.equals("load")){if(scanner.hasNext()){
        oldName = scanner.next();
        MapEditor.drawMes.clear();
        MapEditor.drawMes.add(IslandShape.island);
        MapEditor.drawMes.add(MapEditor.reachBox);
        DataInputStream islandShape = new DataInputStream(new BufferedInputStream(new FileInputStream(maps.resolve(oldName).resolve("islandShape").toFile())));
        IslandShape.island.points = new Point[islandShape.readInt()];
        for(int i = 0; i < IslandShape.island.points.length; i++)IslandShape.island.points[i] = new Point(islandShape.readFloat(),islandShape.readFloat(),islandShape.readFloat(),islandShape.readFloat(),islandShape.readFloat(),islandShape.readFloat(),islandShape.readFloat());
        IslandShape.island.faces = new Face[islandShape.readInt()];
        for(int i = 0;i<IslandShape.island.faces.length;i++)IslandShape.island.faces[i] = new Face(IslandShape.island.points[islandShape.readInt()],IslandShape.island.points[islandShape.readInt()],IslandShape.island.points[islandShape.readInt()]);
        islandShape.close();
        Block.blocks.clear();
        DataInputStream blocks = new DataInputStream(new BufferedInputStream(new FileInputStream(maps.resolve(oldName).resolve("blocks").toFile())));
        for(int i = blocks.readInt();0<i;i--){
          new Block(blocks.readInt(),blocks.readFloat(),blocks.readFloat(),blocks.readFloat(),blocks.readFloat(),blocks.readInt(),blocks.readInt(),blocks.readInt());
        }
        blocks.close();
        responce = ">island loaded";
      }}else if(next.equals("q")){
        MapEditor.quit();
        responce = ">quit";
      }else if(next.equals("fill")){
        int x1;
        int x2;
        int y1;
        int y2;
        int z1;
        int z2;
        if(Inputs.selection1[0] < Inputs.selection2[0]){
          x1 = Inputs.selection1[0];
          x2 = Inputs.selection2[0];
        }else{
          x1 = Inputs.selection2[0];
          x2 = Inputs.selection1[0];
        }
        if(Inputs.selection1[1] < Inputs.selection2[1]){
          y1 = Inputs.selection1[1];
          y2 = Inputs.selection2[1];
        }else{
          y1 = Inputs.selection2[1];
          y2 = Inputs.selection1[1];
        }
        if(Inputs.selection1[2] < Inputs.selection2[2]){
          z1 = Inputs.selection1[2];
          z2 = Inputs.selection2[2];
        }else{
          z1 = Inputs.selection2[2];
          z2 = Inputs.selection1[2];
        }
        for(int x3 = x1;x3 <= x2;x3++){for(int y3 = y1;y3 <= y2;y3++){for(int z3 = z1;z3 <= z2;z3++){
          if(Block.blockThere(x3,y3,z3,MapEditor.cubeSizeInt))continue;
          new Block(MapEditor.cubeSizeInt,MapEditor.cubeR,MapEditor.cubeG,MapEditor.cubeB,MapEditor.cubeA,x3,y3,z3);
        }}}
        responce = ">filled";
      }else if(next.equals("delete") || next.equals("del")){
        Block.searchBlocks(Inputs.selection1[0],Inputs.selection1[1],Inputs.selection1[2],Inputs.selection2[0],Inputs.selection2[1],Inputs.selection2[2],
        new Block.ProcessBlock(){
          @Override boolean processBlock(Block b){
            MapEditor.drawMes.remove(b);
            return true;
          }
        });
        responce = ">deleted";
      }else if(next.equals("rotate") || next.equals("rot")){
        if(scanner.hasNextInt())Inputs.rotations = scanner.nextInt();
        else            Inputs.rotations = 1;
        responce = ">rotated";
      }else if(next.equals("flip")){
          if(scanner.hasNext()){
            String axi = scanner.next();
            if(axi.equals("x"))Inputs.flipX = !Inputs.flipX;
            if(axi.equals("z"))Inputs.flipZ = !Inputs.flipZ;
          }
          responce = ">flipped";
      }else if(next.equals("size")){
      //Inputs.selection1[0], Inputs.selection1[1] Inputs.selection1[2]

          responce = "x: "+(Inputs.selection2[0]-Inputs.selection1[0])+" y: "+(Inputs.selection2[1]-Inputs.selection1[1])+" z: " +(Inputs.selection2[2]-Inputs.selection1[2]) + System.lineSeparator();
      }else if(next.equals("replace")){
        Block targetBlock = Block.getBlock();
        float[] target = {targetBlock.r,targetBlock.g,targetBlock.b,targetBlock.a};
        Block.searchBlocks(Inputs.selection1[0],Inputs.selection1[1],Inputs.selection1[2],Inputs.selection2[0],Inputs.selection2[1],Inputs.selection2[2],
        new Block.ProcessBlock(){
          @Override boolean processBlock(Block b){
            if(b.r == target[0] && b.g == target[1] && b.b == target[2] && b.a == target[3]){
              b.r = MapEditor.cubeR;
              b.g = MapEditor.cubeG;
              b.b = MapEditor.cubeB;
              b.a = MapEditor.cubeA;
              b.redrawColor();
            }
            return false;
          }
        });
        responce = ">replaced";
      }else if(next.equals("fixtrannys")){//sort them too
        LinkedList<Block> trannysToFix = new LinkedList<>();
        for(Block b:Block.blocks){
          if(b.a < 1){
            MapEditor.drawMes.remove(b);
            MapEditor.drawMes.addLast(b);
            trannysToFix.add(b);
          }
        }
        if(trannysToFix.size() != 0){
            Block[] sorted = new Block[trannysToFix.size()];
            int index = 0;
            for(;index < sorted.length;){
              Block block = trannysToFix.getFirst();
              for(Block square:trannysToFix){
                if(square.x <= block.x && block.y <= square.y && square.z <= block.z)block = square;
              }
              trannysToFix.remove(block);
              sorted[index++] = block;
            }
            for(Block square:sorted){
              Block.blocks.remove(square);
              Block.blocks.add(square);
              MapEditor.drawMes.remove(square);
              MapEditor.drawMes.add(square);
            }
        }
        responce = ">straightened out";
      }else if(next.equals("test")){
        for(Block b4:Block.blocks){
          for(Block b1:Block.blocks){
            if(b1.x == b4.x && b1.y == b4.y && b1.z == b4.z && b1.size == b4.size && b1 != b4)System.out.println("poop");
          }
        }
      }else{
        responce = ">commands: island, save, load/open, q (quit), fill, delete/del, rotate/rot [rotations], replace, size, fixtrannys";
      }
    }else{responce = ">commands: island, save, load/open, q (quit), fill, delete/del, rotate/rot [rotations], replace, size, fixtrannys";}
    System.out.printf("%s%n",responce);
  }catch(Exception e){e.printStackTrace();}}
}
