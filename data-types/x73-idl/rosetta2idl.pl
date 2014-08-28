#!/usr/bin/env perl

use Text::ParseWords;

# generates IDL from NIST Rosetta information found at:
# https://rtmms.nist.gov/rtmms/index.htm

$_ = <>;
my @headers = split ",";
my %addresses;

my $REFID;

for(my $i = 0; $i <= $#headers; $i++) {
    if("REFID" eq $headers[$i]) {
  $REFID = $i;
    } elsif("Standard_Table" eq $headers[$i]) {
  $Standard_Table = $i;
    }
  
}

my %nomenclature;

print "#ifndef _ROSETTA_IDL_\n";
print "#define _ROSETTA_IDL_\n";
print "\n";
print "module rosetta {\n";

my $mlen = 0;
my $count = 0;
while(<>) {
    my @data = Text::ParseWords::parse_line(',', 0, $_);
    my $stdtable = $data[$Standard_Table];
    my $r = $data[$REFID];

    ($r =~ /^$/) && next;
    $r =~ s/[^A-Za-z0-9_]//g;
    if(exists $nomenclature{$r}) {
      print STDERR "Redefinition of $r\n";
      next;
    }

    $nomenclature{$r} = $r;
    print "  // from $stdtable\n";
     print "  const string $r = \"$r\";\n";
    $count++;

}

print "};\n";
print "#endif\n";
print "\n";

print STDERR "$count codes\n";
