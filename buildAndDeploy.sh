PARCEL=demo-apps-0.4.3
TARGETS=`cat targets | sed '/^\s*#/d'`
# Add --offline to the following line if no internet access
./gradlew :interop-lab:demo-apps:distZip

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
  ssh ubuntu@$TARGET sudo stop device-adapter
  echo Copying more files to $TARGET
  scp log4j.properties ubuntu@$TARGET:
  scp device-adapter.conf ubuntu@$TARGET:/etc/init/device-adapter.conf
  ssh ubuntu@$TARGET chmod 777 $PARCEL/bin/demo-apps
  rm $TARGET.scp.out
  rm $TARGET.unzip.out
  echo Restarting device-adapter on $TARGET
  ssh ubuntu@$TARGET sudo start device-adapter
done
