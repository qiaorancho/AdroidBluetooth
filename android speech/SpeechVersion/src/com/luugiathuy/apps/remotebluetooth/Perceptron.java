package com.luugiathuy.apps.remotebluetooth;

import java.util.LinkedList;
import java.util.Queue;

public class Perceptron
    {
		private ProfileManager mPM = ProfileManager.getinstant();
        int _NClasses;
        int _NDimensions;
        double _Eta;
        double[][] _WeightMatrix;
        private final double Nomal=0.5;

        Queue<InputAndOutput> _PerceptronPoints = new LinkedList<InputAndOutput>();
        static final int POINTS_Q_MAX = 100;

        double _MaxValue =  -999999999;
        double _MaxIndex = -1;
        double _SecondMax= -999999999;
        double _SeconIndex=-1;
        double _RateRes;
        
        UpdateThread upt=null;
        

        public Perceptron(int Nclasses, int NDimensions, double eta, double[][] initial_w)
        {
            _NClasses = Nclasses;
            _NDimensions = NDimensions;
            _WeightMatrix = initial_w;
            _Eta = eta;
        }

        public Perceptron(int Nclasses, int NDimensions, double[][] initial_w)
        {
            _NClasses = Nclasses;
            _NDimensions = NDimensions;
            _WeightMatrix = initial_w;
            _Eta = Nomal;
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
            _Eta =Nomal;
        }
       
        public void UpdatePerceptron(double[] inputpoint,int label)
        {
            //double[] localinputpoint = inputpoint;
            double[] labelmatrix = new double[_NClasses];
            
            //maxtrix return values.
            double[] classweights = new double[_NClasses];
            
            //check update thread
            if(upt != null){
            	upt.stop();
            	upt=null;
            }
            
            
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
            	 InputAndOutput localinputpoint = new InputAndOutput(inputpoint, labelmatrix);
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
                	double sum=0;
                	for (int i = 0; i < _NClasses; i++){
                		sum+=classweights[i];
                		System.out.println(";"+input.getalgorithmlabels()[i]);
                	}
                	if(sum ==0){
                		fast_update_weight_matrix(input.getinputpoint(), check_class(input.getindex(), -1));
                		System.out.println("..... all 000 .......");
                		break;
                	}
                	
                	fast_update_weight_matrix(input.getinputpoint(), check_class(input.getalgorithmlabels(), algorithm_labels));
                	int err=find_errors(input.getalgorithmlabels(), algorithm_labels);
                	System.out.println("..... err is ......."+ err);
                	
                	// test the degree of the distinction.  		
                	/*
                	 * if(!isdisparate(classweights)  && classweights[(int) _MaxIndex]>=0 && err==0 ){
                		System.out.println("........This is second layer to update...........");
            			err=1;
            			fast_update_weight_matrix(input.getinputpoint(),check_class(_MaxIndex,_SeconIndex));
            			for(int count =2; !isdisparate(  levelweights( input.getinputpoint() ) ) && count<6; count ++ ){
            				_Eta = count*Nomal;
            				fast_update_weight_matrix(input.getinputpoint(),check_class(_MaxIndex,_SeconIndex));
            			}
            			_Eta = Nomal;
            		}
                	 * */
                	
                	
                	// ....ration(max/second_max) check.....
                	if(err==0){
                		System.out.println("........This is second layer testing ..........");
                		if(!isdisparate(classweights)  && classweights[(int) _MaxIndex]>=0  ){
                    		System.out.println("........This is second layer to update...........");
                			err=1;
                			//_Eta = (9-_RateRes)*Nomal;
                			update_weight_matrix(input.getinputpoint(),check_class(_MaxIndex,_SeconIndex));
                			//_Eta = Nomal;
                		}
                		System.out.println("..... before test maxindex's value we see maxindex here "+ _MaxIndex+
                				" .......");
                		/*
                		 * 
                		if(classweights[(int) _MaxIndex]<0){
                			update_weight_matrix(input.getinputpoint(), check_class(input.getindex(), -1));
                			err=1;
                		
                		}
                		*/
                	}
                	else{
                		System.out.println("........err == 1 so came here to update the matrix agian...........");
                		
                		cal_secmax(classweights,(int)_MaxValue);
                		// decrease max and increase our aim.
                		
                		if (0 == (int)input.getalgorithmlabels()[(int)_SeconIndex]&& (int)classweights [(int) _SeconIndex] >0)
                		{
                			 double[] class_difference = input.getalgorithmlabels().clone();
                	            for (int i = 0; i < _NClasses; i++){
                	            	class_difference[i]*=2;
                	            	if (i == (int)_MaxIndex)
                	            		class_difference[i]=-1;
                	            	else if(i == (int)_SeconIndex)
                	            		class_difference[i]=-1;
                	            }
                	            update_weight_matrix(input.getinputpoint(), class_difference);
                		}
                	}
                	errors +=err;
                }
                if (errors == 0)
                	er = 0;
                passes++;
                if (passes > 100)
                {
                	er = 0;
                	passes = 0;
                	System.out.println(" ........Update 100 times  need jump out........");
                	if(upt != null){
                		upt.stop();
                		upt=null;
                	}
                	//  upt=new UpdateThread();
                	//upt.start();
                }
            }
                System.out.println("Done.");
                //Console.WriteLine("Done.");
        }

        public int Predict(double[] inputpoint)
        {
        	if(upt != null){
            	upt.suspend();
            }
        	
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
            if(upt != null){
            	upt.resume();
            }
            
            if (is_disparate_toreturn(classweights))
            	return label;
            else 
            	return _NClasses-1;
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
        	 if (_MaxIndex  == -1)
        		 return 0;
        	 else
             return (int) _MaxIndex;
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
            		class_difference[i]=1;
            	else if(i == predictindex)
            		class_difference[i]=-1;
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
        
        private void fast_update_weight_matrix(double[] inputpoint, double[] classdifference)
        {
            for (int i = 0; i < _NClasses; i++)
            {
                for (int j = 0; j < _NDimensions; j++)
                {
                    _WeightMatrix[i ][j] = _WeightMatrix[i][j] + (3*_Eta * classdifference[i] * inputpoint[j]);
                }
            }
        }
        
        
        
        
        
        
        
        
        
        // IDILE UPDATE
        public int UpdatePerceptron( )
        {
            //double[] localinputpoint = inputpoint;
            double[] labelmatrix = new double[_NClasses];
            
            //maxtrix return values.
            double[] classweights = new double[_NClasses];
            
            
            System.out.println("Retraining Perceptron...");
            for (int i = 0; i < _NClasses; i++)
            	System.out.println(labelmatrix[i] + " ");
            //Console.WriteLine();
            System.out.println();
            
           
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
                	
                	fast_update_weight_matrix(input.getinputpoint(), check_class(input.getalgorithmlabels(), algorithm_labels));
                	int err=find_errors(input.getalgorithmlabels(), algorithm_labels);
                	
                	// test the degree of the distinction.  		
                	/*
                	 * if(!isdisparate(classweights)  && classweights[(int) _MaxIndex]>=0 && err==0 ){
                		System.out.println("........This is second layer to update...........");
            			err=1;
            			fast_update_weight_matrix(input.getinputpoint(),check_class(_MaxIndex,_SeconIndex));
            			for(int count =2; !isdisparate(  levelweights( input.getinputpoint() ) ) && count<6; count ++ ){
            				_Eta = count*Nomal;
            				fast_update_weight_matrix(input.getinputpoint(),check_class(_MaxIndex,_SeconIndex));
            			}
            			_Eta = Nomal;
            		}
                	 * */
                	if(!isdisparate(classweights)  && classweights[(int) _MaxIndex]>=0 && err==0 ){
                		System.out.println("........This is second layer to update...........");
            			err=1;
            			//_Eta = (11-_RateRes)*Nomal;
            			fast_update_weight_matrix(input.getinputpoint(),check_class(_MaxIndex,_SeconIndex));
            			//_Eta = Nomal;
            		}
                    errors +=err;
                }
                if (errors == 0)
                	er = 0; 
                passes++;
                if (passes > 50)
                {
                	er = 0;
                	passes = 0;
                	System.out.println(" ........Update 100 times  need jump out........");
                	return -1;
                }
            }
            System.out.println("Done.");
            //Console.WriteLine("Done.");
            return 1;
        }
        
        private class UpdateThread extends Thread{
        	public UpdateThread(){
        		
        	}
        	public void run() {
        		while(UpdatePerceptron( ) <0){
        		}
        	}
        } 
   }
