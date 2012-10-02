package com.app.GestureGame.Perceptron;

import java.util.Queue;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.app.GestureGame.Profile.ProfileManager;


public class Perceptron 

    {
		private ProfileManager mPM = ProfileManager.getinstant();
        int _NClasses;
        int _NDimensions;
        double _Eta;
        public double[][] _WeightMatrix;
        private final double Nomal=0.5;
        
        public Queue<InputAndOutput> _PerceptronPoints;
        
        static final int POINTS_Q_MAX = 100;

        double _MaxValue =  -999999999;
        double _MaxIndex = -1;
        double _SecondMax= -999999999;
        double _SeconIndex=-1;
        double _RateRes;
        
        public Perceptron(int Nclasses, int NDimensions, double eta, double[][] initial_w)
        {
            _NClasses = Nclasses;
            _NDimensions = NDimensions;
            _WeightMatrix = initial_w;
            _Eta = eta;
            _PerceptronPoints=mPM.mProfile.getPerceptropoints();
        }

        public Perceptron(int Nclasses, int NDimensions, double[][] initial_w)
        {
            _NClasses = Nclasses;
            _NDimensions = NDimensions;
            _WeightMatrix = initial_w;
            _Eta = Nomal;
            _PerceptronPoints=mPM.mProfile.getPerceptropoints();
        }

        public Perceptron(int Nclasses, int NDimensions)
        {
            _NClasses = Nclasses;
            _NDimensions = NDimensions;
           /* _WeightMatrix = new double[_NClasses][_NDimensions];
            for (int i = 0; i < _NClasses; i++)
            {
                for (int j = 0; j < _NDimensions; j++)
                    _WeightMatrix[i][j] = 0;
            }
            */
            _WeightMatrix=mPM.mProfile.getWeight();
            _PerceptronPoints=mPM.mProfile.getPerceptropoints();
            _Eta =Nomal;
        }
       
        /**
         * Add time so that we can use time as one of the reference to compare with prediction.
         * qiao 25,August,2012
         * @param inputpoint
         * @param label
         * @param mTime
         */
        public void UpdatePerceptron(double[] inputpoint,int label, double[] mTime)
        {
            //double[] localinputpoint = inputpoint;
            double[] labelmatrix = new double[_NClasses];
            
            //maxtrix return values.
            double[] classweights = new double[_NClasses];
            WeightData mWeightData=new WeightData();  
            
            //check update thread
            for (int i = 0; i < _NClasses; i++)
            {
                labelmatrix[i] = 0;
            }
            labelmatrix[label] = 1;

            System.out.println("Retraining Perceptron...");
            for (int i = 0; i < _NClasses; i++)
            	System.out.println(labelmatrix[i] + " ");
            //Console.WriteLine();
            System.out.println();
            
            if(label >=0){
            	//put mTime into our InputOutput...
            	InputAndOutput localinputpoint = new InputAndOutput(inputpoint, labelmatrix,mTime);
                _PerceptronPoints.add(localinputpoint);
            }
            
            //Console.WriteLine(_PerceptronPoints.Count);
            System.out.println(_PerceptronPoints.size());
            
            while (_PerceptronPoints.size() > POINTS_Q_MAX)
            {
            	//dequeue?
                _PerceptronPoints.remove();
            }
            
            int er = 1;
            int passes = 0;
            while (er == 1)
            {
                int errors = 0;
                System.out.println(".........Updating" + passes+"... order is........");
                for (InputAndOutput input : _PerceptronPoints)
                {
                	System.out.println("......looping looping .......");
                    //double[] algorithm_labels = winner_takes_all(input.getinputpoint());//calculate error matrix
                	
                	//get evaluation weights of the input 
                	classweights=levelweights( input.getinputpoint() );
                	
                	double[] algorithm_labels=winner_takes_all(weight_win(classweights));
                	
                	// here we can do improvement later...
                	double sum=0;
                	
                	for (int i = 0; i < _NClasses; i++){
                		sum+=classweights[i];
                		System.out.println("target..;"+input.getalgorithmlabels()[i]);
                	}
                	
                	if(sum ==0){
                		update_weight_matrix(input.getinputpoint(), check_class(input.getindex(), -1));
                		System.out.println("..... all 000 .......");
                		break;
                	}
                	
                	mWeightData=setWeightData( classweights,input.getindex());
                	//updatepara(mWeightData);
                	
                	errors+=update_Matrixs(    input.getinputpoint(), mWeightData ,updatepara(mWeightData));
                	System.out.println("HERE WE SEE THE RESULT OF OUR UPDATE.....");
                	classweights=levelweights( input.getinputpoint() );
                }
                if (errors == 0)
                	er = 0;
                passes++;
                if (passes > 100)
                {
                	er = 0;
                	passes = 0;
                	System.out.println(" ........Update 100 times  need jump out........");
                	//  upt=new UpdateThread();
                	//upt.start();
                }
            }
                System.out.println("Done.");
                //Console.WriteLine("Done.");
        }
        
        
        public int Predict(double[] inputpoint,double[] _mSize)
        {
        	double[] classweights=levelweights( inputpoint);
        	double[] algorithm_labelling=winner_takes_all(weight_win(classweights));
            //double[] algorithm_labelling = winner_takes_all(inputpoint);
            
            for (int i = 0; i < _NClasses; i++)
            	System.out.println(algorithm_labelling[i] + " ");
            System.out.println();
            int label = -1;
            for (int i = 0; i < _NClasses; i++)
                if (algorithm_labelling[i] == 1)
                    label = i;
            
            
    		//here we verify our prediction
            InputAndOutput newinput =new InputAndOutput(inputpoint,algorithm_labelling,_mSize);
            if(!Verify(newinput._Refer,label))
            	label =10+label;
            return label;
        }
        
        //obsoleted
        public int Predict(double[] inputpoint )
        {
        	double[] classweights=levelweights( inputpoint);
        	double[] algorithm_labelling=winner_takes_all(weight_win(classweights));
            //double[] algorithm_labelling = winner_takes_all(inputpoint);
            
            for (int i = 0; i < _NClasses; i++)
            	System.out.println(algorithm_labelling[i] + " ");
            System.out.println();
            int label = -1;
            for (int i = 0; i < _NClasses; i++)
                if (algorithm_labelling[i] == 1)
                    label = i;
            return label;
        }
        
        /**
         * verify-- If incorrect rerun false;....
         * @param inputpoint
         * @param label
         * @return
         */
        public boolean Verify(double[] inputpoint,int label){
        	int erro =0;
        	//check every reference..
        	for(InputAndOutput input : _PerceptronPoints){
            	if (label ==input.getindex()){
            		//  newpoint.refer -- input_point
            		if (input.Compare(inputpoint)){
            			erro=0;
            			break;
            		}
            		else
            			erro=1;
            	}
            }
        	//if no one is compared..
            if (erro == 1)
            	return false;
            //else ...
        	return true;
        }
        
        
        private double[] winner_takes_all(double[] currentinputpoint)
        {
        	int flag;
        	flag= weight_win( levelweights(currentinputpoint) );
        	return winner_takes_all(flag);
        }
        
        
        private double[] winner_takes_all(int flag)
        {
            double[] MyClassLabelling = new double[_NClasses];
            for (int i = 0; i < _NClasses; i++)
            {
                if (i == flag)
                    MyClassLabelling[i] = 1;
                else
                    MyClassLabelling[i] = 0;
            }
            System.out.println("winner label is "+flag);
            return MyClassLabelling;
        }
        
        
        
        private double [] levelweights(double[] currentinputpoint){
        	double[] classweights = new double[_NClasses];
            

            System.out.println("Classweight matrix begin. \n");
            
            //calculate weightmatrix
            for (int i = 0; i < _NClasses; i++)
            {	
                for (int j = 0; j < _NDimensions; j++)
                {
                    //Console.Write(_WeightMatrix[i,j]+" ");
                    classweights[i] += _WeightMatrix[i][j] * currentinputpoint[j];
                }
                System.out.println(classweights[i]);
            }

            System.out.println("Classweight matrix end. ");
            return classweights;
        }
        
        
        private int weight_win(double [] classweights){
        	_MaxValue = -Double.MAX_VALUE;
            _MaxIndex = -1;
            _SecondMax= -Double.MAX_VALUE;
            _SeconIndex=-1;
            
        	 for (int i = 0; i < _NClasses; i++)
             {
        		 if (classweights[i] > _MaxValue && (int)classweights[i]!=0)
                 {
                     _MaxValue = classweights[i];
                      _MaxIndex = i;
                 }
             }
        	 
        	 //if no traning data, so default return 0;
        	 if (_MaxIndex  == -1)
        		 return 0;
        	 else
             return (int) _MaxIndex;
        }
        
        
        /**
         * Set WeightData Max and Target so that we can use them later to get ration.
         * 
         */
        
        private WeightData setWeightData(double[] classweights,int target){
        	WeightData data=new WeightData();
        	_MaxValue = -Double.MAX_VALUE;
            _MaxIndex = -1;
            double _Target;
            double _TargetValue;
            
            _Target=target;
            _TargetValue = classweights[target];
            
        	for (int i = 0; i < _NClasses; i++)
             {
        		 if (classweights[i] > _MaxValue && i!=target && (int)classweights[i]!=0)
                 {
                     _MaxValue = classweights[i];
                      _MaxIndex = i;
                 }
             }
        	
        	data.Max=_MaxIndex;
        	data.MaxValue=_MaxValue;
        	data.Target=_Target;
        	data.TargetValue=_TargetValue;
        	return data;
        }
        
        /**
         * Update parameter of the target 
         */
        private double updatepara(WeightData data){

        	double max;
//        	double sec;
        	double ration;
        	
        	
        	if (data.Max<0)
        		return -1;
        	// first time to update target
        	else if (data.TargetValue == 0){
        		return -1;
        	}
        	// not first time but sitll some thing...
        	else if(data.Max== -1 || (data.TargetValue>0 && data.MaxValue<0)  ){
        		return 21;
        	}
        	else if (data.TargetValue <0 && data.MaxValue <=0 ){
        		return -1;
        	}
        	// here get the ration.
        	else {

        		//Target>0 and Max>0;
        		if (data.TargetValue>=data.MaxValue)
        		{
        			ration=data.TargetValue/data.MaxValue;
        			if (ration < 10){
        				return 10- ration;
        			}
        			else return 21;
        		}
        		
        		//Target<0 , max>0; || target >0 and max>0
        		else{
        			
        			if( data.TargetValue>0){
        				ration=data.MaxValue/data.TargetValue;
            			if (ration < 10){
            				return 10+ ration;
            			}
            			else return 20;
        			}
        			//Target<0 , max>0; 
        			else if(data.TargetValue<0&&data.MaxValue>0){
        				ration=data.MaxValue/(-data.TargetValue);
        				
        				//make sure it's fast.
        				if (ration <1)
        					ration=1/ration;
        				
        				//ration =ration +10
        				ration +=10;
        				if (ration>20)
        					return 20;
        				else  
        					return ration;
        			}
        			else
        			{
        					System.out.println("Here is someting Wrong \n" +
        							"update parameters and ratioin is wrong...");
        					return -1;
        			}
        		}
        	}
        }
        
        private int update_Matrixs(double[] intputpoints,WeightData data,double ration){
        	int e=0;
        	double [] check_classes;
        	if (ration<0){
        		check_classes= check_class(data.Target,-1);
        		update_weight_matrix(intputpoints,check_classes);
        		e=1;
        	}
        	else if (ration >20){
        		e=0;
        	}
        	else
        	{
        		check_classes= check_class(data.Target,data.Max,ration);
        		update_weight_matrix(intputpoints,check_classes);
        		e=1;
        	}
        	System.out.println("........Updated the matrixs ..This is ration  "+ ration+"........");
			return e;
        }
        
        private boolean isdisparate(double [] classweights){
        	int maxindex=weight_win(classweights);
        	//get second max and see if max and second max similar.
        	cal_secmax(classweights,maxindex);
              
              if(_SeconIndex <0 ||_SecondMax<=0 )
            	  _RateRes =16;
              else
            	  _RateRes=_MaxValue/_SecondMax;
             System.out.println("........This is rate "+ _RateRes+"........");
             if(_RateRes>6||_RateRes<0)
        	return true;
             else
            	 return false;
        }
        
        private boolean is_disparate_toreturn(double [] classweights){
        	int maxindex=weight_win(classweights);
        	//get second max and see if max and second max similar.
        	cal_secmax(classweights,maxindex);
              
              if(_SeconIndex <0 ||_SecondMax<=0 )
            	  return true;
              else
            	  _RateRes=_MaxValue/_SecondMax;
             System.out.println("........This is rate "+ _RateRes+"........");
             if(_RateRes>2||_RateRes<0)
            	 return true;
             else if(classweights[_NClasses-1] != 0)
            	 return false;
             else 
            	 return true;
        }
        
        public void cal_secmax(double [] classweights,int maxindex){
        	for (int i = 0; i < _NClasses; i++)
            {
            	if (i!=maxindex&&classweights[i] > _SecondMax)
                {
                    _SecondMax = classweights[i];
                    _SeconIndex=i;
                }
            }
        }

        private int find_errors(double[] ground_truth_class_labels, double[] algorithm_class_labels)
        {
            int error = 0; ;
            for (int i = 0; i < _NClasses; i++)
            {
                //Console.Write(ground_truth_class_labels[i] - algorithm_class_labels[i]+ " ");
                if (ground_truth_class_labels[i] - algorithm_class_labels[i] == 1)
                    error++;
            }
            //Console.WriteLine();
            return error;
        }
        
        //double parameter?? 
        private double[] check_class(double[] ground_truth_class_labels, double[] algorithm_class_labels)
        {
            double[] class_difference = new double[_NClasses];
            for (int i = 0; i < _NClasses; i++)
                class_difference[i] = ground_truth_class_labels[i] - algorithm_class_labels[i];
            return class_difference;
        }
        
        private double[] check_class(double trueindex, double predictindex)
        {
            double[] class_difference = new double[_NClasses];
            for (int i = 0; i < _NClasses; i++){
            	
            	class_difference[i] =0;
            	if (i == trueindex)
            		// initially 10
            		class_difference[i]=10;
            	else if(i == predictindex)
            		class_difference[i]=-10;
            }
            return class_difference;
        }
        
        //give ration so it's adaptive to update. 
        private double[] check_class(double trueindex, double predictindex, double ration)
        {
            double[] class_difference = new double[_NClasses];
            for (int i = 0; i < _NClasses; i++){
            	
            	class_difference[i] =0;
            	if (i == trueindex)
            		class_difference[i]=ration;
            	else if(i == predictindex)
            		class_difference[i]=-ration;
            }
            return class_difference;
        }

        private void update_weight_matrix(double[] inputpoint, double[] classdifference)
        {
            for (int i = 0; i < _NClasses; i++)
            {
                for (int j = 0; j < _NDimensions; j++)
                {
                    _WeightMatrix[i ][j] = _WeightMatrix[i][j] + (_Eta * classdifference[i] * inputpoint[j]);
                }
            }
        }
 
        
   }
