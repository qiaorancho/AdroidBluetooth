package com.luugiathuy.apps.remotebluetooth;

import java.util.LinkedList;

    
  public  class FeatureDescriptor
    {
        double[] _featurevector;
        double[] size=new double [2];
        public FeatureDescriptor(LinkedList<Double[]> RawGestureData_raw)
        {
            double[] featurevector = new double[34];

            double[] mean = new double[6];
            double[] variance = new double[6];
            double[] movementintensity = new double[6];
            double[] rms = new double[6];
            double[] ai = new double[2];
            double[] vi = new double[2];
            double[] zerocrossingrate = new double[3];
            //potential new feature.
            double [] gyro_peak=new double [3];
            double [] acc_peak=new double [3];
            double [] peak=new double [7];
            LinkedList<Double[]> RawGestureData=(LinkedList<Double[]>) RawGestureData_raw.clone();
            
            // for matrix N_axis*N_...
            int N_ = RawGestureData.size();
            int N_axis = 6;
            double [][] sum = new double[N_axis][N_];
            double [] accsum = new double [N_];
            
            for(int i = 0; i < 6; i++)
            {
                mean[i] = 0;
                variance[i] = 0;
                rms[i] = 0;
                movementintensity[i] = 0;
                if (i < 2)
                {
                    ai[i] = 0;
                    vi[i] = 0;
                }
                if (i < 3)
                    zerocrossingrate[i] = 0;
            }
            
            /**
             *  Change gyroscope data from raw to summation.
             *  Agu 24, 2012 qiao
             */
	          //N is length of gesture
	          //i is the current gyroscope axis
          
            //clear to zero
            for (int i=0;i<N_axis;i++)
                sum[i][0]=0;
            //loop to get my sum gyroscope data.
            
            for (int t=1;t<N_;t++){
            	for (int j=0 ;j<N_axis;j++){
            		sum[j][t]=sum[j][t-1]+RawGestureData.get(t)[j]*10;
            		if (j>=3)
            			//gyroscope we can change the cave to summation.
            			RawGestureData.get(t)[j]=sum[j][t];
            	}
            	accsum[t]=Math.pow(RawGestureData.get(t)[0], 2)+Math.pow(RawGestureData.get(t)[1], 2)+Math.pow(RawGestureData.get(t)[2], 2)-100;
            }
            
            //	get  first peak value. it's the critical point to get the direction also.
            // 	0-2 for acc 3-6 for gyro.
            peak=cal_peak(N_,N_axis,sum,accsum);

            for  (Double[] point : RawGestureData)
            {
                double accelpow = Math.sqrt(Math.pow(point[0], 2) + Math.pow(point[1], 2) + Math.pow(point[2], 2));
                double gyropow = Math.sqrt(Math.pow(point[3], 2) + Math.pow(point[4], 2) + Math.pow(point[5], 2));
                for (int i = 0; i < 6; i++)
                {
                	//here I change mean method
                    mean[i] += point[i];
                    rms[i] += Math.pow(point[i], 2);
                }
                ai[0] += accelpow;
                ai[1] += gyropow;
            }
            
            //calculate mean
            for (int i = 0; i < 6; i++)
            {
                mean[i] /= RawGestureData.size();
                rms[i] /= RawGestureData.size();
                rms[i] = Math.sqrt(rms[i]);
            }
            ai[0] /= RawGestureData.size();
            ai[1] /= RawGestureData.size();
            
            for (Double[] point : RawGestureData)
            {
                double accelpow = Math.sqrt(Math.pow(point[0], 2) + Math.pow(point[1], 2) + Math.pow(point[2], 2));
                double gyropow = Math.sqrt(Math.pow(point[3], 2) + Math.pow(point[4], 2) + Math.pow(point[5], 2));
                for (int i = 0; i < 6; i++)
                    variance[i] += Math.pow(point[i]-mean[i],2);
                vi[0] += Math.pow(accelpow - ai[0], 2);
                vi[1] += Math.pow(gyropow - ai[1], 2);
            }
            
            for (int i = 0; i < 6; i++)
                variance[i] /= RawGestureData.size();
            vi[0] /= RawGestureData.size();
            vi[1] /= RawGestureData.size();
            
            for(int i=0;i<6;i++)
            {
                featurevector[i] = mean[i];
                featurevector[i + 6] = variance[i];
                featurevector[i + 12] = movementintensity[i];
                featurevector[i + 18] = rms[i];
            }
        
            for(int i=0;i<2;i++)
            {
                featurevector[24 + i] = ai[i];
                featurevector[26 + i] = vi[i];
            }
            //new features
            for(int i=0;i<3;i++)
            {
            	//get peak* abs(peak)
            	 featurevector[28 + i]=peak[i]* Math.abs(peak[i]);
            	 	//System.out.println("acce : "+featurevector[28 + i]);
            	//featurevector[28 + i+3]=gyro_peak[i];
            	 featurevector[28 + i+3]=peak[i+3]* Math.abs(peak[i+3]);
            	 	//System.out.println("gyro : "+featurevector[31 + i]);
            }
            _featurevector = featurevector;
            size[0]=RawGestureData_raw.size();
            size[1]=peak[6];
        }
        
        public double[] getFeaturevector()
        {
                return _featurevector;
            
        }
        
        public double [] getTime()
        {
                return size;
        }
        
        public double [] cal_peak(int N,int axis, double [][] sum, double [] accsum){
        	
        	int [] peak_flag=new int [6];
        	double[] peak= new double[7];
        	for (int t=0;t<N;t++){
            	for (int j=3;j<axis;j++){
            		if (peak[j] > Math.abs(sum[j][t])&& peak_flag[j]==0 ){
            			peak[j]=Math.abs(sum[j][t]);
            			peak_flag[j]=t;
            		}
            		else 
            			peak[j]=Math.abs(sum[j][t]);
            	}
            	//flags are zero at the same time
            	if ( peak_flag[3]>0 &&peak_flag[4]>0&&peak_flag[5]>0 ){
            		break;
            	}
            }
        	
        	//compare the flag points to get the critical point. max peak value is the critical point.
        	int max=3;
        	// get highest value index;
        	for (int k=4;k<6;k++){
        		if (peak[max]<peak[k]){
        			max=k;
        		}
        	}
        	int flag =peak_flag[max];
        	System.out.println("flag is :  "+flag);
        	
        	//get flag time and 0 time difference of acce.
        	for (int k=0;k<axis;k++){
        		if (k<3){
        			//get flag time acce sum value.
        			peak[k]= sum[k][flag];
        			//get flag time acce value
        			if (flag >0)
            		peak[k]-= sum[k][flag-1];
            		//get flag time and 1 time difference.
        			//can't use 0 time because 0 time is 000
            		peak[k]-=sum[k][1];
            		System.out.println("acce differ :  "+peak[k]);
        		}
        		else
        		peak[k]=sum[k][flag];
        	}
        	
        	peak[6]=accsum[flag];
        	return peak;
        }
    }

