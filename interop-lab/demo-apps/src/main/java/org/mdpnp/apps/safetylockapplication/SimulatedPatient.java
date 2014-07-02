package org.mdpnp.apps.safetylockapplication;

import java.util.ArrayList;
import java.util.Random;

import org.mdpnp.apps.safetylockapplication.Resources.Algorithm;

public class SimulatedPatient extends Thread {

	public volatile boolean o2Bad = false;
	public volatile boolean co2Bad = false;
	public volatile boolean respBad = false;
	public volatile boolean O2RocBad = false;
	public volatile boolean Co2RocBad = false;
	public volatile boolean RespRocBad = false;
	public volatile boolean HrprRocBad = false;
	public volatile boolean plethBad = false;
	public volatile boolean hrprBad = false;
	public volatile boolean distressed = false;
	
	private int pulseRate = 70;
	private int heartRate = 70;
	private int o2Saturation = 98;
	private int co2Saturation = 1;
	private int respiratoryRate = 10;
	public Algorithm plethAlgorithm = Algorithm.ALPHA;
	
	private ArrayList<Number> plethysmographSet1 = new ArrayList<Number>();;
	private ArrayList<Number> plethysmographSet2 = new ArrayList<Number>();;
	private ArrayList<Number> plethysmographSet3 = new ArrayList<Number>();;
	private ArrayList<Number> nextPlethysmograph = plethysmographSet1;
	private int plethIndex = 1;
	
	public int maxO2RateOfChange = -1;
	public int maxDHrPrRateOfChange = -1;
	public int maxCo2RateOfChange = -1;
	public int maxRespRateRateOfChange = -1;
	
	private int co2Maximum = 4;
	private int respRateMinimum = 10;
	private int o2Minimum = 95;
	private int hrprDMaximum = 1;
	private int pulseRateMinimum = 50;
	private int pulseRateMaximum = 95;
	
	private boolean O2DirectionSwitch = false;
	private boolean Co2DirectionSwitch = false;
	private boolean HrprDirectionSwitch = false;
	private boolean RespDirectionSwitch = false;
	
	Random randomNumberGenerator;
	
	ArrayList<PatientEventListener> patientEventListeners = new ArrayList<PatientEventListener>();
	
	public SimulatedPatient()
	{	
		initializePleths();
	}
	
	public void addPatientEventListener(PatientEventListener patientEventListener)
	{
		patientEventListeners.add(patientEventListener);
	}
	
	public void setO2Minimum(int newO2Minimum)
	{
		if (newO2Minimum == -1) o2Minimum = 95;
		else o2Minimum = newO2Minimum;
		if (o2Minimum < 90)
			o2Saturation = 95;
		else o2Saturation = o2Minimum + 5;
	}
	
	public void setRespRateMinimum(int newMin)
	{
		if (newMin == -1) respRateMinimum = 10;
		else respRateMinimum = newMin;
		respiratoryRate = respRateMinimum + 5;
	}
	
	public void setCo2Maximum(int newMax)
	{
		if (newMax < 0) co2Maximum = 4;
		else co2Maximum = newMax;
		co2Saturation = co2Maximum - 1;
	}
	
	public void setHrprDMaximum(int newD)
	{
		if (newD == -1) hrprDMaximum = 1;
		else hrprDMaximum = newD;
		pulseRate = 70;
		heartRate = 70;
	}
	
	public void setDistressed(boolean newCondition)
	{
		distressed = newCondition;
		if (distressed && plethBad)
				ruinPleths();
		else if (!distressed)
				initializePleths();
	}
	
	public ArrayList<Number> getPlethysmograph()
	{
		ArrayList<Number> plethysmographToSend = nextPlethysmograph;
		if (plethIndex < 3) plethIndex++;
		else plethIndex = 1;
		if (plethIndex == 1) nextPlethysmograph = plethysmographSet1;
		else if (plethIndex == 2) nextPlethysmograph = plethysmographSet2;
		else if (plethIndex == 3) nextPlethysmograph = plethysmographSet3;
		return plethysmographToSend;
	}
		
	public void run()
	{
		randomNumberGenerator = new Random();

		while (!Thread.currentThread().isInterrupted())
		{
			while (true)
			{
				try {
				    Thread.sleep(1000);
				} catch(InterruptedException ex) {
				    Thread.currentThread().interrupt();
				    return;
				}
				
				if (distressed == true)
				{
					if (o2Bad || O2RocBad)
						this.generateBadO2();
					else generateGoodO2();
					
					if (hrprBad || HrprRocBad)
						generateBadHRPR();
					else generateGoodHRPR();
						
					if (co2Bad || Co2RocBad)
						generateBadCO2();
					else generateGoodCO2();
					
					if (respBad || RespRocBad)
						generateBadResp();
					else generateGoodResp();
				}
				else
				{
					generateGoodO2();
					generateGoodHRPR();
					generateGoodCO2();
					generateGoodResp();
				}
				
				PatientEvent patientEvent = new PatientEvent(this, pulseRate, heartRate, o2Saturation, co2Saturation, 
						respiratoryRate, plethBad, plethAlgorithm, getPlethysmograph());
				if(plethBad && distressed)
					patientEvent.plethIsBad = true;
				else patientEvent.plethIsBad = false;
				for (PatientEventListener patientEventListener : patientEventListeners)
					patientEventListener.actionPerformed(patientEvent);
			}
		}
	}
	
