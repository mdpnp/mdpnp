./gradlew :interop-lab:demo-apps:distZip
export PARCEL=demo-apps-0.1.0
for TARGET in $(cat targets)
do
  echo Shipping bundle to $TARGET
  scp interop-lab/demo-apps/build/distributions/$PARCEL.zip ubuntu@$TARGET: > $TARGET.scp.out &
  ssh ubuntu@$TARGET rm -fR $PARCEL/
done
echo Awaiting shipment completion
wait

for TARGET in $(cat targets)
do
  echo Decompressing to $TARGET
  ssh ubuntu@$TARGET unzip $PARCEL.zip > $TARGET.unzip.out &
done
echo Awaiting decompression
wait
  
for TARGET in $(cat targets)
do
  echo Copying more files to $TARGET
  scp interop-lab/demo-apps/src/main/resources/USER_QOS_PROFILES.xml ubuntu@$TARGET:
  scp log4j.properties ubuntu@$TARGET:
  scp device-adapter ubuntu@$TARGET:
  ssh ubuntu@$TARGET chmod 777 $PARCEL/bin/demo-apps
  rm $TARGET.scp.out
  rm $TARGET.unzip.out
done
