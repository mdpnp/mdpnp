package org.mdpnp.devices.philips.intellivue;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;

import org.mdpnp.devices.math.DCT;
import org.mdpnp.devices.net.NetworkLoop;
import org.mdpnp.devices.net.TaskQueue;
import org.mdpnp.devices.philips.intellivue.action.ExtendedPollDataRequest;
import org.mdpnp.devices.philips.intellivue.action.ExtendedPollDataResult;
import org.mdpnp.devices.philips.intellivue.action.ObservationPoll;
import org.mdpnp.devices.philips.intellivue.action.SingleContextPoll;
import org.mdpnp.devices.philips.intellivue.action.impl.ExtendedPollDataResultImpl;
import org.mdpnp.devices.philips.intellivue.action.impl.ObservationPollImpl;
import org.mdpnp.devices.philips.intellivue.action.impl.SingleContextPollImpl;
import org.mdpnp.devices.philips.intellivue.association.AssociationFinish;
import org.mdpnp.devices.philips.intellivue.attribute.Attribute;
import org.mdpnp.devices.philips.intellivue.attribute.AttributeFactory;
import org.mdpnp.devices.philips.intellivue.data.AttributeId;
import org.mdpnp.devices.philips.intellivue.data.CompoundNumericObservedValue;
import org.mdpnp.devices.philips.intellivue.data.NumericObservedValue;
import org.mdpnp.devices.philips.intellivue.data.ObjectClass;
import org.mdpnp.devices.philips.intellivue.data.ObservedValue;
import org.mdpnp.devices.philips.intellivue.data.RelativeTime;
import org.mdpnp.devices.philips.intellivue.data.SampleArrayCompoundObservedValue;
import org.mdpnp.devices.philips.intellivue.data.SampleArrayObservedValue;
import org.mdpnp.devices.philips.intellivue.data.SampleArraySpecification;
import org.mdpnp.devices.philips.intellivue.data.UnitCode;
import org.mdpnp.devices.philips.intellivue.dataexport.CommandType;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportResult;
import org.mdpnp.devices.philips.intellivue.dataexport.command.ActionResult;
import org.mdpnp.devices.philips.intellivue.dataexport.command.impl.ActionResultImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.impl.DataExportResultImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimulatedPulseOximeterImpl extends IntellivueAcceptor {
    private int count = 0;

    private final static Logger log = LoggerFactory.getLogger(SimulatedPulseOximeterImpl.class);

    private final Attribute<RelativeTime> samplePeriod = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_TIME_PD_SAMP, RelativeTime.class);
    private final SampleArrayObservedValue plethSA = new SampleArrayObservedValue(); //AttributeFactory.getAttribute(0, SampleArrayObservedValue.class);
    private final Attribute<SampleArraySpecification> plethSpec = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_SA_SPECN, SampleArraySpecification.class);
    private final Attribute<CompoundNumericObservedValue> cnov = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_NU_CMPD_VAL_OBS, CompoundNumericObservedValue.class);
    private final Attribute<SampleArrayCompoundObservedValue> sacov = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_SA_CMPD_VAL_OBS, SampleArrayCompoundObservedValue.class);
    private final NumericObservedValue pulse = new NumericObservedValue();
    private final NumericObservedValue spo2 = new NumericObservedValue();
    private final DataExportResult der = new DataExportResultImpl();
    private final ActionResult actionResult = new ActionResultImpl();
    private final ExtendedPollDataResult ePollResult = new ExtendedPollDataResultImpl();
    private final SingleContextPoll scp = new SingleContextPollImpl();
    private final ObservationPoll op = new ObservationPollImpl();

    protected int postIncrCount() {
        int count = this.count;
        this.count = ++this.count>=pleth.length?0:this.count;
        return count;
    }
