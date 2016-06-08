#!/bin/bash

# Tells the scheduler you wish to retain both standard output and error output of the job and to place this output in your
# home directory named for the job name with the suffix .o<jobnumber> for output and .e<jobnumber> for errors. In both cases,
# <jobnumber> is the job ID assigned to your job when submitted to the queue.
#PBS -k oe

# Set the number of nodes, processors per node, memory limit, and maximum wallclock time
#PBS -l nodes=1:ppn=15,mem=15gb,walltime=4:00:00

# Send mail to me
#PBS -M william.baumgartner@ucdenver.edu

# mail alert at "abort" "begin" and "end" of operation
#PBS -m a

# set the name of the job
#PBS -N xml2bioc

#PBS -j oe

# Use submission environment
#PBS -V

JAVA_HOME=/Users/wiba1694/tools/jdk1.8.0_73
INDEX=${PBS_ARRAYID}
MAVEN_HOME=/Users/wiba1694/tools/apache-maven-3.2.2
MAVEN_OPTS="-Xmx10G -Dmaven.repo.local=/scratch/Users/wiba1694/m2"

INPUT_DIRECTORY="/scratch/Users/wiba1694/corpora/medline/baseline-2016"
OUTPUT_DIRECTORY="/scratch/Users/wiba1694/corpora/medline/baseline-2016-bioc"
OUTPUT_SEGMENTATION=ONE_OUTPUT_FILE_PER_INPUT_XML_FILE
#OUTPUT_DIRECTORY="/scratch/Users/wiba1694/corpora/medline/baseline-2016-bioc-one-per-pmid"
#OUTPUT_SEGMENTATION=ONE_FILE_PER_PUBMED_ID

echo "MAVEN OPTS: ${MAVEN_OPTS}"
echo "JAVA HOME: ${JAVA_HOME}"
PATH_TO_ME=`pwd`
echo "PATH_TO_ME: $PATH_TO_ME"

$MAVEN_HOME/bin/mvn -version
cd jobs/nlp-tasks/medline.git
./scripts/xml2bioc.sh \
-i $INPUT_DIRECTORY \
-o $OUTPUT_DIRECTORY \
-s $OUTPUT_SEGMENTATION \
-m $MAVEN_HOME

