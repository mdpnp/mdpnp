#!/bin/sh

# Add --offline to the following line if no internet access
#
if [ -z "$SKIPBUILD" ]; then 
	./gradlew :interop-lab:demo-apps:distZip
fi

LOCATION=interop-lab/demo-apps/build/distributions
VERSION=`ls -rt ${LOCATION}/OpenICE-*.zip | tail -n 1 | sed -e 's/.*OpenICE-\(.*\).zip/\1/'`
TARGETS=`cat targets | sed '/^\s*#/d'`
PARCEL=OpenICE-${VERSION}

for TARGET in $TARGETS
do
  echo Shipping ${PARCEL} bundle to $TARGET
  ssh debian@$TARGET mkdir -p /home/debian/OpenICE
  scp ${LOCATION}/$PARCEL.zip debian@$TARGET:/home/debian/OpenICE/ > $TARGET.scp.out &
done
echo Awaiting shipment completion
wait

for TARGET in $TARGETS
do
  echo Decompressing to $TARGET
  ssh debian@$TARGET unzip /home/debian/OpenICE/$PARCEL.zip -d /home/debian/OpenICE > $TARGET.unzip.out &
done
echo Awaiting decompression
wait

for TARGET in $TARGETS
do
  echo Stopping device-adapter on $TARGET
  ssh debian@$TARGET sudo service device-adapter stop
  ssh debian@$TARGET rm /home/debian/OpenICE/OpenICE.current
  echo Copying more files to $TARGET
  scp log4j.properties debian@$TARGET:
  scp device-adapter debian@$TARGET:/etc/init.d/device-adapter
  ssh debian@$TARGET chmod 777 /home/debian/OpenICE/$PARCEL/bin/OpenICE
  ssh debian@$TARGET ln -s /home/debian/OpenICE/$PARCEL /home/debian/OpenICE/OpenICE.current
  
  rm $TARGET.scp.out
  rm $TARGET.unzip.out
  echo Restarting device-adapter on $TARGET
  ssh debian@$TARGET sudo service device-adapter start
done

    