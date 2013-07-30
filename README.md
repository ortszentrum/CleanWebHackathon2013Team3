
Introduction
============

This project was created during the CleanWeb Hackathon 2013 in Beijing.

The goal is to analyze the taxi dataset for find area in Beijing with high
potential for pedestrial development.

This program use Spark, a distributed computing framework written in Scala.

Team
====

 * Bingyue
 * Florian
 * Laura
 * Ludovic
 * Martin
 * Ray
 * Sam
 * Yao

Processing
==========

The program goes as as follow:
 1. read the input file
 2. parse the text format into binary format (Sample objects)
 3. filter the events "get in" and "get out" a taxi 
 4. join the consecutive events (get in and get out) into "trip" 
 5. and measure the distance of the trip
 6. filter the trips shorter than 3km.
 7. group the departure and arrival points in 10 clusters
 8. for each cluster, print 30 points (for visualization)

Input will be read from the file sample.csv

Output will be saved into:
 * ./depart.txt/part-XXXX : for the positions of the clusters
 * ./groups.txt/part-XXXX : for some departure/arrival points of the clusters


Build
=====

Install sbt (Simple Build Tool) and execute:

	$ sbt assembly 

Run
===

	$ java -jar 


