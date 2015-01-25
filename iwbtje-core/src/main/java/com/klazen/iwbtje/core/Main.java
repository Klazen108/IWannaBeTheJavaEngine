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
import java.util.LinkedList;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {
	
    // We need to strongly reference callback instances.
    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback   keyCallback;
 
    // The window handle
    private long window;
 
    public void run() {
        System.out.println("Hello LWJGL " + Sys.getVersion() + "!");
        
        objects = new LinkedList<GObject>();
        objects.add(new GObject(200,200));
        objects.add(new GObject(600,200));
        objects.add(new GObject(200,400));
        objects.add(new GObject(600,400));
 
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
 
			glUseProgram(pId);
			glBindVertexArray(vao);
			glEnableVertexAttribArray(0);
			glEnableVertexAttribArray(1);
			glEnableVertexAttribArray(2);
			
			checkGLError();
	    	
	    	float left=0;
	    	float right=800;
	    	float top = 0;
	    	float bottom = 600;
	    	float near=-1000;
	    	float far=1000;
	    	float[] matrix = { 2/(right-left),0,0,-1*(right+left)/(right-left),
	    			           0,2/(top-bottom),0,-1*(top+bottom)/(top-bottom),
	    			           0,0,-1/(far-near),-1*(far+near)/(far-near),
	    			           0,0,0,1};
			FloatBuffer mB = BufferUtils.createFloatBuffer(matrix.length);
			mB.put(matrix).flip();
			int loc = glGetUniformLocation(pId, "pMatrix");
	    	glUniformMatrix4(loc,true,mB);
	    	checkGLError();

			draw();
			
			checkGLError();

			glDisableVertexAttribArray(2);
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
    
    List<GObject> objects;
    
    public void draw() {
    	//for each object
    	//  get object position
    	//  increment count
    	//upload to buffer
    	//draw instanced
    	
		//mode = drawing mode
		//first = instance to start at
		//count = number of indices
		//primcount = number of instances
		glDrawArraysInstanced(GL_TRIANGLE_STRIP,0,4,4);
    }
    
    private int createVAO() {
    	int vao = glGenVertexArrays();
    	glBindVertexArray(vao);
    	
    	{
	    	float[] vertices = { 0,0,
	    						100,0,
	    						0,100,
	    						100,100};
	    	FloatBuffer vertB = BufferUtils.createFloatBuffer(vertices.length);
	    	vertB.put(vertices).flip();
	    	
	    	int vboVert = glGenBuffers();
	    	glBindBuffer(GL_ARRAY_BUFFER, vboVert);
	    	glBufferData(GL_ARRAY_BUFFER, vertB, GL_STATIC_DRAW);
	    	glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
	    	glEnableVertexAttribArray(0);
	    	
	    	checkGLError();
    	}
    	{
	    	float[] colors = { 1.0f, 0.0f, 0.0f, 1.0f,
	    					 0.0f, 1.0f, 0.0f, 1.0f,
	    					 0.0f, 0.0f, 1.0f, 1.0f,
	    					 1.0f, 0.0f, 1.0f, 1.0f};
	    	FloatBuffer colorB = BufferUtils.createFloatBuffer(colors.length);
	    	colorB.put(colors).flip();
	    	
	    	int vboCol = glGenBuffers();
	    	glBindBuffer(GL_ARRAY_BUFFER, vboCol);
	    	glBufferData(GL_ARRAY_BUFFER, colorB, GL_STATIC_DRAW);
	    	glVertexAttribPointer(1, 4, GL_FLOAT, false, 0, 0);
	    	glEnableVertexAttribArray(1);
	
	    	checkGLError();
    	}
    	{
	    	float[] instance_positions = { 100,100,
	    								   600,100,
	    								   100,400,
	    								   600,400
	    								  };
	    	FloatBuffer ipB = BufferUtils.createFloatBuffer(instance_positions.length);
	    	ipB.put(instance_positions).flip();
	    	
	    	int vboIp = glGenBuffers();
	    	glBindBuffer(GL_ARRAY_BUFFER, vboIp);
	    	glBufferData(GL_ARRAY_BUFFER, ipB, GL_STATIC_DRAW);
	    	glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, 0);
	    	glVertexAttribDivisor(2,1);
	    	glEnableVertexAttribArray(2);

	    	checkGLError();
    	}

    	glBindBuffer(GL_ARRAY_BUFFER,0);
    	glBindVertexArray(0);
    	
    	return vao;
    }
    
    private void checkGLError() {

    	int error = glGetError();
    	if (error != GL_NO_ERROR) {
    		System.out.println("Error occurred! Code: "+error);
    		throw new RuntimeException();
    	}
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