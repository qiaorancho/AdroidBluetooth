package com.luugiathuy.apps.remotebluetooth;


public class InputAndOutput
{
        double[] _InputPoint;
        double[] _AlgorithmLabel;
        public InputAndOutput(double[] inputpoint, double[] algorithm_labels)
        {
            _InputPoint = inputpoint;
            _AlgorithmLabel = algorithm_labels;
        }
        public double[] getinputpoint()
        {
                return _InputPoint;
        }
        public double[] getalgorithmlabels ()
        {
         
                return _AlgorithmLabel;
        }
}
