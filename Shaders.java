package com.evansgame.newproject.mapeditor;
public class Shaders{
  public static String getVertexShader(){
    return "#version 130\n"+
      "uniform mat4 mvp;"+
      "in vec3 position;"+
      "in vec4 color;"+
      "out vec4 vertexColor;"+
      "void main(){"+
        "vertexColor = color;"+
        "gl_Position = mvp * vec4(position, 1.0);"+
      "}"    
    ;
  }
  public static String getFragmentShader(){
    return "#version 130\n"+
      "in vec4 vertexColor;"+
      "out vec4 fragColor;"+
      "void main(){"+
        "fragColor = vertexColor;"+
      "}" 
    ;
  }
}
