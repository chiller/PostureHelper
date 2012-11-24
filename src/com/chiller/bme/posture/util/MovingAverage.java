package com.chiller.bme.posture.util;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MovingAverage {
	private Queue<Float> values;
	private int maxlength;
	public MovingAverage(int maxlength){
		values = new LinkedList<Float>();
		this.maxlength = maxlength;
	}
	
	public void printvalues(){
		Iterator it=this.values.iterator();

        while(it.hasNext())
        {
            Float iteratorValue=(Float)it.next();
            
            System.out.print(String.valueOf(iteratorValue)+" ");
        }
        System.out.print("Done");
		
	}
	
	public void push(Float f){
		this.values.add(f);
		
		if (values.size() > this.maxlength){
			values.poll();	
		}
	}
	
	public Float average(){
		Iterator it=this.values.iterator();
		int i = 0;
		Float result = (float) 0;
        while(it.hasNext())
        {
            i++;
        	Float iteratorValue=(Float)it.next();
            result+=iteratorValue;
            
        }
        return result/i;
	}
}
