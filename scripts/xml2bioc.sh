#!/bin/bash

# This script facilitates conversion of Medline XML files to the BioC format.

#
# NOTE: input arguments must be absolute paths

function print_usage {
    echo "Usage:"
    echo "$(basename $0) [OPTIONS]"
    echo "  [-i <input file or directory>]: the Medline XML file or directory containing Medline XML files to process"
    echo "  [-o <base output directory>]: base output directory where the BioC formatted files will be created"
    echo "  [-m <maven home>]: maven home directory"
    echo " Also requires MAVEN_HOME environment variable to be set."

}

while getopts "i:o:m:h" OPTION; do
    case $OPTION in
        # Medline XML file or directory of Medline XML files to process
        i) INPUT_FILE_OR_DIR=$OPTARG
        ;;
        # base output directory
        o) BASE_OUTPUT_DIR=$OPTARG
        ;;
        # output segmentation = 
        s) OUTPUT_SEGMENTATION=$OPTARG
        ;;
        # maven home directory
        m) MAVEN_HOME=$OPTARG
        ;;
        # HELP!
        h) print_usage; exit 0
        ;;
    esac
done

if [[ -z $INPUT_FILE_OR_DIR || -z $BASE_OUTPUT_DIR || -z $MAVEN_HOME ]]; then
	echo "missing input arguments!!!!!"
    print_usage
    echo "input_file_or_dir=$INPUT_FILE_OR_DIR"
    echo "base_output_dir=$BASE_OUTPUT_DIR"
    echo "output_segmentation=$OUTPUT_SEGMENTATION"
    echo "maven_home=$MAVEN_HOME"
    exit 1
fi

if ! [[ -e README.md ]]; then
    echo "Please run from the root of the project."
    exit 1
fi

PATH_TO_ME=`pwd`

$MAVEN_HOME/bin/mvn -e -f scripts/pom-files/pom-medline-xml2bioc.xml exec:exec \
        -DxmlFileOrDirectory=$INPUT_FILE_OR_DIR \
        -DbaseOutputDirectory=$BASE_OUTPUT_DIR \
        -DoutputSegmentation=$OUTPUT_SEGMENTATION

