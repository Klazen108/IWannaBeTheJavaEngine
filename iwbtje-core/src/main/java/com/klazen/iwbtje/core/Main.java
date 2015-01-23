package com.klazen.iwbtje.core;

import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.io.IOException;
import java.nio.ByteBuffer;
 
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {
	
    // We need to strongly reference callback instances.
    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback   keyCallback;
 
    // The window handle
    private long window;
 
    public void run() {
        System.out.println("Hello LWJGL " + Sys.getVersion() + "!");
 
        try {
            init();
            loop();
 
            // Release window and window callbacks
            glfwDestroyWindow(window);
            keyCallback.release();
        } finally {
            // Terminate GLFW and release the GLFWerrorfun
            glfwTerminate();
            errorCallback.release();
        }
    }
 
    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));
 
        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( glfwInit() != GL11.GL_TRUE )
            throw new IllegalStateException("Unable to initialize GLFW");
 
        // Configure our window
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable
 
        int WIDTH = 800;
        int HEIGHT = 600;
 
        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");
 
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                    glfwSetWindowShouldClose(window, GL_TRUE); // We will detect this in our rendering loop
            }
        });
 
        // Get the resolution of the primary monitor
        ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center our window
        glfwSetWindowPos(
            window,
            (GLFWvidmode.width(vidmode) - WIDTH) / 2,
            (GLFWvidmode.height(vidmode) - HEIGHT) / 2
        );
 
        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);
 
        // Make the window visible
        glfwShowWindow(window);
    }
 
    private void loop() {
        GLContext.createFromCurrent();
 
        int pId = 0;
		try {
			pId = createShaderProgram();
		} catch (IOException e) {
			System.exit(-1);
			e.printStackTrace();
		}
        int vao = createVAO();
        
        glClearColor(0.0f, 0.1f, 0.2f, 0.0f);
 
        while ( glfwWindowShouldClose(window) == GL_FALSE ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
 
            //drawing code here
            glUseProgram(pId);
            glBindVertexArray(vao);
            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);

        	int error = glGetError();
        	if (error != GL_NO_ERROR) System.out.println("Error occurred! Code: "+error);
            
            glDrawArrays(GL_TRIANGLES,0,3);

        	error = glGetError();
        	if (error != GL_NO_ERROR) System.out.println("Error occurred! Code: "+error);
            
            glDisableVertexAttribArray(1);
            glDisableVertexAttribArray(0);
            glBindVertexArray(0);
            glUseProgram(0);

            glfwSwapBuffers(window);
            
            glfwPollEvents();
        }
        
        glDeleteVertexArrays(vao);
        glDeleteProgram(pId);
    }
    
    private int createVAO() {
    	float[] vertices = { -.25f, -.25f,
    			              0f, -.25f,
    			              0f,  .25f };
    	FloatBuffer vertB = BufferUtils.createFloatBuffer(vertices.length);
    	vertB.put(vertices).flip();
    	
    	float[] colors = { 1.0f, 0.0f, 0.0f, 1.0f,
    					 0.0f, 1.0f, 0.0f, 1.0f,
    					 0.0f, 0.0f, 1.0f, 1.0f };
    	FloatBuffer colorB = BufferUtils.createFloatBuffer(colors.length);
    	colorB.put(colors).flip();
    	
    	
    	int vao = glGenVertexArrays();
    	glBindVertexArray(vao);
    	
    	int vboVert = glGenBuffers();
    	glBindBuffer(GL_ARRAY_BUFFER, vboVert);
    	glBufferData(GL_ARRAY_BUFFER, vertB, GL_STATIC_DRAW);
    	glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
    	glEnableVertexAttribArray(0);
    	
    	int error = glGetError();
    	if (error != GL_NO_ERROR) System.out.println("Error occurred! Code: "+error);

    	int vboCol = glGenBuffers();
    	glBindBuffer(GL_ARRAY_BUFFER, vboCol);
    	glBufferData(GL_ARRAY_BUFFER, colorB, GL_STATIC_DRAW);
    	glVertexAttribPointer(1, 4, GL_FLOAT, false, 0, 0);
    	glEnableVertexAttribArray(1);

    	error = glGetError();
    	if (error != GL_NO_ERROR) System.out.println("Error occurred! Code: "+error);
    	
    	glBindBuffer(GL_ARRAY_BUFFER,0);
    	glBindVertexArray(0);
    	
    	return vao;
    }
    
    private int createShaderProgram() throws IOException {
		int vertShaderId = loadShader("src\\main\\resources\\shader.vert",GL_VERTEX_SHADER);
		int fragShaderId = loadShader("src\\main\\resources\\shader.frag",GL_FRAGMENT_SHADER);
		
		int pId = glCreateProgram();
		glAttachShader(pId,vertShaderId);
		glAttachShader(pId,fragShaderId);
		glLinkProgram(pId);

    	int error = glGetProgrami(pId, GL_LINK_STATUS);
    	System.out.println("Linked program");
    	System.out.println("Status code: "+error);
    	if (error != GL_TRUE) System.out.println(glGetProgramInfoLog(pId));
		
		glDeleteShader(vertShaderId);
		glDeleteShader(fragShaderId);
		return pId;
    }
    
    private int loadShader(String filename, int shaderType) throws IOException {
    	int id = glCreateShader(shaderType);
    	String shaderSource = new String(Files.readAllBytes(Paths.get(filename)));
    	glShaderSource(id,shaderSource);
    	glCompileShader(id);
    	
    	int error = glGetShaderi(id, GL_COMPILE_STATUS);
    	System.out.println("Compiled shader: "+filename);
    	System.out.println("Status code: "+error);
    	if (error != GL_TRUE) System.out.println(glGetShaderInfoLog(id));
    	
    	return id;
    }
 
    public static void main(String[] args) {
        new Main().run();
    }
 
}