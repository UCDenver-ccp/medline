# medline
Code related to processing the leased content of Medline provided by NLM

## Converting to BioC format
The `xml2bioc.sh` script facilitates conversion of Medline XML to the [BioC format](http://bioc.sourceforge.net/).
Requires input arguments include:
1) a Medline XML file or a directory containing Medline XML files to be converted to BioC format
2) a base output directory where the BioC formatted files will be stored

By convention, the BioC output files are compressed using gzip and are stored in a two-level
directory structure based on their PubMed IDs in order to distribute the files evenly among
directories. Using this approach, the maximum number of files appearing in any one directory is 1000.

Example pairings of PubMed ID and the path to the corresponding BioC format are shown below:
12345678 --->  [BASE_DIRECTORY]/12/345/12345678.bioc.xml.gz
123456 --->  [BASE_DIRECTORY]/123/123456.bioc.xml.gz
1234 --->  [BASE_DIRECTORY]/1/234.bioc.xml.gz
1 --->  [BASE_DIRECTORY]/0/1.bioc.xml.gz 

```bash
scripts/xml2bioc.sh <input_file_or_directory> <base_output_directory>
```