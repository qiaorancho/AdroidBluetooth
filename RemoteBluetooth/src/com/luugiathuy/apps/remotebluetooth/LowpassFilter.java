package com.luugiathuy.apps.remotebluetooth;



/*
 * This function is just a lowpass_filter.
 * 
 * */


// class that implements a low pass filter for accel and gyroscope data
    // sampling frequency is assumed to be 50 Hz
    // half db can be 2 Hz or 1 Hz
    // 2 Hz is preferred
    // y[n]=b(1)x[n]+b(2)x[n−1]−a(2)y[n−1]

 public  class LowpassFilter
    {
        //state variables
        double _x_n_minus_1 = 0;
        double _y_n_minus_1 = 0;
        double _x_n = 0;
        double _y_n = 0;

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
        public void lowpass_filter()
        {
            _x_n = _x_n_minus_1 = _y_n = _y_n_minus_1 = 0;
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

    }