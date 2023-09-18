#!/bin/bash

directorio="7tm"

for dir in /home/cristian/benchmarks/ref7/*/
do
	dir=${dir%*/}      # remove the trailing "/"
    	echo "director: "${dir##*/}    # print everything after the final "/"
	cd $dir
	for i in $(ls *.msf)
	do
	    echo $i
	    /home/cristian/mview-1.65/bin/mview -in msf $i -out fasta > $i.fasta
	done
done


