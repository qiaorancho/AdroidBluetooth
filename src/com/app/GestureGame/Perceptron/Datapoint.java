package com.app.GestureGame.Perceptron;

public class Datapoint
    {
        double _accx, _accy, _accz;
        double _gyrx, _gyry, _gyrz;
        double _magx, _magy, _magz;
        double Gyro_Gain_X = 1.0 / 14.375;
        public Double[] myDebug=new Double[6];

        public Datapoint(String message)
        {

            String[] split_message = message.split(",");
           myDebug[0]= _accx = Double.valueOf(split_message[1])/25.6;
           myDebug[1]= _accy = Double.valueOf(split_message[2])/25.6;
           myDebug[2]= _accz = Double.valueOf(split_message[3])/25.6+1;
           myDebug[3]= _gyrx = Gyro_Scaled(Double.valueOf(split_message[4]));
           myDebug[4]= _gyry = Gyro_Scaled(Double.valueOf(split_message[5]));
           myDebug[5]= _gyrz = Gyro_Scaled(Double.valueOf(split_message[6]));
            _magx = Double.valueOf(split_message[7]);
            _magy = Double.valueOf(split_message[8]);
            _magz = Double.valueOf(split_message[9]);
        }

        double Gyro_Scaled(double rawdata)
        {
            return rawdata * ToRad(Gyro_Gain_X);
        }

        double ToRad(double x)
        {
            return x * Math.PI / 180.0;
        }
        double ToDeg(double x)
        {
            return x * 180.0 / Math.PI;
        }

        public float [] Acc()
        {		float [] acc =new float[3];
        		acc[0]=(float)_accx;
        		acc[1]=(float)_accy;
        		acc[2]=(float)_accz;
                return acc;
        }
        
        public double AccX()
        {
                return _accx;
        }
        public double AccY()
        {
                return _accy;
        }
        public double AccZ()
        {
                return _accz;
        }
        
        public float [] Gyr()
        {		float [] gyr =new float[3];
        		gyr[0]=(float)_gyrx;
        		gyr[1]=(float)_gyry;
        		gyr[2]=(float)_gyrz;
                return gyr;
        }
        
        
        public double GyrX()
        {
                return _gyrx;
        }
        public double GyrY()
        {
                return _gyry;
        }
        public double GyrZ()
        {
                return _gyrz;
        }

        public float [] Mag()
        {		float [] mag =new float[3];
        		mag[0]=(float)_magx;
        		mag[1]=(float)_magy;
        		mag[2]=(float)_magz;
                return mag;
        }
        
        public double MagX()
        {
                return _magx;
        }
        public double MagY()
        {
                return _magy;
        }
        public double MagZ()
        {
                return _magz;
        }
    }