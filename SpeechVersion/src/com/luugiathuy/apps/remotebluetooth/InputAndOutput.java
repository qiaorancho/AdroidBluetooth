package com.luugiathuy.apps.remotebluetooth;


public class InputAndOutput
{
        double[] _InputPoint;
        double[] _AlgorithmLabel;
        double _Time=0;
        double[] _Refer;
        
        public InputAndOutput(double[] inputpoint, double[] algorithm_labels)
        {
            _InputPoint = inputpoint;
            _AlgorithmLabel = algorithm_labels;
        }
        public InputAndOutput(double[] inputpoint, double[] algorithm_labels, double _time)
        {
            _InputPoint = inputpoint;
            _AlgorithmLabel = algorithm_labels;
        }
        
        public void  Calculate_Refer(double [] inputpoint, double time)
        {
        	
        }
        
        public double[] getinputpoint()
        {
                return _InputPoint;
        }
        
        public double[] getalgorithmlabels ()
        {
         
                return _AlgorithmLabel;
        }
        
        //get_index of the 
        public int getindex(){
        	int len=_AlgorithmLabel.length;
        	for (int i=0;i<len;i++){
        		if (_AlgorithmLabel[i]==1)
        			return i;
        	}
        		return -1;
        }
}
