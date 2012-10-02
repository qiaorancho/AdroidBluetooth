package com.app.GestureGame.Filter;



/*
 * This function is just a lowpass_filter.
 * 
 * */


// class that implements a low pass filter for accel and gyroscope data
    // sampling frequency is assumed to be 50 Hz
    // half db can be 2 Hz or 1 Hz
    // 2 Hz is preferred
    // y[n]=b(1)x[n]+b(2)x[nâˆ?]âˆ’a(2)y[nâˆ?]

 public  class LowpassFilter
    {
        //state variables
        double _x_n_minus_1 = 0;
        double _y_n_minus_1 = 0;
        double _x_n = 0;
        double _y_n = 0;
        
        private static double [][] coef;

        //filter coefficients (2 Hz half db)
        //double a1 = 1;
        //double a2 = -0.881618592363189;
        //double b1 = 0.059190703818405;
        //double b2 = 0.059190703818405;

        //filter coefficients (1 Hz half db)
        //double a1 = 1;
        //double a2 = -0.939062505817492;
        //double b1 = 0.030468747091254;
        //double b2 = 0.030468747091254;

        //filter coefficients (0.5 Hz half db)
        //double a1 = 1;
        //double a2 = -0.969067417193793;
        //double b1 = 0.015466291403103;
        //double b2 = 0.015466291403103;

        //filter coefficients (0.4 Hz half db)
        double a1 = 1;
        double a2 = -0.975177876180649;
        double b1 = 0.012411061909675;
        double b2 = 0.012411061909675;

        //filter coefficients (0.33 Hz half db)
        //double a1 = 1;
        //double a2 = -0.979272350725784;
        //double b1 = 0.010363824637108;
        //double b2 = 0.010363824637108;

        //initial constructor
        public LowpassFilter()
        {
            _x_n = _x_n_minus_1 = _y_n = _y_n_minus_1 = 0;
            coef =new double[5][4];
            coef[0][0] = 1;
            coef[0][1] = -0.979272350725784;
            coef[0][2] =  0.010363824637108;
            coef[0][3] =  0.010363824637108;
            
            coef[1][0] = 1;
            coef[1][1] = -0.975177876180649;
            coef[1][2] = 0.012411061909675;
            coef[1][3] = 0.012411061909675;
            
            coef[2][0] = 1;
            coef[2][1] = -0.969067417193793;
            coef[2][2] = 0.015466291403103;
            coef[2][3] =  0.015466291403103;
            
            coef[3][0] = 1;
            coef[3][1] = -0.939062505817492;
            coef[3][2] = 0.030468747091254;
            coef[3][3] = 0.030468747091254;
            
            coef[4][0] = 1;
            coef[4][1] = -0.881618592363189;
            coef[4][2] = 0.059190703818405;
            coef[4][3] = 0.059190703818405;
            
        }

        public void lowpass_filter(double x_n, double x_n_minus_1, double y_n, double y_n_minus_1)
        {
            _x_n = x_n;
            _x_n_minus_1 = x_n_minus_1;
            _y_n = y_n;
            _y_n_minus_1 = y_n_minus_1;
        }

        public double filtervalue(double x_n)
        {
            _x_n = x_n;
            _y_n = b1 * _x_n + b2 * _x_n_minus_1 - a2 * _y_n_minus_1;

            //shift values back in time
            _y_n_minus_1 = _y_n;
            _x_n_minus_1 = _x_n;
            return _y_n;
        }
        public double filtervalue(double x_n,int index)
        {
            _x_n = x_n;
            _y_n = coef[index][2] * _x_n + coef[index][3] * _x_n_minus_1 - coef[index][1] * _y_n_minus_1;

            //shift values back in time
            _y_n_minus_1 = _y_n;
            _x_n_minus_1 = _x_n;
            return _y_n;
        }

    }