//	private final ThreadGroup tg = new ThreadGroup("") {
//		@Override
//		public void uncaughtException(Thread t, Throwable e) {
//			e.printStackTrace();
//			super.uncaughtException(t, e);
//		}
//	};
//	private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
//		@Override
//		public Thread newThread(Runnable r) {
//			return new Thread(tg, r);
//		}
//	});
    private ScheduledFuture<?> task;

    private Integer invoke;

    @Override
    protected void handle(ExtendedPollDataRequest action) {
        super.handle(action);
        invoke = action.getAction().getMessage().getInvoke();

        log.debug("extended poll invoke="+invoke);
    }


    private final class MyTask extends TaskQueue.TaskImpl<Void> {
        @Override
        public Void doExecute(TaskQueue queue) {
            try {
                short[] values = plethSA.getValue();
                int samples = plethSpec.getValue().getArraySize();
                int sampleSizeBytes = plethSpec.getValue().getSampleSize() / Byte.SIZE;

                int [] realSamples = new int[samples];
                for(int i = 0; i < samples; i++) {
                    int pl = (int) pleth[postIncrCount()];
                    realSamples[i] = pl;
                    values[i*sampleSizeBytes + 0] = (short) (0xFFFF & (pl>>Byte.SIZE));
                    values[i*sampleSizeBytes + 1] = (short) (0xFFFF & pl);
                }
                log.debug("SampleSize:"+plethSpec.getValue().getSampleSize()+" "+Arrays.toString(realSamples));

                if(count == 0) {
    //				offset = offsetRandom.nextInt(pleth.length);
                }
                state = transition(state);
                currentDraw = state.nextDraw();
                myDate.setTime(currentDraw.getTimestamp());
                ePollResult.getAbsoluteTime().setNow();
                pulse.getValue().setFloat(currentDraw.getHeartRate());
                spo2.getValue().setFloat(currentDraw.getSpO2());

                if(null != invoke) {
                    der.setInvoke(invoke);


                    send(der);
                } else {
//					log.debug("NOT SENDING DATA");
                }
            } catch (Throwable t) {
                log.error("error sending data", t);
            }

            return null;
        }
    };

    private static class Draw {
        private int heartRate;
        private int spO2;
        private long time;

        public long getTimestamp() {
            return time;
        }
        public int getHeartRate() {
            return heartRate;
        }
        public int getSpO2() {
            return spO2;
        }
        public void setHeartRate(int heartRate) {
            this.heartRate = heartRate;
        }
        public void setSpO2(int spO2) {
            this.spO2 = spO2;
        }
        public void setTimestamp(long now) {
            this.time = now;
        }
    }


    private final double[] coeffs = new double[] {572784,-3815,-7452,-2196,51,2412,3227,4118,3404,11455,30013,-28722,-1132,-5540,-125,-3859,2048,-1922,4651,1557,26806,-10959,-8725,4525,39,3857,2839,5123,4767,4598,5504,-13121,-1791,4544,65,3178,890,2998,1112,1703,698,-422,-1836,2910,38,1454,206,1504,337,1153,664,372,-3175,1447,-226,345,-263,520,-158,214,-431,-437,-1592,894,41,292,-13,396,73,287,2,269,-106,416,303,360,185,319,154,267,50,241,-66,-53,78,96,-66,84,-47,89,-91,49,-120,20,32,144,0,158,57,185,63,182,76,180,75,201,64,163,42,131,7,91,-22,56,-46,40,-58,22,-63,22,-56,30,-38,48,-16,67,8,91,30,107,48,118,53,118,49,106,32,91,18,64,-1,44,-17,26,-33,16,-48,2,-46,5,-38,14,-22,31,-4,50,12,63,26,77,37,79,40,76,36,71,28,51,12,35,-6,20,-19,6,-31,-2,-35,-4,-35,0,-24,9,-13,22,2,38,15,49,27,56,33,58,32,55,27,45,16,33,2,18,-11,5,-23,-5,-29,-10,-31,-9,-27,-4,-19,7,-6,19,8,30,18,40,26,45,29,44,27,38,19,29,9,17,-3,5,-15,-7,-24,-13,-27,-16,-27,-12,-21,-5,-12,6,1,17,12,27,21,34,26,36,26,34,22,27,14,17,2,5,-9,-6,-19,-16,-25,-19,-27,-19,-23,-15,-15,-5,-5,6,6,15,16,24,23,30,26,30,23,24,18,16,9,5,-3,-6,-13,-15,-22,-22,-25,-24,-24,-22,-20,-15,-10,-5,};
    private final double[] pleth = new double[coeffs.length];


    private void initPleth() {
        DCT.idct(coeffs, 0, 60 , pleth);

        SampleArraySpecification plethSpec = this.plethSpec.getValue();
        int samples = 25;
        int byteLength = Short.SIZE / Byte.SIZE * samples;
        plethSA.setValue(new short[byteLength]);
        plethSA.setLength(byteLength);
        plethSpec.setArraySize(samples);
        plethSpec.setSampleSize((short) Short.SIZE);
        plethSpec.setSignificantBits((short) Short.SIZE);
        samplePeriod.getValue().fromMicroseconds(13333L);

    }

    public SimulatedPulseOximeterImpl(int port) throws IOException {
        super(port);

        cnov.getValue().getList().add(pulse);
        cnov.getValue().getList().add(spo2);

        sacov.getValue().getList().add(plethSA);
        plethSA.setPhysioId(ObservedValue.NOM_PLETH.asOID());



        pulse.setPhysioId(ObservedValue.NOM_PLETH_PULS_RATE.asOID());
        pulse.setUnitCode(UnitCode.NOM_DIM_BEAT_PER_MIN.asOID());

        spo2.setPhysioId(ObservedValue.NOM_PULS_OXIM_SAT_O2.asOID());
        spo2.setUnitCode(UnitCode.NOM_DIM_PERCENT.asOID());
        der.setCommandType(CommandType.ConfirmedAction);
        der.setCommand(actionResult);
        actionResult.setActionType(ObjectClass.NOM_ACT_POLL_MDIB_DATA_EXT.asOID());
        actionResult.setAction(ePollResult);
        ePollResult.setPolledAttributeGroup(AttributeId.NOM_ATTR_GRP_METRIC_VAL_OBS.asOid());
        ePollResult.getPollInfoList().add(scp);
        scp.getPollInfo().add(op);
        op.getHandle().setHandle(1);
        op.getAttributes().add(plethSpec);
        op.getAttributes().add(samplePeriod);
        op.getAttributes().add(cnov);
        op.getAttributes().add(sacov);

        initPleth();


        state = transition(state);
        currentDraw = state.nextDraw();
        if(task != null) {
            task.cancel(false);
            task = null;
        }

    }

    private static class State {
        private final double avgHeartRate;
        private final double avgSpO2;
        private final double stdevHeartRate;
        private final double stdevSpO2;
        private final double floorHeartRate;
        private final double floorSpO2;
        private final double ceilingHeartRate;
        private final double ceilingSpO2;

        private final Draw draw = new Draw();

//		private final MiniMean heartRate;
//		private final MiniMean spo2;

        private double heartRate = 75;
        private double spo2 = 98;

        private final Random random = new Random(System.currentTimeMillis());

        State(double avgHeartRate, double avgSpO2, double stdevHeartRate, double stdevSpO2, double floorHeartRate, double ceilingHeartRate, double floorSpO2, double ceilingSpO2) {
            this.avgHeartRate = avgHeartRate;
            // TODO it's pretty stupid to average a bunch of Guassian draws
            // the mean needs some kind of markov property to create a more interesting simulation
//			this.heartRate = new MiniMean(10, avgHeartRate);
            this.avgSpO2 = avgSpO2;
//			this.spo2 = new MiniMean(4, avgSpO2);
            this.stdevHeartRate = stdevHeartRate;
            this.stdevSpO2 = stdevSpO2;
            this.floorHeartRate = floorHeartRate;
            this.floorSpO2 = floorSpO2;
            this.ceilingHeartRate = ceilingHeartRate;
            this.ceilingSpO2 = ceilingSpO2;

        }

        public Draw nextDraw() {
            heartRate = heartRate + random.nextGaussian() * stdevHeartRate + avgHeartRate;
            spo2 = spo2 + random.nextGaussian() * stdevSpO2 + avgSpO2;
            heartRate = Math.max(heartRate, floorHeartRate);
            heartRate = Math.min(heartRate, ceilingHeartRate);

            spo2 = Math.max(spo2, floorSpO2);
            spo2 = Math.min(spo2, ceilingSpO2);

            draw.setHeartRate((int)Math.round(heartRate));
            draw.setSpO2((int)Math.round(this.spo2));
            draw.setTimestamp(System.currentTimeMillis());
            return draw;
        }

    }

    private static State transition(State state) {
        return state;
    }

    private Draw currentDraw = null;
    private State state = new State(0, 0, 0.25, 0.05, 50, 200, 80, 100);

    private final Date myDate = new Date();

    @Override
    protected synchronized void handle(SocketAddress sockaddr, AssociationFinish message) throws IOException {
        invoke = null;
        super.handle(sockaddr, message);
    }

    public MyTask createMyTask() {
        return new MyTask();
    }

    public static void main(String[] args) throws IOException {
        final int port = args.length > 0 ? Integer.parseInt(args[0]) : Intellivue.DEFAULT_UNICAST_PORT;
        final NetworkLoop networkLoop = new NetworkLoop();
        final SimulatedPulseOximeterImpl ia = new SimulatedPulseOximeterImpl(port);
        ia.accept(networkLoop);
        MyTask task = ia.createMyTask();
        task.setInterval(334L);
        networkLoop.add(task);
        networkLoop.runLoop();

//		DataExportResult der = new DataExportResultImpl();
//		der.setInvoke(0);
//		der.setCommandType(CommandType.ConfirmedAction);
//
//
//
//		ActionResult actionResult = new ActionResultImpl();
//		der.setCommand(actionResult);
//
//		actionResult.setActionType(ObjectClass.NOM_ACT_POLL_MDIB_DATA_EXT.asOID());
//		ExtendedPollDataResult ePollResult = new ExtendedPollDataResultImpl();
//		actionResult.setAction(ePollResult);
//		SingleContextPoll scp = new SingleContextPollImpl();
//
//		ObservationPoll op = new ObservationPollImpl();
//		AttributeNumericObservedValue pulse = new AttributeNumericObservedValue(OIDType.lookup(AttributeId.NOM_ATTR_NU_VAL_OBS.asInt()));
//		pulse.setPhysioId(ObservedValue.NOM_PULS_RATE.asOID());
//		pulse.setUnitCode(UnitCode.NOM_DIM_BEAT_PER_MIN.asOID());
//		pulse.getValue().setFloat(60.0);
//		op.getAttributes().add(pulse);
//		scp.getPollInfo().add(op);
//
//		ePollResult.getPollInfoList().add(scp);
//
//		ePollResult.setPolledAttributeGroup(AttributeId.NOM_ATTR_GRP_METRIC_VAL_OBS.asOid());
//
//		ByteBuffer bb = ByteBuffer.allocate(5000);
//		der.format(bb);
//		bb.flip();
//
//		DataExportResult der1 = new DataExportResultImpl();
//		der1.parse(bb);
    }

}
