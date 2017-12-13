package com.evansgame.newproject.mapeditor;
import static org.lwjgl.glfw.GLFW.*;
import static com.evansgame.newproject.mapeditor.MapEditor.*;
import org.lwjgl.glfw.*;
import java.nio.*;
import org.lwjgl.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import static org.lwjgl.opengl.GL11.*;
import java.util.*;
class Inputs{
  static int[] selection1 = new int[3];
  static int[] selection2 = new int[3];
  static float lookRight;
  static float lookDown;
  static boolean hideMouse = true;
  static float xVelocity = 0;
  static float zVelocity = 0;
  static void pollInputs(){
    if(hideMouse){
    double[] xpos = new double[1];
    double[] ypos = new double[1];
    glfwGetCursorPos(window,xpos,ypos);
    glfwSetCursorPos(window,halfwindowWidth,halfwindowHeight);
    lookRight += 10 * (xpos[0] - halfwindowWidth)/windowWidth;
    lookDown += 10 * (ypos[0] - halfwindowHeight)/windowHeight;
    for(;lookRight < 0;){lookRight += 360;}
    for(;360 < lookRight;){lookRight -= 360;}
    if(lookDown < -90)lookDown = -90;
    if(90 < lookDown)lookDown = 90;
    }
    //WASD TRANSLATION
    float speed = 1f;
    if(GLFW_PRESS == glfwGetKey(window, GLFW_KEY_LEFT_SHIFT))speed = 10f;
    byte right = 0;
    byte up = 0;
    if(GLFW.GLFW_PRESS == GLFW.glfwGetKey(window, GLFW.GLFW_KEY_W))
      up++;
    if(GLFW.GLFW_PRESS == GLFW.glfwGetKey(window, GLFW.GLFW_KEY_A))
      right--;
    if(GLFW.GLFW_PRESS == GLFW.glfwGetKey(window, GLFW.GLFW_KEY_S))
      up--;
    if(GLFW.GLFW_PRESS == GLFW.glfwGetKey(window, GLFW.GLFW_KEY_D))
      right++;

    float wasdAngle = 0;
    boolean move = true;
    if(right == 1){
      if(up == 1){wasdAngle = 45f;}
      else if(up == 0){wasdAngle = 90f;}
      else{wasdAngle = 135f;}//up == -1
    }else if(right == 0){
      if(up == 1){wasdAngle = 0;}
      else if(up == 0){move = false;}
      else{wasdAngle = 180f;}//up == -1
    }else{ //right = -1
      if(up == 1){wasdAngle = 315f;}
      else if(up == 0){wasdAngle = 270f;}
      else{wasdAngle = 225f;}//up == -1
    }
    if(move){
      wasdAngle += lookRight;
      xVelocity = (float)Math.sin(Math.toRadians(wasdAngle)) * time * speed;
      zVelocity = (float)-Math.cos(Math.toRadians(wasdAngle)) * time * speed;
    }else{
      xVelocity = 0;
      zVelocity = 0;
    }
  }
  static GLFWKeyCallback keyCallback = new GLFWKeyCallback(){
    @Override public void invoke(long window,int key,int scancode,int action,int mods){
      if(action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT){
        switch(key){
        case GLFW_KEY_F10:
          glReadBuffer(GL_FRONT);
          ByteBuffer buffer = BufferUtils.createByteBuffer(windowWidth * windowHeight * 4); /* 4 for rgba */
          glReadPixels(0, 0, windowWidth, windowHeight, GL_RGBA, GL_UNSIGNED_BYTE, buffer );//unsigned in java???
          File file = new File(new java.lang.Long(java.util.Calendar.getInstance().getTimeInMillis()).toString()+".png");
          BufferedImage image = new BufferedImage(windowWidth, windowHeight, BufferedImage.TYPE_INT_RGB);
          for(int x = 0; x < windowWidth; x++){
          for(int y = 0; y < windowHeight; y++){
          int i = (x + (windowWidth * y)) * 4;/* 4 for rgba */
          int r = buffer.get(i) & 0xFF;
          int g = buffer.get(i + 1) & 0xFF;
          int b = buffer.get(i + 2) & 0xFF;
          image.setRGB(x, windowHeight - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
          }
          }
          try{ImageIO.write(image, "PNG", file);}catch(IOException e){e.printStackTrace();}
        break;
        case GLFW_KEY_ESCAPE:
          hideMouse = !hideMouse;
          if(hideMouse){
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
            glfwSetCursorPos(window,halfwindowWidth,halfwindowHeight);
          }else{
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
          }
        break;
        case GLFW_KEY_EQUAL:
          Block b = Block.getBlock();
          if(b!=null){
            cubeR = b.r;
            cubeG = b.g;
            cubeB = b.b;
            cubeA = b.a;
          }
        break;
        case GLFW_KEY_1:
          selection1[0] = xs;
          selection1[1] = ys;
          selection1[2] = zs;
        break;
        case GLFW_KEY_2:
          selection2[0] = xs;
          selection2[1] = ys;
          selection2[2] = zs;
        break;
        case GLFW_KEY_C:
          if(mods == GLFW_MOD_CONTROL){
            clipboard.clear();
            rotations = 0;
            flipX = false;
            flipZ = false;
            Block.searchBlocks(selection1[0],selection1[1],selection1[2],selection2[0],selection2[1],selection2[2],
            new Block.ProcessBlock(){
              @Override boolean processBlock(Block b){
                clipboard.add(b);
                clipboardPos[0] = xs;
                clipboardPos[1] = ys;
                clipboardPos[2] = zs;
                return false;
              }
            });
          }
        break;
        case GLFW_KEY_V:
          if(mods == GLFW_MOD_CONTROL){
            Block[] pasted = new Block[clipboard.size()];
            int i = 0;
            for(Block bl:clipboard){//paste
              pasted[i++] = Block.waitToDraw(cubeSizeInt,bl.r,bl.g,bl.b,bl.a,bl.x,bl.y,bl.z);
            }
            if(flipX){
              for(Block bl:pasted){
                bl.x = clipboardPos[0] - (bl.x - clipboardPos[0]);
              }
            }
            if(flipZ){
              for(Block bl:pasted){
                bl.z = clipboardPos[2] - (bl.z - clipboardPos[2]);
              }
            }
            for(int evan = 0;evan < rotations;evan++){for(i = 0;i<pasted.length;i++){//rotate
              int kizer = pasted[i].z - clipboardPos[2];
              pasted[i].z = clipboardPos[2] - (pasted[i].x - clipboardPos[0]);
              pasted[i].x = kizer + clipboardPos[0];
            }}
            for(Block bl:pasted){//translate
              bl.x += xs - clipboardPos[0];
              bl.y += ys - clipboardPos[1];
              bl.z += zs - clipboardPos[2];
              if(Block.blockThere(bl.x,bl.y,bl.z,bl.size))continue;
              bl.draw();
              MapEditor.drawMes.add(bl);
              Block.blocks.add(bl);
            }
          }
        break;
        }

      }
    }
  };
  static LinkedList<Block> clipboard = new LinkedList<>();
  static int[] clipboardPos = new int[3];
  static int rotations = 0;
  static boolean flipX = false;
  static boolean flipZ = false;
  static GLFWScrollCallback scrollCallback = new GLFWScrollCallback(){@Override public void invoke(long window,double xoffset,double yoffset){
    boolean defeault = true;
    if(glfwGetKey(window, GLFW_KEY_R) == GLFW_PRESS){
      cubeR += yoffset / 10;
      if(1 < cubeR)cubeR = 1;
      if(cubeR < 0)cubeR = 0;
      defeault = false;
    }if(glfwGetKey(window, GLFW_KEY_G) == GLFW_PRESS){
      cubeG += yoffset / 10;
      if(1 < cubeG)cubeG = 1;
      if(cubeG < 0)cubeG = 0;
      defeault = false;
    }if(glfwGetKey(window, GLFW_KEY_B) == GLFW_PRESS){
      cubeB += yoffset / 10;
      if(1 < cubeB)cubeB = 1;
      if(cubeB < 0)cubeB = 0;
      defeault = false;
    }else if(glfwGetKey(window, GLFW_KEY_T) == GLFW_PRESS){
      cubeA += yoffset / 10;
      if(1 < cubeA)cubeA = 1;
      if(cubeA < 0)cubeA = 0;
    }else if(glfwGetKey(window, GLFW_KEY_8) == GLFW_PRESS){
      cubeSizeInt += yoffset;
      if(cubeSizeInt < 1)cubeSizeInt = 1;
      cubeSize = .1f * cubeSizeInt;
      cubeSize2 = .2f * cubeSizeInt;
      reachBoxDistance = 1 + cubeSize2 + additionalReach;
    }else if(glfwGetKey(window, GLFW_KEY_TAB) == GLFW_PRESS){
      additionalReach += yoffset / 10;
      reachBoxDistance = 1 + cubeSize2 + additionalReach;
    }else if(defeault){
      playerY += yoffset / 2;
    }
  }};
  static GLFWMouseButtonCallback mouseCallback = new GLFWMouseButtonCallback(){
    @Override
    public void invoke(long window,int button,int action,int mods){if(action == GLFW.GLFW_PRESS){switch(button){
    case GLFW_MOUSE_BUTTON_1:
      Block.placeBlock();
    break;
    case GLFW_MOUSE_BUTTON_2:
      for(Iterator<Block> i = Block.blocks.iterator();i.hasNext();){
        Block b = i.next();
        if(b.x == xs && b.y == ys && b.z == zs && b.size == cubeSizeInt){
          drawMes.remove(b);
          i.remove();
        }
      }
    break;
    }}}
  };
  static float additionalReach = 0;
}
