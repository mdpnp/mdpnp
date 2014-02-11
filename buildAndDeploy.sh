PARCEL=demo-apps-0.1.4-SNAPSHOT-linux-arm
TARGETS=`cat targets | sed '/^\s*#/d'`
# Add --offline to the following line if no internet access
./gradlew  -POS_NAME=linux -POS_ARCH=arm :interop-lab:demo-apps:distZip

for TARGET in $TARGETS
do
  echo Shipping bundle to $TARGET
  scp interop-lab/demo-apps/build/distributions/$PARCEL.zip ubuntu@$TARGET: > $TARGET.scp.out &
  ssh ubuntu@$TARGET rm -fR $PARCEL/
done
echo Awaiting shipment completion
wait

for TARGET in $TARGETS
do
  echo Decompressing to $TARGET
  ssh ubuntu@$TARGET unzip $PARCEL.zip > $TARGET.unzip.out &
done
echo Awaiting decompression
wait

for TARGET in $TARGETS
do
  echo Stopping device-adapter on $TARGET
  ssh ubuntu@$TARGET sudo service device-adapter stop
  echo Copying more files to $TARGET
  scp interop-lab/demo-apps/src/main/resources/USER_QOS_PROFILES.xml ubuntu@$TARGET:
  scp log4j.properties ubuntu@$TARGET:
  scp device-adapter ubuntu@$TARGET:
  ssh ubuntu@$TARGET chmod 777 $PARCEL/bin/demo-apps
  rm $TARGET.scp.out
  rm $TARGET.unzip.out
  echo Restarting device-adapter on $TARGET
  ssh ubuntu@$TARGET sudo service device-adapter start
done
