# 4O3A Rotator Genius 
## rotctld Interface
Implements the HamLib rotctld network interface.

Written in Scala to allow write-once run anywhere.

*This should be added to HamLib but I haven't written any C code for about 20 years! And I want to use my new Rotator Genius with MacLoggerDX now.*


## Building
### Requiments
- java JDK Tested with 11 and 18
- sbt See https://www.scala-sbt.org

### Build command
`% sbt universal:packageBin`
This should produce :
`target/universal/rgrotctld-0.0.1.zip`

### Install via zip file
1. Unzip the rgrotctld-x.x.x.zip file. 
2. Navigate to the extracted bin directory.
3. Invoke comdnd command line:

`Mac or Linux:  ./rgrotctld --rgHost 192.168.0.16`

`Windows: rgrotctld --rgHost 192.168.0.16`

This will listen for rotctld command on port 4533. You can change port and other details from the command line. Trye:
`bin/rgrotctld --help`


###Status
This the beginning of a work-in-progress.
- Works with MacLoggreDX. rotator 1 hardcoded

Source code at https://github.com/dicklieber/rotatorgenius

###Useful Info
Using JFreeChart with Scala-swing: https://alvinalexander.com/scala/jfreechart-how-create-xy-plot-graph/

JFreeChart Compass: http://www.java2s.com/Code/Java/Chart/JFreeChartCompassChartSample.htm

