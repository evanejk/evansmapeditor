package com.evansgame.newproject.mapeditor;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import java.io.*;
import java.util.*;
import java.nio.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;
class MapEditor{
  static long window;
  static boolean quit = false;//atonmic
  static String consoleCommand = "";
  public static void main(String[] args){
    new Thread(){
      @Override public void run(){
        Console console = System.console();
        for(;!quit;){
          String temp = console.readLine();
          synchronized(consoleCommand){consoleCommand = temp;}
        }
      }
    }.start();
    GLFWErrorCallback.createPrint(System.err).set();
    glfwInit();
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
    window = glfwCreateWindow(windowWidth, windowHeight, "Map Editor", NULL, NULL);
    // Get the resolution of the primary monitor
    GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
    // Center our window
    glfwSetWindowPos(
        window,
        (vidmode.width() - windowWidth) / 2,
        (vidmode.height() - windowHeight) / 2
    );
    glfwMakeContextCurrent(window);
    glfwSwapInterval(1);//vsync on uses less cpu
    glfwShowWindow(window);
    createCapabilities();
    glClearColor(0.0f, 1.0f, 1.0f, 0.0f);

    glfwSetKeyCallback(window, Inputs.keyCallback);
    glfwSetScrollCallback(window, Inputs.scrollCallback);
    glfwSetMouseButtonCallback(window, Inputs.mouseCallback);
    glfwSetFramebufferSizeCallback(window, resizeWindow);
    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    glBindVertexArray(glGenVertexArrays());
    glBindBuffer(GL_ARRAY_BUFFER, glGenBuffers());
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, glGenBuffers());
    glEnable(GL_DEPTH_TEST);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);
    glEnable(GL_CULL_FACE);

    int vertexShaderInt = glCreateShader(GL_VERTEX_SHADER);
    glShaderSource(vertexShaderInt,Shaders.getVertexShader());
    glCompileShader(vertexShaderInt);

    int fragmentShaderInt = glCreateShader(GL_FRAGMENT_SHADER);
    glShaderSource(fragmentShaderInt,Shaders.getFragmentShader());
    glCompileShader(fragmentShaderInt);

    int shaderProgramInt = glCreateProgram();
    glAttachShader(shaderProgramInt,vertexShaderInt);
    glAttachShader(shaderProgramInt,fragmentShaderInt);
    glLinkProgram(shaderProgramInt);
    if(glGetShaderi(vertexShaderInt,GL_COMPILE_STATUS) != GL_TRUE){
      throw new RuntimeException(glGetShaderInfoLog(vertexShaderInt));
    }
    if(glGetShaderi(fragmentShaderInt,GL_COMPILE_STATUS) != GL_TRUE){
      throw new RuntimeException(glGetShaderInfoLog(fragmentShaderInt));
    }
    if(glGetProgrami(shaderProgramInt, GL_LINK_STATUS) != GL_TRUE){
      throw new RuntimeException(glGetProgramInfoLog(shaderProgramInt));
    }
    glUseProgram(shaderProgramInt);

    int posAttrib = glGetAttribLocation(shaderProgramInt, "position");
    glEnableVertexAttribArray(posAttrib);
    glVertexAttribPointer(posAttrib, 3, GL_FLOAT, false, 7 * 4, 0);
    int colAttrib = glGetAttribLocation(shaderProgramInt, "color");
    glEnableVertexAttribArray(colAttrib);
    glVertexAttribPointer(colAttrib, 4, GL_FLOAT, false, 7 * 4, 3 * 4);
    int uniMVP = glGetUniformLocation(shaderProgramInt, "mvp");

    IslandShape.genIsland();
    reachBox.points = new Point[8];
    for(int i = 0;i<8;i++){reachBox.points[i] = new Point();}
    reachBox.faces = Block.makeFaces(reachBox.points);
    drawMes.add(reachBox);
    for(;!glfwWindowShouldClose(window);){
      synchronized(consoleCommand){if(!consoleCommand.equals("")){
        ConsoleCommander.process(consoleCommand);
        consoleCommand = "";
      }}
      time = clock;
      clock = (float)glfwGetTime();
      time = clock - time;//happens before inputs
      glfwPollEvents();
      Inputs.pollInputs();
      playerX += Inputs.xVelocity;
      playerZ += Inputs.zVelocity;
      //reach box
      Matrix4f m = Matrix4f.pitch(-Inputs.lookRight).mul(Matrix4f.roll(-Inputs.lookDown));
      float cubeX = playerX - (reachBoxDistance * m.m02);//transform (0,0,-l) and translate to player position
      float cubeY = playerY - (reachBoxDistance * m.m12);
      float cubeZ = playerZ - (reachBoxDistance * m.m22);

      float xsf = cubeX / cubeSize2;
      xs = (int)xsf;
      if(.5 <= xsf - xs)        xs++;
      cubeX = cubeSize2 * xs;
      float ysf = cubeY / cubeSize2;
      ys = (int)ysf;
      if(.5 <= ysf - ys)        ys++;
      cubeY = cubeSize2 * ys;
      float zsf = cubeZ / cubeSize2;
      zs = (int)zsf;
      if(.5 <= zsf - zs)        zs++;
      cubeZ = cubeSize2 * zs;

      reachBox.points[0].x = cubeX - cubeSize;
      reachBox.points[1].x = reachBox.points[0].x;
      reachBox.points[5].x = reachBox.points[0].x;
      reachBox.points[4].x = reachBox.points[0].x;
      reachBox.points[3].x = cubeX + cubeSize;
      reachBox.points[2].x = reachBox.points[3].x;
      reachBox.points[6].x = reachBox.points[3].x;
      reachBox.points[7].x = reachBox.points[3].x;
      reachBox.points[0].y = cubeY + cubeSize;
      reachBox.points[3].y = reachBox.points[0].y;
      reachBox.points[4].y = reachBox.points[0].y;
      reachBox.points[7].y = reachBox.points[0].y;
      reachBox.points[1].y = cubeY - cubeSize;
      reachBox.points[2].y = reachBox.points[1].y;
      reachBox.points[5].y = reachBox.points[1].y;
      reachBox.points[6].y = reachBox.points[1].y;
      reachBox.points[0].z = cubeZ + cubeSize;
      reachBox.points[1].z = reachBox.points[0].z;
      reachBox.points[2].z = reachBox.points[0].z;
      reachBox.points[3].z = reachBox.points[0].z;
      reachBox.points[4].z = cubeZ - cubeSize;
      reachBox.points[5].z = reachBox.points[4].z;
      reachBox.points[6].z = reachBox.points[4].z;
      reachBox.points[7].z = reachBox.points[4].z;
      for(Point p : reachBox.points){
        p.r = cubeR;
        p.g = cubeG;
        p.b = cubeB;
        p.a = cubeA;
      }
      //mvp
      glUniformMatrix4fv(uniMVP, false, projection.mul(
        //rotation
        Matrix4f.roll(Inputs.lookDown).mul(Matrix4f.pitch(Inputs.lookRight))
      ).mul(
        //translation
        Matrix4f.translate(-playerX,-playerY,-playerZ)
      ).getBuffer());

      initializeDataStore();

      if(!glUnmapBuffer(GL_ARRAY_BUFFER))initializeDataStore();//if false https://www.opengl.org/sdk/docs/man2/xhtml/glMapBuffer.xml reinintialize data store
      if(!glUnmapBuffer(GL_ELEMENT_ARRAY_BUFFER))initializeDataStore();
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
      glDrawElements(GL_TRIANGLES, numFaces * 3, GL_UNSIGNED_INT, 0);
      glfwSwapBuffers(window);
    }
    quit = true;
    glfwFreeCallbacks(window);
    glfwDestroyWindow(window);
    glfwTerminate();
    glfwSetErrorCallback(null).free();
    System.exit(0);
  }
  private static int numFaces;
  private static void initializeDataStore(){
      numFaces = 0;
      int numPoints = 0;
      for(DrawMe d : drawMes){
        numFaces += d.faces.length;
        numPoints += d.points.length;
      }
      
      glBufferData(GL_ARRAY_BUFFER,numPoints * 7 * 4,GL_DYNAMIC_DRAW);
      ByteBuffer vertices = glMapBuffer(GL_ARRAY_BUFFER,GL_WRITE_ONLY);

      glBufferData(GL_ELEMENT_ARRAY_BUFFER,numFaces * 3 * 4,GL_DYNAMIC_DRAW);
      ByteBuffer indeces = glMapBuffer(GL_ELEMENT_ARRAY_BUFFER,GL_READ_WRITE);

      int pointNumber = 0;
      for(DrawMe d : drawMes){
        for(Point p : d.points){
          p.tempPointNumber = pointNumber++;
          vertices.putFloat(p.x).putFloat(p.y).putFloat(p.z).putFloat(p.r).putFloat(p.g).putFloat(p.b).putFloat(p.a);
        }
        for(Face f : d.faces){
          indeces.putInt(f.p1.tempPointNumber).putInt(f.p2.tempPointNumber).putInt(f.p3.tempPointNumber);
        }
      }
  }
  static float clock;
  static float time;
  static Matrix4f projection = Matrix4f.projection();
  private static GLFWFramebufferSizeCallback resizeWindow = new GLFWFramebufferSizeCallback(){
    @Override
    public void invoke(long window, int width, int height){
      glViewport(0,0,width,height);
      windowWidth = width;
      halfwindowWidth = windowWidth / 2;
      windowHeight = height;
      halfwindowHeight = height / 2;
      aspect = windowWidth / windowHeight;
    }
  };
  static int windowWidth = 720;
  static int windowHeight = 480;
  static int halfwindowWidth = 360;
  static int halfwindowHeight = 240;
  static float aspect = windowWidth / windowHeight;
  static LinkedList<DrawMe> drawMes = new LinkedList<>();
  //static float fov = 100;
  static float playerX = 0;
  static float playerY = 0;
  static float playerZ = 0;
  public static void quit(){
    glfwSetWindowShouldClose(window, true);
  }
  static DrawMe reachBox = new DrawMe();
  static int cubeSizeInt = 1;
  static float cubeSize = .1f;
  static float cubeSize2 = .2f;
  static float reachBoxDistance = 1.5f;
  static float cubeR = 1f;
  static float cubeG = 1f;
  static float cubeB = 1f;
  static float cubeA = 1f;
  static int xs;
  static int ys;
  static int zs;
}
