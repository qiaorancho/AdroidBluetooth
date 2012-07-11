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

        Queue<InputAndOutput> _PerceptronPoints = new LinkedList<InputAndOutput>();
        static final int POINTS_Q_MAX = 100;

        double _MaxValue = 0;
        double _MaxIndex = -1;
        double _SecondMax=0;

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
            _Eta = 0.5;
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
            _Eta = 0.5;
        }
       
        public void UpdatePerceptron(double[] inputpoint,int label)
        {
            //double[] localinputpoint = inputpoint;
            double[] labelmatrix = new double[_NClasses];
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
            InputAndOutput localinputpoint = new InputAndOutput(inputpoint, labelmatrix);
            _PerceptronPoints.add(localinputpoint);
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
                for (InputAndOutput input : _PerceptronPoints)
                {
                    //for (int i = 0; i < 10; i++)
                    //    Console.Write(input.inputpoint[i] + " ");
                    //Console.WriteLine();
                    double[] algorithm_labels = winner_takes_all(input.getinputpoint());//calculate error matrix
                    update_weight_matrix(input.getinputpoint(), check_class(input.getalgorithmlabels(), algorithm_labels));
                    errors += find_errors(input.getalgorithmlabels(), algorithm_labels);
                }
                if (errors == 0)
                    er = 0;
                passes++;
                if (passes > 100)
                {
                    er = 0;
                    passes = 0;
                }
            }
            System.out.println("Done.");
            //Console.WriteLine("Done.");
        }

        public int Predict(double[] inputpoint)
        {
            double[] algorithm_labelling = winner_takes_all(inputpoint);
            for (int i = 0; i < _NClasses; i++)
            	System.out.println(algorithm_labelling[i] + " ");
            System.out.println();
            int label = -1;
            for (int i = 0; i < _NClasses; i++)
                if (algorithm_labelling[i] == 1)
                    label = i;
            return label;
        }
        private double[] winner_takes_all(double[] currentinputpoint)
        {
            double[] MyClassLabelling = new double[_NClasses];
            double[] classweights = new double[_NClasses];
            double TempIndex=-1;
            _MaxValue = -999999999;
            _MaxIndex = -1;
            _SecondMax= -999999999;
            

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
            
            for (int i = 0; i < _NClasses; i++)
            {
                if (classweights[i] > _MaxValue)
                {
                    _MaxValue = classweights[i];
                    _MaxIndex = i;
                }
            }
            
            //get second max and see if max and second max similar.
             for (int i = 0; i < _NClasses; i++)
            {
            	if (i!=_MaxIndex&&classweights[i] > _SecondMax)
                {
                    _SecondMax = classweights[i];
                    TempIndex=i;
                }
            }
             System.out.println("This is rate "+ _MaxValue/_SecondMax);
             
            //generate the class vector
            for (int i = 0; i < _NClasses; i++)
            {
                if (i == _MaxIndex)
                    MyClassLabelling[i] = 1;
                else
                    MyClassLabelling[i] = 0;
                
            }
            return MyClassLabelling;
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

        private double[] check_class(double[] ground_truth_class_labels, double[] algorithm_class_labels)
        {
            double[] class_difference = new double[_NClasses];
            for (int i = 0; i < _NClasses; i++)
                class_difference[i] = ground_truth_class_labels[i] - algorithm_class_labels[i];
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
