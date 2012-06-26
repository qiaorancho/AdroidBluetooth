package com.luugiathuy.apps.remotebluetooth;

import java.util.LinkedList;

    
  public  class FeatureDescriptor
    {
        double[] _featurevector;
        
        public FeatureDescriptor(LinkedList<Double[]> RawGestureData)
        {
            double[] featurevector = new double[28];

            double[] mean = new double[6];
            double[] variance = new double[6];
            double[] movementintensity = new double[6];
            double[] rms = new double[6];
            double[] ai = new double[2];
            double[] vi = new double[2];
            double[] zerocrossingrate = new double[3];
            
            for (int i = 0; i < 6; i++)
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
            for  (Double[] point : RawGestureData)
            {
                double accelpow = Math.sqrt(Math.pow(point[0], 2) + Math.pow(point[1], 2) + Math.pow(point[2], 2));
                double gyropow = Math.sqrt(Math.pow(point[3], 2) + Math.pow(point[4], 2) + Math.pow(point[5], 2));
                for (int i = 0; i < 6; i++)
                {
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
            _featurevector = featurevector;
        }

        public double[] getFeaturevector()
        {
                return _featurevector;
            
        }
    }