	private void generateBadO2()
	{
		if (O2RocBad)
		{
			if (o2Saturation == o2Minimum)
				o2Saturation = o2Minimum + maxO2RateOfChange + 1;
			else o2Saturation = o2Minimum;
		}
		else
		{
			if (o2Saturation >= o2Minimum) o2Saturation -= 1;
			else if (o2Saturation < o2Minimum-1 && o2Saturation >= o2Minimum - 6)
			{
				if (randomNumberGenerator.nextInt(2) == 0)
					o2Saturation += randomNumberGenerator.nextInt(2);
				else o2Saturation -= randomNumberGenerator.nextInt(2);
			}
			else if (o2Saturation < o2Minimum-6)
			{
				if (randomNumberGenerator.nextInt(2) == 0)
					o2Saturation += randomNumberGenerator.nextInt(4);
				else o2Saturation -= randomNumberGenerator.nextInt(2);
			}
		}
	}
	
	private void generateGoodO2()
	{
		if (o2Saturation <= o2Minimum)
			o2Saturation += 1;
		else if (o2Saturation > o2Minimum && o2Saturation <= 98)
		{
			if (randomNumberGenerator.nextInt(2) == 0)
				o2Saturation += randomNumberGenerator.nextInt(2);
			else o2Saturation -= randomNumberGenerator.nextInt(2);
		}
		else o2Saturation -= 1;
	}
	
	private void generateBadHRPR()
	{
		if (HrprRocBad)
		{
			if (HrprDirectionSwitch == false)
			{
				pulseRate += maxDHrPrRateOfChange/2 + 1;
				heartRate -= (maxDHrPrRateOfChange/2 + 1);
				HrprDirectionSwitch = !HrprDirectionSwitch;
			}
			else
			{
				pulseRate -= maxDHrPrRateOfChange/2 + 1;
				heartRate += (maxDHrPrRateOfChange/2 + 1);
				HrprDirectionSwitch = !HrprDirectionSwitch;
			}
		}
		else 
		{
			int oldPulse = pulseRate;
			if (randomNumberGenerator.nextInt(2) == 0)
				pulseRate += randomNumberGenerator.nextInt(2);
			else pulseRate -= randomNumberGenerator.nextInt(2);
			
			if (pulseRate < pulseRateMinimum) pulseRate += 1;
			else if (pulseRate > pulseRateMaximum) pulseRate -= 1;
			
			int range = hrprDMaximum;
			if (Math.abs(heartRate - oldPulse) > range)
			{
				if (pulseRate > oldPulse)
					heartRate++;
				else if (pulseRate < oldPulse)
					heartRate--;
			}
			else if (heartRate < oldPulse) heartRate--;
			else if (heartRate > oldPulse) heartRate++;
		}
	}
	
	private void generateGoodHRPR()
	{	
		if (randomNumberGenerator.nextInt(2) == 0)
			pulseRate += randomNumberGenerator.nextInt(2);
		else pulseRate -= randomNumberGenerator.nextInt(2);
		if (pulseRate < pulseRateMinimum) pulseRate += 1;
		else if (pulseRate > pulseRateMaximum) pulseRate -= 1;
		
		heartRate = pulseRate;
	}
	
	private void generateBadCO2()
	{
		if (Co2RocBad)
		{
			if (co2Saturation == co2Maximum)
				co2Saturation = co2Maximum - maxCo2RateOfChange - 1;
			else co2Saturation = co2Maximum;
		}
		else
		{
			if (co2Saturation <= co2Maximum)
				co2Saturation++;
		}
	}
	
	private void generateGoodCO2()
	{		
		if (co2Saturation > co2Maximum)
			co2Saturation --;
	}
	
	private void generateBadResp()
	{	
		if (RespRocBad)
		{
			if (respiratoryRate == respRateMinimum)
				respiratoryRate = respRateMinimum + maxRespRateRateOfChange + 1;
			else respiratoryRate = respRateMinimum;
		}
		else
		{
			if (respiratoryRate >= respRateMinimum) respiratoryRate--;
			else if (respiratoryRate < respRateMinimum - 1 && respiratoryRate >= respRateMinimum - 3)
			{
				if (randomNumberGenerator.nextInt(2) == 0)
					respiratoryRate += randomNumberGenerator.nextInt(2);
				else respiratoryRate -= randomNumberGenerator.nextInt(2);
			}
			else if (respiratoryRate < respRateMinimum-3)
			{
				if (randomNumberGenerator.nextInt(2) == 0)
					respiratoryRate += randomNumberGenerator.nextInt(4);
				else respiratoryRate -= randomNumberGenerator.nextInt(2);
			}
		}
	}
	
