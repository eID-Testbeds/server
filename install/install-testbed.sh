#!/bin/bash

TARGET=/opt
PAYARA_DIR=$TARGET/payara41
TESTBED_APP=eidsrv-testbed-application-1.0-beta-4.ear
TESTCA=ca1.war

# -- changes below this line may require further changes in the script --
asadmin=$PAYARA_DIR/bin/asadmin
required="$TESTCA $TESTBED_APP domain.xml h2-1.4.191.jar"
_user=`id -un`


if ! ls $required &>/dev/null; then
  echo "-- the install-script needs the following files in the current directory:"
  echo "  $required --"
  exit 1
fi

if pgrep -f payara >/dev/null; then
  echo "-- please stop running payara instances and restart install --"
  echo "e.g. PAYARA_DIR/bin/asadmin stop-domain"
  exit 1
fi

sudo mv $PAYARA_DIR $PAYARA_DIR.bak.`date +%s`
sudo unzip payara-4.1.2.174.zip -d $TARGET
sudo chown -R $_user:$_user $PAYARA_DIR

cp domain.xml $PAYARA_DIR/glassfish/domains/domain1/config/
cp h2-1.4.191.jar $PAYARA_DIR/glassfish/lib/

$asadmin start-domain
$asadmin deploy --contextroot ca1 --name ca1 $TESTCA
$asadmin deploy $TESTBED_APP

curl 'http://localhost:8080/eID-Server-Testbed/' &>/dev/null && \
  echo "-- The Server-Testbed should now be accessible at: http://localhost:8080/eID-Server-Testbed/ --"

#TODO download server certificates from: https://localhost:8181/ca1/DVCA_CertDescriptionService?Tester to payara.pem
