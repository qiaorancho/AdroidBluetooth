package com.app.GestureGame.ThreeD;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup;

import com.app.GestureGame.R;
import com.app.GestureGame.Controller.MessageController;

public class OpenGLDemoActivity extends Activity 
implements SensorEventListener, RadioGroup.OnCheckedChangeListener{
	
	
	private MessageController mMsgclr= MessageController.getinstant();
	
	
	//for sensor
private SensorManager mSensorManager = null;
	
    // angular speeds from gyro
    private float[] gyro = new float[3];
 
    // rotation matrix from gyro data
    private float[] gyroMatrix = new float[9];
 
    // orientation angles from gyro matrix
    private float[] gyroOrientation = new float[3];
 
    // magnetic field vector
    private float[] magnet = new float[3];
 
    // accelerometer vector
    private float[] accel = new float[3];
 
    // orientation angles from accel and magnet
    private float[] accMagOrientation = new float[3];
 
    // final orientation angles from sensor fusion
    private float[] fusedOrientation = new float[3];
 
    // accelerometer and magnetometer based rotation matrix
    private float[] rotationMatrix = new float[9];
    
    public static final float EPSILON = 0.000000001f;
    private static final float NS2S = 1.0f / 1000000000.0f;
	private float timestamp;
	private boolean initState = true;
    
	public static final int TIME_CONSTANT = 30;
	public static final float FILTER_COEFFICIENT = 0.98f;
	private Timer fuseTimer = new Timer();
	
	// The following members are only for displaying the sensor output.
	public Handler mHandler;
	//private RadioGroup mRadioGroup;
	/*private TextView mAzimuthView;
	private TextView mPitchView;
	private TextView mRollView;*/
	private int radioSelection;
	DecimalFormat d = new DecimalFormat("#.##");
	
	
	
	//cube part
    float [] cube_rotattion = new float[3];
	
	
	//system part
    boolean Stop_flag=true;
    boolean My_turn=true;
	
        
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        // system flags      
        Stop_flag=false;
        mMsgclr.ShouldWork=false;
        
        // Go fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        GLSurfaceView view = new GLSurfaceView(this);
        view.setRenderer(new OpenGLRenderer());
        setContentView(view);
        
        
        ///sensor part
        gyroOrientation[0] = 0.0f;
        gyroOrientation[1] = 0.0f;
        gyroOrientation[2] = 0.0f;
 
        // initialise gyroMatrix with identity matrix
        gyroMatrix[0] = 1.0f; gyroMatrix[1] = 0.0f; gyroMatrix[2] = 0.0f;
        gyroMatrix[3] = 0.0f; gyroMatrix[4] = 1.0f; gyroMatrix[5] = 0.0f;
        gyroMatrix[6] = 0.0f; gyroMatrix[7] = 0.0f; gyroMatrix[8] = 1.0f;
 
        // get sensorManager and initialise sensor listeners
        mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        initListeners();
        
        // wait for one second until gyroscope and magnetometer/accelerometer
        // data is initialised then scedule the complementary filter task
        fuseTimer.scheduleAtFixedRate(new calculateFusedOrientationTask(),
                                      100, TIME_CONSTANT);
        
        // GUI stuff
        mHandler = new Handler();
        radioSelection = 0;
        d.setRoundingMode(RoundingMode.HALF_UP);
        d.setMaximumFractionDigits(3);
        d.setMinimumFractionDigits(3);
        /*mRadioGroup = (RadioGroup)findViewById(R.id.radioGroup1);
        mAzimuthView = (TextView)findViewById(R.id.textView4);
        mPitchView = (TextView)findViewById(R.id.textView5);
        mRollView = (TextView)findViewById(R.id.textView6);
        mRadioGroup.setOnCheckedChangeListener(this);
        */
        
    }
    
    class OpenGLRenderer implements Renderer {

        private Cube mCube = new Cube();
        private float mCubeRotation;

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f); 
                
            gl.glClearDepthf(1.0f);
            gl.glEnable(GL10.GL_DEPTH_TEST);
            gl.glDepthFunc(GL10.GL_LEQUAL);

            gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
                      GL10.GL_NICEST);
                
        }

        public synchronized void onDrawFrame(GL10 gl) {
           //
        	
        	 if(My_turn){
        		 gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);        
                 gl.glLoadIdentity();
                 
                 gl.glTranslatef(0.0f, 0.0f, -10.0f);
                 //gl.glRotatef(mCubeRotation, 1.0f, 1.0f, 1.0f);
                 
               
                 // x axis pitch 
                 gl.glRotatef(cube_rotattion[1], 1.0f, 0.0f, 0.0f);
                 
                 // y axis roll
                 gl.glRotatef(cube_rotattion[2], 0.0f, 1.0f, 0.0f);
                 
                 // z axis azimull
                 gl.glRotatef(cube_rotattion[0], 0.0f, 0.0f, 1.0f);

                 mCube.draw(gl);
                 gl.glLoadIdentity();  
                 My_turn=false;
        	 }

             
            //mCubeRotation -= 0.15f; 
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            gl.glViewport(0, 0, width, height);
            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glLoadIdentity();
            GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 0.1f, 100.0f);
            gl.glViewport(0, 0, width, height);

            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();
        }
    }


    class Cube {
        
        private FloatBuffer mVertexBuffer;
        private FloatBuffer mColorBuffer;
        private ByteBuffer  mIndexBuffer;
            
        private float vertices[] = {
                                    -1.0f, -1.0f, -1.0f,
                                    1.0f, -1.0f, -1.0f,
                                    1.0f,  1.0f, -1.0f,
                                    -1.0f, 1.0f, -1.0f,
                                    -1.0f, -1.0f,  1.0f,
                                    1.0f, -1.0f,  1.0f,
                                    1.0f,  1.0f,  1.0f,
                                    -1.0f,  1.0f,  1.0f
                                    };
        private float colors[] = {
                                   0.0f,  1.0f,  0.0f,  1.0f,
                                   0.0f,  1.0f,  0.0f,  1.0f,
                                   1.0f,  0.5f,  0.0f,  1.0f,
                                   1.0f,  0.5f,  0.0f,  1.0f,
                                   1.0f,  0.0f,  0.0f,  1.0f,
                                   1.0f,  0.0f,  0.0f,  1.0f,
                                   0.0f,  0.0f,  1.0f,  1.0f,
                                   1.0f,  0.0f,  1.0f,  1.0f
                                };
       
        private byte indices[] = {
                                  0, 4, 5, 0, 5, 1,
                                  1, 5, 6, 1, 6, 2,
                                  2, 6, 7, 2, 7, 3,
                                  3, 7, 4, 3, 4, 0,
                                  4, 7, 6, 4, 6, 5,
                                  3, 0, 1, 3, 1, 2
                                  };
                    
        public Cube() {
                ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
                byteBuf.order(ByteOrder.nativeOrder());
                mVertexBuffer = byteBuf.asFloatBuffer();
                mVertexBuffer.put(vertices);
                mVertexBuffer.position(0);
                    
                byteBuf = ByteBuffer.allocateDirect(colors.length * 4);
                byteBuf.order(ByteOrder.nativeOrder());
                mColorBuffer = byteBuf.asFloatBuffer();
                mColorBuffer.put(colors);
                mColorBuffer.position(0);
                    
                mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
                mIndexBuffer.put(indices);
                mIndexBuffer.position(0);
        }

        public void draw(GL10 gl) {             
                gl.glFrontFace(GL10.GL_CW);
                
                gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
                gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
                
                gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
                gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
                 
                gl.glDrawElements(GL10.GL_TRIANGLES, 36, GL10.GL_UNSIGNED_BYTE, 
                                mIndexBuffer);
                    
                gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
                gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        }
    }

    
    
    
    
    //*************************** Sensor Part ************************************
	
   /* @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
 
        
    }*/
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
   		MenuInflater inflater = getMenuInflater();
           inflater.inflate(R.menu.option_d, menu);
           return true;
   	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.d1:
        	radioSelection=0;
        	break;
        case R.id.d2:
        	radioSelection=1;
        	break;
        case R.id.d3:
        	radioSelection=2;
        	break;
        }
        return false;  
    } 
    
    
    @Override
    public void onStop() {
    	Stop_flag=true;
    	mMsgclr.ShouldWork=true;
		super.onStop();
    	// unregister sensor listeners to prevent the activity from draining the device's battery.
    	mSensorManager.unregisterListener(this);
    }
	
    @Override
    public void onDestroy() {
    	Stop_flag=true;
    	mMsgclr.ShouldWork=true;
    	fuseTimer.cancel();
    	
    	super.onDestroy();
    	// unregister sensor listeners to prevent the activity from draining the device's battery.
    	mSensorManager.unregisterListener(this);
    }

    
    @Override
    protected void onPause() {
    	Stop_flag=true;
    	
        super.onPause();
        // unregister sensor listeners to prevent the activity from draining the device's battery.
        mSensorManager.unregisterListener(this);
    }
    
    @Override
    public void onResume() {
    	Stop_flag=false;
    	mMsgclr.ShouldWork=false;
    	super.onResume();
    	
    	// restore the sensor listeners when user resumes the application.
    	initListeners();
    }
    
    // This function registers sensor listeners for the accelerometer, magnetometer and gyroscope.
    public void initListeners(){
        mSensorManager.registerListener(this,
            mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_FASTEST);
     
        mSensorManager.registerListener(this,
            mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
            SensorManager.SENSOR_DELAY_FASTEST);
     
        mSensorManager.registerListener(this,
            mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
            SensorManager.SENSOR_DELAY_FASTEST);
    }

	public void onAccuracyChanged(Sensor sensor, int accuracy) {		
	}

	public void onSensorChanged(SensorEvent event) {
		/*switch(event.sensor.getType()) {
	    case Sensor.TYPE_ACCELEROMETER:
	        // copy new accelerometer data into accel array and calculate orientation
	        System.arraycopy(event.values, 0, accel, 0, 3);
	        calculateAccMagOrientation();
	        break;
	 
	    case Sensor.TYPE_GYROSCOPE:
	        // process gyro data
	        gyroFunction(event);
	        break;
	 
	    case Sensor.TYPE_MAGNETIC_FIELD:
	    	// copy new magnetometer data into magnet array
	        System.arraycopy(event.values, 0, magnet, 0, 3);
	        
	        break;
	    }*/
	}
	
	
	public void Calculate_sensors(){
		System.arraycopy(mMsgclr.public_data.Acc(), 0, accel, 0, 3);
		calculateAccMagOrientation();
		
		gyroFunction(mMsgclr.public_data.Gyr());
		
		System.arraycopy(mMsgclr.public_data.Mag(), 0, magnet, 0, 3);
	}
	
	// calculates orientation angles from accelerometer and magnetometer output
	public void calculateAccMagOrientation() {
	    if(SensorManager.getRotationMatrix(rotationMatrix, null, accel, magnet)) {
	        SensorManager.getOrientation(rotationMatrix, accMagOrientation);
	    }
	}
	
	// This function is borrowed from the Android reference
	// at http://developer.android.com/reference/android/hardware/SensorEvent.html#values
	// It calculates a rotation vector from the gyroscope angular speed values.
    private void getRotationVectorFromGyro(float[] gyroValues,
            float[] deltaRotationVector,
            float timeFactor)
	{
		float[] normValues = new float[3];
		
		// Calculate the angular speed of the sample
		float omegaMagnitude =
		(float)Math.sqrt(gyroValues[0] * gyroValues[0] +
		gyroValues[1] * gyroValues[1] +
		gyroValues[2] * gyroValues[2]);
		
		// Normalize the rotation vector if it's big enough to get the axis
		if(omegaMagnitude > EPSILON) {
		normValues[0] = gyroValues[0] / omegaMagnitude;
		normValues[1] = gyroValues[1] / omegaMagnitude;
		normValues[2] = gyroValues[2] / omegaMagnitude;
		}
		
		// Integrate around this axis with the angular speed by the timestep
		// in order to get a delta rotation from this sample over the timestep
		// We will convert this axis-angle representation of the delta rotation
		// into a quaternion before turning it into the rotation matrix.
		float thetaOverTwo = omegaMagnitude * timeFactor;
		float sinThetaOverTwo = (float)Math.sin(thetaOverTwo);
		float cosThetaOverTwo = (float)Math.cos(thetaOverTwo);
		deltaRotationVector[0] = sinThetaOverTwo * normValues[0];
		deltaRotationVector[1] = sinThetaOverTwo * normValues[1];
		deltaRotationVector[2] = sinThetaOverTwo * normValues[2];
		deltaRotationVector[3] = cosThetaOverTwo;
	}
	
    // This function performs the integration of the gyroscope data.
    // It writes the gyroscope based orientation into gyroOrientation.
    public void gyroFunction(SensorEvent event) {
        // don't start until first accelerometer/magnetometer orientation has been acquired
        if (accMagOrientation == null)
            return;
     
        // initialisation of the gyroscope based rotation matrix
        if(initState) {
            float[] initMatrix = new float[9];
            initMatrix = getRotationMatrixFromOrientation(accMagOrientation);
            float[] test = new float[3];
            SensorManager.getOrientation(initMatrix, test);
            gyroMatrix = matrixMultiplication(gyroMatrix, initMatrix);
            initState = false;
        }

        // copy the new gyro values into the gyro array
        // convert the raw gyro data into a rotation vector
        float[] deltaVector = new float[4];
        if(timestamp != 0) {
            final float dT = (event.timestamp - timestamp) * NS2S;
        System.arraycopy(event.values, 0, gyro, 0, 3);
        
        getRotationVectorFromGyro(gyro, deltaVector, dT / 2.0f);
        }
     
        // measurement done, save current time for next interval
        timestamp = event.timestamp;
     
        // convert rotation vector into rotation matrix
        float[] deltaMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(deltaMatrix, deltaVector);
     
        // apply the new rotation interval on the gyroscope based rotation matrix
        gyroMatrix = matrixMultiplication(gyroMatrix, deltaMatrix);
     
        // get the gyroscope based orientation from the rotation matrix
        SensorManager.getOrientation(gyroMatrix, gyroOrientation);
    }
    
    
    // override
    public void gyroFunction(float [] event) {
        // don't start until first accelerometer/magnetometer orientation has been acquired
        if (accMagOrientation == null)
            return;
     
        // initialisation of the gyroscope based rotation matrix
        if(initState) {
            float[] initMatrix = new float[9];
            initMatrix = getRotationMatrixFromOrientation(accMagOrientation);
            float[] test = new float[3];
            SensorManager.getOrientation(initMatrix, test);
            gyroMatrix = matrixMultiplication(gyroMatrix, initMatrix);
            initState = false;
        }

        // copy the new gyro values into the gyro array
        // convert the raw gyro data into a rotation vector
        float[] deltaVector = new float[4];
        if(timestamp != 0) {
            final float dT = (System.nanoTime() - timestamp) * NS2S;
        System.arraycopy(event , 0, gyro, 0, 3);
        
        getRotationVectorFromGyro(gyro, deltaVector, dT / 2.0f);
        }
     
        // measurement done, save current time for next interval
        timestamp = System.nanoTime();
     
        // convert rotation vector into rotation matrix
        float[] deltaMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(deltaMatrix, deltaVector);
     
        // apply the new rotation interval on the gyroscope based rotation matrix
        gyroMatrix = matrixMultiplication(gyroMatrix, deltaMatrix);
     
        // get the gyroscope based orientation from the rotation matrix
        SensorManager.getOrientation(gyroMatrix, gyroOrientation);
    }
    
    
    private float[] getRotationMatrixFromOrientation(float[] o) {
        float[] xM = new float[9];
        float[] yM = new float[9];
        float[] zM = new float[9];
     
        float sinX = (float)Math.sin(o[1]);
        float cosX = (float)Math.cos(o[1]);
        float sinY = (float)Math.sin(o[2]);
        float cosY = (float)Math.cos(o[2]);
        float sinZ = (float)Math.sin(o[0]);
        float cosZ = (float)Math.cos(o[0]);
     
        // rotation about x-axis (pitch)
        xM[0] = 1.0f; xM[1] = 0.0f; xM[2] = 0.0f;
        xM[3] = 0.0f; xM[4] = cosX; xM[5] = sinX;
        xM[6] = 0.0f; xM[7] = -sinX; xM[8] = cosX;
     
        // rotation about y-axis (roll)
        yM[0] = cosY; yM[1] = 0.0f; yM[2] = sinY;
        yM[3] = 0.0f; yM[4] = 1.0f; yM[5] = 0.0f;
        yM[6] = -sinY; yM[7] = 0.0f; yM[8] = cosY;
     
        // rotation about z-axis (azimuth)
        zM[0] = cosZ; zM[1] = sinZ; zM[2] = 0.0f;
        zM[3] = -sinZ; zM[4] = cosZ; zM[5] = 0.0f;
        zM[6] = 0.0f; zM[7] = 0.0f; zM[8] = 1.0f;
     
        // rotation order is y, x, z (roll, pitch, azimuth)
        float[] resultMatrix = matrixMultiplication(xM, yM);
        resultMatrix = matrixMultiplication(zM, resultMatrix);
        return resultMatrix;
    }
    
    private float[] matrixMultiplication(float[] A, float[] B) {
        float[] result = new float[9];
     
        result[0] = A[0] * B[0] + A[1] * B[3] + A[2] * B[6];
        result[1] = A[0] * B[1] + A[1] * B[4] + A[2] * B[7];
        result[2] = A[0] * B[2] + A[1] * B[5] + A[2] * B[8];
     
        result[3] = A[3] * B[0] + A[4] * B[3] + A[5] * B[6];
        result[4] = A[3] * B[1] + A[4] * B[4] + A[5] * B[7];
        result[5] = A[3] * B[2] + A[4] * B[5] + A[5] * B[8];
     
        result[6] = A[6] * B[0] + A[7] * B[3] + A[8] * B[6];
        result[7] = A[6] * B[1] + A[7] * B[4] + A[8] * B[7];
        result[8] = A[6] * B[2] + A[7] * B[5] + A[8] * B[8];
     
        return result;
    }
    
    class calculateFusedOrientationTask extends TimerTask {
        public void run() {
            
        	
        	if(mMsgclr.public_data != null)
        	Calculate_sensors();
        	mMsgclr.ShouldWork=false;
        	
        	
        	float oneMinusCoeff = 1.0f - FILTER_COEFFICIENT;
            
            
            /*
             * Fix for 179�<--> -179�transition problem:
             * Check whether one of the two orientation angles (gyro or accMag) is negative while the other one is positive.
             * If so, add 360�(2 * math.PI) to the negative value, perform the sensor fusion, and remove the 360�from the result
             * if it is greater than 180� This stabilizes the output in positive-to-negative-transition cases.
             */
            
            // azimuth
            if (gyroOrientation[0] < -0.5 * Math.PI && accMagOrientation[0] > 0.0) {
            	fusedOrientation[0] = (float) (FILTER_COEFFICIENT * (gyroOrientation[0] + 2.0 * Math.PI) + oneMinusCoeff * accMagOrientation[0]);
        		fusedOrientation[0] -= (fusedOrientation[0] > Math.PI) ? 2.0 * Math.PI : 0;
            }
            else if (accMagOrientation[0] < -0.5 * Math.PI && gyroOrientation[0] > 0.0) {
            	fusedOrientation[0] = (float) (FILTER_COEFFICIENT * gyroOrientation[0] + oneMinusCoeff * (accMagOrientation[0] + 2.0 * Math.PI));
            	fusedOrientation[0] -= (fusedOrientation[0] > Math.PI)? 2.0 * Math.PI : 0;
            }
            else {
            	fusedOrientation[0] = FILTER_COEFFICIENT * gyroOrientation[0] + oneMinusCoeff * accMagOrientation[0];
            }
            
            // pitch
            if (gyroOrientation[1] < -0.5 * Math.PI && accMagOrientation[1] > 0.0) {
            	fusedOrientation[1] = (float) (FILTER_COEFFICIENT * (gyroOrientation[1] + 2.0 * Math.PI) + oneMinusCoeff * accMagOrientation[1]);
        		fusedOrientation[1] -= (fusedOrientation[1] > Math.PI) ? 2.0 * Math.PI : 0;
            }
            else if (accMagOrientation[1] < -0.5 * Math.PI && gyroOrientation[1] > 0.0) {
            	fusedOrientation[1] = (float) (FILTER_COEFFICIENT * gyroOrientation[1] + oneMinusCoeff * (accMagOrientation[1] + 2.0 * Math.PI));
            	fusedOrientation[1] -= (fusedOrientation[1] > Math.PI)? 2.0 * Math.PI : 0;
            }
            else {
            	fusedOrientation[1] = FILTER_COEFFICIENT * gyroOrientation[1] + oneMinusCoeff * accMagOrientation[1];
            }
            
            // roll
            if (gyroOrientation[2] < -0.5 * Math.PI && accMagOrientation[2] > 0.0) {
            	fusedOrientation[2] = (float) (FILTER_COEFFICIENT * (gyroOrientation[2] + 2.0 * Math.PI) + oneMinusCoeff * accMagOrientation[2]);
        		fusedOrientation[2] -= (fusedOrientation[2] > Math.PI) ? 2.0 * Math.PI : 0;
            }
            else if (accMagOrientation[2] < -0.5 * Math.PI && gyroOrientation[2] > 0.0) {
            	fusedOrientation[2] = (float) (FILTER_COEFFICIENT * gyroOrientation[2] + oneMinusCoeff * (accMagOrientation[2] + 2.0 * Math.PI));
            	fusedOrientation[2] -= (fusedOrientation[2] > Math.PI)? 2.0 * Math.PI : 0;
            }
            else {
            	fusedOrientation[2] = FILTER_COEFFICIENT * gyroOrientation[2] + oneMinusCoeff * accMagOrientation[2];
            }
     
            // overwrite gyro matrix and orientation with fused orientation
            // to comensate gyro drift
            gyroMatrix = getRotationMatrixFromOrientation(fusedOrientation);
            System.arraycopy(fusedOrientation, 0, gyroOrientation, 0, 3);
            
            
            // update sensor output in GUI
            mHandler.post(updateOreintationDisplayTask);
        }
    }
    
    
    // **************************** GUI FUNCTIONS *********************************
    
    public void onCheckedChanged(RadioGroup group, int checkedId) {
		/*switch(checkedId) {
		case R.id.radio0:
			radioSelection = 0;
			break;
		case R.id.radio1:
			radioSelection = 1;
			break;
		case R.id.radio2:
			radioSelection = 2;
			break;
		}*/
	}
    
    public void updateOreintationDisplay() {
    	
    	switch(radioSelection) {
    	case 0:
	    	cube_rotattion[0]=(float) (accMagOrientation[0]* 180*(-2)/Math.PI);
	    	cube_rotattion[1]=(float) (accMagOrientation[1]* 180*(-2)/Math.PI);
	    	cube_rotattion[2]=(float) (accMagOrientation[2]* 180*(2)/Math.PI); 
    		break;
    	case 1:
    		cube_rotattion[0]=(float) (gyroOrientation[0]* 180*(-2)/Math.PI);
	    	cube_rotattion[1]=(float) (gyroOrientation[1]* 180*(-2)/Math.PI);
	    	cube_rotattion[2]=(float) (gyroOrientation[2]* 180*(2)/Math.PI); 
    		break;
    	case 2:
    		cube_rotattion[0]=(float) (fusedOrientation[0]* 180*(-2)/Math.PI);
	    	cube_rotattion[1]=(float) (fusedOrientation[1]* 180*(-2)/Math.PI);
	    	cube_rotattion[2]=(float) (fusedOrientation[2]* 180*(2)/Math.PI); 
    		break;
    	}

    	My_turn=true;    	
    	
    	/*switch(radioSelection) {
    	case 0:
    		mAzimuthView.setText(d.format(accMagOrientation[0] * 180/Math.PI) + "");
            mPitchView.setText(d.format(accMagOrientation[1] * 180/Math.PI) + "");
            mRollView.setText(d.format(accMagOrientation[2] * 180/Math.PI) + "");
    		break;
    	case 1:
    		mAzimuthView.setText(d.format(gyroOrientation[0] * 180/Math.PI) + "");
            mPitchView.setText(d.format(gyroOrientation[1] * 180/Math.PI) + "");
            mRollView.setText(d.format(gyroOrientation[2] * 180/Math.PI) + "");
    		break;
    	case 2:
    		mAzimuthView.setText(d.format(fusedOrientation[0] * 180/Math.PI) + "");
            mPitchView.setText(d.format(fusedOrientation[1] * 180/Math.PI) + "");
            mRollView.setText(d.format(fusedOrientation[2] * 180/Math.PI) + "");
    		break;
    	}*/
    }
    
    private Runnable updateOreintationDisplayTask = new Runnable() {
		public void run() {
			updateOreintationDisplay();
		
		}
	};


    
}