	private void generateGoodResp()
	{		
		if (respiratoryRate <= respRateMinimum)
			respiratoryRate++;
		else if (respiratoryRate > respRateMinimum && respiratoryRate <= 25)
		{
			if (randomNumberGenerator.nextInt(2) == 0)
				respiratoryRate += randomNumberGenerator.nextInt(2);
			else respiratoryRate -= randomNumberGenerator.nextInt(2);
		}
		else respiratoryRate--;
	}
	
	private void initializePleths()
	{
		plethysmographSet1.clear();
		plethysmographSet2.clear();
		plethysmographSet3.clear();
		
		plethysmographSet1.add(32194);
		plethysmographSet1.add(32137);
		plethysmographSet1.add(32083);
		plethysmographSet1.add(32033);
		plethysmographSet1.add(31989);
		plethysmographSet1.add(31951);
		plethysmographSet1.add(31921);
		plethysmographSet1.add(31895);
		plethysmographSet1.add(31874);
		plethysmographSet1.add(31859);
		plethysmographSet1.add(31848);
		plethysmographSet1.add(31837);
		plethysmographSet1.add(31830);
		plethysmographSet1.add(31828);
		plethysmographSet1.add(31827);
		plethysmographSet1.add(31825);
		plethysmographSet1.add(31823);
		plethysmographSet1.add(31824);
		plethysmographSet1.add(31823);
		plethysmographSet1.add(31823);
		plethysmographSet1.add(31827);
		plethysmographSet1.add(31839);
		plethysmographSet1.add(31851);
		plethysmographSet1.add(31860);
		plethysmographSet1.add(31867);

		plethysmographSet2.add(31869);
		plethysmographSet2.add(31866);
		plethysmographSet2.add(31861);
		plethysmographSet2.add(31862);
		plethysmographSet2.add(31885);
		plethysmographSet2.add(31962);
		plethysmographSet2.add(32136);
		plethysmographSet2.add(32432);
		plethysmographSet2.add(32843);
		plethysmographSet2.add(33333);
		plethysmographSet2.add(33840);
		plethysmographSet2.add(34288);
		plethysmographSet2.add(34627);
		plethysmographSet2.add(34836);
		plethysmographSet2.add(34930);
		plethysmographSet2.add(34928);
		plethysmographSet2.add(34857);
		plethysmographSet2.add(34739);
		plethysmographSet2.add(34602);
		plethysmographSet2.add(34459);
		plethysmographSet2.add(34320);
		plethysmographSet2.add(34185);
		plethysmographSet2.add(34054);
		plethysmographSet2.add(33919);
		plethysmographSet2.add(33771);

		plethysmographSet3.add(33614);
		plethysmographSet3.add(33458);
		plethysmographSet3.add(33315);
		plethysmographSet3.add(33196);
		plethysmographSet3.add(33108);
		plethysmographSet3.add(33053);
		plethysmographSet3.add(33027);
		plethysmographSet3.add(33020);
		plethysmographSet3.add(33020);
		plethysmographSet3.add(33019);
		plethysmographSet3.add(33007);
		plethysmographSet3.add(32982);
		plethysmographSet3.add(32939);
		plethysmographSet3.add(32884);
		plethysmographSet3.add(32819);
		plethysmographSet3.add(32748);
		plethysmographSet3.add(32670);
		plethysmographSet3.add(32590);
		plethysmographSet3.add(32511);
		plethysmographSet3.add(32436);
		plethysmographSet3.add(32366);
		plethysmographSet3.add(32303);
		plethysmographSet3.add(32247);
		plethysmographSet3.add(32222);
		plethysmographSet3.add(32197);
	}
	
	private void ruinPleths()
	{
		plethysmographSet1.clear();
		plethysmographSet2.clear();
		plethysmographSet3.clear();
		
		Random randomNumberGenerator = new Random();
		for (int i = 0; i < 25; i++)
			plethysmographSet1.add(randomNumberGenerator.nextInt(50000));
		for (int i = 0; i < 25; i++)
			plethysmographSet2.add(randomNumberGenerator.nextInt(50000));
		for (int i = 0; i < 25; i++)
			plethysmographSet3.add(randomNumberGenerator.nextInt(50000));
	}

	public void setMaxO2RateOfChange(int maxO2RateOfChange2) {
		O2DirectionSwitch = false;
		maxO2RateOfChange = maxO2RateOfChange2;
	}

	public void setMaxDHrPrRateOfChange(int maxDHrPrRateOfChange2) {
		HrprDirectionSwitch = false;
		maxDHrPrRateOfChange = maxDHrPrRateOfChange2;
	}

	public void setMaxRespRateRateOfChange(int maxRespRateRateOfChange2) {
		RespDirectionSwitch = false;
		maxRespRateRateOfChange = maxRespRateRateOfChange2;
	}

	public void setMaxCo2RateOfChange(int maxCo2RateOfChange2) {
		Co2DirectionSwitch = false;
		maxCo2RateOfChange = maxCo2RateOfChange2;
	}
}
