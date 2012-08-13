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
        public int getindex(){
        	int len=_AlgorithmLabel.length;
        	for (int i=0;i<len;i++){
        		if (_AlgorithmLabel[i]==1)
        			return i;
        	}
        		return -1;
        }
}
