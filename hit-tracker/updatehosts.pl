#!/usr/bin/perl
use DBI;
use Net::Nslookup;

my $dsn = "DBI:mysql:database=hittracker;host=localhost;port=3306";

my $dbh = DBI->connect($dsn, "root", "NHIE-Gateway");

my $sth = $dbh->prepare("select distinct remote_addr from hits where remote_addr = remote_host;");
my $sth1 = $dbh->prepare("update hits set remote_host = ? where remote_addr = ?;");

$sth->execute();

while(my @row = $sth->fetchrow_array()) {
 my $name = nslookup(host => $row[0], type => "PTR");
 if(!("" eq $name)) {
  print $row[0]."\t".$name."\n";
  $sth1->execute($name, $row[0]);
 }
}

$sth1->finish();
$sth->finish();

$dbh->disconnect();
