#!/usr/bin/perl

my $name;
my %namevals = {};

my @contents = [];

while(<>) {
    push @contents, $_;
    /^\s*case\s*(NOM_.+)\:\s$/ && do { $name = $1; next; };
    /^\s*return\s*(0x[0-9A-F]+)\;\s*$/ && do { $namevals{$name} = $1; next; };

#    /^\s*NOM_.*,\s*$/ && print;
}

#for my $x (keys %namevals) {
#    print $x . ":" . $namevals{$x} . "\n";
#}
#print keys(%namevals) . "\n";
#print scalar(@contents) . "\n";

for $_ (@contents) {
    /^\s*(NOM_.+),\s*$/ && do {
	$name = $1;
	my $value = $namevals{$name};
	s/$name/$name($value)/;
    };
    print;
}
