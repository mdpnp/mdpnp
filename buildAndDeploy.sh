PARCEL=OpenICE-0.6.3-SNAPSHOT
TARGETS=`cat targets | sed '/^\s*#/d'`
# Add --offline to the following line if no internet access
./gradlew :interop-lab:demo-apps:distZip

for TARGET in $TARGETS
do
  echo Shipping bundle to $TARGET
  scp interop-lab/demo-apps/build/distributions/$PARCEL.zip debian@$TARGET: > $TARGET.scp.out &
  ssh debian@$TARGET rm -fR $PARCEL/
done
echo Awaiting shipment completion
wait

for TARGET in $TARGETS
do
  echo Decompressing to $TARGET
  ssh debian@$TARGET unzip $PARCEL.zip > $TARGET.unzip.out &
done
echo Awaiting decompression
wait

for TARGET in $TARGETS
do
  echo Stopping device-adapter on $TARGET
  ssh debian@$TARGET sudo service device-adapter stop
  echo Copying more files to $TARGET
  scp log4j.properties debian@$TARGET:
  scp device-adapter debian@$TARGET:/etc/init.d/device-adapter
  ssh debian@$TARGET chmod 777 $PARCEL/bin/OpenICE
  rm $TARGET.scp.out
  rm $TARGET.unzip.out
  echo Restarting device-adapter on $TARGET
  ssh debian@$TARGET sudo service device-adapter start
done
