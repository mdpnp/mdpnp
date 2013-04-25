#!/usr/bin/env perl

use Text::ParseWords;

# generates IDL from NIST Rosetta information found at:
# http://hit-testing.nist.gov:13110/rtmms/getCSVRosettaVendor.do?currentView=false&vendor=All&name=rosetta_terms&filter=%5B%5D&start=0&limit=50

$_ = <>;
my @headers = split ",";
my %addresses;

my $REFID;
my $CODE10;
my $Standard_Table;

#print join(",", @headers);

for(my $i = 0; $i <= $#headers; $i++) {
#    print "$i \t $headers[$i]\n";
    if("REFID" eq $headers[$i]) {
	$REFID = $i;
    } elsif("CODE10" eq $headers[$i]) {
	$CODE10 = $i;
    } elsif("Standard_Table" eq $headers[$i]) {
	$Standard_Table = $i;
    }
}

my %nomenclature;
my %codes;

#print "REFID=$REFID CODE10=$CODE10 Standard_Table=$Standard_Table\n";

print "#ifndef _NOMENCLATURE_IDL_\n";
print "#define _NOMENCLATURE_IDL_\n";
print "\n";
print "module org {\n";
print "  module mdpnp {\n";
print "    module types {\n";
print "      enum Physio {\n";


while(<>) {
    my @data = Text::ParseWords::parse_line(',', 0, $_);
    my $stdtable = $data[$Standard_Table];
    my $r = $data[$REFID];
    my $c = $data[$CODE10];



#    print "$stdtable $r $c\n";

#    ($stdtable =~ /^$/) && next;
    ($r =~ /^$/) && next;
    ($c =~ /^$/) && next;

    if(exists $nomenclature{$r}) {
	if(!$nomenclature{$r} eq $c) {
	    print STDERR "Redefinition of $r; was $nomenclature{$r}, now $c\n";
	} else {
#	    print STDERR "Redefinition of $r; harmless\n";
	}
	next;
    }
    if(exists $codes{$c}) {
	if(!$codes{$c} eq $r) {
	    print STDERR "Redefinition of $c; was $codes{$c}, now $r\n";
	}
	next;
    }

    $codes{$c} = $r;
    $nomenclature{$r} = $c;
    print "        // from $stdtable\n";
#    print "        const unsigned long $r = $c;\n";
    print "        $r = $c,\n";

}

print "      };\n";
print "    };\n";
print "  };\n";
print "};\n";
print "#endif\n";
print "\n";
